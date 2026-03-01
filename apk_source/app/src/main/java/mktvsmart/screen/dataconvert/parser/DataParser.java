package mktvsmart.screen.dataconvert.parser;

import java.io.InputStream;
import java.util.List;

/* loaded from: classes.dex */
public interface DataParser {
    List<?> parse(InputStream inputStream, int i) throws Exception;

    String serialize(List<?> list, int i) throws Exception;
}
