plugins {
    id("myproject.library-conventions")
}

dependencies {
    implementation(libs.guava)
    implementation(libs.commons.io)
    implementation(libs.commons.lang3)
    implementation(libs.gson)

    implementation(libs.netty.all)

    api(project(":protocol"))
}

