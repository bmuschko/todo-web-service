package com.bmuschko.todo.webservice.env;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class DatabaseUrlInfoContributor implements InfoContributor {

    @Autowired
    private Environment env;

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("database", Collections.singletonMap("database-url", env.getProperty("spring.datasource.url")));
    }
}
