package com.nerdysoft.annotation.generator;

import com.nerdysoft.annotation.util.FileUtil;
import java.io.PrintWriter;
import java.util.UUID;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.FieldSpec;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.TypeName;
import org.springframework.javapoet.TypeSpec;

@RequiredArgsConstructor
public class DtoGenerator {
    private static final String DTO_PACKAGE_NAME = "com.nerdysoft.dto";

    private final FileUtil fileUtil;

    public void generateDto(String modelName, String basicField, TypeMirror basicFieldTypeMirror, ProcessingEnvironment pe) {
        FieldSpec idField = FieldSpec.builder(UUID.class, "id", Modifier.PRIVATE, Modifier.FINAL).build();
        FieldSpec dynamicField = FieldSpec.builder(TypeName.get(basicFieldTypeMirror), basicField, Modifier.PRIVATE, Modifier.FINAL).build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(UUID.class, "id")
                .addParameter(TypeName.get(basicFieldTypeMirror), basicField)
                .addStatement("this.id = id")
                .addStatement("this.$L = $L", basicField, basicField)
                .build();

        TypeSpec dtoClass = TypeSpec.classBuilder(modelName + "BasicInfoDto")
                .addAnnotation(ClassName.get("lombok", "Getter"))
                .addAnnotation(ClassName.get("lombok", "Setter"))
                .addModifiers(Modifier.PUBLIC)
                .addField(idField)
                .addField(dynamicField)
                .addMethod(constructor)
                .build();

        fileUtil.write(pe, dtoClass, DTO_PACKAGE_NAME);
    }
}
