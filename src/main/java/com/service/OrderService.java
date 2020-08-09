package com.service;

import com.annotation.JwqAutowired;
import com.annotation.JwqComponent;
import com.annotation.JwqScope;
import com.jwqspring.BeanNameAware;
import com.jwqspring.InitializingBean;

@JwqComponent(value = "orderService")
@JwqScope(value = "prototype")
public class OrderService implements InitializingBean, BeanNameAware {

    @JwqAutowired
    private UserService userService;

    //实现定义的BeanNameAware接口，自动为该属性赋值
    private String beanName;

    public void test(){
        System.err.println(this);
        System.err.println(userService);
    }
    public void setBeanName(String beanName) {
        this.beanName=beanName;
        System.err.println(this+"实现了BeanNameAware接口，创建Bean过程中调用setBeanName方法完成属性beanName赋值==>>"+this.beanName);
    }
    public void afterPropertiesSet() {
        System.err.println(this+"实现了InitializingBean接口，初始化过程中调用afterPropertiesSet方法");
    }


}
