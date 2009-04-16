package com.rosenvold.spring.beans;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A bean that verifies that spring bypasses erasure for base classes.
 * @author <a href="mailto:kristian AT zenior no">Kristian Rosenvold</a>
 */
@Component
public class ClientSubBean2 extends BaseBean<ServiceSingleton2> {
    @Autowired
    ClientSubBean1 clientSubBean1;

    String problem;
}
