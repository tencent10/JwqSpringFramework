package com.jwqspring;

/**
 * 1.afterPropertiesSet方法，初始化bean的时候执行，可以针对某个具体的bean进行配置。afterPropertiesSet 必须实现 InitializingBean接口
 *
 * 2.init-method 与afterPropertiesSet 都是在初始化bean的时候执行，执行顺序是afterPropertiesSet 先执行，init-method 后执行，
 * 从BeanPostProcessor的作用，可以看出最先执行的是postProcessBeforeInitialization，
 * 然后是afterPropertiesSet，然后是init-method，然后是postProcessAfterInitialization。
 */
public interface InitializingBean {

    public void afterPropertiesSet();
}
