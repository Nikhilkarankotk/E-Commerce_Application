package com.nkk.Users.service.auditing;

import com.nkk.Users.entity.auditing.AuditLog;
import com.nkk.Users.repository.AuditLogRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {
    @Autowired
    private AuditLogRepository auditLogRepository;
    @Transactional
    public void logAction(String action, String username, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setUsername(username);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }
}
