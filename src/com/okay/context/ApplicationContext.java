package com.okay.context;

import com.okay.util.ContextUtils;

import java.util.HashMap;

public class ApplicationContext {

    private HashMap<String, Class<?>> beanMap;

    public Object getBeanByName(String beanName) throws InstantiationException, IllegalAccessException {
        return ContextUtils.createBeanObject(beanMap.get(beanName));
    }

    public void displayAllBeans() {
        System.out.println("");
        System.out.println("-Bean List-");
        if (beanMap != null) {
            beanMap.forEach((key, value) -> System.out.println("Name : " + key + ", Type : " + value));
        }
    }

    public HashMap<String, Class<?>> getBeanMap() {
        return beanMap;
    }

    public void setBeanMap(HashMap<String, Class<?>> beanMap) {
        this.beanMap = beanMap;
    }
}