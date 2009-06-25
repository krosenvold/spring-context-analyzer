package com.rosenvold.spring.beans;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author <a href="mailto:kristian@zenior*dot*no">Kristian Rosenvold</a>
 */
@Component
public class ClientWithScopeProxyProblem {
   @Autowired public ServiceSessionScopedAutowired serviceSessionScopedAutowired;   // This one is illegal beacuse it does not have a scope proxy
}