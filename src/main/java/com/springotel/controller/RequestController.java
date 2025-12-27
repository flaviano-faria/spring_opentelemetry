package com.springotel.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestController {

    private static final Logger logger = LogManager.getLogger(RequestController.class);

    @GetMapping
    public String home(){
        logger.info("RequestController home");
        return "home";
    }

    @GetMapping("/welcome/{name}")
    public String welcome(@PathVariable String name){
        logger.info("RequestController welcome");
        return "welcome " + name ;
    }

    @GetMapping("/process")
    public String process() throws InterruptedException {
        logger.info("RequestController process");
        Thread.sleep(5000);
        logger.info("RequestController process completed");

        return "processed";
    }


}
