package com.rosenvold.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;

import static junit.framework.Assert.*;
import com.rosenvold.spring.beans.Service1;
import com.rosenvold.spring.beans.SubBean1;
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
    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void testAnalyzeCurrentSpringContext() {
        SpringContextAnalyzer springContextAnalyzer = new SpringContextAnalyzer(applicationContext);
        final List<Problem> problemList = springContextAnalyzer.analyzeCurrentSpringContext();
        assertEquals(1, problemList.size());
        String description = springContextAnalyzer.describe( problemList);
        System.out.println(description);
    }

    @Test
    public void checkAnnotation(){
        SpringContextAnalyzer springContextAnalyzer = new SpringContextAnalyzer(applicationContext);
        assertTrue( springContextAnalyzer.isComponent(Service1.class));
        assertTrue( springContextAnalyzer.hasAutowiredField(SubBean1.class));
        assertFalse( springContextAnalyzer.hasAutowiredField(Service1.class));

    }

    @Test
    public void checkNonPresentAnnotation(){
        SpringContextAnalyzer springContextAnalyzer = new SpringContextAnalyzer(applicationContext);
        assertFalse( springContextAnalyzer.isComponent(FieldProblem.class));
        assertFalse( springContextAnalyzer.hasAutowiredField(FieldProblem.class));
    }

        
}
