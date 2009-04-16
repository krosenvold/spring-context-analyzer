package com.rosenvold.spring.beans;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.rosenvold.spring.NonSpringManaged;

/**         
 * A bean that verifies that spring bypasses erasure for base classes.
 * @author <a href="mailto:kristian@zenior*dot*no">Kristian Rosenvold</a>
 */
@Component
public class ClientSubBean1 extends BaseBean<ServiceSingleton1> {
    @Autowired
    ClientSubBean2 clientSubBean2;

    @NonSpringManaged
    String problem;

}
