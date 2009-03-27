package com.rosenvold.spring.beans;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author <a href="mailto:kristian AT zenior no">Kristian Rosenvold</a>
 */
@Component
public class SubBean2 extends BaseBean<Service2> {
    @Autowired
    SubBean1 subBean1;

    String problem;
}
