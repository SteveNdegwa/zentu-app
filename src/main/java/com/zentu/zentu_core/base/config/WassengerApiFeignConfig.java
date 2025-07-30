package com.zentu.zentu_core.base.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class WassengerApiFeignConfig {

    @Value("${wassenger.token}")
    private String wassengerToken;

    @Bean
    public RequestInterceptor wassengerAuthInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Content-Type", "application/json");
            requestTemplate.header("Token", wassengerToken);
        };
    }

}

