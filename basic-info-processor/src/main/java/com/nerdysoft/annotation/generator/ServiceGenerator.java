package com.nerdysoft.annotation.generator;

import com.nerdysoft.annotation.BasicInfoController;
import javax.lang.model.element.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.FieldSpec;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.TypeSpec;

public class ServiceGenerator {
    private final RepositoryGenerator repositoryGenerator;

    public ServiceGenerator(RepositoryGenerator repositoryGenerator) {
        this.repositoryGenerator = repositoryGenerator;
    }

    public TypeSpec generateService(String entityName, BasicInfoController.DatabaseType databaseType) {
        FieldSpec repoField = FieldSpec.builder(
                ClassName.bestGuess(entityName + "Repository"),
                "repository",
                Modifier.PRIVATE, Modifier.FINAL
        ).build();

        return TypeSpec.classBuilder(entityName + "Service")
                .addModifiers(Modifier.PUBLIC)
                .addField(repoField)
                .addMethod(generateGetByIdMethod())
                .build();
    }

    private MethodSpec generateGetByIdMethod() {
        return MethodSpec.methodBuilder("getBasicInfoById")
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get("com.nerdysoft.annotation.dto", "BasicInfoDto"))
                .addParameter(UUID.class, "id")
                .addStatement("return ")

                .build();
    }
}
