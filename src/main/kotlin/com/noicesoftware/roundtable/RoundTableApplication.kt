package com.noicesoftware.roundtable

import com.noicesoftware.roundtable.redis.RedisConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(RedisConfig::class)
class RoundTableApplication

fun main(args: Array<String>) {
	runApplication<RoundTableApplication>(*args)
}
