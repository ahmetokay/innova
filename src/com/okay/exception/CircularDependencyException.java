package com.okay.exception;

import java.util.List;

public class CircularDependencyException extends CustomInjectionException {

    private List<Class<?>> classList;

    public List<Class<?>> getClassList() {
        return classList;
    }

    public void setClassList(List<Class<?>> classList) {
        this.classList = classList;
    }
}