package com.nerdysoft.annotation.generator;

import com.nerdysoft.annotation.BasicInfoController;
import com.nerdysoft.annotation.util.FileUtil;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.ParameterizedTypeName;
import org.springframework.javapoet.TypeSpec;

@RequiredArgsConstructor
public class RepositoryGenerator {
    private static final String REPOSITORY_PACKAGE_NAME = "com.nerdysoft.repo";
    private static final String ENTITY_PACKAGE_NAME = "com.nerdysoft.entity";

    private final FileUtil fileUtil;

    public void generateRepository(String entityName, BasicInfoController.DatabaseType databaseType, ProcessingEnvironment pe) {
        TypeSpec repo = TypeSpec.interfaceBuilder(entityName + "BasicInfoRepository")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface((databaseType.equals(BasicInfoController.DatabaseType.POSTGRES))
                                ? ParameterizedTypeName.get(
                                ClassName.get(JpaRepository.class),
                                ClassName.get(ENTITY_PACKAGE_NAME, entityName),
                                ClassName.get(UUID.class))
                                : ParameterizedTypeName.get(
                                ClassName.get(MongoRepository.class),
                                ClassName.get(ENTITY_PACKAGE_NAME, entityName),
                                ClassName.get(UUID.class)
                        )
                )
                .build();

        fileUtil.write(pe, repo, REPOSITORY_PACKAGE_NAME);
    }
}
