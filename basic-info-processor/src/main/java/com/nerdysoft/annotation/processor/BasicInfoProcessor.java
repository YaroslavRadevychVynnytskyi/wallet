package com.nerdysoft.annotation.processor;

import com.google.auto.service.AutoService;
import com.nerdysoft.annotation.BasicInfoController;
import com.nerdysoft.annotation.generator.ControllerGenerator;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.PrintWriter;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.javapoet.AnnotationSpec;
import org.springframework.javapoet.ClassName;
import org.springframework.javapoet.MethodSpec;
import org.springframework.javapoet.TypeSpec;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.nerdysoft.annotation.BasicInfoController")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@RequiredArgsConstructor
public class BasicInfoProcessor extends AbstractProcessor {
    private static final String SPRING_WEB_PACKAGE_PATH = "org.springframework.web.bind.annotation";
    private static final String WRITE_TO_PATH = "./src/main/java/com/nerdysoft";

    private final ControllerGenerator controllerGenerator;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BasicInfoController.class);
        elements.forEach(e -> {
            BasicInfoController annotation = e.getAnnotation(BasicInfoController.class);
            generateController(e, annotation);
        });

        return true;
    }

    private void generateController(Element element, BasicInfoController annotation) {
        String entityName = element.getSimpleName().toString();
        String basicField = annotation.basicField();
        boolean pagination = annotation.pagination();
        BasicInfoController.DatabaseType databaseType = annotation.databaseType();

        TypeSpec controller = TypeSpec.classBuilder(entityName + "BasicInfoController")
                .addAnnotation(ClassName.get(SPRING_WEB_PACKAGE_PATH, "RestController"))
                .addAnnotation(AnnotationSpec.builder(ClassName.get(SPRING_WEB_PACKAGE_PATH, "RequestMapping"))
                        .addMember("value", "$S", "/" + entityName.toLowerCase() + "/basic-info")
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .addMethod(generateGetByIdMethod(entityName, basicField))
                .addMethod(generateGetAllMethod(entityName, basicField, pagination))
                .build();

        try (PrintWriter writer = new PrintWriter(
                processingEnv.getFiler().createSourceFile(controller.name).openWriter()
        )){
            writer.println(controller);
        } catch (Exception e) {
            throw new RuntimeException("Can't write to a file");
        }
    }

    private MethodSpec generateGetByIdMethod(String entityName, String basicField) {
        return MethodSpec.methodBuilder("get" + entityName + "ById")
                .addAnnotation(ClassName.get(SPRING_WEB_PACKAGE_PATH, "GetMapping"))
                .addAnnotation(AnnotationSpec.builder(ClassName.get(SPRING_WEB_PACKAGE_PATH, "PathVariable"))
                        .addMember("value", "$S", "id")
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get("java.util", "Map"))
                .addParameter(Long.class, "id")
                .addStatement("return service.getBasicInfoById(id)")
                .build();
    }

    private MethodSpec generateGetAllMethod(String entityName, String basicField, boolean pagination) {
        String returnType = pagination ? "Page<Map<String, Object>>" : "List<Map<String, Object>>";
        return MethodSpec.methodBuilder("getAll" + entityName)
                .addAnnotation(ClassName.get(SPRING_WEB_PACKAGE_PATH, "GetMapping"))
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.bestGuess(returnType))
                .addStatement("return service.getAllBasicInfo()")
                .build();
    }
}
