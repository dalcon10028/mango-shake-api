plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "why_mango"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2023.0.3"

dependencies {
    implementation(project(":shared"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.openfeign:feign-hc5:13.4")
    implementation("io.github.openfeign:feign-kotlin:13.4")
    implementation("io.github.openfeign:feign-slf4j:9.3.1")
    implementation("io.github.openfeign:feign-gson:9.4.0")
    implementation("com.auth0:java-jwt:4.4.0")
//    implementation("org.springframework.boot:spring-boot-starter-cache")
//    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
//    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest.extensions:kotest-extensions-wiremock:3.1.0")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
