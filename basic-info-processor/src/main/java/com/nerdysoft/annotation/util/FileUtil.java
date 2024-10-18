package com.nerdysoft.annotation.util;

import java.io.PrintWriter;
import javax.annotation.processing.ProcessingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.javapoet.JavaFile;
import org.springframework.javapoet.TypeSpec;

@RequiredArgsConstructor
public class FileUtil {
    public void write(ProcessingEnvironment pe, TypeSpec typeSpec, String packageName) {
        try (PrintWriter writer = new PrintWriter(
                pe.getFiler().createSourceFile(packageName + "." + typeSpec.name).openWriter()
        )) {
            JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
            writer.println(javaFile);
        } catch (Exception e) {
            throw new RuntimeException("Can't write type spec to a file", e);
        }
    }
}
