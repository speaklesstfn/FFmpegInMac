#!/bin/bash
export NDK=/Users/tfn/Library/Android/sdk/ndk-bundle
export PREBUILT=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/darwin-x86_64
export PLATFORM=$NDK/platforms/android-16/arch-arm
export FFMPEGDIR=/Users/tfn/AndroidStudioProjects/FFmpegInMac/ffmpeg/ffmpegsrc
export PREFIX=$(pwd)/android
export CC=$PREBUILT/bin/arm-linux-androideabi-gcc
export CPREFIX=$PREBUILT/bin/arm-linux-androideabi-
export NM=$PREBUILT/bin/arm-linux-androideabi-nm


function build_one
{
$FFMPEGDIR/configure \
    --prefix=$PREFIX \
    --target-os=linux \
    --cross-prefix=$CPREFIX \
    --arch=arm \
    --sysroot=$PLATFORM \
    --extra-cflags="-I$PLATFORM/usr/include" \
    --cc=$CC \
    --nm=$NM \
    --enable-shared \
    --enable-runtime-cpudetect \
    --enable-gpl \
    --enable-cross-compile \
    --disable-debug \
    --disable-static \
    --disable-doc \
    --disable-asm \
    --disable-ffmpeg \
    --disable-ffplay \
    --disable-ffprobe \
    --disable-ffserver \
    --disable-postproc \
    --disable-avdevice \
    --disable-symver \
    --disable-stripping \
$ADDITIONAL_CONFIGURE_FLAG
sed -i '' 's/HAVE_LRINT 0/HAVE_LRINT 1/g' config.h
sed -i '' 's/HAVE_LRINTF 0/HAVE_LRINTF 1/g' config.h
sed -i '' 's/HAVE_ROUND 0/HAVE_ROUND 1/g' config.h
sed -i '' 's/HAVE_ROUNDF 0/HAVE_ROUNDF 1/g' config.h
sed -i '' 's/HAVE_TRUNC 0/HAVE_TRUNC 1/g' config.h
sed -i '' 's/HAVE_TRUNCF 0/HAVE_TRUNCF 1/g' config.h
sed -i '' 's/HAVE_CBRT 0/HAVE_CBRT 1/g' config.h
sed -i '' 's/HAVE_RINT 0/HAVE_RINT 1/g' config.h
make clean
make -j4
make install
} 
ADDI_FLAG="-marm" 
build_one

