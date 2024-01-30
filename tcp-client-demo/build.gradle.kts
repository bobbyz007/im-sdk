plugins {
    id("myproject.java-conventions")
}

dependencies {
    implementation(libs.guava)
    implementation(libs.commons.io)
    implementation(libs.commons.lang3)
    implementation(libs.gson)

    implementation(libs.netty.all)

    // implementation fileTree(dir: 'lib', includes: ['*.jar'])
    implementation(project(":tcp-client"))
}