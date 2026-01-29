#!/bin/bash

# 默认参数
TAG="dev"
REGISTRY="${DOCKER_REGISTRY:-registry.baomagangwan.com}"
IMAGE_NAME="${DOCKER_IMAGE_NAME:-$(basename "$(pwd)")}"
BUILDER_NAME="${DOCKER_BUILDER_NAME:-cloud-builder}"
BUILDKIT_IMAGE="${DOCKER_BUILDKIT_IMAGE:-moby/buildkit:latest}"
PLATFORMS="${DOCKER_PLATFORMS:-}" # 允许通过环境变量直接指定
DOCKERFILE_PATH="${DOCKERFILE:-Dockerfile}"
DOCKER_CONTEXT="${DOCKER_CONTEXT:-.}"
PUSH_SKIP="${DOCKER_PUSH_SKIP:-false}"
PUSH_MODE="${DOCKER_PUSH_MODE:-}"
BUILD_OUTPUT="${DOCKER_OUTPUT:-}"
PROVENANCE_ENABLED="${DOCKER_PROVENANCE:-false}"
SBOM_ENABLED="${DOCKER_SBOM:-false}"
PUSH=true

# 环境变量覆盖
if [ -n "$DOCKER_TAG" ]; then TAG="$DOCKER_TAG"; fi
if [ -n "$DOCKER_REGISTRY_PASSWORD" ]; then export DOCKER_REGISTRY_PASSWORD="$DOCKER_REGISTRY_PASSWORD"; fi
if [ -n "$DOCKER_BUILD_MODE" ] && [ -z "$PLATFORMS" ]; then
    if [ "$DOCKER_BUILD_MODE" == "multi" ]; then
        PLATFORMS="linux/arm64,linux/amd64"
    fi
fi

# 默认单平台：按宿主机架构选择（未显式指定 PLATFORMS 时）
if [ -z "$PLATFORMS" ]; then
    ARCH="$(uname -m)"
    if [[ "$ARCH" == "arm64" || "$ARCH" == "aarch64" ]]; then
        PLATFORMS="linux/arm64"
    else
        PLATFORMS="linux/amd64"
    fi
fi

# 帮助信息
function show_help {
    echo "Usage: $0 [options]"
    echo "Options:"
    echo "  -t, --tag <tag>          Specify the image tag (default: dev)"
    echo "  -m, --mode <mode>        Build mode: single or multi (default: single)"
    echo "  -p, --platforms <list>   Specify platforms manually (overrides -m)"
    echo "  --no-push                Build only, do not push"
    echo "  -h, --help               Show this help message"
}

# 解析参数
while [[ $# -gt 0 ]]; do
    case $1 in
        -t|--tag)
            TAG="$2"
            shift 2
            ;;
        -m|--mode)
            if [ "$2" == "multi" ]; then
                PLATFORMS="linux/arm64,linux/amd64"
            else
                ARCH="$(uname -m)"
                if [[ "$ARCH" == "arm64" || "$ARCH" == "aarch64" ]]; then
                    PLATFORMS="linux/arm64"
                else
                    PLATFORMS="linux/amd64"
                fi
            fi
            shift 2
            ;;
        -p|--platforms)
            PLATFORMS="$2"
            shift 2
            ;;
        --no-push)
            PUSH=false
            shift
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
done

echo "Starting build process..."
echo "Target Image: ${REGISTRY}/${IMAGE_NAME}:${TAG}"
echo "Platforms: ${PLATFORMS}"
echo "Dockerfile: ${DOCKERFILE_PATH}"
echo "Context: ${DOCKER_CONTEXT}"
echo "Builder: ${BUILDER_NAME}"
echo "BuildKit Image: ${BUILDKIT_IMAGE}"
echo "Push: ${PUSH}"

if [ "${PUSH_SKIP}" = "true" ]; then
    echo "Docker push is skipped (DOCKER_PUSH_SKIP=true)"
    exit 0
fi

# 登录仓库 (如果设置了密码环境变量)
if [ -n "$DOCKER_REGISTRY_PASSWORD" ]; then
    echo "Logging into registry..."
    echo "$DOCKER_REGISTRY_PASSWORD" | docker login -u bmgw01 --password-stdin https://${REGISTRY}
fi

# 检查 / 自愈 buildx builder（存在 -> bootstrap 健康检查；异常则重建）
if docker buildx inspect "${BUILDER_NAME}" >/dev/null 2>&1; then
    echo "Builder ${BUILDER_NAME} exists, checking status..."
    if docker buildx inspect "${BUILDER_NAME}" --bootstrap >/dev/null 2>&1; then
        echo "Builder ${BUILDER_NAME} is healthy"
        docker buildx use "${BUILDER_NAME}"
    else
        echo "Builder ${BUILDER_NAME} is unhealthy, removing and recreating..."
        docker buildx rm "${BUILDER_NAME}" 2>/dev/null || true
        docker buildx create --use --name "${BUILDER_NAME}" --driver docker-container --driver-opt image="${BUILDKIT_IMAGE}"
        docker buildx use "${BUILDER_NAME}"
        docker buildx inspect "${BUILDER_NAME}" --bootstrap >/dev/null 2>&1 || true
    fi
else
    echo "Builder ${BUILDER_NAME} does not exist, creating..."
    docker buildx create --use --name "${BUILDER_NAME}" --driver docker-container --driver-opt image="${BUILDKIT_IMAGE}"
    docker buildx use "${BUILDER_NAME}"
    docker buildx inspect "${BUILDER_NAME}" --bootstrap >/dev/null 2>&1 || true
fi

# 确保 binfmt 支持多平台
echo "Ensuring binfmt support..."
docker run --rm --privileged tonistiigi/binfmt --install all > /dev/null 2>&1

IMAGE="${REGISTRY}/${IMAGE_NAME}:${TAG}"
PROVENANCE_FLAG="--provenance=false"
SBOM_FLAG="--sbom=false"
if [ "${PROVENANCE_ENABLED}" = "true" ]; then PROVENANCE_FLAG="--provenance=true"; fi
if [ "${SBOM_ENABLED}" = "true" ]; then SBOM_FLAG="--sbom=true"; fi

# 构建命令构建
CMD="docker buildx build --builder ${BUILDER_NAME} --platform ${PLATFORMS} -t ${IMAGE} -f ${DOCKERFILE_PATH} ${DOCKER_CONTEXT} ${PROVENANCE_FLAG} ${SBOM_FLAG}"

if [ -n "${PUSH_MODE}" ]; then
    if [ "${PUSH_MODE}" = "push" ]; then
        PUSH=true
    else
        PUSH=false
    fi
fi

if [ "$PUSH" = true ]; then
    CMD="$CMD --push"
else
    if [ -n "${BUILD_OUTPUT}" ]; then
        CMD="$CMD --output ${BUILD_OUTPUT}"
    else
        CMD="$CMD --load"
        if [[ "$PLATFORMS" == *","* ]]; then
            echo "Warning: Multi-platform build without output/push will only verify build (no output loaded to docker daemon)."
            CMD="docker buildx build --builder ${BUILDER_NAME} --platform ${PLATFORMS} -t ${IMAGE} -f ${DOCKERFILE_PATH} ${DOCKER_CONTEXT} ${PROVENANCE_FLAG} ${SBOM_FLAG}"
        fi
    fi
fi

echo "Executing: $CMD"
eval $CMD

# 验证结果
if [ $? -eq 0 ]; then
    echo "Build success!"
    if [ "$PUSH" = true ]; then
        echo "Inspect image manifest:"
        docker buildx imagetools inspect ${REGISTRY}/${IMAGE_NAME}:${TAG}
    fi
else
    echo "Build failed!"
    exit 1
fi
