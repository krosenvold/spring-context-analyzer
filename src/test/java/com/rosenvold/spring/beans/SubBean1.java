package com.rosenvold.spring.beans;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.rosenvold.spring.NonSpringManaged;

/**
 * @author <a href="mailto:kristian@zenior.no">Kristian Rosenvold</a>
 */
@Component
public class SubBean1 extends BaseBean<Service1> {
    @Autowired
    SubBean2 subBean1;

    @NonSpringManaged
    String problem;

}
