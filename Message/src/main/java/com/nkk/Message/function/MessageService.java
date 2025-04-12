//package com.nkk.Message.function;
//import com.nkk.Message.dto.OrderEvent;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import java.util.function.Function;
//
//@Configuration
//public class MessageService {
//
//    private final Logger logger = LoggerFactory.getLogger(MessageService.class);
//
//    @Bean
//    public Function<OrderEvent,OrderEvent> orderCreated(){
//        return order -> {
//            logger.info("Order Created event published: " + order.toString());
//            return order;
//        };
//    }
////    @Bean
////    public Consumer<PaymentConfirmedEvent> paymentProcessed() {
////        return payment -> {
////            String status = payment.getStatus();
////            logger.info("PAYMENT {}: {}",
////                status.equals("SUCCESS") ? "✅ SUCCESS" : "❌ FAILED",
////                payment);
////        };
////    }
//}
