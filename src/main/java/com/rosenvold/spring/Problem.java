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

import java.util.List;

/**
 * @author <a href="mailto:kristian AT zenior no">Kristian Rosenvold</a>
 */
public class Problem {
    List<FieldProblem> fieldProblems;
    String beanName;

    public Problem(List<FieldProblem> fieldProblems, String beanName) {
        this.fieldProblems = fieldProblems;
        this.beanName = beanName;
    }

    public List<FieldProblem> getFieldProblems() {
        return fieldProblems;
    }

    public String getBeanName() {
        return beanName;
    }

    public String describe(){
        StringBuilder resp = new StringBuilder();

        resp.append(  beanName);
        for ( FieldProblem fieldProblem : fieldProblems){
            resp.append( fieldProblem.describe());
        }
        return resp.toString();
    }
}
