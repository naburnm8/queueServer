package ru.naburnm8.queueserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QueueServerApplication

fun main(args: Array<String>) {
    runApplication<QueueServerApplication>(*args)
}
