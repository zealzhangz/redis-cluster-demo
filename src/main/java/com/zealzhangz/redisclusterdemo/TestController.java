package com.zealzhangz.redisclusterdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Created by zealzhangz.<br/>
 * @version Version: 0.0.1
 * @date DateTime: 2019/07/30 16:14:00<br/>
 */
@RestController
public class TestController {
    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public String test(){
        testService.testSaveEntry("test","test",60);
        return testService.findUsername("test");
    }

}
