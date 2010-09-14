package com.rosenvold.spring;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:kristian@zenior.no">Kristian Rosenvold</a>
 */
public class FieldProblem {
    Field field;
    FieldProblemType fieldProblemType;


    public FieldProblem(Field field, FieldProblemType fieldProblemType) {
        this.field = field;
        this.fieldProblemType = fieldProblemType;
    }

    public Field getField() {
        return field;
    }

    public FieldProblemType getFieldProblemType() {
        return fieldProblemType;
    }

    public String describe(){
        return "Field \"" + field.getName() + "\"  has problem " + fieldProblemType;
    }

}
