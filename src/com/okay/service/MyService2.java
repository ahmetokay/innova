package com.okay.service;

import com.okay.annotations.Bean;
import com.okay.annotations.Init;
import com.okay.annotations.Inject;

@Bean
public class MyService2 {

    @Inject
    private MyService3 myService3;

    @Inject
    private MyService4 myService4;

    @Init
    public void init() {
        System.out.println("MyService2 init()");
    }

    public void test() {
        System.out.println("MyService2 -> test()");
        myService3.test();
        myService4.test();
    }
}