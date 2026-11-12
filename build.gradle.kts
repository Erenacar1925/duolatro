plugins {
  id("java")
  id("application")
  id("com.diffplug.spotless") version "7.0.4"
  id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.example"

version = "1.0-SNAPSHOT"

repositories { mavenCentral() }

dependencies {
  testImplementation(platform("org.junit:junit-bom:5.9.1"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  implementation("com.google.code.gson:gson:2.10.1")
}

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

tasks.test { useJUnitPlatform() }

spotless {
  java { googleJavaFormat() }
  kotlinGradle {
    target("*.gradle.kts")
    ktfmt()
  }
}

javafx {
  version = "23"
  modules("javafx.base", "javafx.graphics", "javafx.controls")
}

application { mainClass.set("client.Main") }
