@echo off
setlocal EnableExtensions EnableDelayedExpansion

rem Default params
set "TAG=dev"
set "PLATFORMS="
set "REGISTRY=registry.baomagangwan.com"
set "IMAGE_NAME="
set "BUILDER_NAME=cloud-builder"
set "BUILDKIT_IMAGE=docker.1ms.run/moby/buildkit:latest"
set "PUSH=true"
set "DOCKERFILE_PATH=Dockerfile"
set "DOCKER_CONTEXT=."
set "PUSH_SKIP=false"
set "PUSH_MODE="
set "BUILD_OUTPUT="
set "PROVENANCE_ENABLED=false"
set "SBOM_ENABLED=false"

rem Detect default single-platform from host arch (fallback amd64)
call :detect_arch

rem Derive defaults by env / current folder name
for %%I in ("%CD%") do set "DEFAULT_IMAGE_NAME=%%~nxI"
set "IMAGE_NAME=%DEFAULT_IMAGE_NAME%"

rem Env overrides
if not "%DOCKER_TAG%"=="" set "TAG=%DOCKER_TAG%"
if not "%DOCKER_REGISTRY%"=="" set "REGISTRY=%DOCKER_REGISTRY%"
if not "%DOCKER_IMAGE_NAME%"=="" set "IMAGE_NAME=%DOCKER_IMAGE_NAME%"
if not "%DOCKER_BUILDER_NAME%"=="" set "BUILDER_NAME=%DOCKER_BUILDER_NAME%"
if not "%DOCKER_BUILDKIT_IMAGE%"=="" set "BUILDKIT_IMAGE=%DOCKER_BUILDKIT_IMAGE%"
if not "%DOCKER_PLATFORMS%"=="" set "PLATFORMS=%DOCKER_PLATFORMS%"
if not "%DOCKERFILE%"=="" set "DOCKERFILE_PATH=%DOCKERFILE%"
if not "%DOCKER_CONTEXT%"=="" set "DOCKER_CONTEXT=%DOCKER_CONTEXT%"
if not "%DOCKER_PUSH_SKIP%"=="" set "PUSH_SKIP=%DOCKER_PUSH_SKIP%"
if not "%DOCKER_PUSH_MODE%"=="" set "PUSH_MODE=%DOCKER_PUSH_MODE%"
if not "%DOCKER_OUTPUT%"=="" set "BUILD_OUTPUT=%DOCKER_OUTPUT%"
if not "%DOCKER_PROVENANCE%"=="" set "PROVENANCE_ENABLED=%DOCKER_PROVENANCE%"
if not "%DOCKER_SBOM%"=="" set "SBOM_ENABLED=%DOCKER_SBOM%"
if "%PLATFORMS%"=="" (
  if /I "%DOCKER_BUILD_MODE%"=="multi" set "PLATFORMS=linux/arm64,linux/amd64"
)

rem Parse args
:parse_args
if "%~1"=="" goto after_parse

if /I "%~1"=="-t" (
  set "TAG=%~2"
  shift
  shift
  goto parse_args
)
if /I "%~1"=="--tag" (
  set "TAG=%~2"
  shift
  shift
  goto parse_args
)

if /I "%~1"=="-m" (
  call :set_mode "%~2"
  shift
  shift
  goto parse_args
)
if /I "%~1"=="--mode" (
  call :set_mode "%~2"
  shift
  shift
  goto parse_args
)

if /I "%~1"=="-p" (
  set "PLATFORMS=%~2"
  shift
  shift
  goto parse_args
)
if /I "%~1"=="--platforms" (
  set "PLATFORMS=%~2"
  shift
  shift
  goto parse_args
)

if /I "%~1"=="--no-push" (
  set "PUSH=false"
  shift
  goto parse_args
)

if /I "%~1"=="-h" goto show_help
if /I "%~1"=="--help" goto show_help

echo Unknown option: %~1
goto show_help

:after_parse
if "%PLATFORMS%"=="" set "PLATFORMS=%DEFAULT_PLATFORM%"

echo Starting build process...
echo Target Image: %REGISTRY%/%IMAGE_NAME%:%TAG%
echo Platforms: %PLATFORMS%
echo Dockerfile: %DOCKERFILE_PATH%
echo Context: %DOCKER_CONTEXT%
echo Builder: %BUILDER_NAME%
echo BuildKit Image: %BUILDKIT_IMAGE%
echo Push: %PUSH%

if /I "%PUSH_SKIP%"=="true" (
  echo Docker push is skipped (DOCKER_PUSH_SKIP=true)
  exit /b 0
)

rem Login (if password env set). Use PowerShell to avoid special-char issues in cmd echo pipe.
if not "%DOCKER_REGISTRY_PASSWORD%"=="" (
  echo Logging into registry...
  powershell -NoProfile -Command "$env:DOCKER_REGISTRY_PASSWORD | docker login -u bmgw01 --password-stdin https://%REGISTRY%"
  if errorlevel 1 (
    echo Docker login failed.
    exit /b 1
  )
)

rem Ensure builder exists and is healthy (bootstrap). If unhealthy, recreate.
docker buildx inspect %BUILDER_NAME% >nul 2>&1
if errorlevel 1 (
  echo Builder %BUILDER_NAME% does not exist, creating...
  docker buildx create --use --name %BUILDER_NAME% --driver docker-container --driver-opt image=%BUILDKIT_IMAGE%
  if errorlevel 1 exit /b 1
  docker buildx use %BUILDER_NAME% >nul 2>&1
  docker buildx inspect %BUILDER_NAME% --bootstrap >nul 2>&1
) else (
  echo Builder %BUILDER_NAME% exists, checking status...
  docker buildx inspect %BUILDER_NAME% --bootstrap >nul 2>&1
  if errorlevel 1 (
    echo Builder %BUILDER_NAME% is unhealthy, removing and recreating...
    docker buildx rm %BUILDER_NAME% >nul 2>&1
    docker buildx create --use --name %BUILDER_NAME% --driver docker-container --driver-opt image=%BUILDKIT_IMAGE%
    if errorlevel 1 exit /b 1
    docker buildx use %BUILDER_NAME% >nul 2>&1
    docker buildx inspect %BUILDER_NAME% --bootstrap >nul 2>&1
  ) else (
    echo Builder %BUILDER_NAME% is healthy
    docker buildx use %BUILDER_NAME%
    if errorlevel 1 exit /b 1
  )
)

echo Ensuring binfmt support...
docker run --rm --privileged tonistiigi/binfmt --install all >nul 2>&1

set "IMAGE=%REGISTRY%/%IMAGE_NAME%:%TAG%"
set "PROVENANCE_FLAG=--provenance=false"
set "SBOM_FLAG=--sbom=false"
if /I "%PROVENANCE_ENABLED%"=="true" set "PROVENANCE_FLAG=--provenance=true"
if /I "%SBOM_ENABLED%"=="true" set "SBOM_FLAG=--sbom=true"

set "CMD=docker buildx build --builder %BUILDER_NAME% --platform %PLATFORMS% -t %IMAGE% -f %DOCKERFILE_PATH% %DOCKER_CONTEXT% %PROVENANCE_FLAG% %SBOM_FLAG%"

if not "%PUSH_MODE%"=="" (
  if /I "%PUSH_MODE%"=="push" (
    set "PUSH=true"
  ) else (
    set "PUSH=false"
  )
)

if /I "%PUSH%"=="true" (
  set "CMD=%CMD% --push"
) else (
  if not "%BUILD_OUTPUT%"=="" (
    set "CMD=%CMD% --output %BUILD_OUTPUT%"
  ) else (
    if not "%PLATFORMS:,=%"=="%PLATFORMS%" (
      echo Warning: Multi-platform build without output/push will only verify build (no output loaded to docker daemon).
      set "CMD=docker buildx build --builder %BUILDER_NAME% --platform %PLATFORMS% -t %IMAGE% -f %DOCKERFILE_PATH% %DOCKER_CONTEXT% %PROVENANCE_FLAG% %SBOM_FLAG%"
    ) else (
      set "CMD=%CMD% --load"
    )
  )
)

echo Executing: %CMD%
call %CMD%
if errorlevel 1 (
  echo Build failed!
  exit /b 1
)

echo Build success!
if /I "%PUSH%"=="true" (
  echo Inspect image manifest:
  docker buildx imagetools inspect %REGISTRY%/%IMAGE_NAME%:%TAG%
)
exit /b 0

:detect_arch
set "DEFAULT_PLATFORM=linux/amd64"
if /I "%PROCESSOR_ARCHITECTURE%"=="ARM64" set "DEFAULT_PLATFORM=linux/arm64"
if /I "%PROCESSOR_ARCHITEW6432%"=="ARM64" set "DEFAULT_PLATFORM=linux/arm64"
exit /b 0

:set_mode
set "MODE=%~1"
if /I "%MODE%"=="multi" (
  set "PLATFORMS=linux/arm64,linux/amd64"
  exit /b 0
)
set "PLATFORMS=%DEFAULT_PLATFORM%"
exit /b 0

:show_help
echo Usage: build-docker.bat [options]
echo Options:
echo   -t, --tag ^<tag^>          Specify the image tag (default: dev)
echo   -m, --mode ^<mode^>        Build mode: single or multi (default: single)
echo   -p, --platforms ^<list^>   Specify platforms manually (overrides -m)
echo   --no-push                  Build only, do not push
echo   -h, --help                 Show this help message
exit /b 0
