package com.zentu.zentu_core.base.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@Slf4j
public class WassengerApiFeignConfig {

    @Value("${wassenger.token}")
    private String wassengerToken;


    @Bean
    public RequestInterceptor wassengerAuthInterceptor() {
        log.info("TOKEN--------{}", wassengerToken);
        return requestTemplate -> {
            requestTemplate.header("Content-Type", "application/json");
            requestTemplate.header("Token", wassengerToken);
        };
    }

}

