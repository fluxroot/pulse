import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
	alias(libs.plugins.kotlin.multiplatform)
	distribution
}

kotlin {
	compilerOptions {
		allWarningsAsErrors.set(true)
	}
	linuxX64 {
		binaries {
			executable()
		}
	}
	macosX64 {
		binaries {
			executable()
		}
	}
	mingwX64 {
		binaries {
			executable()
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
	from(rootProject.layout.projectDirectory.file("LICENSE"))
}

tasks.withType<Tar> {
	compression = Compression.GZIP
	archiveExtension.set("tar.gz")
}

distributions {
	create("linux") {
		contents {
			from(kotlin.linuxX64().binaries.getExecutable(NativeBuildType.RELEASE).linkTaskProvider)
			with(assets)
		}
	}
	create("macos") {
		contents {
			from(kotlin.macosX64().binaries.getExecutable(NativeBuildType.RELEASE).linkTaskProvider)
			with(assets)
		}
	}
	create("windows") {
		contents {
			from(kotlin.mingwX64().binaries.getExecutable(NativeBuildType.RELEASE).linkTaskProvider)
			with(assets)
		}
	}
}
