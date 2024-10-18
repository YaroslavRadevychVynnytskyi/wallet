package com.nerdysoft.config;

import com.nerdysoft.annotation.generator.ControllerGenerator;
import com.nerdysoft.annotation.generator.DtoGenerator;
import com.nerdysoft.annotation.generator.RepositoryGenerator;
import com.nerdysoft.annotation.generator.ServiceGenerator;
import com.nerdysoft.annotation.util.FileUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public FileUtil fileUtil() {
        return new FileUtil();
    }

    @Bean
    public DtoGenerator dtoGenerator() {
        return new DtoGenerator(fileUtil());
    }

    @Bean
    public RepositoryGenerator repositoryGenerator() {
        return new RepositoryGenerator(fileUtil());
    }

    @Bean
    public ServiceGenerator serviceGenerator() {
        return new ServiceGenerator(fileUtil());
    }

    @Bean
    public ControllerGenerator controllerGenerator() {
        return new ControllerGenerator(fileUtil());
    }
}
