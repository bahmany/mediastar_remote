package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import java.awt.Color;
import java.io.IOException;
import java.lang.reflect.Type;

/* loaded from: classes.dex */
public class ColorCodec implements ObjectSerializer, ObjectDeserializer {
    public static final ColorCodec instance = new ColorCodec();

    @Override // com.alibaba.fastjson.serializer.ObjectSerializer
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.getWriter();
        Color color = (Color) object;
        if (color == null) {
            out.writeNull();
            return;
        }
        char sep = '{';
        if (out.isEnabled(SerializerFeature.WriteClassName)) {
            out.write('{');
            out.writeFieldName(JSON.DEFAULT_TYPE_KEY);
            out.writeString(Color.class.getName());
            sep = ',';
        }
        out.writeFieldValue(sep, "r", color.getRed());
        out.writeFieldValue(',', "g", color.getGreen());
        out.writeFieldValue(',', "b", color.getBlue());
        if (color.getAlpha() > 0) {
            out.writeFieldValue(',', "alpha", color.getAlpha());
        }
        out.write('}');
    }

    @Override // com.alibaba.fastjson.parser.deserializer.ObjectDeserializer
    public <T> T deserialze(DefaultJSONParser defaultJSONParser, Type type, Object obj) {
        JSONLexer lexer = defaultJSONParser.getLexer();
        if (lexer.token() != 12 && lexer.token() != 16) {
            throw new JSONException("syntax error");
        }
        lexer.nextToken();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (lexer.token() != 13) {
            if (lexer.token() == 4) {
                String strStringVal = lexer.stringVal();
                lexer.nextTokenWithColon(2);
                if (lexer.token() == 2) {
                    int iIntValue = lexer.intValue();
                    lexer.nextToken();
                    if (strStringVal.equalsIgnoreCase("r")) {
                        i = iIntValue;
                    } else if (strStringVal.equalsIgnoreCase("g")) {
                        i2 = iIntValue;
                    } else if (strStringVal.equalsIgnoreCase("b")) {
                        i3 = iIntValue;
                    } else if (strStringVal.equalsIgnoreCase("alpha")) {
                        i4 = iIntValue;
                    } else {
                        throw new JSONException("syntax error, " + strStringVal);
                    }
                    if (lexer.token() == 16) {
                        lexer.nextToken(4);
                    }
                } else {
                    throw new JSONException("syntax error");
                }
            } else {
                throw new JSONException("syntax error");
            }
        }
        lexer.nextToken();
        return (T) new Color(i, i2, i3, i4);
    }

    @Override // com.alibaba.fastjson.parser.deserializer.ObjectDeserializer
    public int getFastMatchToken() {
        return 12;
    }
}
