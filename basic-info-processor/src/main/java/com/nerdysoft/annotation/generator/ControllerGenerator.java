package com.nerdysoft.annotation.generator;

import javax.lang.model.element.Modifier;
import org.springframework.javapoet.AnnotationSpec;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.TypeSpec;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class ControllerGenerator {

    public TypeSpec generateController(String entityName, String basicField, boolean pagination) {
        return TypeSpec.classBuilder(entityName + "BasicInfoController")
                .addAnnotation(RestController.class)
                .addAnnotation(AnnotationSpec.builder(RequestMapping.class)
                        .addMember("value", "$S", "/" + entityName.toLowerCase() + "/basic-info")
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .addField(//service)
                .addMethod(generateGetByIdMethod(entityName, basicField))
                .build();

    }

    private MethodSpec generateGetByIdMethod(String entityName, String basicField) {
        return MethodSpec.methodBuilder("get" + entityName + "ById")
                .addAnnotation(GetMapping.class)
                .addAnnotation(AnnotationSpec.builder(PathVariable.class)
                        .addMember("value", "$S", "id")
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get("com.nerdysoft.annotation.dto", "BasicInfoDto"))
                .addParameter(Long.class, "id")
                .addStatement("return service.getBasicInfoById(id)")
                .build();
    }
}
