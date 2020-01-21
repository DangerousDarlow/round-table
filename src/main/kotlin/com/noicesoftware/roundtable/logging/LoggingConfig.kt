package com.noicesoftware.roundtable.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InjectionPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoggingConfig {
    @Bean
    fun logger(injectionPoint: InjectionPoint): Logger =
            LoggerFactory.getLogger(injectionPoint.methodParameter?.containingClass)
}