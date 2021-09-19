package com.okay.service;

import com.okay.annotations.Bean;
import com.okay.annotations.Init;

@Bean
public class MyServiceTest implements IMyServiceTest {

    @Init
    public void init() {
        System.out.println("MyServiceTest init()");
    }

    @Override
    public void test() {
        System.out.println("MyServiceTest -> test()");
    }
}