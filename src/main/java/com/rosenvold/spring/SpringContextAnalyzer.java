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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.config.BeanDefinition;
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
            resp.append(problem.describe());
        }
        return resp.toString();
    }

    public List<Problem> analyzeCurrentSpringContext() {
        List<Problem> problems = new ArrayList<Problem>();
        List<FieldProblem> problemsForClass;
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            final Object bean = applicationContext.getBean(beanName);

            BeanDefinition beanDefinition = getBeanDefinition( beanName);

            if (applicationContext.isSingleton( beanName) && !isSpringFrameworkClass( bean.getClass())) {
                problemsForClass = getFieldProblemsForSingletonBean( bean, beanDefinition);
                if (problemsForClass.size() > 0) {
                    problems.add(new Problem(problemsForClass, beanName));
                }
            }

        }
        return problems;
    }

    BeanDefinition getBeanDefinition(String beanName){
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext;
        return  beanDefinitionRegistry.getBeanDefinition( beanName);
    }

    private boolean isSpringFrameworkClass( Class clazz){
        return clazz.getCanonicalName().startsWith("org.springframework");
    }
    
    List<FieldProblem> getFieldProblemsForSingletonBean(Object singletonBean, BeanDefinition beanDefinition) {
        List<FieldProblem> fieldProblems = new ArrayList<FieldProblem>();
        Field[] fields = singletonBean.getClass().getDeclaredFields();
        for (Field field : fields) {
            Class clazz = field.getDeclaringClass();
            if (!isInLegalRuntimeState(singletonBean , field)) {  // This is the best analysis. Simply checks that all declared fields have object values when spring is done.
                fieldProblems.add( new FieldProblem(field, FieldProblemType.NotInitializedAtRuntime));
            }

            if(violatesProxyRequirementsForSingletonClient( singletonBean, field)){
                fieldProblems.add( new FieldProblem(field, FieldProblemType.RequiresScopeProxy));

            }
            /*else if (!isLegalForSingletonBean(field, beanDefinition)) {
                fieldProblems.add( new FieldProblem(field, FieldProblemType.NotVaildForSingleton));
            } */
        }
        return fieldProblems;
    }

    /**
     * Checks if the value in a given field can be accessed from a singleton bean without a proxy.
     * @param singletonBean The client bean
     * @param field The field to check
     * @return true if the requirements are verified not to be met, false in all other cases
     */
    boolean violatesProxyRequirementsForSingletonClient(Object singletonBean, Field field) {
        Object injectedBean = getFieldValue( singletonBean, field);
        if (injectedBean == null) return false; // Dont care
        String[] beanNamesForType = applicationContext.getBeanNamesForType(injectedBean.getClass());
        if (beanNamesForType.length != 1 ) return false;
        BeanDefinition injectedBeanDef = getBeanDefinition( beanNamesForType[0]);
        return (isWebScope(injectedBeanDef.getScope()) && !isProxy( injectedBean));
    }

    private boolean isWebScope(String scope){
        return "request".equals( scope) || "session".equals(scope);
    }
    private boolean isProxy(Object value){
         return java.lang.reflect.Proxy.isProxyClass(value.getClass());
    }


    private boolean isLegalForSingletonBean(Field field, BeanDefinition beanDefinition) {
        return isNonSpringManaged(field) || isAutowired(field) || isResource(field) || isStatic(field) || isFinal(field);
    }


    private boolean isInLegalRuntimeState(Object instance, Field field){
        return isNonSpringManaged(field) || isInitialized( instance, field);
    }
    /*
     * Indicates if the supplied field has been initialized to a value. This will include any postconstruct spring blocks etc and is a quite reliable way of analyzing the result.
     */
    private boolean isInitialized(Object instance, Field field) {
        return getFieldValue(instance, field)!= null;
    }

    private Object getFieldValue(Object instance, Field field) {
        if (!field.isAccessible())
            field.setAccessible(true);
        try {
            return  field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
