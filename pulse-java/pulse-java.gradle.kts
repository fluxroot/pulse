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
