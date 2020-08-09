package com.enums;

public enum  ScopeEnum {

    SINGLETON("singleton"),

    PROTOTYPE("prototype")

    ;
    private String scopeName;

    public String getScopeName() {
        return scopeName;
    }

    ScopeEnum(String scopeName) {
        this.scopeName = scopeName;
    }
}
