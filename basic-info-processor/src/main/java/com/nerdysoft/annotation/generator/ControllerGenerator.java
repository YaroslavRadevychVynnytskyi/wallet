package com.nerdysoft.annotation.generator;

import com.nerdysoft.annotation.util.FileUtil;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.javapoet.AnnotationSpec;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.FieldSpec;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.ParameterSpec;
import org.springframework.javapoet.ParameterizedTypeName;
import org.springframework.javapoet.TypeSpec;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
public class ControllerGenerator {
    private static final String CONTROLLER_PACKAGE_NAME = "com.nerdysoft.controller";
    private static final String SERVICE_PACKAGE_NAME = "com.nerdysoft.service";
    private static final String DTO_PACKAGE_NAME = "com.nerdysoft.dto";

    private final FileUtil fileUtil;

    public void generateController(String entityName, ProcessingEnvironment pe, boolean pagination) {
        FieldSpec serviceField = FieldSpec.builder(
                ClassName.get(SERVICE_PACKAGE_NAME, entityName + "BasicInfoService"),
                "service",
                Modifier.PRIVATE, Modifier.FINAL
        ).build();

        TypeSpec.Builder controllerBuilder = TypeSpec.classBuilder(entityName + "BasicInfoController")
                .addAnnotation(RestController.class)
                .addAnnotation(AnnotationSpec.builder(RequestMapping.class)
                        .addMember("value", "$S", "/" + entityName.toLowerCase() + "/basic-info")
                        .build())
                .addAnnotation(ClassName.get("lombok", "RequiredArgsConstructor"))
                .addModifiers(Modifier.PUBLIC)
                .addField(serviceField)
                .addMethod(generateGetByIdMethod(entityName))
                .addMethod(generateGetAllBasicInfo(entityName));

        if (pagination) {
            controllerBuilder.addMethod(generateGetAllBasicInfoPaginated(entityName));
        }

        TypeSpec controller = controllerBuilder.build();

        fileUtil.write(pe, controller, CONTROLLER_PACKAGE_NAME);
    }

    private MethodSpec generateGetByIdMethod(String entityName) {
        return MethodSpec.methodBuilder("getById")
                .addAnnotation(AnnotationSpec.builder(GetMapping.class)
                        .addMember("value", "$S", "/{id}")
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(DTO_PACKAGE_NAME, entityName + "BasicInfoDto"))
                .addParameter(ParameterSpec.builder(UUID.class, "id")
                        .addAnnotation(PathVariable.class)
                        .build()
                )
                .addStatement("return service.getBasicInfoById(id)")
                .build();
    }

    private MethodSpec generateGetAllBasicInfo(String entityName) {
        return MethodSpec.methodBuilder("getAllBasicInfo")
                .addAnnotation(AnnotationSpec.builder(GetMapping.class)
                        .addMember("value", "$S", "/all")
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(DTO_PACKAGE_NAME, entityName + "BasicInfoDto")))
                .addStatement("return service.getAllBasicInfo()")
                .build();
    }

    private MethodSpec generateGetAllBasicInfoPaginated(String entityName) {
        return MethodSpec.methodBuilder("getAllBasicInfoPaginated")
                .addAnnotation(GetMapping.class)
                .addParameter(Pageable.class, "pageable")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(DTO_PACKAGE_NAME, entityName + "BasicInfoDto")))
                .addStatement("return service.getAllBasicInfoPaginated(pageable)")
                .build();
    }
}
