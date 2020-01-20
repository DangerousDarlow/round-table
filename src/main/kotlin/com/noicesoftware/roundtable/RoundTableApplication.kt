package com.noicesoftware.roundtable

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RoundTableApplication

fun main(args: Array<String>) {
	runApplication<RoundTableApplication>(*args)
}
