package com.rosenvold.spring.test;

import org.springframework.test.context.support.GenericXmlContextLoader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.*;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:kristian@zenior*dot*no">Kristian Rosenvold</a>
 */
public class MockScopeLoader extends GenericXmlContextLoader {
    @Override
    protected void customizeContext(GenericApplicationContext context) {
        SessionScope testSessionScope = new TestSessionScope();
        context.getBeanFactory().registerScope("session", testSessionScope);
        RequestScope requestScope = new TestRequestScope();
        context.getBeanFactory().registerScope("request", requestScope);

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        ServletRequestAttributes attributes = new ServletRequestAttributes(mockHttpServletRequest);
        mockHttpServletRequest.setAttribute(RequestContextListener.class.getName() + ".REQUEST_ATTRIBUTES", attributes);
        LocaleContextHolder.setLocale(mockHttpServletRequest.getLocale(), true);
        RequestContextHolder.setRequestAttributes( attributes, true);  
    }

public class TestSessionScope extends SessionScope {

    @Override
    public Object get(String name, ObjectFactory objectFactory) {
        return super.get(name, objectFactory);
    }
}

    public class TestRequestScope extends RequestScope {

    @Override
    public Object get(String name, ObjectFactory objectFactory) {
        return super.get(name, objectFactory);
    }

}
}