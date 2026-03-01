package com.alibaba.fastjson.support.odps.udf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.aliyun.odps.udf.OdpsType;
import com.aliyun.odps.udf.UDFException;
import com.aliyun.odps.udf.UDTF;
import java.util.Arrays;

/* loaded from: classes.dex */
public class JSONTuple extends UDTF {
    public OdpsType[] initialize(OdpsType[] signature) throws Exception {
        OdpsType[] types = new OdpsType[signature.length - 1];
        Arrays.fill(types, OdpsType.STRING);
        return types;
    }

    public void process(Object[] args) throws UDFException {
        String jsonStr = (String) args[0];
        Object object = JSON.parse(jsonStr);
        Object[] values = new Object[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            String path = (String) args[i];
            Object value = JSONPath.eval(object, path);
            values[i - 1] = JSON.toJSONString(value);
        }
        forward(values);
    }
}
