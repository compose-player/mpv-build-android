package builders

import base.*
import tasks.*
import utils.*

class FFmpegBuilder(override val target: AndroidABI) : BaseBuilder() {

  override val lib: Library = Library.ffmpeg

  override val cloneTask: Task = CloneTask(lib)

  override val buildTask: Task = AutoBuildTask(
    lib = lib,
    target = target,
    environmentVariables = buildMap {
      this["ASFLAGS"] = "-fPIC"
      this["CFLAGS"] = "-fPIC"
    },
    arguments = let {
      val prefixDir = prefixDirectoryOf(lib, target)
      val cpu: String = when (target) {
        AndroidABI.arm32 -> "armv7-a"
        AndroidABI.arm64 -> "armv8-a"
        AndroidABI.x86 -> "i686"
        AndroidABI.x86_64 -> "x86-64"
      }

      val arch: String = when (target) {
        AndroidABI.arm32 -> "arm"
        AndroidABI.arm64 -> "aarch64"
        AndroidABI.x86 -> "x86"
        AndroidABI.x86_64 -> "x86_64"
      }

      val extraCFlags = buildString {
        append("\"")
        when (target) {
          AndroidABI.arm32 -> {
            append("-Wl,-z,max-page-size=16384 -fPIC -I${prefixDir.resolve("include").absolutePath} -mfpu=neon -march=armv7-a")
          }
          AndroidABI.arm64, AndroidABI.x86, AndroidABI.x86_64 -> {
            append("-Wl,-z,max-page-size=16384 -fPIC -I${prefixDir.resolve("include").absolutePath}")
          }
        }
        append("\"")

      }
      val extraLdFlags = buildString {
        append("\"")
        "-Wl,-z,max-page-size=16384 -L${prefixDir.resolve("lib").absolutePath}"
        append("\"")
      }
      arrayOf(
        // Licensing options:
        "--disable-gpl",
        "--enable-version3",
        "--disable-nonfree",

        // Configuration options:
        "--disable-static",
        "--enable-shared",
        "--enable-small",
        "--disable-runtime-cpudetect",
        "--disable-gray",
        "--disable-swscale-alpha",
        "--disable-all",
        "--disable-autodetect",

        // Program options:
        "--disable-programs",
        "--disable-ffmpeg",
        "--disable-ffplay",
        "--disable-ffprobe",

        //Documentation options:
        "--disable-doc",
        "--disable-htmlpages",
        "--disable-manpages",
        "--disable-podpages",
        "--disable-txtpages",

        // Component options:
        "--disable-avdevice",
        "--enable-avcodec",
        "--enable-avformat",
        "--enable-swresample",
        "--enable-swscale",
        "--disable-postproc",
        "--enable-avfilter",
        "--enable-pthreads",
        "--disable-w32threads",
        "--disable-os2threads",
        "--enable-network",
        "--disable-dwt",
        "--enable-error-resilience",
        "--disable-lsp",
        "--disable-faan",
        "--disable-iamf",
        "--disable-pixelutils",


        // Individual component options:
        "--disable-encoders",
        "--disable-decoders",
        "--disable-hwaccels",
        "--disable-muxers",
        "--disable-demuxers",
        "--disable-parsers",
        "--disable-bsfs",
        "--disable-protocols",
        "--disable-indevs",
        "--disable-outdevs",
        "--disable-devices",
        "--disable-filters",

        "--enable-decoder=h263,h263i,h263p,h264,h264_mediacodec,hevc,hevc_mediacodec,mpeg2video,mpeg4,mpeg4_mediacodec,msmpeg4v2,msmpeg4v3",
        "--enable-decoder=aac,aac_latm,ac3,eac3,mp3,opus,vorbis,dca,mlp,truehd,pcm_s16le",
        "--enable-decoder=subrip,ass,ssa,movtext,dvbsub,pgssub,webvtt",

        "--enable-muxer=spdif",

        "--enable-demuxer=mpegts,mpegtsraw,hls,mp4,mov,matroska,avi,flv,webm,webm_dash_manifest,obu",
        "--enable-demuxer=aac,ac3,eac3,dts,dtshd,truehd,mp3,wav",
        "--enable-demuxer=subrip,ass,dvdsub,pgssub,webvtt,srt",
        "--enable-demuxer=rtp,rtsp,sdp",
        "--enable-demuxer=mpegps,mpegvideo,mjpeg",

        "--enable-parser=h263,h264,hevc,mpegvideo,mpeg4video,vp8,vp9",
        "--enable-parser=aac,aac_latm,ac3,dca,mlp,mpegaudio,opus,vorbis",
        "--enable-parser=mjpeg,png,bmp",
        "--enable-parser=dvdsub,dvbsub,dvd_nav",

        "--enable-bsf=aac_adtstoasc,h264_mp4toannexb,hevc_mp4toannexb,extract_extradata,remove_extradata,pgs_frame_merge,dca_core,truehd_core",

        "--enable-protocol=android_content,file,fd,data,http,https,httpproxy,rtmp,rtmps,tcp,udp,ftp",

        "--enable-filter=null,format,scale,anull,aformat,aresample",

        // External library support:
        "--enable-mediacodec",
        "--enable-jni",


        // Toolchain options:
        "--arch=$arch",
        "--cpu=$cpu",
        "--enable-cross-compile",
        "--target-os=android",
        "--nm=${TOOLCHAINS_DIR.resolve("llvm-nm").absolutePath}",
        "--ar=${TOOLCHAINS_DIR.resolve("llvm-ar").absolutePath}",
        "--cc=${TOOLCHAINS_DIR.resolve(target.CLANG).absolutePath}",
        "--as=${TOOLCHAINS_DIR.resolve(target.CLANG).absolutePath}",
        "--strip=${TOOLCHAINS_DIR.resolve("llvm-strip").absolutePath}",
        "--cxx=${TOOLCHAINS_DIR.resolve(target.CPP).absolutePath}",
        "--pkg-config=pkg-config",
        //"--pkg-config-flags=--static",
        "--ranlib=${TOOLCHAINS_DIR.resolve("llvm-ranlib").absolutePath}",
        "--extra-cflags=$extraCFlags",
        "--extra-ldflags=$extraLdFlags",
        "--enable-pic",

        // Optimization options:
        *when (target) {
          AndroidABI.arm32, AndroidABI.arm64 -> emptyArray()
          AndroidABI.x86, AndroidABI.x86_64 -> arrayOf("--disable-asm")
        },

        // Developer options:
        "--enable-optimizations",
        "--disable-stripping",
      )
    },
  )

}


