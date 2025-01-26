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
	implementation(project(":shared"))
	implementation(project(":market_broker"))

	// data analysis
	implementation("org.jetbrains.kotlinx:dataframe:0.15.0")

	// r2dbc
	api("org.springframework.boot:spring-boot-starter-data-r2dbc")

	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("com.auth0:java-jwt:4.4.0")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.postgresql:postgresql")
	implementation("org.postgresql:r2dbc-postgresql")

	// http query string
	implementation("org.apache.httpcomponents.client5:httpclient5:5.4.1")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
////	testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
//	testImplementation("org.testcontainers:testcontainers:1.20.0")
//	testImplementation("org.testcontainers:r2dbc:1.20.0")
//	testImplementation("org.testcontainers:postgresql:1.20.0")
	testRuntimeOnly("com.h2database:h2")
	testRuntimeOnly("io.r2dbc:r2dbc-h2")
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
