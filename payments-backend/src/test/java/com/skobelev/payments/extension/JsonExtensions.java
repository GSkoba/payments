package com.skobelev.payments.extension;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStreamReader;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JsonExtensions implements ParameterResolver {

    private static final ResourceLoader resourceLoader = new DefaultResourceLoader();
    private static final Gson mapper = new Gson();

    @Override
    @SneakyThrows
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.isAnnotated(JsonData.class);
    }

    @Override
    @SneakyThrows
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class classType = parameterContext.getParameter().getType();
        String resource = parameterContext.getParameter().getDeclaredAnnotation(JsonData.class).value();
        return mapper.fromJson(new InputStreamReader(resourceLoader.getResource(resource).getInputStream(), UTF_8), classType);
    }
}
