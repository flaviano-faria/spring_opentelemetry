package com.springotel.controller;

import com.springotel.model.Payment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class PaymentController {

    private static final Logger logger =
            LoggerFactory.getLogger(PaymentController.class);


    @PostMapping("/payment")
    public String doPayment(@RequestBody Payment payment) {
        logger.info("Payment received: " + payment);

        return "payment successful";
    }
}
