package com.rosenvold.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.*;
import com.rosenvold.spring.beans.Service1;
import com.rosenvold.spring.beans.SubBean1;

/**
 * @author <a href="mailto:kristian AT zenior no">Kristian Rosenvold</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/applicationContext.xml"
})

public class SpringContextAnalyzerTest {
    @Autowired
    SpringContextAnalyzer springContextAnalyzer ;
        
    @Test
    public void testAnalyzeCurrentSpringContext() {
        final List<Problem> problemList = springContextAnalyzer.analyzeCurrentSpringContext();
        assertEquals(1, problemList.size());
        String description = springContextAnalyzer.describe( problemList);
        System.out.println(description);
    }

    @Test
    public void checkAnnotation(){
        assertTrue( springContextAnalyzer.isComponent(Service1.class));
        assertTrue( springContextAnalyzer.hasAutowiredField(SubBean1.class));
        assertFalse( springContextAnalyzer.hasAutowiredField(Service1.class));

    }

    @Test
    public void checkNonPresentAnnotation(){
        assertFalse( springContextAnalyzer.isComponent(FieldProblem.class));
        assertFalse( springContextAnalyzer.hasAutowiredField(FieldProblem.class));
    }

        
}
