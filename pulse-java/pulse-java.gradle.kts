plugins {
	application
}

group = "com.fluxchess.pulse"

dependencies {
	implementation(libs.jcpi)
	testImplementation(libs.junit)
	testImplementation(libs.assertj)
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
