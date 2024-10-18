package com.nerdysoft.annotation.generator;

import com.nerdysoft.annotation.BasicInfoController;
import javax.lang.model.element.Modifier;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.ParameterizedTypeName;
import org.springframework.javapoet.TypeSpec;

public class RepositoryGenerator {

    public TypeSpec generateRepository(String entityName, BasicInfoController.DatabaseType databaseType) {
        return TypeSpec.interfaceBuilder(entityName + "Repository")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface((databaseType.equals(BasicInfoController.DatabaseType.POSTGRES))
                        ? ParameterizedTypeName.get(
                                ClassName.get(JpaRepository.class),
                                ClassName.bestGuess(entityName),
                                ClassName.get(UUID.class))
                        : ParameterizedTypeName.get(
                                ClassName.get(MongoRepository.class),
                                ClassName.bestGuess(entityName),
                                ClassName.get(String.class)
                        )
                )
                .build();
    }
}
