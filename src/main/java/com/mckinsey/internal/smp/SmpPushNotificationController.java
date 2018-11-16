package com.mckinsey.internal.smp;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class SmpPushNotificationController {

    @RequestMapping("/")
    public String helloWorld(){
        return "Hello World!!";
    }
}
