rootProject.name = "pulse"

val modules = listOf(
	"pulse-java"
)

modules.forEach {
	include(it)
	project(":$it").buildFileName = "$it.gradle.kts"
}

dependencyResolutionManagement {
	repositories {
		mavenCentral()
	}
}
