package com.example.taras.core.helpercore

fun String.removeNameExtra(): String {
    return this.replace("_", " ")
}
