Build scripts to compile mpv & its dependencies for Android  

## Prepare CLI
```shell
./gradlew cli:jar
```

## Build
```shell
./mpv-build --clean=all --libraries=all --targets=all
```
**Targets**: all **OR** arm32, arm64, x86, x86_64\
**Libraries**: all **OR** ffmpeg,dav1d,placebo,mbedtls,mpv,freetype,harfbuzz,fribidi,ass\
**Clean**: all **OR** ffmpeg,dav1d,placebo,mbedtls,mpv,freetype,harfbuzz,fribidi,ass\


## TODO
- ~~shaderc,vulkan,lcms, dovi~~ Performance is just horrible
- unibreak
- uchardet
- png