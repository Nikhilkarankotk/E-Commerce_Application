package com.nkk.Products.service;// SkuLockService.java

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class SkuLockService {
    private static final Logger log = LoggerFactory.getLogger(SkuLockService.class);

    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> acquisitionCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> waitNanos = new ConcurrentHashMap<>();

    /**
     * Execute action while holding a per-sku lock. Uses tryLock with timeout to avoid deadlocks.
     * @param sku key for lock
     * @param timeoutMillis how long to wait to acquire lock
     * @param action supplier that returns a result
     * @param <T> result type
     * @return result from action
     * @throws LockAcquisitionException if lock not acquired
     */
    public <T> T withLock(String sku, long timeoutMillis, Supplier<T> action) {
        Objects.requireNonNull(sku, "sku");
        ReentrantLock lock = locks.computeIfAbsent(sku, k -> new ReentrantLock(true)); // fair = true optional
        boolean acquired = false;
        try {
            acquired = lock.tryLock(timeoutMillis, TimeUnit.MILLISECONDS);
            if (!acquired) {
                log.warn("Timeout acquiring lock for sku={} after {}ms", sku, timeoutMillis);
                throw new LockAcquisitionException("Could not acquire lock for sku: " + sku);
            }
            // Diagnostic: owner thread, queue length
            if (lock.getQueueLength() > 0) {
                log.debug("Lock queue length for sku={} is {}", sku, lock.getQueueLength());
            }
            return action.get();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new LockAcquisitionException("Interrupted while acquiring lock for sku: " + sku, ie);
        } finally {
            if (acquired) {
                lock.unlock();
                // Cleanup to avoid memory leak: only remove if no holder and no queued waiters
                if (!lock.isLocked() && lock.getQueueLength() == 0) {
                    locks.remove(sku, lock);
                }
            }
        }
    }
    public Map<String, Long> getAcquisitionCounts() {
        return acquisitionCounts.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));
    }

    public Map<String, Long> getTotalWaitMillis() {
        return waitNanos.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> TimeUnit.NANOSECONDS.toMillis(e.getValue().get())));
    }

    public int activeLockCount() {
        return locks.size();
    }

    // Optional helper: clear metrics (for tests)
    public void resetMetrics() {
        acquisitionCounts.clear();
        waitNanos.clear();
    }
    public static class LockAcquisitionException extends RuntimeException {
        public LockAcquisitionException(String message) { super(message); }
        public LockAcquisitionException(String message, Throwable cause) { super(message, cause); }
    }
}
