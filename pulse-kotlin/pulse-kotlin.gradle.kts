@Suppress("DSL_SCOPE_VIOLATION")
plugins {
	alias(libs.plugins.kotlin.multiplatform)
	application
}

kotlin {
	jvm() {
		withJava()
		jvmToolchain(17)
		testRuns["test"].executionTask.configure {
			useJUnitPlatform()
		}
	}
	linuxX64() {
		binaries {
			executable()
		}
	}
	mingwX64() {
		binaries {
			executable()
		}
	}
	targets.all {
		compilations.all {
			compilerOptions.configure {
				allWarningsAsErrors.set(true)
			}
		}
	}
	sourceSets {
		val commonTest by getting {
			dependencies {
				implementation(libs.kotlin.test)
			}
		}
	}
}

application {
	mainClass.set("com.fluxchess.pulse.kotlin.jvm.MainKt")
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
