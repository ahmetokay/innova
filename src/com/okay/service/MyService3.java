package com.okay.service;

import com.okay.annotations.Bean;
import com.okay.annotations.Init;
import com.okay.annotations.Inject;

@Bean
public class MyService3 {

    @Inject
    private MyService4 myService44;

    @Init
    public void init() {
        System.out.println("MyService3 init()");
    }

    public void test() {
        System.out.println("MyService3 -> test()");
        myService44.test();
    }
}