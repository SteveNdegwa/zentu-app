package com.zentu.zentu_core.base.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class FeignGlobalConfig {
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
