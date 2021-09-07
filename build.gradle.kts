import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    application
    distribution
    id("com.github.andrescv.jcup") version "1.0" // https://github.com/andrescv/jcup-gradle-plugin
//    id("org.jetbrains.grammarkit") version "2021.1.3" // https://github.com/JetBrains/gradle-grammar-kit-plugin
//    id("org.xbib.gradle.plugin.jflex") version "1.4.0"
}

group = "nl.jrdie.taal20"
version = "0.1.0"

repositories {
    mavenCentral()
}

val cup: Configuration by configurations.creating

dependencies {
    implementation("com.github.vbmacher:java-cup-runtime:11b-20160615")
    cup("com.github.vbmacher:java-cup:11b-20160615")
    testImplementation(kotlin("test"))
    implementation("org.jsoup:jsoup:1.14.2")
}

tasks {
    test {
        useJUnitPlatform()
    }

    withType<KotlinCompile>() {
        kotlinOptions.jvmTarget = "13"
    }

    jcup {
        input = file("src/main/kotlin/taal20.cup")
        destdir = file("src/gen/java/nl/jrdie/taal20/_parser")
        parser = "Taal20Parser"
        symbols = "Taal20SymbolType"

    }

//    task<org.jetbrains.grammarkit.tasks.GenerateLexer>("generateTaal20Lexer") {
//        source = "src/main/kotlin/taal20.flex"
//        targetDir = "src/gen/java/nl/jrdie/taal20/lexer"
////        targetDir = "src/gen/java"
//        targetClass = "Taal20Lexer"
//
//    }

}




application {
    mainClass.set("nl.jrdie.taal20.MainKt")
}

distributions {
    create("cup") {
        distributionBaseName.set("cup")
        contents {
            from(cup)
            rename("java-cup-.*.jar", "cup.jar")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(13))
    }
    sourceSets {
        main {
            java {
                srcDir("src/gen/java")
            }
//            jflex {
//                srcDir("src/gen/java")
//            }
        }
    }
}
