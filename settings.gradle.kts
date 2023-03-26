rootProject.name = "pulse"

val modules = listOf(
	"pulse-java",
	"pulse-kotlin"
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
