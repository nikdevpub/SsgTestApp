// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.org.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.hiltPlugin) apply false
    alias(libs.plugins.kaptPlugin) apply false
    alias(libs.plugins.kspPlugin) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose.compiler) apply false
}
true // Needed to make the Suppress annotation work for the plugins block
