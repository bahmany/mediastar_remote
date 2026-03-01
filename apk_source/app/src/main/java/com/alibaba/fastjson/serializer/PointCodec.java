package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import java.awt.Point;
import java.io.IOException;
import java.lang.reflect.Type;

/* loaded from: classes.dex */
public class PointCodec implements ObjectSerializer, ObjectDeserializer {
    public static final PointCodec instance = new PointCodec();

    @Override // com.alibaba.fastjson.serializer.ObjectSerializer
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.getWriter();
        Point font = (Point) object;
        if (font == null) {
            out.writeNull();
            return;
        }
        char sep = '{';
        if (out.isEnabled(SerializerFeature.WriteClassName)) {
            out.write('{');
            out.writeFieldName(JSON.DEFAULT_TYPE_KEY);
            out.writeString(Point.class.getName());
            sep = ',';
        }
        out.writeFieldValue(sep, "x", font.getX());
        out.writeFieldValue(',', "y", font.getY());
        out.write('}');
    }

    @Override // com.alibaba.fastjson.parser.deserializer.ObjectDeserializer
    public <T> T deserialze(DefaultJSONParser defaultJSONParser, Type type, Object obj) {
        JSONLexer lexer = defaultJSONParser.getLexer();
        if (lexer.token() == 8) {
            lexer.nextToken(16);
            return null;
        }
        if (lexer.token() != 12 && lexer.token() != 16) {
            throw new JSONException("syntax error");
        }
        lexer.nextToken();
        int i = 0;
        int i2 = 0;
        while (lexer.token() != 13) {
            if (lexer.token() == 4) {
                String strStringVal = lexer.stringVal();
                if (JSON.DEFAULT_TYPE_KEY.equals(strStringVal)) {
                    defaultJSONParser.acceptType("java.awt.Point");
                } else {
                    lexer.nextTokenWithColon(2);
                    if (lexer.token() == 2) {
                        int iIntValue = lexer.intValue();
                        lexer.nextToken();
                        if (strStringVal.equalsIgnoreCase("x")) {
                            i = iIntValue;
                        } else if (strStringVal.equalsIgnoreCase("y")) {
                            i2 = iIntValue;
                        } else {
                            throw new JSONException("syntax error, " + strStringVal);
                        }
                        if (lexer.token() == 16) {
                            lexer.nextToken(4);
                        }
                    } else {
                        throw new JSONException("syntax error : " + lexer.tokenName());
                    }
                }
            } else {
                throw new JSONException("syntax error");
            }
        }
        lexer.nextToken();
        return (T) new Point(i, i2);
    }

    @Override // com.alibaba.fastjson.parser.deserializer.ObjectDeserializer
    public int getFastMatchToken() {
        return 12;
    }
}
