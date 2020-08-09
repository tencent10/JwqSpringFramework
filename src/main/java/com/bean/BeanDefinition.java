package com.bean;

/**
 * bean的属性定义，单例，prototype...
 */
public class BeanDefinition {

    private String scope;

    private Class aClass;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }
}
