package com.rosenvold.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author <a href="mailto:kristian@zenior.no">Kristian Rosenvold</a>
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
    }
        
}
