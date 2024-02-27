pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url= uri("https://nightly.maven.mozilla.org/maven2")
        }
        maven {
            url = uri("https://maven.mozilla.org/maven2")
        }
    }
}

rootProject.name = "yamaneko"
include(":app")
 