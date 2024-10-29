package com.nerdysoft.annotation.generator;

import static com.nerdysoft.annotation.util.StringUtil.capitalizeFirstChar;

import com.nerdysoft.annotation.util.FileUtil;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.FieldSpec;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.ParameterizedTypeName;
import org.springframework.javapoet.TypeSpec;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
public class ServiceGenerator {
    private static final String SERVICE_PACKAGE_NAME = "com.nerdysoft.service";
    private static final String REPO_PACKAGE_NAME = "com.nerdysoft.repo";
    private static final String DTO_PACKAGE_NAME = "com.nerdysoft.dto";
    private static final String ENTITY_PACKAGE_NAME = "com.nerdysoft.model";

    private final FileUtil fileUtil;

    public void generateService(String entityName,
                                String basicField,
                                ProcessingEnvironment pe,
                                boolean pagination) {

        FieldSpec repoField = FieldSpec.builder(
                ClassName.get(REPO_PACKAGE_NAME, entityName + "BasicInfoRepository"),
                "repository",
                Modifier.PRIVATE, Modifier.FINAL
        ).build();

        TypeSpec.Builder serviceBuilder = TypeSpec.classBuilder(entityName + "BasicInfoService")
                .addAnnotation(Service.class)
                .addAnnotation(ClassName.get("lombok", "RequiredArgsConstructor"))
                .addModifiers(Modifier.PUBLIC)
                .addField(repoField)
                .addMethod(generateGetByIdMethod(entityName, basicField))
                .addMethod(generateGetAllBasicInfoMethod(entityName, basicField));

        if (pagination) {
            serviceBuilder.addMethod(generateGetAllBasicInfoPaginatedMethod(entityName, basicField));
        }

        TypeSpec service = serviceBuilder.build();

        fileUtil.write(pe, service, SERVICE_PACKAGE_NAME);
    }

    private MethodSpec generateGetByIdMethod(String entityName, String basicField) {
        return MethodSpec.methodBuilder("getBasicInfoById")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(DTO_PACKAGE_NAME, entityName + "BasicInfoDto"))
                .addParameter(UUID.class, "id")
                .addStatement("$T entity = repository.findById(id).orElseThrow()",
                        ClassName.get(ENTITY_PACKAGE_NAME, entityName))
                .addStatement("return new " + entityName + "BasicInfoDto(entity.get" + entityName + "Id(), entity.get"
                        + capitalizeFirstChar(basicField) + "())")
                .build();
    }

    private MethodSpec generateGetAllBasicInfoMethod(String entityName, String basicField) {
        return MethodSpec.methodBuilder("getAllBasicInfo")
                .addModifiers(Modifier.PUBLIC)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(DTO_PACKAGE_NAME, entityName + "BasicInfoDto")))
                .addStatement("return repository.findAll().stream()\n" +
                        "            .map(a -> {\n" +
                        "              return new " + entityName + "BasicInfoDto(a.get" + entityName + "Id(), a.get" + capitalizeFirstChar(basicField) + "());\n" +
                        "            })\n" +
                        "            .toList()")
                .build();
    }

    private MethodSpec generateGetAllBasicInfoPaginatedMethod(String entityName, String basicField) {
        return MethodSpec.methodBuilder("getAllBasicInfoPaginated")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Pageable.class, "pageable")
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(DTO_PACKAGE_NAME, entityName + "BasicInfoDto")))
                .addStatement("return repository.findAll(pageable).stream()\n" +
                        "            .map(a -> {\n" +
                        "              return new " + entityName + "BasicInfoDto(a.get" + entityName + "Id(), a.get" + capitalizeFirstChar(basicField) + "());\n" +
                        "            })\n" +
                        "            .toList()")
                .build();
    }
}
