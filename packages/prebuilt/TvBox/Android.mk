# Copyright (C) 2008 The Android Open Source Project
# Copyright (C) 2012 Broadcom Corporation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)
LOCAL_CERTIFICATE := PRESIGNED

LOCAL_MODULE := TvBox

LOCAL_SRC_FILES := tvbox_866155.apk

LOCAL_MODULE_TARGET_ARCH := arm
LOCAL_PREBUILT_JNI_LIBS := \
	lib/armeabi-v7a/libconceal.so \
	lib/armeabi-v7a/libijkffmpeg.so \
	lib/armeabi-v7a/libijksdl.so \
	lib/armeabi-v7a/libp2p.so \
	lib/armeabi-v7a/libplayer.so \
	lib/armeabi-v7a/libquickjs.so \
	lib/armeabi-v7a/librtmp-jni.so \
	lib/armeabi-v7a/libxl_stat.so \
	lib/armeabi-v7a/libxl_thunder_sdk.so

include $(BUILD_PREBUILT)