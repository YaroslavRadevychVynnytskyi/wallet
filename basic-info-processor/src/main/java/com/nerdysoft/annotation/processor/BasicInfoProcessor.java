package com.nerdysoft.annotation.processor;

import static com.nerdysoft.annotation.util.StringUtil.extractDomainModelName;

import com.google.auto.service.AutoService;
import com.nerdysoft.annotation.BasicInfoController;
import com.nerdysoft.annotation.generator.ControllerGenerator;
import com.nerdysoft.annotation.generator.DtoGenerator;
import com.nerdysoft.annotation.generator.RepositoryGenerator;
import com.nerdysoft.annotation.generator.ServiceGenerator;
import com.nerdysoft.annotation.util.FileUtil;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.nerdysoft.annotation.BasicInfoController")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class BasicInfoProcessor extends AbstractProcessor {
    private final DtoGenerator dtoGenerator;
    private final RepositoryGenerator repositoryGenerator;
    private final ServiceGenerator serviceGenerator;
    private final ControllerGenerator controllerGenerator;

    public BasicInfoProcessor() {
        FileUtil fileUtil = new FileUtil();

        dtoGenerator = new DtoGenerator(fileUtil);
        repositoryGenerator = new RepositoryGenerator(fileUtil);
        serviceGenerator = new ServiceGenerator(fileUtil);
        controllerGenerator = new ControllerGenerator(fileUtil);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        roundEnv.getElementsAnnotatedWith(BasicInfoController.class).forEach(e -> {
            BasicInfoController annotation = e.getAnnotation(BasicInfoController.class);

            String modelName = extractDomainModelName(e.getSimpleName().toString());
            String basicFieldType = annotation.basicFieldType();

            dtoGenerator.generateDto(modelName, annotation.basicField(), basicFieldType, processingEnv);
            repositoryGenerator.generateRepository(modelName, annotation.databaseType(), processingEnv);
            serviceGenerator.generateService(modelName, annotation.basicField(), processingEnv, annotation.pagination());
            controllerGenerator.generateController(modelName, processingEnv, annotation.pagination());
        });

        return true;
    }
}
