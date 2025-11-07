plugins {
    id("java")
}

group = "com.ezequiel"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.opencsv:opencsv:5.12.0")
}

tasks.test {
    useJUnitPlatform()
}