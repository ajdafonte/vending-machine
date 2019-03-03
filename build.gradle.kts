plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    testCompile("org.junit.jupiter", "junit-jupiter-api", "5.4.0")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.4.0")

    // IMPORTANT: Use this version. Why? See https://github.com/mockito/mockito/issues/1604
    testCompile("org.mockito", "mockito-junit-jupiter", "2.23.4")

    testCompile("org.hamcrest","hamcrest-library","1.3")
}

tasks {
    // Use the built-in JUnit support of Gradle.
    "test"(Test::class) {
        useJUnitPlatform()
    }
}