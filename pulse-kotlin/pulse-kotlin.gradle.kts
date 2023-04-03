import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
	alias(libs.plugins.kotlin.multiplatform)
	distribution
}

kotlin {
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
		getByName("commonTest") {
			dependencies {
				implementation(libs.kotlin.test)
			}
		}
	}
}

val assets = copySpec {
	from(rootProject.layout.projectDirectory.dir("src/main/dist"))
	from(rootProject.layout.projectDirectory.file("README.md"))
	from(rootProject.layout.projectDirectory.file("CHANGES.md"))
	from(rootProject.layout.projectDirectory.file("LICENSE"))
}

distributions {
	create("linux") {
		contents {
			from(kotlin.linuxX64().binaries.getExecutable(NativeBuildType.RELEASE).outputFile)
			with(assets)
		}
	}
	create("windows") {
		contents {
			from(kotlin.mingwX64().binaries.getExecutable(NativeBuildType.RELEASE).outputFile)
			with(assets)
		}
	}
}
