package com.nerdysoft.config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AppContext {
    private static AnnotationConfigApplicationContext context;

    public static void initializeContext() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}
