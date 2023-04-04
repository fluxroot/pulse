plugins {
	application
}

dependencies {
	implementation(libs.jcpi)
	testImplementation(libs.junit)
	testImplementation(libs.assertj)
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
}

tasks.withType<JavaCompile> {
	options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
}

tasks.test {
	useJUnitPlatform()
}

application {
	mainClass.set("com.fluxchess.pulse.java.Main")
	executableDir = ""
}

tasks.named<JavaExec>("run") {
	standardInput = System.`in`
}

tasks.withType<Tar> {
	enabled = false
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
