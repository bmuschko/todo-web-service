package com.bmuschko.todo.webservice.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

public class DockerSecretsDatabasePasswordProcessor implements EnvironmentPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(DockerSecretsDatabasePasswordProcessor.class);

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Resource resource = new FileSystemResource("/run/secrets/db-password");

        if (resource.exists()) {
            try {
                if (logger.isInfoEnabled()) {
                    logger.info("Using database password from injected Docker secret file");
                }

                String dbPassword = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
                Properties props = new Properties();
                props.put("spring.datasource.password", dbPassword);
                environment.getPropertySources().addLast(new PropertiesPropertySource("dbProps", props));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
