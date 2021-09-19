package com.okay.service;

import com.okay.annotations.Bean;
import com.okay.annotations.Init;
import com.okay.annotations.Inject;

@Bean
public class MyService1 {

    @Inject
    private MyService2 myService2;

    @Inject
    private IMyServiceTest myServiceTest;

    @Init
    public void init() {
        System.out.println("MyService1 init()");
    }

    public void test() {
        System.out.println("MyService1 -> test()");
        myService2.test();
        myServiceTest.test();
    }
}