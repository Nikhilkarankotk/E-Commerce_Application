//package com.nkk.Message.function;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import java.util.function.Function;
//import java.util.logging.Logger;
//
//@Configuration
//public class MessagingFunctions {
//    private static final Logger logger = Logger.getLogger(MessagingFunctions.class.getName());
//
//    @Bean
//    public Function<OrderEvent,OrderEvent> publishOrderCreatedEvent() {
//        return orderEvent -> {
//            logger.info("Order Created event published: " + orderEvent.toString());
//            return orderEvent;
//        };
//    }
//    @Bean
//    public Function<PaymentConfirmedEvent,PaymentConfirmedEvent> processPaymentConfirmedEvent() {
//        return paymentConfirmedEvent -> {
//            logger.info("Payment Confirmed event received: " + paymentConfirmedEvent.toString());
//            return paymentConfirmedEvent;
//        };
//    }
//}
