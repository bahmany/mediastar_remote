package com.google.android.gms.drive.query;

import com.google.android.gms.drive.metadata.MetadataField;
import com.google.android.gms.drive.query.internal.Operator;
import com.google.android.gms.drive.query.internal.f;
import com.hisilicon.multiscreen.protocol.ClientInfo;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class c implements f<String> {
    public <T> String a(com.google.android.gms.drive.metadata.b<T> bVar, T t) {
        return String.format("contains(%s,%s)", bVar.getName(), t);
    }

    @Override // com.google.android.gms.drive.query.internal.f
    /* renamed from: a, reason: merged with bridge method [inline-methods] */
    public <T> String b(Operator operator, MetadataField<T> metadataField, T t) {
        return String.format("cmp(%s,%s,%s)", operator.getTag(), metadataField.getName(), t);
    }

    @Override // com.google.android.gms.drive.query.internal.f
    /* renamed from: a, reason: merged with bridge method [inline-methods] */
    public String b(Operator operator, List<String> list) {
        StringBuilder sb = new StringBuilder(operator.getTag() + "(");
        String str = "";
        Iterator<String> it = list.iterator();
        while (true) {
            String str2 = str;
            if (!it.hasNext()) {
                return sb.append(")").toString();
            }
            String next = it.next();
            sb.append(str2);
            sb.append(next);
            str = ClientInfo.SEPARATOR_BETWEEN_VARS;
        }
    }

    @Override // com.google.android.gms.drive.query.internal.f
    public /* synthetic */ String b(com.google.android.gms.drive.metadata.b bVar, Object obj) {
        return a((com.google.android.gms.drive.metadata.b<com.google.android.gms.drive.metadata.b>) bVar, (com.google.android.gms.drive.metadata.b) obj);
    }

    @Override // com.google.android.gms.drive.query.internal.f
    /* renamed from: bn, reason: merged with bridge method [inline-methods] */
    public String j(String str) {
        return String.format("not(%s)", str);
    }

    @Override // com.google.android.gms.drive.query.internal.f
    /* renamed from: c, reason: merged with bridge method [inline-methods] */
    public String d(MetadataField<?> metadataField) {
        return String.format("fieldOnly(%s)", metadataField.getName());
    }

    @Override // com.google.android.gms.drive.query.internal.f
    /* renamed from: c, reason: merged with bridge method [inline-methods] */
    public <T> String d(MetadataField<T> metadataField, T t) {
        return String.format("has(%s,%s)", metadataField.getName(), t);
    }

    @Override // com.google.android.gms.drive.query.internal.f
    /* renamed from: ir, reason: merged with bridge method [inline-methods] */
    public String is() {
        return "all()";
    }
}
