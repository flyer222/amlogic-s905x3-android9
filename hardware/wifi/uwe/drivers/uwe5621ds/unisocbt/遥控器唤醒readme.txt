1.休眠前已下发遥控器至whitelist中
2.休眠时可以断开蓝牙
3.遥控器唤醒时发送包含adv type为 0x16的广播包，具体格式

#define  UNISOC_WOBLE_UUID (0xFD01)

const uint8 adv = {
         0x05,
         0x16,
         UNISOC_WOBLE_UUID & 0Xff,  (UNISOC_WOBLE_UUID >> 8) & 0Xff,
         0x00, 0x01
}