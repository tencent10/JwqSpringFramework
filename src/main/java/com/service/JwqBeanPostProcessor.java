package com.service;

import com.annotation.JwqComponent;
import com.jwqspring.BeanPostProcessor;

@JwqComponent
public class JwqBeanPostProcessor implements BeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.err.println("postProcessBeforeInitialization==>>初始化前");
        return null;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.err.println("postProcessBeforeInitialization==>>初始化后");
        return null;
    }
}
