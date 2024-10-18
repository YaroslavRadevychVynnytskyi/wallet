package com.nerdysoft.annotation.processor;

import static com.nerdysoft.annotation.util.StringUtil.extractDomainModelName;

import com.google.auto.service.AutoService;
import com.nerdysoft.annotation.BasicInfoController;
import com.nerdysoft.annotation.generator.ControllerGenerator;
import com.nerdysoft.annotation.generator.DtoGenerator;
import com.nerdysoft.annotation.generator.RepositoryGenerator;
import com.nerdysoft.annotation.generator.ServiceGenerator;
import com.nerdysoft.config.AppContext;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import org.springframework.stereotype.Component;

@Component
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.nerdysoft.annotation.BasicInfoController")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class BasicInfoProcessor extends AbstractProcessor {
    private final DtoGenerator dtoGenerator;
    private final RepositoryGenerator repositoryGenerator;
    private final ServiceGenerator serviceGenerator;
    private final ControllerGenerator controllerGenerator;

    public BasicInfoProcessor() {
        AppContext.initializeContext();

        dtoGenerator = AppContext.getBean(DtoGenerator.class);
        repositoryGenerator = AppContext.getBean(RepositoryGenerator.class);
        serviceGenerator = AppContext.getBean(ServiceGenerator.class);
        controllerGenerator = AppContext.getBean(ControllerGenerator.class);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BasicInfoController.class);
        elements.forEach(e -> {
            BasicInfoController annotation = e.getAnnotation(BasicInfoController.class);

            String modelName = extractDomainModelName(e.getSimpleName().toString());

            TypeMirror basicFieldTypeMirror = null;
            try {
                annotation.basicFieldType();
            } catch (MirroredTypeException mte) {
                basicFieldTypeMirror = mte.getTypeMirror();
            }

            dtoGenerator.generateDto(modelName, annotation.basicField(), basicFieldTypeMirror, processingEnv);
            repositoryGenerator.generateRepository(modelName, annotation.databaseType(), processingEnv);
            serviceGenerator.generateService(modelName, annotation.basicField(), annotation.databaseType(), processingEnv);
            controllerGenerator.generateController(modelName, annotation.pagination(), processingEnv);

        });

        return true;
    }
}
