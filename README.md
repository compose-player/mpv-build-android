Build scripts to compile mpv & its dependencies for Android  
Produces shared & static binaries

## How to build
```shell
./gradlew assemble[dav1d]
./gradlew assemble[freetype]
./gradlew assemble[harfbuzz]
./gradlew assemble[fribidi]
./gradlew assemble[ass]
./gradlew assemble[placebo]
./gradlew assemble[mbedtls]
./gradlew assemble[ffmpeg]
./gradlew assemble[mpv]
```

## TODO
- shaderc
- vulkan
- lcms
- unibreak
- uchardet
- png
- dovi