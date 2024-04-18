import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	java
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
}

group = "ru.raticate"
version = "0.0.2-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}


dependencies {
	implementation("redis.clients:jedis:4.3.1")
	implementation(libs.org.firebirdsql.jdbc.jaybird)
	implementation(libs.org.apache.poi.poi)
	implementation(libs.org.apache.poi.poi.ooxml)
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	// https://mvnrepository.com/artifact/org.thymeleaf/thymeleaf-spring6
	implementation("org.thymeleaf:thymeleaf-spring6:3.1.2.RELEASE")
	implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")
	
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}


tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}
