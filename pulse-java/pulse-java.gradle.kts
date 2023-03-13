plugins {
	application
}

group = "com.fluxchess.pulse"

dependencies {
	implementation(libs.jcpi)
	testImplementation(libs.junit)
	testImplementation(libs.assertj)
}

application {
	mainClass.set("com.fluxchess.pulse.Main")
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(11))
	}
	withJavadocJar()
	withSourcesJar()
}

tasks.test {
	useJUnitPlatform()
}

tasks.named<JavaExec>("run") {
	standardInput = System.`in`
}

distributions {
	main {
		contents {
			from(rootProject.layout.projectDirectory.dir("src/main/dist"))
			from(rootProject.layout.projectDirectory.file("README.md"))
			from(rootProject.layout.projectDirectory.file("CHANGES.md"))
			from(rootProject.layout.projectDirectory.file("LICENSE"))
		}
	}
}
