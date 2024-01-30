dependencyResolutionManagement {
    /**
     * Central declaration of repositories
     * 不用配置allprojects或subprojects或子项目中指定，此处统一配置仓库
     */
    repositories {
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://mirrors.huaweicloud.com/repository/maven/")
        // 或者公司本地仓库
        mavenCentral()
    }
    // 默认值
    repositoriesMode = RepositoriesMode.PREFER_PROJECT
}

rootProject.name = "im-sdk"
include("protocol")
include("server")
include("tcp-client")
include("udp-client")
include("websocket-client")
include("server-demo")
include("tcp-client-demo")
include("udp-client-demo")

