package com.zentu.zentu_core.base.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class IdentityServiceFeignConfig {
    @Value("${identity.api-key}")
    private String identityApiKey;

    @Bean
    public RequestInterceptor identityServiceAuthInterceptor() {
        return requestTemplate ->
                requestTemplate.header("X-API-KEY", identityApiKey);
    }
}
