package com.mckinsey.internal.smp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
public class SmpPushNotificationController {

    @Autowired
    private SmpPushNotificationService service;


    @RequestMapping(method = RequestMethod.GET, path = "/")
    public String helloWorld(){
        return "Hello World!!";
    }

    @RequestMapping(method = RequestMethod.POST, path = "/push")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String newPush(@RequestBody String payload) {
        // TODO validate the payload, return error
        String response = service.parseNotificationInfo(payload);
        return response;
    }
}
