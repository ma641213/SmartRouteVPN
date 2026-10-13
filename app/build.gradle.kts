plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "tw.smartroute.vpn"
    compileSdk = 35
    defaultConfig {
        applicationId = "tw.smartroute.vpn"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "0.2.1-prototype"
    }
}
