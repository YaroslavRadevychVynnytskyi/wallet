package com.nerdysoft.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface BasicInfoController {
    String basicField();
    String basicFieldType();
    boolean pagination() default false;
    DatabaseType databaseType();

    enum DatabaseType {
        POSTGRES, MONGO
    }
}
