package com.okay;

import com.okay.context.ApplicationContext;
import com.okay.exception.CircularDependencyException;
import com.okay.exception.InjectErrorException;
import com.okay.service.MyService1;
import com.okay.service.MyService2;
import com.okay.service.MyService3;
import com.okay.util.ContextUtils;

import java.util.stream.Collectors;

public class ServiceApplication {

    public static void main(String[] args) {
        try {
            ApplicationContext applicationContext = ContextUtils.createApplicationContext(ServiceApplication.class);
            System.out.println("");

            MyService1 service1 = (MyService1) applicationContext.getBeanByName("MyService1");
            System.out.println("MyService1 -> test() called");
            service1.test();

//            MyService2 service2 = (MyService2) applicationContext.getBeanByName("MyService2");
//            System.out.println("MyService2 -> test() called");
//            service2.test();
//
//            MyService3 service3 = (MyService3) applicationContext.getBeanByName("MyService3");
//            System.out.println("MyService3 -> test() called");
//            service3.test();

            applicationContext.displayAllBeans();
        } catch (InjectErrorException e) {
            System.err.println("Inject bean does not find: " + e.getBeanName());
        } catch (CircularDependencyException e) {
            System.err.println("Application Context Failed: Circular Dependency");
            System.err.println(String.join(" -> ", e.getClassList().stream().map(d -> d.getSimpleName()).collect(Collectors.toList())));
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }
}