package com.rosenvold.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;

import java.util.List;

import static junit.framework.Assert.*;
import com.rosenvold.spring.beans.ServiceSingleton1;
import com.rosenvold.spring.beans.ClientSubBean1;
import com.rosenvold.spring.beans.ScopedServiceInterface;
import com.rosenvold.spring.test.MockScopeLoader;

/**
 * Yo
 * @author <a href="mailto:kristian AT zenior no">Kristian Rosenvold</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/applicationContext.xml"
}, loader = MockScopeLoader.class)

public class SpringContextAnalyzerTest {
    private String clientWithScopeProxyProblem = "clientWithScopeProxyProblem";

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void testAnalyzeCurrentSpringContext() {
        SpringContextAnalyzer springContextAnalyzer = new SpringContextAnalyzer(applicationContext);
        final List<Problem> problemList = springContextAnalyzer.analyzeCurrentSpringContext();
        assertEquals(2, problemList.size());
        String description = springContextAnalyzer.describe( problemList);
        System.out.println(description);
    }

    @Test
    public void checkAnnotation(){
        SpringContextAnalyzer springContextAnalyzer = new SpringContextAnalyzer(applicationContext);
        assertTrue( springContextAnalyzer.isComponent(ServiceSingleton1.class));
        assertTrue( springContextAnalyzer.hasAutowiredField(ClientSubBean1.class));
        assertFalse( springContextAnalyzer.hasAutowiredField(ServiceSingleton1.class));

    }

    @Test
    public void checkNonPresentAnnotation(){
        SpringContextAnalyzer springContextAnalyzer = new SpringContextAnalyzer(applicationContext);
        assertFalse( springContextAnalyzer.isComponent(FieldProblem.class));
        assertFalse( springContextAnalyzer.hasAutowiredField(FieldProblem.class));
    }


    @Test
    public void testGetFieldProblems(){
        SpringContextAnalyzer springContextAnalyzer = new SpringContextAnalyzer(applicationContext);
        BeanDefinition beanDefinition = springContextAnalyzer.getBeanDefinition(clientWithScopeProxyProblem);
        Object bean = applicationContext.getBean(clientWithScopeProxyProblem);
        List<FieldProblem> list = springContextAnalyzer.getFieldProblemsForSingletonBean(bean, beanDefinition);
        assertEquals(1, list.size());
        FieldProblem fieldProblem = list.get(0);
        assertEquals(FieldProblemType.RequiresScopeProxy, fieldProblem.fieldProblemType);


    }
    @Test
    public void testProxySitutation(){
        ScopedServiceInterface proxied = (ScopedServiceInterface) applicationContext.getBean("sessionScopedWithProxy");
        assertNotNull( proxied);
        assertTrue( java.lang.reflect.Proxy.isProxyClass(proxied.getClass()));
        ScopedServiceInterface nonproxied = (ScopedServiceInterface) applicationContext.getBean("sessionScopedWithoutProxy");
        assertNotNull( nonproxied);
        assertFalse( java.lang.reflect.Proxy.isProxyClass(nonproxied.getClass()));

    }

        
}
