package com.jwqspring;

/**
 *后置处理器
 *BeanPostProcessor也称为Bean后置处理器，它是Spring中定义的接口，在Spring容器的创建过程中（具体为Bean初始化前后）会回调BeanPostProcessor中定义的两个方法。
 *
 */
public interface BeanPostProcessor {

    /**
     * 其中postProcessBeforeInitialization方法会在每一个bean对象的初始化方法调用之前回调；
     *
     * @param bean
     * @param beanName
     * @return
     */
    Object postProcessBeforeInitialization(Object bean, String beanName) ;

    /**
     * postProcessAfterInitialization方法会在每个bean对象的初始化方法调用之后被回调。
     *
     * @param bean
     * @param beanName
     * @return
     */
    Object postProcessAfterInitialization(Object bean, String beanName) ;
}
