/*
 * Copyright 2009 Kristian Rosenvold
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.rosenvold.spring;


import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.ArrayList;

import javax.annotation.Resource;

/**
 * @author <a href="mailto:kristian AT zenior no">Kristian Rosenvold</a>
 */
public class SpringContextAnalyzer {
    ApplicationContext applicationContext;


    public SpringContextAnalyzer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public String describe(List<Problem> problems) {
        if (problems.size() == 0) return null;
        StringBuilder resp = new StringBuilder();
        for (Problem problem : problems) {
            resp.append(problem.getBeanName());
            resp.append(": Contains member fields not under visible spring management\n");
        }
        return resp.toString();
    }

    public List<Problem> analyzeCurrentSpringContext() {
        List<Problem> problems = new ArrayList<Problem>();
        List<FieldProblem> problemsForClass;
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext;
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            final Object bean = applicationContext.getBean(beanName);

            BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition( beanName);

            if (applicationContext.isSingleton( beanName) && !isSpringFrameworkClass( bean.getClass())) {
                problemsForClass = getFieldProblems( bean.getClass());
                if (problemsForClass.size() > 0) {
                    problems.add(new Problem(problemsForClass, beanName));
                }
            }

        }
        return problems;
    }


    private boolean isSpringFrameworkClass( Class clazz){
        return clazz.getCanonicalName().startsWith("org.springframework");
    }
    private boolean hasLegalSingletonFields(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!isLegalForSingletonBean(field)) return false;
        }
        return true;
    }
    private List<FieldProblem> getFieldProblems(Class clazz) {
        List<FieldProblem> fieldProblems = new ArrayList<FieldProblem>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!isLegalForSingletonBean(field)) {
                fieldProblems.add( new FieldProblem(field, FieldProblemType.NotVaildForSingleton));
            }
        }
        return fieldProblems;
    }


    private boolean isLegalForSingletonBean(Field field) {
        return isNonSpringManaged(field) || isAutowired(field) || isResource(field) || isStatic(field) || isFinal(field);
    }

    private boolean isStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    private boolean isFinal(Field field) {
        return Modifier.isFinal(field.getModifiers());
    }

    boolean hasAutowiredField(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (isAutowired(field)) return true;
        }
        return false;
    }


    private boolean isSingletonBean(Class clazz) {
        return !isWebScoped(clazz); // Oooh. This one is a bit quick.
    }

    private boolean isWebScoped(Class clazz) {
        return isRequestScoped(clazz) || isSessionScoped(clazz);
    }

    private boolean isRequestScoped(Class clazz) {
        Scope scope = getScope(clazz);
        return scope != null && "request".equals(scope.value());
    }

    private boolean isSessionScoped(Class clazz) {
        Scope scope = getScope(clazz);
        return scope != null && "session".equals(scope.value());
    }

    private Scope getScope(Class clazz) {
        @SuppressWarnings({"unchecked"}) final Annotation annotation = clazz.getAnnotation(Scope.class);
        if (annotation == null) return null;
        return (Scope) annotation;
    }

     boolean isComponent(Class clazz) {
        final Annotation[] annotations = clazz.getAnnotations();
        return containsAnnotation(annotations, Component.class);
    }

    private boolean isNonSpringManaged(Field field) {
        return containsAnnotation(field.getDeclaredAnnotations(), NonSpringManaged.class);
    }

    private boolean isAutowired(Field field) {
        return containsAnnotation(field.getDeclaredAnnotations(), Autowired.class);
    }

    private boolean isResource(Field field) {
        return containsAnnotation(field.getDeclaredAnnotations(), Resource.class);
    }

    private boolean containsAnnotation(Annotation[] annotations, Class<? extends Annotation> requestedType) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(requestedType)) {
                return true;
            }
            if (contains1LevelNestedAnnotation(annotation, requestedType)) {
                return true;
            }
        }
        return false;
    }

    private boolean contains1LevelNestedAnnotation(Annotation annotation, Class<? extends Annotation> requestedType) {
        final Annotation[] declaredAnnotations = annotation.annotationType().getDeclaredAnnotations();
        for (Annotation anAnnotation : declaredAnnotations) {
            if (anAnnotation.annotationType().equals(requestedType)) {
                return true;
            }
        }
        return false;
    }
}
