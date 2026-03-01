package master.flame.danmaku.danmaku.parser.android;

import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Locale;
import master.flame.danmaku.danmaku.model.AlphaValue;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.Duration;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/* loaded from: classes.dex */
public class BiliDanmukuParser extends BaseDanmakuParser {
    private float mDispScaleX;
    private float mDispScaleY;

    static {
        System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
    }

    @Override // master.flame.danmaku.danmaku.parser.BaseDanmakuParser
    public Danmakus parse() throws SAXException, IOException {
        if (this.mDataSource != null) {
            AndroidFileSource source = (AndroidFileSource) this.mDataSource;
            try {
                XMLReader xmlReader = XMLReaderFactory.createXMLReader();
                XmlContentHandler contentHandler = new XmlContentHandler();
                xmlReader.setContentHandler(contentHandler);
                xmlReader.parse(new InputSource(source.data()));
                return contentHandler.getResult();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }

    public class XmlContentHandler extends DefaultHandler {
        private static final String TRUE_STRING = "true";
        public Danmakus result = null;
        public BaseDanmaku item = null;
        public boolean completed = false;
        public int index = 0;

        public XmlContentHandler() {
        }

        public Danmakus getResult() {
            return this.result;
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startDocument() throws SAXException {
            this.result = new Danmakus();
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void endDocument() throws SAXException {
            this.completed = true;
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException, NumberFormatException {
            String tagName = localName.length() != 0 ? localName : qName;
            if (tagName.toLowerCase(Locale.getDefault()).trim().equals("d")) {
                String pValue = attributes.getValue("p");
                String[] values = pValue.split(ClientInfo.SEPARATOR_BETWEEN_VARS);
                if (values.length > 0) {
                    long time = (long) (Float.parseFloat(values[0]) * 1000.0f);
                    int type = Integer.parseInt(values[1]);
                    float textSize = Float.parseFloat(values[2]);
                    int color = Integer.parseInt(values[3]) | ViewCompat.MEASURED_STATE_MASK;
                    this.item = DanmakuFactory.createDanmaku(type, BiliDanmukuParser.this.mDisp);
                    if (this.item != null) {
                        this.item.time = time;
                        this.item.textSize = (BiliDanmukuParser.this.mDispDensity - 0.6f) * textSize;
                        this.item.textColor = color;
                        this.item.textShadowColor = color <= -16777216 ? -1 : ViewCompat.MEASURED_STATE_MASK;
                    }
                }
            }
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (this.item != null) {
                if (this.item.duration != null) {
                    String tagName = localName.length() != 0 ? localName : qName;
                    if (tagName.equalsIgnoreCase("d")) {
                        this.item.setTimer(BiliDanmukuParser.this.mTimer);
                        this.result.addItem(this.item);
                    }
                }
                this.item = null;
            }
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void characters(char[] ch, int start, int length) throws NumberFormatException {
            if (this.item != null) {
                DanmakuFactory.fillText(this.item, decodeXmlString(new String(ch, start, length)));
                BaseDanmaku baseDanmaku = this.item;
                int i = this.index;
                this.index = i + 1;
                baseDanmaku.index = i;
                String text = this.item.text.trim();
                if (this.item.getType() == 7 && text.startsWith("[") && text.endsWith("]")) {
                    String[] textArr = null;
                    try {
                        JSONArray jsonArray = new JSONArray(text);
                        textArr = new String[jsonArray.length()];
                        for (int i2 = 0; i2 < textArr.length; i2++) {
                            textArr[i2] = jsonArray.getString(i2);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (textArr == null || textArr.length < 5) {
                        this.item = null;
                        return;
                    }
                    this.item.text = textArr[4];
                    float beginX = Float.parseFloat(textArr[0]);
                    float beginY = Float.parseFloat(textArr[1]);
                    float endX = beginX;
                    float endY = beginY;
                    String[] alphaArr = textArr[2].split("-");
                    int beginAlpha = (int) (AlphaValue.MAX * Float.parseFloat(alphaArr[0]));
                    int endAlpha = beginAlpha;
                    if (alphaArr.length > 1) {
                        endAlpha = (int) (AlphaValue.MAX * Float.parseFloat(alphaArr[1]));
                    }
                    long alphaDuraion = (long) (Float.parseFloat(textArr[3]) * 1000.0f);
                    long translationDuration = alphaDuraion;
                    long translationStartDelay = 0;
                    float rotateY = 0.0f;
                    float rotateZ = 0.0f;
                    if (textArr.length >= 7) {
                        rotateZ = Float.parseFloat(textArr[5]);
                        rotateY = Float.parseFloat(textArr[6]);
                    }
                    if (textArr.length >= 11) {
                        endX = Float.parseFloat(textArr[7]);
                        endY = Float.parseFloat(textArr[8]);
                        if (!"".equals(textArr[9])) {
                            translationDuration = Integer.parseInt(textArr[9]);
                        }
                        if (!"".equals(textArr[10])) {
                            translationStartDelay = (long) Float.parseFloat(textArr[10]);
                        }
                    }
                    this.item.duration = new Duration(alphaDuraion);
                    this.item.rotationZ = rotateZ;
                    this.item.rotationY = rotateY;
                    DanmakuFactory.fillTranslationData(this.item, beginX, beginY, endX, endY, translationDuration, translationStartDelay, BiliDanmukuParser.this.mDispScaleX, BiliDanmukuParser.this.mDispScaleY);
                    DanmakuFactory.fillAlphaData(this.item, beginAlpha, endAlpha, alphaDuraion);
                    if (textArr.length >= 12 && !TextUtils.isEmpty(textArr[11]) && TRUE_STRING.equals(textArr[11])) {
                        this.item.textShadowColor = 0;
                    }
                    int length2 = textArr.length;
                    int length3 = textArr.length;
                    if (textArr.length >= 15 && !"".equals(textArr[14])) {
                        String motionPathString = textArr[14].substring(1);
                        String[] pointStrArray = motionPathString.split("L");
                        if (pointStrArray != null && pointStrArray.length > 0) {
                            float[][] points = (float[][]) Array.newInstance((Class<?>) Float.TYPE, pointStrArray.length, 2);
                            for (int i3 = 0; i3 < pointStrArray.length; i3++) {
                                String[] pointArray = pointStrArray[i3].split(ClientInfo.SEPARATOR_BETWEEN_VARS);
                                points[i3][0] = Float.parseFloat(pointArray[0]);
                                points[i3][1] = Float.parseFloat(pointArray[1]);
                            }
                            DanmakuFactory.fillLinePathData(this.item, points, BiliDanmukuParser.this.mDispScaleX, BiliDanmukuParser.this.mDispScaleY);
                        }
                    }
                }
            }
        }

        private String decodeXmlString(String title) {
            if (title.contains("&amp;")) {
                title = title.replace("&amp;", "&");
            }
            if (title.contains("&quot;")) {
                title = title.replace("&quot;", "\"");
            }
            if (title.contains("&gt;")) {
                title = title.replace("&gt;", ">");
            }
            if (title.contains("&lt;")) {
                return title.replace("&lt;", "<");
            }
            return title;
        }
    }

    @Override // master.flame.danmaku.danmaku.parser.BaseDanmakuParser
    public BaseDanmakuParser setDisplayer(IDisplayer disp) {
        super.setDisplayer(disp);
        this.mDispScaleX = this.mDispWidth / 682.0f;
        this.mDispScaleY = this.mDispHeight / 438.0f;
        return this;
    }
}
