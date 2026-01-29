## Thymeleaf + Mybatis + Flyway + Druid + Log4j2

1、Thymeleaf 视图渲染
2、Mybatis 数据库操作
3、Flyway 数据库脚本自动升级和版本管理
4、Druid 高性能数据库连接池:自带SQL安全防护
5、Log4j2 高性能日志记录



### 推送镜像到私有仓库

#### 使用构建脚本（推荐）

项目提供了统一的构建脚本 `build-docker.sh`，支持单平台和多平台构建。

**基本用法：**

```bash
# 单平台构建并推送（默认）
./build-docker.sh -t 2.7.x.20251219-SNAPSHOT

# 多平台构建并推送
./build-docker.sh -t 2.7.x.20251219-SNAPSHOT -m multi

# 只构建不推送
./build-docker.sh -t 2.7.x.20251219-SNAPSHOT --no-push

# 查看帮助
./build-docker.sh --help
```

**使用环境变量：**

```bash
# 设置环境变量后直接运行
export DOCKER_TAG=2.7.x.20251219-SNAPSHOT
export DOCKER_BUILD_MODE=multi
export DOCKER_IMAGE_NAME=cloud-mall-ui
export DOCKER_BUILDER_NAME=cloud-builder
export DOCKER_BUILDKIT_IMAGE=moby/buildkit:latest
export DOCKERFILE=Dockerfile
export DOCKER_CONTEXT=.
export DOCKER_PUSH_SKIP=false
export DOCKER_PUSH_MODE=push
export DOCKER_OUTPUT=type=image,push=false
export DOCKER_PROVENANCE=false
export DOCKER_SBOM=false
export DOCKER_REGISTRY_PASSWORD='******'
./build-docker.sh
```

**脚本特性：**
- 支持单平台和多平台构建
- 自动处理 buildx builder 的创建和初始化
- 支持腾讯云镜像源（适合国内环境）
- 自动登录 Docker 仓库
- 可配置的构建参数
- 详细的构建日志输出

#### 使用 Maven 构建（docker-maven-plugin）

该方式仅依赖 `io.fabric8:docker-maven-plugin`，通过 Maven 属性对齐脚本的主要参数语义：

- `-t/--tag` → `-Ddocker.tag=...`
- `-m multi` / `-p/--platforms` → `-Ddocker.platforms=linux/amd64,linux/arm64`
- `--no-push` / `DOCKER_PUSH_SKIP=true` → `-Ddocker.push.skip=true`（或只执行 `install` 不执行 `deploy`）
- `DOCKER_REGISTRY` → `-Ddocker.registry.host=...`
- `DOCKER_IMAGE_NAME` → `-Ddocker.image.name=...`

```bash
# 在仓库根目录执行（只构建该 sample 模块，不推送）
mvn -pl ddd4j-boot-samples/ddd4j-boot-sample-starter-druid -am \
  -Ddocker.skip=false -Ddocker.push.skip=true \
  -Ddocker.tag=2.7.x.20251219-SNAPSHOT \
  install

# 单平台构建并推送
mvn -pl ddd4j-boot-samples/ddd4j-boot-sample-starter-druid -am \
  -Ddocker.skip=false -Ddocker.push.skip=false \
  -Ddocker.tag=2.7.x.20251219-SNAPSHOT \
  deploy

# 多平台构建并推送（跨平台镜像）
mvn -pl ddd4j-boot-samples/ddd4j-boot-sample-starter-druid -am \
  -Ddocker.skip=false -Ddocker.push.skip=false \
  -Ddocker.tag=2.7.x.20251219-SNAPSHOT \
  -Ddocker.platforms=linux/amd64,linux/arm64 \
  -Ddocker.buildx.skip=false \
  deploy
```

说明：
- 多平台构建需要启用 buildx；通过 Maven 方式构建跨平台镜像时，可用 `exec-maven-plugin` 在 `verify` 阶段自动安装 binfmt 并创建/自愈 builder。
- Linux/macOS 多平台示例：追加 `-Ddocker.buildx.skip=false -Ddocker.buildx.unix.skip=false -Ddocker.buildx.windows.skip=true`
- Windows 多平台示例：追加 `-Ddocker.buildx.skip=false -Ddocker.buildx.unix.skip=true -Ddocker.buildx.windows.skip=false`

#### Windows (CMD/PowerShell) 构建脚本

Windows 环境请使用 `build-docker.bat`，参数与 `build-docker.sh` 保持一致：

**CMD 基本用法：**

```bat
REM 单平台构建并推送（默认）
build-docker.bat -t 2.7.x.20251219-SNAPSHOT

REM 多平台构建并推送
build-docker.bat -t 2.7.x.20251219-SNAPSHOT -m multi

REM 只构建不推送
build-docker.bat -t 2.7.x.20251219-SNAPSHOT --no-push

REM 查看帮助
build-docker.bat --help
```

**CMD 环境变量：**

```bat
set DOCKER_TAG=2.7.x.20251219-SNAPSHOT
set DOCKER_IMAGE_NAME=cloud-mall-ui
set DOCKER_BUILDER_NAME=cloud-builder
set DOCKER_BUILDKIT_IMAGE=moby/buildkit:latest
set DOCKERFILE=Dockerfile
set DOCKER_CONTEXT=.
set DOCKER_PUSH_SKIP=false
set DOCKER_PUSH_MODE=push
set DOCKER_OUTPUT=type=image,push=false
set DOCKER_PROVENANCE=false
set DOCKER_SBOM=false
set DOCKER_REGISTRY_PASSWORD=******
build-docker.bat
```

**PowerShell 环境变量：**

```powershell
$env:DOCKER_TAG="2.7.x.20251219-SNAPSHOT"
$env:DOCKER_IMAGE_NAME="cloud-mall-ui"
$env:DOCKER_BUILDER_NAME="cloud-builder"
$env:DOCKER_BUILDKIT_IMAGE="moby/buildkit:latest"
$env:DOCKERFILE="Dockerfile"
$env:DOCKER_CONTEXT="."
$env:DOCKER_PUSH_SKIP="false"
$env:DOCKER_PUSH_MODE="push"
$env:DOCKER_OUTPUT="type=image,push=false"
$env:DOCKER_PROVENANCE="false"
$env:DOCKER_SBOM="false"
$env:DOCKER_REGISTRY_PASSWORD="******"
.\build-docker.bat
```

说明：
- Windows 下脚本会优先按宿主机架构选择单平台（ARM64 -> linux/arm64，其他 -> linux/amd64）。
- `DOCKER_REGISTRY_PASSWORD` 建议只在本机环境变量中设置，不要写入仓库文件或提交到 Git。

#### Docker 单平台构建（以本机架构为基础）

```bash
docker login -u ddd4j --password-stdin https://registry.ddd4j.com
docker build -t ddd4j-mall-ui:2.7.x.20251205-SNAPSHOT -f Dockerfile .
docker tag ddd4j-mall-ui:2.7.x.20251205-SNAPSHOT registry.ddd4j.com/ddd4j-mall-ui:2.7.x.20251205-SNAPSHOT
docker push registry.ddd4j.com/ddd4j-mall-ui:2.7.x.20251205-SNAPSHOT
```

#### Docker 多平台构建

```bash
docker login -u ddd4j --password-stdin https://registry.ddd4j.com
docker run --rm --privileged tonistiigi/binfmt --install all
docker buildx create --use --name ddd4jbuilder --driver docker-container --driver-opt image=moby/buildkit:latest
docker buildx use ddd4jbuilder
TAG=2.7.x.20251219-SNAPSHOT
docker buildx build --platform linux/arm64,linux/amd64 \
  -t registry.ddd4j.com/ddd4j-mall-ui:${TAG} \
  -f Dockerfile . \
  --push
docker buildx imagetools inspect registry.ddd4j.com/ddd4j-mall-ui:${TAG}
```
