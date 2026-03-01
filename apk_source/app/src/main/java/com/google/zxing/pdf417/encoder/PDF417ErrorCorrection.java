package com.google.zxing.pdf417.encoder;

import android.support.v4.media.TransportMediator;
import com.alibaba.fastjson.asm.Opcodes;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.identity.intents.AddressConstants;
import com.google.zxing.WriterException;
import com.hisilicon.multiscreen.protocol.message.KeyInfo;
import com.hisilicon.multiscreen.protocol.message.PushMessageHead;
import com.iflytek.speech.VoiceWakeuperAidl;
import org.cybergarage.multiscreenhttp.HTTPStatus;
import org.videolan.libvlc.EventHandler;
import org.videolan.libvlc.VLCObject;

/* loaded from: classes.dex */
final class PDF417ErrorCorrection {
    private static final int[][] EC_COEFFICIENTS = {new int[]{27, 917}, new int[]{522, 568, 723, 809}, new int[]{KeyInfo.KEYCODE_K, KeyInfo.KEYCODE_ANDD, 436, 284, 646, 653, 428, 379}, new int[]{EventHandler.MediaPlayerVout, 562, KeyInfo.KEYCODE_D, 755, 599, 524, 801, Opcodes.IINC, 295, 116, 442, 428, 295, 42, Opcodes.ARETURN, 65}, new int[]{361, 575, 922, 525, Opcodes.ARETURN, 586, 640, 321, 536, 742, 677, 742, 687, 284, Opcodes.INSTANCEOF, 517, 273, 494, 263, Opcodes.I2S, 593, 800, 571, 320, 803, 133, KeyInfo.KEYCODE_S, 390, 685, 330, 63, 410}, new int[]{539, 422, 6, 93, 862, PushMessageHead.GET_APPS_RESPONSE, 453, 106, 610, 287, FitnessActivities.KICK_SCOOTER, HTTPStatus.HTTP_VERSION_NOT_SUPPORTED, 733, 877, 381, 612, 723, 476, 462, Opcodes.IRETURN, 430, 609, 858, 822, 543, 376, 511, 400, 672, 762, 283, Opcodes.INVOKESTATIC, 440, 35, 519, 31, 460, 594, KeyInfo.KEYCODE_P, 535, 517, KeyInfo.KEYCODE_BIG_THAN, 605, 158, 651, HTTPStatus.CREATED, 488, HTTPStatus.BAD_GATEWAY, 648, 733, 717, 83, 404, 97, 280, PushMessageHead.GET_APPS_RESPONSE, 840, 629, 4, 381, 843, 623, 264, 543}, new int[]{521, KeyInfo.KEYCODE_LEFTBRACKET, 864, 547, 858, 580, 296, 379, 53, 779, 897, 444, 400, 925, 749, HTTPStatus.UNSUPPORTED_MEDIA_TYPE, 822, 93, KeyInfo.KEYCODE_SEARCH, 208, 928, KeyInfo.KEYCODE_Z, 583, 620, KeyInfo.KEYCODE_C, Opcodes.LCMP, 447, 631, 292, 908, 490, 704, VLCObject.Events.MediaListEndReached, VoiceWakeuperAidl.RES_SPECIFIED, 457, 907, 594, 723, 674, 292, 272, 96, 684, 432, 686, 606, 860, 569, Opcodes.INSTANCEOF, KeyInfo.KEYCODE_R, 129, 186, KeyInfo.KEYCODE_J, 287, Opcodes.CHECKCAST, 775, EventHandler.MediaPlayerESAdded, 173, 40, 379, 712, 463, 646, 776, 171, 491, 297, 763, Opcodes.IFGE, 732, 95, 270, 447, 90, 507, 48, 228, 821, 808, 898, 784, 663, 627, 378, 382, EventHandler.MediaPlayerStopped, 380, 602, 754, 336, 89, 614, 87, 432, 670, 616, Opcodes.IFGT, 374, 242, 726, 600, 269, 375, 898, 845, 454, 354, TransportMediator.KEYCODE_MEDIA_RECORD, 814, 587, 804, 34, 211, 330, 539, 297, 827, 865, 37, 517, 834, 315, 550, 86, 801, 4, 108, 539}, new int[]{524, 894, 75, 766, 882, 857, 74, HTTPStatus.NO_CONTENT, 82, 586, 708, KeyInfo.KEYCODE_M, 905, 786, 138, 720, 858, 194, KeyInfo.KEYCODE_RIGHTBRACKET, 913, EventHandler.MediaPlayerScrambledChanged, 190, 375, 850, 438, 733, 194, 280, HTTPStatus.CREATED, 280, 828, 757, 710, 814, 919, 89, 68, 569, 11, HTTPStatus.NO_CONTENT, 796, 605, 540, 913, 801, 700, 799, 137, 439, 418, 592, 668, 353, 859, 370, 694, 325, 240, KeyInfo.KEYCODE_Q, 257, 284, 549, 209, 884, 315, 70, 329, 793, 490, EventHandler.MediaPlayerVout, 877, Opcodes.IF_ICMPGE, 749, 812, 684, 461, 334, 376, 849, 521, KeyInfo.KEYCODE_AND, 291, 803, 712, 19, KeyInfo.KEYCODE_INFO, 399, 908, 103, 511, 51, 8, 517, KeyInfo.KEYCODE_P, 289, 470, 637, 731, 66, 255, 917, 269, 463, 830, 730, 433, 848, 585, 136, 538, 906, 90, 2, 290, 743, Opcodes.IFNONNULL, 655, 903, 329, 49, 802, 580, 355, 588, 188, 462, 10, 134, 628, 320, KeyInfo.KEYCODE_MORE, TransportMediator.KEYCODE_MEDIA_RECORD, 739, 71, 263, 318, 374, 601, Opcodes.CHECKCAST, 605, 142, 673, 687, KeyInfo.KEYCODE_G, 722, 384, Opcodes.RETURN, 752, 607, 640, 455, Opcodes.INSTANCEOF, 689, 707, 805, 641, 48, 60, 732, 621, 895, 544, EventHandler.MediaPlayerPaused, 852, 655, KeyInfo.KEYCODE_STAR, 697, 755, 756, 60, KeyInfo.KEYCODE_S, PushMessageHead.PLAY_MEDIA, 434, 421, 726, 528, HTTPStatus.SERVICE_UNAVAILABLE, 118, 49, 795, 32, 144, 500, KeyInfo.KEYCODE_L, 836, 394, 280, 566, 319, 9, 647, 550, 73, 914, 342, 126, 32, 681, 331, 792, 620, 60, 609, 441, Opcodes.GETFIELD, 791, 893, 754, 605, 383, 228, 749, 760, 213, 54, 297, 134, 54, 834, 299, 922, 191, 910, 532, 609, 829, 189, 20, Opcodes.GOTO, 29, 872, 449, 83, 402, 41, 656, HTTPStatus.HTTP_VERSION_NOT_SUPPORTED, 579, 481, 173, 404, 251, 688, 95, 497, AddressConstants.ErrorCodes.ERROR_CODE_NO_APPLICABLE_ADDRESSES, 642, 543, KeyInfo.KEYCODE_AND, 159, 924, 558, 648, 55, 497, 10}, new int[]{KeyInfo.KEYCODE_BIG_THAN, 77, 373, HTTPStatus.GATEWAY_TIMEOUT, 35, 599, 428, 207, 409, 574, 118, 498, 285, 380, 350, 492, 197, EventHandler.MediaPlayerEndReached, 920, Opcodes.IFLT, 914, 299, 229, 643, 294, 871, KeyInfo.KEYCODE_PERCENT, 88, 87, Opcodes.INSTANCEOF, KeyInfo.KEYCODE_BIG_THAN, 781, 846, 75, KeyInfo.KEYCODE_RIGHT_BIG_BRACKET, 520, 435, 543, HTTPStatus.NON_AUTHORITATIVE_INFORMATION, 666, KeyInfo.KEYCODE_N, 346, 781, 621, 640, EventHandler.MediaPlayerPositionChanged, 794, 534, 539, 781, HTTPStatus.REQUEST_TIMEOUT, 390, 644, 102, 476, 499, 290, 632, 545, 37, 858, 916, 552, 41, 542, 289, 122, 272, 383, 800, 485, 98, 752, 472, 761, FitnessActivities.KICK_SCOOTER, 784, 860, 658, 741, 290, HTTPStatus.NO_CONTENT, 681, HTTPStatus.PROXY_AUTHENTICATION_REQUIRED, 855, 85, 99, 62, 482, Opcodes.GETFIELD, 20, 297, 451, 593, 913, 142, 808, 684, 287, 536, 561, 76, 653, 899, 729, 567, 744, 390, 513, Opcodes.CHECKCAST, VLCObject.Events.MediaListEndReached, VoiceWakeuperAidl.RES_SPECIFIED, 240, 518, 794, 395, 768, 848, 51, 610, 384, 168, 190, 826, 328, 596, 786, KeyInfo.KEYCODE_AT, 570, 381, HTTPStatus.UNSUPPORTED_MEDIA_TYPE, 641, Opcodes.IFGE, KeyInfo.KEYCODE_K, Opcodes.DCMPL, 429, 531, 207, 676, 710, 89, 168, KeyInfo.KEYCODE_WELL, 402, 40, 708, 575, Opcodes.IF_ICMPGE, 864, 229, 65, 861, 841, 512, 164, 477, KeyInfo.KEYCODE_Y, 92, KeyInfo.KEYCODE_INFO, 785, 288, 357, 850, 836, 827, 736, 707, 94, 8, 494, KeyInfo.KEYCODE_VOLUME_DOWN, 521, 2, 499, 851, 543, Opcodes.DCMPG, 729, PushMessageHead.GET_APPS_RESPONSE, 95, KeyInfo.KEYCODE_B, 361, 578, 323, 856, 797, 289, 51, 684, 466, 533, 820, 669, 45, 902, 452, Opcodes.GOTO, 342, KeyInfo.KEYCODE_Z, 173, 35, 463, 651, 51, 699, 591, 452, 578, 37, 124, 298, 332, 552, 43, 427, 119, 662, 777, 475, 850, 764, 364, 578, 911, 283, 711, 472, 420, KeyInfo.KEYCODE_X, 288, 594, 394, 511, KeyInfo.KEYCODE_RIGHT_BIG_BRACKET, 589, 777, 699, 688, 43, HTTPStatus.REQUEST_TIMEOUT, 842, 383, 721, 521, 560, 644, 714, 559, 62, Opcodes.I2B, 873, 663, 713, 159, 672, 729, 624, 59, Opcodes.INSTANCEOF, HTTPStatus.EXPECTATION_FAILED, 158, 209, 563, 564, KeyInfo.KEYCODE_OR, 693, KeyInfo.KEYCODE_NEXT, 608, 563, 365, Opcodes.PUTFIELD, PushMessageHead.LAUNCH_APP, 677, KeyInfo.KEYCODE_LEFTBRACKET, KeyInfo.KEYCODE_B, 353, 708, 410, 579, 870, 617, 841, 632, 860, 289, 536, 35, 777, 618, 586, 424, 833, 77, 597, 346, 269, 757, 632, 695, 751, 331, KeyInfo.KEYCODE_V, Opcodes.INVOKESTATIC, 45, 787, 680, 18, 66, HTTPStatus.PROXY_AUTHENTICATION_REQUIRED, 369, 54, 492, 228, 613, 830, 922, 437, 519, 644, 905, 789, 420, KeyInfo.KEYCODE_DOLLAR, 441, 207, 300, 892, 827, 141, 537, 381, 662, 513, 56, 252, KeyInfo.KEYCODE_REVERSE, 242, 797, 838, 837, 720, KeyInfo.KEYCODE_O, KeyInfo.KEYCODE_AND, 631, 61, 87, 560, KeyInfo.KEYCODE_LEFTBRACKET, 756, 665, 397, 808, 851, KeyInfo.KEYCODE_STAR, 473, 795, 378, 31, 647, 915, 459, 806, 590, 731, 425, KeyInfo.KEYCODE_Q, 548, KeyInfo.KEYCODE_N, 321, 881, 699, 535, 673, 782, 210, 815, 905, KeyInfo.KEYCODE_AT, 843, 922, 281, 73, 469, 791, 660, Opcodes.IF_ICMPGE, 498, KeyInfo.KEYCODE_ANDD, Opcodes.IFLT, 422, 907, 817, Opcodes.NEW, 62, 16, 425, 535, 336, 286, 437, 375, 273, 610, 296, Opcodes.INVOKESPECIAL, 923, 116, 667, 751, 353, 62, 366, 691, 379, 687, 842, 37, 357, 720, 742, 330, 5, 39, 923, KeyInfo.KEYCODE_RIGHTBRACKET, 424, 242, 749, 321, 54, 669, 316, 342, 299, 534, 105, 667, 488, 640, 672, 576, 540, 316, 486, 721, 610, 46, 656, 447, 171, 616, 464, 190, 531, 297, 321, 762, 752, 533, 175, 134, 14, 381, 433, 717, 45, 111, 20, 596, 284, 736, 138, 646, 411, 877, 669, 141, 919, 45, 780, HTTPStatus.PROXY_AUTHENTICATION_REQUIRED, 164, 332, 899, Opcodes.IF_ACMPEQ, 726, 600, 325, 498, 655, 357, 752, 768, KeyInfo.KEYCODE_I, 849, 647, 63, KeyInfo.KEYCODE_LEFTBRACKET, 863, 251, 366, KeyInfo.KEYCODE_WELL, 282, 738, 675, 410, 389, KeyInfo.KEYCODE_Z, 31, 121, KeyInfo.KEYCODE_AT, 263}};

    private PDF417ErrorCorrection() {
    }

    static int getErrorCorrectionCodewordCount(int errorCorrectionLevel) {
        if (errorCorrectionLevel < 0 || errorCorrectionLevel > 8) {
            throw new IllegalArgumentException("Error correction level must be between 0 and 8!");
        }
        return 1 << (errorCorrectionLevel + 1);
    }

    static int getRecommendedMinimumErrorCorrectionLevel(int n) throws WriterException {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be > 0");
        }
        if (n <= 40) {
            return 2;
        }
        if (n <= 160) {
            return 3;
        }
        if (n <= 320) {
            return 4;
        }
        if (n <= 863) {
            return 5;
        }
        throw new WriterException("No recommendation possible");
    }

    static String generateErrorCorrection(CharSequence dataCodewords, int errorCorrectionLevel) {
        int k = getErrorCorrectionCodewordCount(errorCorrectionLevel);
        char[] e = new char[k];
        int sld = dataCodewords.length();
        for (int i = 0; i < sld; i++) {
            int t1 = (dataCodewords.charAt(i) + e[e.length - 1]) % 929;
            for (int j = k - 1; j >= 1; j--) {
                int t2 = (EC_COEFFICIENTS[errorCorrectionLevel][j] * t1) % 929;
                int t3 = 929 - t2;
                e[j] = (char) ((e[j - 1] + t3) % 929);
            }
            int t22 = (EC_COEFFICIENTS[errorCorrectionLevel][0] * t1) % 929;
            int t32 = 929 - t22;
            e[0] = (char) (t32 % 929);
        }
        StringBuilder sb = new StringBuilder(k);
        for (int j2 = k - 1; j2 >= 0; j2--) {
            if (e[j2] != 0) {
                e[j2] = (char) (929 - e[j2]);
            }
            sb.append(e[j2]);
        }
        return sb.toString();
    }
}
