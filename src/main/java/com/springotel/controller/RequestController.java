package com.springotel.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestController {

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    @GetMapping
    public String home(){
        logger.trace("RequestController home");
        return "home";
    }

    @GetMapping("/welcome/{name}")
    public String welcome(@PathVariable String name){
        logger.trace("RequestController welcome");
        return "welcome " + name ;
    }

    @GetMapping("/process")
    public String process() throws InterruptedException {
        logger.trace("RequestController process");
        Thread.sleep(5000);
        logger.info("RequestController process completed");

        return "processed";
    }


}
