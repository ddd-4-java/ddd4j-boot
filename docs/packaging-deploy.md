# 打包与部署演示

## 脚本模式（Appassembler）

- 父 POM：`ddd4j-boot-parent/pom.xml` 已配置 `appassembler-maven-plugin`
- 产物结构：`target/generated-resources/appassembler/${artifactId}`
- 启动入口：`org.springframework.boot.loader.JarLauncher`
- 生成的脚本：`bin/ddd4j-boot.sh`（示例配置），支持环境变量 `setenv`

## 服务模式（守护进程）

- 通过 `generate-daemons` 目标生成 `jsw` 平台守护脚本
- 日志、仓库目录、JVM 选项在父 POM 中统一声明（`wrapper.logfile` 等）

## Docker 镜像

- 插件：`docker-maven-plugin` 与 `dockerfile-maven-plugin`
- 镜像名称：`${project.artifactId}:${project.version}`，支持打 tag 与 push 到私有仓库
- 工作目录：`${docker.workdir}`；入口命令：`sh ./bin/${appassembler-bin-fileName}.sh`

## 生成步骤

```bash
# 聚合构建并生成脚本/守护进程与 Docker 镜像（示例）
mvn -DskipTests package

# 若需推送镜像，确保私有仓库与凭证可用，再执行 deploy 阶段
mvn deploy
```

## 配置与覆盖

- 运行时配置位于 `conf/`，父 POM 的 `maven-antrun-plugin` 会在打包阶段复制 `application*.yaml` 到产物 `conf` 目录
- JVM 参数与 GC 日志、HeapDump 等已在父 POM 写入，按需调整

