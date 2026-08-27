// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    id("androidx.room") version "2.8.4" apply false
//    id("de.jensklingenberg.ktorfit") version "2.7.5"
}