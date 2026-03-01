#pragma once

namespace stb {
namespace keys {

// ═══════════════════════════════════════════════════════════════════
// RCU key codes — from APK GsRemoteControlActivity.java
// Two sets: default (most STBs incl. MediaStar 4030) and platform 30.
// The sendRemoteKey() path sends these integer values inside the
// "KeyValue" field of GMS_MSG_DO_REMOTE_CONTROL (1040).
// ═══════════════════════════════════════════════════════════════════

// ---------- Default platform (platform_id != 30) ----------

// Navigation
constexpr int KEY_UP    = 1;
constexpr int KEY_DOWN  = 2;
constexpr int KEY_LEFT  = 3;
constexpr int KEY_RIGHT = 4;
constexpr int KEY_ENTER = 5;           // OK
constexpr int KEY_MENU  = 6;
constexpr int KEY_BACK  = 7;           // Exit

// Color buttons
constexpr int KEY_RED    = 8;
constexpr int KEY_GREEN  = 9;
constexpr int KEY_YELLOW = 10;
constexpr int KEY_BLUE   = 11;

// Numeric keys
constexpr int KEY_0 = 12;
constexpr int KEY_1 = 13;
constexpr int KEY_2 = 14;
constexpr int KEY_3 = 15;
constexpr int KEY_4 = 16;
constexpr int KEY_5 = 17;
constexpr int KEY_6 = 18;
constexpr int KEY_7 = 19;
constexpr int KEY_8 = 20;
constexpr int KEY_9 = 21;

// TV/Radio & Mute
constexpr int KEY_TV_RADIO = 22;
constexpr int KEY_MUTE     = 23;

// Recall
constexpr int KEY_RECALL = 29;

// Subtitle / EPG / TTX / Info
constexpr int KEY_SUBTITLE = 31;
constexpr int KEY_EPG      = 32;
constexpr int KEY_TTX      = 34;       // Teletext

// Channel up/down (actual channel switch keys from RcuKeyValue)
constexpr int KEY_CH_UP   = 69;        // RcuKeyValue.CH_UP_KEY
constexpr int KEY_CH_DOWN = 70;        // RcuKeyValue.CH_DOWN_KEY

// Page up/down (used in APK page_up_button / page_down_button)
constexpr int KEY_PAGE_UP   = 37;      // PGUP_KEY
constexpr int KEY_PAGE_DOWN = 38;      // PGDN_KEY

// Volume (long-press rotate orientation in APK)
constexpr int KEY_VOL_DOWN = 35;       // rotateOrientation 13
constexpr int KEY_VOL_UP   = 36;       // rotateOrientation 14

// Power
constexpr int KEY_POWER = 42;

// USB
constexpr int KEY_USB = 43;

// Playback control
constexpr int KEY_RECORD       = 58;
constexpr int KEY_REWIND       = 59;   // fast_back
constexpr int KEY_FAST_FORWARD = 60;   // fast_go
constexpr int KEY_PLAY_PAUSE   = 61;   // play
constexpr int KEY_STOP         = 62;
constexpr int KEY_PAUSE        = 63;
constexpr int KEY_PREVIOUS     = 64;
constexpr int KEY_NEXT         = 65;

// Info / Zoom / Audio
constexpr int KEY_INFO  = 57;          // RcuKeyValue.INFO_KEY
constexpr int KEY_ZOOM  = 56;          // RcuKeyValue.ZOOM_KEY
constexpr int KEY_AUDIO = 54;          // RcuKeyValue.AUDIO_KEY
constexpr int KEY_PAUSE_RCU = 55;      // RcuKeyValue.PAUSE_KEY (physical remote)

// Favorites
constexpr int KEY_FAV      = 33;       // RcuKeyValue.FAV_KEY
constexpr int KEY_FAV_PREV = 52;       // RcuKeyValue.FAV_PREV_KEY
constexpr int KEY_FAV_NEXT = 53;       // RcuKeyValue.FAV_NEXT_KEY

// PVR list
constexpr int KEY_PVR_LIST = 66;       // RcuKeyValue.PVR_LIST_KEY

// Display / PIP / Mode / Time
constexpr int KEY_DISPLAY   = 24;      // RcuKeyValue.DISPLAY_KEY
constexpr int KEY_MODE      = 25;      // RcuKeyValue.MODE_KEY
constexpr int KEY_TIME      = 26;      // RcuKeyValue.TIME_KEY
constexpr int KEY_PIP       = 27;      // RcuKeyValue.PIP_KEY
constexpr int KEY_MULTI_PIC = 28;      // RcuKeyValue.MULTI_PIC_KEY
constexpr int KEY_SAT       = 30;      // RcuKeyValue.SAT_KEY
constexpr int KEY_FIND      = 39;      // RcuKeyValue.FIND_KEY
constexpr int KEY_MOSAIC    = 40;      // RcuKeyValue.MOSAIC_KEY
constexpr int KEY_SLEEP     = 41;      // RcuKeyValue.SLEEP_KEY

// F-keys (F1=POWER=44 already mapped separately)
constexpr int KEY_F1 = 44;
constexpr int KEY_F2 = 45;
constexpr int KEY_F3 = 46;
constexpr int KEY_F4 = 47;
constexpr int KEY_F5 = 48;
constexpr int KEY_F6 = 49;
constexpr int KEY_F7 = 50;
constexpr int KEY_F8 = 51;

// TV Source / TV Menu / Format / HDMI / OPT
constexpr int KEY_TV_SOURCE = 67;      // RcuKeyValue.TV_SOURCE_KEY
constexpr int KEY_TV_MENU   = 68;      // RcuKeyValue.TV_MENU_KEY
constexpr int KEY_OPT       = 71;      // RcuKeyValue.OPT_KEY
constexpr int KEY_HDMI      = 72;      // RcuKeyValue.HDMI_KEY
constexpr int KEY_FORMAT    = 73;      // RcuKeyValue.FORMAT_KEY

// Apps / misc
constexpr int KEY_YOUTUBE = 74;        // RcuKeyValue.YOUTUBE_KEY
constexpr int KEY_WEATHER = 75;        // RcuKeyValue.WEATHER_KEY
constexpr int KEY_NETAPP  = 76;        // RcuKeyValue.NETAPP_KEY
constexpr int KEY_HD      = 77;        // RcuKeyValue.HD_KEY
constexpr int KEY_MOTOR   = 78;        // RcuKeyValue.MOTOR_KEY
constexpr int KEY_FUNC    = 79;        // RcuKeyValue.FUNC_KEY
constexpr int KEY_HELP    = 80;        // RcuKeyValue.HELP_KEY
constexpr int KEY_BACK_RCU = 81;       // RcuKeyValue.BACK_KEY (physical remote back)

// ────────────────────────────────────────────────────────────
// Platform 30 key codes (alternative STB firmware)
// ────────────────────────────────────────────────────────────
namespace p30 {

constexpr int KEY_UP    = 24;
constexpr int KEY_DOWN  = 25;
constexpr int KEY_LEFT  = 22;
constexpr int KEY_RIGHT = 23;
constexpr int KEY_ENTER = 27;          // OK
constexpr int KEY_MENU  = 20;
constexpr int KEY_BACK  = 21;          // Exit

constexpr int KEY_RED    = 16;
constexpr int KEY_GREEN  = 17;
constexpr int KEY_YELLOW = 18;
constexpr int KEY_BLUE   = 19;

constexpr int KEY_0 = 10;
constexpr int KEY_1 = 1;
constexpr int KEY_2 = 2;
constexpr int KEY_3 = 3;
constexpr int KEY_4 = 4;
constexpr int KEY_5 = 5;
constexpr int KEY_6 = 6;
constexpr int KEY_7 = 7;
constexpr int KEY_8 = 8;
constexpr int KEY_9 = 9;

constexpr int KEY_TV_RADIO = 11;
constexpr int KEY_MUTE     = 12;

constexpr int KEY_RECALL   = 58;
constexpr int KEY_SUBTITLE = 28;
constexpr int KEY_EPG      = 29;
constexpr int KEY_TTX      = 30;

constexpr int KEY_CH_UP   = 34;        // page_up
constexpr int KEY_CH_DOWN = 35;        // page_down

constexpr int KEY_VOL_DOWN = 56;       // rotateOrientation 13
constexpr int KEY_VOL_UP   = 57;       // rotateOrientation 14

constexpr int KEY_POWER = 44;
constexpr int KEY_USB   = 46;

constexpr int KEY_RECORD       = 43;
constexpr int KEY_REWIND       = 36;   // fast_back
constexpr int KEY_FAST_FORWARD = 37;   // fast_go
constexpr int KEY_PLAY_PAUSE   = 38;   // play
constexpr int KEY_STOP         = 39;
constexpr int KEY_PAUSE        = 42;
constexpr int KEY_PREVIOUS     = 40;
constexpr int KEY_NEXT         = 41;

} // namespace p30

} // namespace keys
} // namespace stb
