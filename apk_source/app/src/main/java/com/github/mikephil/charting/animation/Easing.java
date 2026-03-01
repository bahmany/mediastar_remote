package com.github.mikephil.charting.animation;

/* loaded from: classes.dex */
public class Easing {
    private static /* synthetic */ int[] $SWITCH_TABLE$com$github$mikephil$charting$animation$Easing$EasingOption;

    public enum EasingOption {
        Linear,
        EaseInQuad,
        EaseOutQuad,
        EaseInOutQuad,
        EaseInCubic,
        EaseOutCubic,
        EaseInOutCubic,
        EaseInQuart,
        EaseOutQuart,
        EaseInOutQuart,
        EaseInSine,
        EaseOutSine,
        EaseInOutSine,
        EaseInExpo,
        EaseOutExpo,
        EaseInOutExpo,
        EaseInCirc,
        EaseOutCirc,
        EaseInOutCirc,
        EaseInElastic,
        EaseOutElastic,
        EaseInOutElastic,
        EaseInBack,
        EaseOutBack,
        EaseInOutBack,
        EaseInBounce,
        EaseOutBounce,
        EaseInOutBounce;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static EasingOption[] valuesCustom() {
            EasingOption[] easingOptionArrValuesCustom = values();
            int length = easingOptionArrValuesCustom.length;
            EasingOption[] easingOptionArr = new EasingOption[length];
            System.arraycopy(easingOptionArrValuesCustom, 0, easingOptionArr, 0, length);
            return easingOptionArr;
        }
    }

    static /* synthetic */ int[] $SWITCH_TABLE$com$github$mikephil$charting$animation$Easing$EasingOption() {
        int[] iArr = $SWITCH_TABLE$com$github$mikephil$charting$animation$Easing$EasingOption;
        if (iArr == null) {
            iArr = new int[EasingOption.valuesCustom().length];
            try {
                iArr[EasingOption.EaseInBack.ordinal()] = 23;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[EasingOption.EaseInBounce.ordinal()] = 26;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[EasingOption.EaseInCirc.ordinal()] = 17;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[EasingOption.EaseInCubic.ordinal()] = 5;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[EasingOption.EaseInElastic.ordinal()] = 20;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[EasingOption.EaseInExpo.ordinal()] = 14;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[EasingOption.EaseInOutBack.ordinal()] = 25;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[EasingOption.EaseInOutBounce.ordinal()] = 28;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[EasingOption.EaseInOutCirc.ordinal()] = 19;
            } catch (NoSuchFieldError e9) {
            }
            try {
                iArr[EasingOption.EaseInOutCubic.ordinal()] = 7;
            } catch (NoSuchFieldError e10) {
            }
            try {
                iArr[EasingOption.EaseInOutElastic.ordinal()] = 22;
            } catch (NoSuchFieldError e11) {
            }
            try {
                iArr[EasingOption.EaseInOutExpo.ordinal()] = 16;
            } catch (NoSuchFieldError e12) {
            }
            try {
                iArr[EasingOption.EaseInOutQuad.ordinal()] = 4;
            } catch (NoSuchFieldError e13) {
            }
            try {
                iArr[EasingOption.EaseInOutQuart.ordinal()] = 10;
            } catch (NoSuchFieldError e14) {
            }
            try {
                iArr[EasingOption.EaseInOutSine.ordinal()] = 13;
            } catch (NoSuchFieldError e15) {
            }
            try {
                iArr[EasingOption.EaseInQuad.ordinal()] = 2;
            } catch (NoSuchFieldError e16) {
            }
            try {
                iArr[EasingOption.EaseInQuart.ordinal()] = 8;
            } catch (NoSuchFieldError e17) {
            }
            try {
                iArr[EasingOption.EaseInSine.ordinal()] = 11;
            } catch (NoSuchFieldError e18) {
            }
            try {
                iArr[EasingOption.EaseOutBack.ordinal()] = 24;
            } catch (NoSuchFieldError e19) {
            }
            try {
                iArr[EasingOption.EaseOutBounce.ordinal()] = 27;
            } catch (NoSuchFieldError e20) {
            }
            try {
                iArr[EasingOption.EaseOutCirc.ordinal()] = 18;
            } catch (NoSuchFieldError e21) {
            }
            try {
                iArr[EasingOption.EaseOutCubic.ordinal()] = 6;
            } catch (NoSuchFieldError e22) {
            }
            try {
                iArr[EasingOption.EaseOutElastic.ordinal()] = 21;
            } catch (NoSuchFieldError e23) {
            }
            try {
                iArr[EasingOption.EaseOutExpo.ordinal()] = 15;
            } catch (NoSuchFieldError e24) {
            }
            try {
                iArr[EasingOption.EaseOutQuad.ordinal()] = 3;
            } catch (NoSuchFieldError e25) {
            }
            try {
                iArr[EasingOption.EaseOutQuart.ordinal()] = 9;
            } catch (NoSuchFieldError e26) {
            }
            try {
                iArr[EasingOption.EaseOutSine.ordinal()] = 12;
            } catch (NoSuchFieldError e27) {
            }
            try {
                iArr[EasingOption.Linear.ordinal()] = 1;
            } catch (NoSuchFieldError e28) {
            }
            $SWITCH_TABLE$com$github$mikephil$charting$animation$Easing$EasingOption = iArr;
        }
        return iArr;
    }

    public static EasingFunction getEasingFunctionFromOption(EasingOption easing) {
        switch ($SWITCH_TABLE$com$github$mikephil$charting$animation$Easing$EasingOption()[easing.ordinal()]) {
            case 2:
                return EasingFunctions.EaseInQuad;
            case 3:
                return EasingFunctions.EaseOutQuad;
            case 4:
                return EasingFunctions.EaseInOutQuad;
            case 5:
                return EasingFunctions.EaseInCubic;
            case 6:
                return EasingFunctions.EaseOutCubic;
            case 7:
                return EasingFunctions.EaseInOutCubic;
            case 8:
                return EasingFunctions.EaseInQuart;
            case 9:
                return EasingFunctions.EaseOutQuart;
            case 10:
                return EasingFunctions.EaseInOutQuart;
            case 11:
                return EasingFunctions.EaseInSine;
            case 12:
                return EasingFunctions.EaseOutSine;
            case 13:
                return EasingFunctions.EaseInOutSine;
            case 14:
                return EasingFunctions.EaseInExpo;
            case 15:
                return EasingFunctions.EaseOutExpo;
            case 16:
                return EasingFunctions.EaseInOutExpo;
            case 17:
                return EasingFunctions.EaseInCirc;
            case 18:
                return EasingFunctions.EaseOutCirc;
            case 19:
                return EasingFunctions.EaseInOutCirc;
            case 20:
                return EasingFunctions.EaseInElastic;
            case 21:
                return EasingFunctions.EaseOutElastic;
            case 22:
                return EasingFunctions.EaseInOutElastic;
            case 23:
                return EasingFunctions.EaseInBack;
            case 24:
                return EasingFunctions.EaseOutBack;
            case 25:
                return EasingFunctions.EaseInOutBack;
            case 26:
                return EasingFunctions.EaseInBounce;
            case 27:
                return EasingFunctions.EaseOutBounce;
            case 28:
                return EasingFunctions.EaseInOutBounce;
            default:
                return EasingFunctions.Linear;
        }
    }

    private static class EasingFunctions {
        public static final EasingFunction Linear = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.1
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                return input;
            }
        };
        public static final EasingFunction EaseInQuad = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.2
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                return input * input;
            }
        };
        public static final EasingFunction EaseOutQuad = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.3
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                return (-input) * (input - 2.0f);
            }
        };
        public static final EasingFunction EaseInOutQuad = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.4
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                float position = input / 0.5f;
                if (position < 1.0f) {
                    return 0.5f * position * position;
                }
                float position2 = position - 1.0f;
                return (-0.5f) * (((position2 - 2.0f) * position2) - 1.0f);
            }
        };
        public static final EasingFunction EaseInCubic = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.5
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                return input * input * input;
            }
        };
        public static final EasingFunction EaseOutCubic = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.6
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                float input2 = input - 1.0f;
                return (input2 * input2 * input2) + 1.0f;
            }
        };
        public static final EasingFunction EaseInOutCubic = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.7
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                float position = input / 0.5f;
                if (position < 1.0f) {
                    return 0.5f * position * position * position;
                }
                float position2 = position - 2.0f;
                return ((position2 * position2 * position2) + 2.0f) * 0.5f;
            }
        };
        public static final EasingFunction EaseInQuart = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.8
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                return input * input * input * input;
            }
        };
        public static final EasingFunction EaseOutQuart = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.9
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                float input2 = input - 1.0f;
                return -((((input2 * input2) * input2) * input2) - 1.0f);
            }
        };
        public static final EasingFunction EaseInOutQuart = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.10
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                float position = input / 0.5f;
                if (position < 1.0f) {
                    return 0.5f * position * position * position * position;
                }
                float position2 = position - 2.0f;
                return (-0.5f) * ((((position2 * position2) * position2) * position2) - 2.0f);
            }
        };
        public static final EasingFunction EaseInSine = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.11
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                return (-((float) Math.cos(input * 1.5707963267948966d))) + 1.0f;
            }
        };
        public static final EasingFunction EaseOutSine = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.12
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                return (float) Math.sin(input * 1.5707963267948966d);
            }
        };
        public static final EasingFunction EaseInOutSine = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.13
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                return (-0.5f) * (((float) Math.cos(3.141592653589793d * input)) - 1.0f);
            }
        };
        public static final EasingFunction EaseInExpo = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.14
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                if (input == 0.0f) {
                    return 0.0f;
                }
                return (float) Math.pow(2.0d, 10.0f * (input - 1.0f));
            }
        };
        public static final EasingFunction EaseOutExpo = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.15
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                if (input == 1.0f) {
                    return 1.0f;
                }
                return -((float) Math.pow(2.0d, (1.0f + input) * (-10.0f)));
            }
        };
        public static final EasingFunction EaseInOutExpo = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.16
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                if (input == 0.0f) {
                    return 0.0f;
                }
                if (input == 1.0f) {
                    return 1.0f;
                }
                float position = input / 0.5f;
                if (position < 1.0f) {
                    return ((float) Math.pow(2.0d, 10.0f * (position - 1.0f))) * 0.5f;
                }
                return ((-((float) Math.pow(2.0d, (-10.0f) * (position - 1.0f)))) + 2.0f) * 0.5f;
            }
        };
        public static final EasingFunction EaseInCirc = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.17
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                return -(((float) Math.sqrt(1.0f - (input * input))) - 1.0f);
            }
        };
        public static final EasingFunction EaseOutCirc = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.18
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                float input2 = input - 1.0f;
                return (float) Math.sqrt(1.0f - (input2 * input2));
            }
        };
        public static final EasingFunction EaseInOutCirc = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.19
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                float position = input / 0.5f;
                if (position < 1.0f) {
                    return (-0.5f) * (((float) Math.sqrt(1.0f - (position * position))) - 1.0f);
                }
                float position2 = position - 2.0f;
                return (((float) Math.sqrt(1.0f - (position2 * position2))) + 1.0f) * 0.5f;
            }
        };
        public static final EasingFunction EaseInElastic = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.20
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                if (input == 0.0f) {
                    return 0.0f;
                }
                if (input == 1.0f) {
                    return 1.0f;
                }
                float s = (0.3f / 6.2831855f) * ((float) Math.asin(1.0d));
                float position = input - 1.0f;
                return -(((float) Math.pow(2.0d, 10.0f * position)) * ((float) Math.sin(((position - s) * 6.283185307179586d) / 0.3f)));
            }
        };
        public static final EasingFunction EaseOutElastic = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.21
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                if (input == 0.0f) {
                    return 0.0f;
                }
                if (input == 1.0f) {
                    return 1.0f;
                }
                float s = (0.3f / 6.2831855f) * ((float) Math.asin(1.0d));
                return (((float) Math.pow(2.0d, (-10.0f) * input)) * ((float) Math.sin(((input - s) * 6.283185307179586d) / 0.3f))) + 1.0f;
            }
        };
        public static final EasingFunction EaseInOutElastic = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.22
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                if (input == 0.0f) {
                    return 0.0f;
                }
                float position = input / 0.5f;
                if (position == 2.0f) {
                    return 1.0f;
                }
                float s = (0.45000002f / 6.2831855f) * ((float) Math.asin(1.0d));
                if (position < 1.0f) {
                    float position2 = position - 1.0f;
                    return (-0.5f) * ((float) Math.sin((((1.0f * position2) - s) * 6.283185307179586d) / 0.45000002f)) * ((float) Math.pow(2.0d, 10.0f * position2));
                }
                float position3 = position - 1.0f;
                return (((float) Math.pow(2.0d, (-10.0f) * position3)) * ((float) Math.sin((((position3 * 1.0f) - s) * 6.283185307179586d) / 0.45000002f)) * 0.5f) + 1.0f;
            }
        };
        public static final EasingFunction EaseInBack = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.23
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                return input * input * ((2.70158f * input) - 1.70158f);
            }
        };
        public static final EasingFunction EaseOutBack = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.24
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                float position = input - 1.0f;
                return (position * position * ((2.70158f * position) + 1.70158f)) + 1.0f;
            }
        };
        public static final EasingFunction EaseInOutBack = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.25
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                float position = input / 0.5f;
                if (position < 1.0f) {
                    float s = 1.70158f * 1.525f;
                    return position * position * (((1.0f + s) * position) - s) * 0.5f;
                }
                float position2 = position - 2.0f;
                float s2 = 1.70158f * 1.525f;
                return ((position2 * position2 * (((1.0f + s2) * position2) + s2)) + 2.0f) * 0.5f;
            }
        };
        public static final EasingFunction EaseInBounce = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.26
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                return 1.0f - EasingFunctions.EaseOutBounce.getInterpolation(1.0f - input);
            }
        };
        public static final EasingFunction EaseOutBounce = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.27
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                if (input < 0.36363637f) {
                    return 7.5625f * input * input;
                }
                if (input < 0.72727275f) {
                    float position = input - 0.54545456f;
                    return (7.5625f * position * position) + 0.75f;
                }
                if (input < 0.90909094f) {
                    float position2 = input - 0.8181818f;
                    return (7.5625f * position2 * position2) + 0.9375f;
                }
                float position3 = input - 0.95454544f;
                return (7.5625f * position3 * position3) + 0.984375f;
            }
        };
        public static final EasingFunction EaseInOutBounce = new EasingFunction() { // from class: com.github.mikephil.charting.animation.Easing.EasingFunctions.28
            @Override // com.github.mikephil.charting.animation.EasingFunction, android.animation.TimeInterpolator
            public float getInterpolation(float input) {
                return input < 0.5f ? EasingFunctions.EaseInBounce.getInterpolation(2.0f * input) * 0.5f : (EasingFunctions.EaseOutBounce.getInterpolation((2.0f * input) - 1.0f) * 0.5f) + 0.5f;
            }
        };

        private EasingFunctions() {
        }
    }
}
