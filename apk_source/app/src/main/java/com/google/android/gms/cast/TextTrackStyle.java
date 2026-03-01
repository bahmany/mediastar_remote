package com.google.android.gms.cast;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.accessibility.CaptioningManager;
import com.google.android.gms.common.internal.m;
import com.google.android.gms.internal.ik;
import com.google.android.gms.internal.jz;
import com.google.android.gms.internal.kc;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class TextTrackStyle {
    public static final int COLOR_UNSPECIFIED = 0;
    public static final float DEFAULT_FONT_SCALE = 1.0f;
    public static final int EDGE_TYPE_DEPRESSED = 4;
    public static final int EDGE_TYPE_DROP_SHADOW = 2;
    public static final int EDGE_TYPE_NONE = 0;
    public static final int EDGE_TYPE_OUTLINE = 1;
    public static final int EDGE_TYPE_RAISED = 3;
    public static final int EDGE_TYPE_UNSPECIFIED = -1;
    public static final int FONT_FAMILY_CASUAL = 4;
    public static final int FONT_FAMILY_CURSIVE = 5;
    public static final int FONT_FAMILY_MONOSPACED_SANS_SERIF = 1;
    public static final int FONT_FAMILY_MONOSPACED_SERIF = 3;
    public static final int FONT_FAMILY_SANS_SERIF = 0;
    public static final int FONT_FAMILY_SERIF = 2;
    public static final int FONT_FAMILY_SMALL_CAPITALS = 6;
    public static final int FONT_FAMILY_UNSPECIFIED = -1;
    public static final int FONT_STYLE_BOLD = 1;
    public static final int FONT_STYLE_BOLD_ITALIC = 3;
    public static final int FONT_STYLE_ITALIC = 2;
    public static final int FONT_STYLE_NORMAL = 0;
    public static final int FONT_STYLE_UNSPECIFIED = -1;
    public static final int WINDOW_TYPE_NONE = 0;
    public static final int WINDOW_TYPE_NORMAL = 1;
    public static final int WINDOW_TYPE_ROUNDED = 2;
    public static final int WINDOW_TYPE_UNSPECIFIED = -1;
    private JSONObject Fl;
    private float Gd;
    private int Ge;
    private int Gf;
    private int Gg;
    private int Gh;
    private int Gi;
    private int Gj;
    private String Gk;
    private int Gl;
    private int Gm;
    private int xm;

    public TextTrackStyle() {
        clear();
    }

    private int aC(String str) throws NumberFormatException {
        if (str == null || str.length() != 9 || str.charAt(0) != '#') {
            return 0;
        }
        try {
            return Color.argb(Integer.parseInt(str.substring(7, 9), 16), Integer.parseInt(str.substring(1, 3), 16), Integer.parseInt(str.substring(3, 5), 16), Integer.parseInt(str.substring(5, 7), 16));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void clear() {
        this.Gd = 1.0f;
        this.Ge = 0;
        this.xm = 0;
        this.Gf = -1;
        this.Gg = 0;
        this.Gh = -1;
        this.Gi = 0;
        this.Gj = 0;
        this.Gk = null;
        this.Gl = -1;
        this.Gm = -1;
        this.Fl = null;
    }

    public static TextTrackStyle fromSystemSettings(Context context) {
        TextTrackStyle textTrackStyle = new TextTrackStyle();
        if (!kc.hH()) {
            return textTrackStyle;
        }
        CaptioningManager captioningManager = (CaptioningManager) context.getSystemService("captioning");
        textTrackStyle.setFontScale(captioningManager.getFontScale());
        CaptioningManager.CaptionStyle userStyle = captioningManager.getUserStyle();
        textTrackStyle.setBackgroundColor(userStyle.backgroundColor);
        textTrackStyle.setForegroundColor(userStyle.foregroundColor);
        switch (userStyle.edgeType) {
            case 1:
                textTrackStyle.setEdgeType(1);
                break;
            case 2:
                textTrackStyle.setEdgeType(2);
                break;
            default:
                textTrackStyle.setEdgeType(0);
                break;
        }
        textTrackStyle.setEdgeColor(userStyle.edgeColor);
        Typeface typeface = userStyle.getTypeface();
        if (typeface != null) {
            if (Typeface.MONOSPACE.equals(typeface)) {
                textTrackStyle.setFontGenericFamily(1);
            } else if (!Typeface.SANS_SERIF.equals(typeface) && Typeface.SERIF.equals(typeface)) {
                textTrackStyle.setFontGenericFamily(2);
            } else {
                textTrackStyle.setFontGenericFamily(0);
            }
            boolean zIsBold = typeface.isBold();
            boolean zIsItalic = typeface.isItalic();
            if (zIsBold && zIsItalic) {
                textTrackStyle.setFontStyle(3);
            } else if (zIsBold) {
                textTrackStyle.setFontStyle(1);
            } else if (zIsItalic) {
                textTrackStyle.setFontStyle(2);
            } else {
                textTrackStyle.setFontStyle(0);
            }
        }
        return textTrackStyle;
    }

    private String t(int i) {
        return String.format("#%02X%02X%02X%02X", Integer.valueOf(Color.red(i)), Integer.valueOf(Color.green(i)), Integer.valueOf(Color.blue(i)), Integer.valueOf(Color.alpha(i)));
    }

    public JSONObject bL() throws JSONException {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("fontScale", this.Gd);
            if (this.Ge != 0) {
                jSONObject.put("foregroundColor", t(this.Ge));
            }
            if (this.xm != 0) {
                jSONObject.put("backgroundColor", t(this.xm));
            }
            switch (this.Gf) {
                case 0:
                    jSONObject.put("edgeType", "NONE");
                    break;
                case 1:
                    jSONObject.put("edgeType", "OUTLINE");
                    break;
                case 2:
                    jSONObject.put("edgeType", "DROP_SHADOW");
                    break;
                case 3:
                    jSONObject.put("edgeType", "RAISED");
                    break;
                case 4:
                    jSONObject.put("edgeType", "DEPRESSED");
                    break;
            }
            if (this.Gg != 0) {
                jSONObject.put("edgeColor", t(this.Gg));
            }
            switch (this.Gh) {
                case 0:
                    jSONObject.put("windowType", "NONE");
                    break;
                case 1:
                    jSONObject.put("windowType", "NORMAL");
                    break;
                case 2:
                    jSONObject.put("windowType", "ROUNDED_CORNERS");
                    break;
            }
            if (this.Gi != 0) {
                jSONObject.put("windowColor", t(this.Gi));
            }
            if (this.Gh == 2) {
                jSONObject.put("windowRoundedCornerRadius", this.Gj);
            }
            if (this.Gk != null) {
                jSONObject.put("fontFamily", this.Gk);
            }
            switch (this.Gl) {
                case 0:
                    jSONObject.put("fontGenericFamily", "SANS_SERIF");
                    break;
                case 1:
                    jSONObject.put("fontGenericFamily", "MONOSPACED_SANS_SERIF");
                    break;
                case 2:
                    jSONObject.put("fontGenericFamily", "SERIF");
                    break;
                case 3:
                    jSONObject.put("fontGenericFamily", "MONOSPACED_SERIF");
                    break;
                case 4:
                    jSONObject.put("fontGenericFamily", "CASUAL");
                    break;
                case 5:
                    jSONObject.put("fontGenericFamily", "CURSIVE");
                    break;
                case 6:
                    jSONObject.put("fontGenericFamily", "SMALL_CAPITALS");
                    break;
            }
            switch (this.Gm) {
                case 0:
                    jSONObject.put("fontStyle", "NORMAL");
                    break;
                case 1:
                    jSONObject.put("fontStyle", "BOLD");
                    break;
                case 2:
                    jSONObject.put("fontStyle", "ITALIC");
                    break;
                case 3:
                    jSONObject.put("fontStyle", "BOLD_ITALIC");
                    break;
            }
            if (this.Fl != null) {
                jSONObject.put("customData", this.Fl);
            }
        } catch (JSONException e) {
        }
        return jSONObject;
    }

    public void c(JSONObject jSONObject) throws JSONException {
        clear();
        this.Gd = (float) jSONObject.optDouble("fontScale", 1.0d);
        this.Ge = aC(jSONObject.optString("foregroundColor"));
        this.xm = aC(jSONObject.optString("backgroundColor"));
        if (jSONObject.has("edgeType")) {
            String string = jSONObject.getString("edgeType");
            if ("NONE".equals(string)) {
                this.Gf = 0;
            } else if ("OUTLINE".equals(string)) {
                this.Gf = 1;
            } else if ("DROP_SHADOW".equals(string)) {
                this.Gf = 2;
            } else if ("RAISED".equals(string)) {
                this.Gf = 3;
            } else if ("DEPRESSED".equals(string)) {
                this.Gf = 4;
            }
        }
        this.Gg = aC(jSONObject.optString("edgeColor"));
        if (jSONObject.has("windowType")) {
            String string2 = jSONObject.getString("windowType");
            if ("NONE".equals(string2)) {
                this.Gh = 0;
            } else if ("NORMAL".equals(string2)) {
                this.Gh = 1;
            } else if ("ROUNDED_CORNERS".equals(string2)) {
                this.Gh = 2;
            }
        }
        this.Gi = aC(jSONObject.optString("windowColor"));
        if (this.Gh == 2) {
            this.Gj = jSONObject.optInt("windowRoundedCornerRadius", 0);
        }
        this.Gk = jSONObject.optString("fontFamily", null);
        if (jSONObject.has("fontGenericFamily")) {
            String string3 = jSONObject.getString("fontGenericFamily");
            if ("SANS_SERIF".equals(string3)) {
                this.Gl = 0;
            } else if ("MONOSPACED_SANS_SERIF".equals(string3)) {
                this.Gl = 1;
            } else if ("SERIF".equals(string3)) {
                this.Gl = 2;
            } else if ("MONOSPACED_SERIF".equals(string3)) {
                this.Gl = 3;
            } else if ("CASUAL".equals(string3)) {
                this.Gl = 4;
            } else if ("CURSIVE".equals(string3)) {
                this.Gl = 5;
            } else if ("SMALL_CAPITALS".equals(string3)) {
                this.Gl = 6;
            }
        }
        if (jSONObject.has("fontStyle")) {
            String string4 = jSONObject.getString("fontStyle");
            if ("NORMAL".equals(string4)) {
                this.Gm = 0;
            } else if ("BOLD".equals(string4)) {
                this.Gm = 1;
            } else if ("ITALIC".equals(string4)) {
                this.Gm = 2;
            } else if ("BOLD_ITALIC".equals(string4)) {
                this.Gm = 3;
            }
        }
        this.Fl = jSONObject.optJSONObject("customData");
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TextTrackStyle)) {
            return false;
        }
        TextTrackStyle textTrackStyle = (TextTrackStyle) other;
        if ((this.Fl == null) != (textTrackStyle.Fl == null)) {
            return false;
        }
        if (this.Fl == null || textTrackStyle.Fl == null || jz.d(this.Fl, textTrackStyle.Fl)) {
            return this.Gd == textTrackStyle.Gd && this.Ge == textTrackStyle.Ge && this.xm == textTrackStyle.xm && this.Gf == textTrackStyle.Gf && this.Gg == textTrackStyle.Gg && this.Gh == textTrackStyle.Gh && this.Gj == textTrackStyle.Gj && ik.a(this.Gk, textTrackStyle.Gk) && this.Gl == textTrackStyle.Gl && this.Gm == textTrackStyle.Gm;
        }
        return false;
    }

    public int getBackgroundColor() {
        return this.xm;
    }

    public JSONObject getCustomData() {
        return this.Fl;
    }

    public int getEdgeColor() {
        return this.Gg;
    }

    public int getEdgeType() {
        return this.Gf;
    }

    public String getFontFamily() {
        return this.Gk;
    }

    public int getFontGenericFamily() {
        return this.Gl;
    }

    public float getFontScale() {
        return this.Gd;
    }

    public int getFontStyle() {
        return this.Gm;
    }

    public int getForegroundColor() {
        return this.Ge;
    }

    public int getWindowColor() {
        return this.Gi;
    }

    public int getWindowCornerRadius() {
        return this.Gj;
    }

    public int getWindowType() {
        return this.Gh;
    }

    public int hashCode() {
        return m.hashCode(Float.valueOf(this.Gd), Integer.valueOf(this.Ge), Integer.valueOf(this.xm), Integer.valueOf(this.Gf), Integer.valueOf(this.Gg), Integer.valueOf(this.Gh), Integer.valueOf(this.Gi), Integer.valueOf(this.Gj), this.Gk, Integer.valueOf(this.Gl), Integer.valueOf(this.Gm), this.Fl);
    }

    public void setBackgroundColor(int backgroundColor) {
        this.xm = backgroundColor;
    }

    public void setCustomData(JSONObject customData) {
        this.Fl = customData;
    }

    public void setEdgeColor(int edgeColor) {
        this.Gg = edgeColor;
    }

    public void setEdgeType(int edgeType) {
        if (edgeType < 0 || edgeType > 4) {
            throw new IllegalArgumentException("invalid edgeType");
        }
        this.Gf = edgeType;
    }

    public void setFontFamily(String fontFamily) {
        this.Gk = fontFamily;
    }

    public void setFontGenericFamily(int fontGenericFamily) {
        if (fontGenericFamily < 0 || fontGenericFamily > 6) {
            throw new IllegalArgumentException("invalid fontGenericFamily");
        }
        this.Gl = fontGenericFamily;
    }

    public void setFontScale(float fontScale) {
        this.Gd = fontScale;
    }

    public void setFontStyle(int fontStyle) {
        if (fontStyle < 0 || fontStyle > 3) {
            throw new IllegalArgumentException("invalid fontStyle");
        }
        this.Gm = fontStyle;
    }

    public void setForegroundColor(int foregroundColor) {
        this.Ge = foregroundColor;
    }

    public void setWindowColor(int windowColor) {
        this.Gi = windowColor;
    }

    public void setWindowCornerRadius(int windowCornerRadius) {
        if (windowCornerRadius < 0) {
            throw new IllegalArgumentException("invalid windowCornerRadius");
        }
        this.Gj = windowCornerRadius;
    }

    public void setWindowType(int windowType) {
        if (windowType < 0 || windowType > 2) {
            throw new IllegalArgumentException("invalid windowType");
        }
        this.Gh = windowType;
    }
}
