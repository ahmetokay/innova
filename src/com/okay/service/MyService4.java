package com.okay.service;

import com.okay.annotations.Bean;
import com.okay.annotations.Init;
import com.okay.annotations.Inject;

@Bean
public class MyService4 {

//    @Inject
//    private MyService2 myService2;
//
//    @Inject
//    private MyService1 myService1;

    @Init
    public void init() {
        System.out.println("MyService4 init()");
    }

    public void test() {
        System.out.println("MyService4 -> test()");
//        myService2.test();
//        myService1.test();
    }
}