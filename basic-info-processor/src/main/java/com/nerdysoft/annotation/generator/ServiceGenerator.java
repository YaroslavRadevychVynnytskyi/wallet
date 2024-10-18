package com.nerdysoft.annotation.generator;

import static com.nerdysoft.annotation.util.StringUtil.capitalizeFirstChar;

import com.nerdysoft.annotation.BasicInfoController;
import com.nerdysoft.annotation.util.FileUtil;
import java.util.UUID;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import lombok.RequiredArgsConstructor;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.FieldSpec;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.TypeSpec;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
public class ServiceGenerator {
    private static final String SERVICE_PACKAGE_NAME = "com.nerdysoft.service";

    private final FileUtil fileUtil;

    public void generateService(String entityName,
                                String basicField,
                                BasicInfoController.DatabaseType databaseType,
                                ProcessingEnvironment pe) {

        FieldSpec repoField = FieldSpec.builder(
                ClassName.get("com.nerdysoft.repo", entityName + "BasicInfoRepository"),
                "repository",
                Modifier.PRIVATE, Modifier.FINAL
        ).build();

        TypeSpec service = TypeSpec.classBuilder(entityName + "BasicInfoService")
                .addAnnotation(Service.class)
                .addAnnotation(ClassName.get("lombok", "RequiredArgsConstructor"))
                .addModifiers(Modifier.PUBLIC)
                .addField(repoField)
                .addMethod(generateGetByIdMethod(entityName, basicField))
                .build();

        fileUtil.write(pe, service, SERVICE_PACKAGE_NAME);
    }

    private MethodSpec generateGetByIdMethod(String entityName, String basicField) {
        return MethodSpec.methodBuilder("getBasicInfoById")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get("com.nerdysoft.dto", entityName + "BasicInfoDto"))
                .addParameter(UUID.class, "id")
                .addStatement("$T entity = repository.findById(id).orElseThrow()",
                        ClassName.get("com.nerdysoft.entity", entityName))
                .addStatement("return new " + entityName + "BasicInfoDto(entity.get" + entityName + "Id(), entity.get"
                        + capitalizeFirstChar(basicField) + "())")
                .build();
    }
}
