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

dependencies {
	val springDocVersion by extra { "2.6.0" }
	val jjwtVersion by extra { "0.12.6" }

	implementation(project(":domain"))
	implementation(project(":shared"))
	implementation(project(":market_broker"))
	implementation(project(":finance"))

	// security
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-security")
//	implementation("org.springframework.security:spring-security-config")
	implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
	implementation("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
	implementation("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	// docs
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:$springDocVersion")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-api:$springDocVersion")

	// metrics
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
//	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
