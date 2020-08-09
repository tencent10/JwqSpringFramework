package com.test;

import com.config.AppConfig;
import com.jwqspring.JwqSpringApplicationContext;
import com.service.OrderService;

/**
 * 手写Spring框架
 */
public class TestMyFramework {
    public static void main(String[] args) {
        JwqSpringApplicationContext applicationContext = new JwqSpringApplicationContext(AppConfig.class);

        OrderService orderService = (OrderService) applicationContext.getBean("orderService");
       /* OrderService orderService1 = (OrderService) applicationContext.getBean("orderService");
        OrderService orderService2 = (OrderService) applicationContext.getBean("orderService");
        System.err.println("测试单例/多例bean"+orderService);
        System.err.println("测试单例/多例bean"+orderService1);
        System.err.println("测试单例/多例bean"+orderService2);*/

        orderService.test();


    }
}
