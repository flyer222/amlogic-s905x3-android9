BOARD_HAVE_BLUETOOTH := true
BOARD_HAVE_BLUETOOTH_SPRD :=true
BOARD_HAVE_BLUETOOTH_UNISOC:= true


PRODUCT_COPY_FILES += \
	hardware/wifi/unisoc/uwe5621ds/configs/bt/bt_configure_pskey.ini:$(TARGET_COPY_OUT_VENDOR)/etc/bt_configure_pskey.ini \
	hardware/wifi/unisoc/uwe5621ds/configs/bt/bt_configure_rf_marlin3e_3.ini:$(TARGET_COPY_OUT_VENDOR)/etc/bt_configure_rf.ini

PRODUCT_COPY_FILES += \
    frameworks/native/data/etc/android.hardware.bluetooth.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.bluetooth.xml \
    frameworks/native/data/etc/android.hardware.bluetooth_le.xml:$(TARGET_COPY_OUT_VENDOR)/etc/permissions/android.hardware.bluetooth_le.xml

PRODUCT_PRODUCT_PROPERTIES += \
    wc_transport.soc_initialized=0

PRODUCT_PACKAGES += \
    libbt-vendor-uwe \
    bluetooth_unisoc.default \
    audio.a2dp.unisoc \
    bt_stack_unisoc.conf \
    bt_did_unisoc.conf \
    auto_pair_devlist_unisoc.conf \
    bdt_unisoc \
    libbt-utils_unisoc \
    libbt-hci_unisoc \
    btools_unisoc
