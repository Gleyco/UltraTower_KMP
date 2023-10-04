plugins {
    kotlin("multiplatform")
  //  kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("dev.icerock.mobile.multiplatform-resources")
    kotlin("plugin.serialization") version "1.9.10"

}

kotlin {
    androidTarget()



   // jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }


   // cocoapods {
   //     version = "1.0.0"
   //     summary = "Some description for the Shared Module"
   //     homepage = "Link to the Shared Module homepage"
   //     ios.deploymentTarget = "14.1"
   //     podfile = project.file("../iosApp/Podfile")
   //     framework {
   //         baseName = "shared"
   //         isStatic = true
      //  }
    //    extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
  //      extraSpecAttributes["exclude_files"] = "['src/commonMain/resources/MR/**']"
 //   }

    sourceSets {
        val compose_version = "1.4.1"

        val mokoPermissionsVersion = extra["moko.permissions.version"] as String
        val mokoMvvmVersion = extra["moko.mvvm.version"] as String
        val mokoResourcesVersion = extra["moko.resources.version"] as String

        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)



                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")



                val voyagerVersion = "1.0.0-rc05"

                // Navigator
                implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-koin:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-kodein:$voyagerVersion")

                implementation("dev.icerock.moko:permissions-compose:$mokoPermissionsVersion")
                implementation("dev.icerock.moko:mvvm-compose:$mokoMvvmVersion")
                implementation("dev.icerock.moko:resources-compose:$mokoResourcesVersion")

               // commonTestImplementation("dev.icerock.moko:permissions-test:0.16.0")


                implementation("com.juul.kable:core:0.26.0")

                implementation("org.jetbrains.kotlinx:atomicfu:0.17.3")
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                api("androidx.activity:activity-compose:1.6.1")
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.9.0")


                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
                implementation("androidx.startup:startup-runtime:1.1.1")
                implementation("com.louiscad.splitties:splitties-fun-pack-android-base:3.0.0")
            }
        }
    /*    val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
*/
        val iosX64Main by getting {
            resources.srcDirs("build/generated/moko/iosX64Main/src")
        }
        val iosArm64Main by getting {
            resources.srcDirs("build/generated/moko/iosArm64Main/src")
        }
        val iosSimulatorArm64Main by getting {
            resources.srcDirs("build/generated/moko/iosSimulatorArm64Main/src")
        }
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }


       /* val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
            }
        }*/
    }
}



multiplatformResources {
    multiplatformResourcesPackage = "com.gleyco.UltraTower" // required
    multiplatformResourcesClassName = "SharedRes" // optional, default MR
  //  multiplatformResourcesVisibility = MRVisibility.Internal // optional, default Public


}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.gleyco.UltraTower.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    sourceSets {
        getByName("main").java.srcDirs("build/generated/moko/androidMain/src")
    }
    sourceSets["main"].resources.exclude("src/commonMain/resources/MR")


    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}
dependencies {
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
}
