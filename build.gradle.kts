plugins {
    java
    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

application {
    group = "com.dexma.hometest"
    version = "1.0-SNAPSHOT"
    mainClassName = "com.dexma.hometest.Startup"
}


repositories {
    mavenCentral()
}

dependencies {
    testCompile("org.junit.jupiter", "junit-jupiter-api", "5.4.0")
}
