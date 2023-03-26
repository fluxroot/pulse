import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
	alias(libs.plugins.kotlin.jvm)
	application
}

dependencies {
	testImplementation(libs.kotlin.test)
	testImplementation(libs.assertj)
}

kotlin {
	jvmToolchain(17)
}

tasks.withType<KotlinJvmCompile> {
	compilerOptions {
		allWarningsAsErrors.set(true)
	}
}

tasks.test {
	useJUnitPlatform()
}

application {
	mainClass.set("com.fluxchess.pulse.kotlin.MainKt")
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
