package com.rosenvold.spring.beans;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author <a href="mailto:kristian AT zenior no">Kristian Rosenvold</a>
 */
@Component
public abstract class BaseBean<T>  {
    private T service;

    public T getService() {
        return service;
    }

    @Autowired
    public void setService(T service) {
        this.service = service;
    }
}
