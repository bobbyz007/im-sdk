plugins {
    id("myproject.library-conventions")
}

dependencies {
    implementation(libs.guava)
    implementation(libs.commons.io)
    implementation(libs.commons.lang3)
    implementation(libs.gson)

    implementation(libs.netty.all)
    implementation(libs.amqp.client)

    // 注意api方法属于 id: java-library 里面的方法
    api(project(":protocol"))

    // 使用spring starter log
    implementation(libs.spring.boot.starter.log4j2)

    /*// 抽象层
    implementation 'org.slf4j:slf4j-api:2.0.4'
    // 中间层: slf4j和log4j2的桥接包
    implementation 'org.apache.logging.log4j:log4j-slf4j2-impl:2.19.0'
    // 实现层
    implementation 'org.apache.logging.log4j:log4j-core:2.19.0'*/
}
