plugins {
	java
	id("net.fabricmc.fabric-loom-remap") version "1.15.+"
	id("ploceus") version "1.15.+"
	`maven-publish`
	id("com.modrinth.minotaur") version "2.+"
	id("io.freefair.lombok") version "9.+"
	id("dev.yumi.gradle.licenser") version "1.0.+"
}

var featherBuild = "1"
var fabricLoader = "0.19.1"
var client = "3.1.10-beta.1"
var config = "3.1.13"
var osl = "0.17.2"
var legacyLwjgl3 = "1.2.11"
version = "1.1.0"
group = "io.github.axolotlclient.oldanimations"
base.archivesName = "AxolotlClient-OldAnimations"

repositories {
	maven("https://moehreag.duckdns.org/maven/snapshots")
	maven("https://moehreag.duckdns.org/maven/releases")
	maven("https://repo.hypixel.net/repository/Hypixel/") {
		content {
			includeGroup("net.hypixel")
		}
	}
	mavenLocal()
	mavenCentral()
}

ploceus {
	setIntermediaryGeneration(2)
}

loom {
	accessWidenerPath.set(file("src/main/resources/oldanimations.accesswidener"))

	mods {
		create("axolotlclient-oldanimations") {
			sourceSet("main")
		}
	}
	runs {
		getByName("client") {
			vmArgs("-XX:+AllowEnhancedClassRedefinition", "-XX:+IgnoreUnrecognizedVMOptions")
		}
		remove(getByName("server"))
	}
}

dependencies {
	minecraft("com.mojang:minecraft:1.8.9")
	mappings(ploceus.featherMappings(featherBuild))

	modImplementation("net.fabricmc:fabric-loader:$fabricLoader")

	modImplementation("io.github.axolotlclient:AxolotlClient:$client+1.8.9")
	modImplementation(include("io.github.axolotlclient:AxolotlClient-config:$config+1.8.9")!!)
	ploceus.dependOsl(osl)

	modImplementation("io.github.moehreag:legacy-lwjgl3:$legacyLwjgl3+1.8.9")
	modImplementation("com.terraformersmc:modmenu:0.4.0+mc1.8.9")
}

configurations.configureEach {
	exclude("org.lwjgl.lwjgl")
	resolutionStrategy {
		dependencySubstitution {
			substitute(module("io.netty:netty-all:4.0.23.Final")).using(module("io.netty:netty-all:4.0.56.Final"))
		}
		force("io.netty:netty-all:4.0.56.Final")
	}
}

tasks.processResources {
	inputs.property("version", version)

	filesMatching("fabric.mod.json") {
		expand("version" to version)
	}
}

tasks.withType(JavaCompile::class).configureEach {
	options.encoding = "UTF-8"

	if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_18)) {
		options.release = 17
	}
}

java {
	withSourcesJar()
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

license {
	rule(file("HEADER"))
	include("**/*.java")
}

// Configure the maven publication
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = base.archivesName.get()
			from(components["java"])
		}
	}

	repositories {
		maven {
			name = "owlMaven"
			val repository = if (project.version.toString().contains("beta") || project.version.toString()
					.contains("alpha")
			) "snapshots" else "releases"
			url = uri("https://maven.axolotlclient.com/$repository")
			credentials(PasswordCredentials::class)
			authentication {
				create<BasicAuthentication>("basic")
			}
		}
	}
}

modrinth {
	token = System.getenv("MODRINTH_TOKEN")
	projectId = "UD5CuiYt"
	versionNumber = "${project.version}"
	versionType = "release"
	uploadFile = tasks.remapJar.get()
	gameVersions.set(
		listOf("1.8.9")
	)
	loaders.set(
		listOf("fabric", "quilt")
	)
	additionalFiles.set(listOf(tasks.remapSourcesJar))
	dependencies {
		required.project("osl")
		optional.project("axolotlclient")
	}

	// Changelog fetching: Credit LambdAurora.
	// https://github.com/LambdAurora/LambDynamicLights/blob/1ef85f486084873b5d97b8a08df72f57859a3295/build.gradle#L145
	// License: MIT
	val changelogText = file("CHANGELOG.md").readText()
	val regexVersion =
		((project.version) as String).split("+")[0].replace("\\.".toRegex(), "\\.").replace("\\+".toRegex(), "+")
	val changelogRegex = "###? ${regexVersion}\\n\\n(( *- .+\\n)+)".toRegex()
	val matcher = changelogRegex.find(changelogText)

	if (matcher != null) {
		var changelogContent = matcher.groups[1]?.value

		val changelogLines = changelogText.split("\n")
		val linkRefRegex = "^\\[([A-z0-9 _\\-/+.]+)]: ".toRegex()
		for (line in changelogLines.reversed()) {
			if ((linkRefRegex.matches(line)))
				changelogContent += "\n" + line
			else break
		}
		changelog = changelogContent
	} else {
		afterEvaluate {
			tasks.modrinth.configure { isEnabled = false }
		}
	}
}
