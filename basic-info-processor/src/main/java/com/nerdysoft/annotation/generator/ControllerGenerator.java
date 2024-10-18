package com.nerdysoft.annotation.generator;

import com.nerdysoft.annotation.util.FileUtil;
import java.io.PrintWriter;
import java.util.UUID;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import lombok.RequiredArgsConstructor;
import org.springframework.javapoet.AnnotationSpec;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.FieldSpec;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.ParameterSpec;
import org.springframework.javapoet.TypeSpec;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
public class ControllerGenerator {
    private static final String CONTROLLER_PACKAGE_NAME = "com.nerdysoft.controller";

    private final FileUtil fileUtil;

    public void generateController(String entityName, boolean pagination, ProcessingEnvironment pe) {
        FieldSpec serviceField = FieldSpec.builder(
                ClassName.get("com.nerdysoft.service", entityName + "BasicInfoService"),
                "service",
                Modifier.PRIVATE, Modifier.FINAL
        ).build();

        TypeSpec controller = TypeSpec.classBuilder(entityName + "BasicInfoController")
                .addAnnotation(RestController.class)
                .addAnnotation(AnnotationSpec.builder(RequestMapping.class)
                        .addMember("value", "$S", "/" + entityName.toLowerCase() + "/basic-info")
                        .build())
                .addAnnotation(ClassName.get("lombok", "RequiredArgsConstructor"))
                .addModifiers(Modifier.PUBLIC)
                .addField(serviceField)
                .addMethod(generateGetByIdMethod(entityName))
                .build();

        fileUtil.write(pe, controller, CONTROLLER_PACKAGE_NAME);
    }

    private MethodSpec generateGetByIdMethod(String entityName) {
        return MethodSpec.methodBuilder("getById")
                .addAnnotation(AnnotationSpec.builder(GetMapping.class)
                        .addMember("value", "$S", "/{id}")
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get("com.nerdysoft.dto", entityName + "BasicInfoDto"))
                .addParameter(ParameterSpec.builder(UUID.class, "id")
                        .addAnnotation(PathVariable.class)
                        .build()
                )
                .addStatement("return service.getBasicInfoById(id)")
                .build();
    }
}
