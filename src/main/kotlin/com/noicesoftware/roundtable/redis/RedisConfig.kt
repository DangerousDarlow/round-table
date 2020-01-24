package com.noicesoftware.roundtable.redis

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("redis")
data class RedisConfig(
        val url: String,
        val defaultDuration: Long
)