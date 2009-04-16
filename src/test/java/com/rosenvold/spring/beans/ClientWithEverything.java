package com.rosenvold.spring.beans;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author <a href="mailto:kristian@zenior*dot*no">Kristian Rosenvold</a>
 */
@Component
public class ClientWithEverything {
   @Autowired public ServiceSessionScopedXmlWired serviceSessionScopedXmlWired;
   @Autowired public ServiceSingleton1 serviceSingleton1;
}
