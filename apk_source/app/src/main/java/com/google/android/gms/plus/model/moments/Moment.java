package com.google.android.gms.plus.model.moments;

import com.google.android.gms.common.data.Freezable;
import com.google.android.gms.internal.nt;
import com.google.android.gms.internal.nv;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes.dex */
public interface Moment extends Freezable<Moment> {

    public static class Builder {
        private String BL;
        private final Set<Integer> alR = new HashSet();
        private String amE;
        private nt amM;
        private nt amN;
        private String uO;

        public Moment build() {
            return new nv(this.alR, this.BL, this.amM, this.amE, this.amN, this.uO);
        }

        public Builder setId(String id) {
            this.BL = id;
            this.alR.add(2);
            return this;
        }

        public Builder setResult(ItemScope result) {
            this.amM = (nt) result;
            this.alR.add(4);
            return this;
        }

        public Builder setStartDate(String startDate) {
            this.amE = startDate;
            this.alR.add(5);
            return this;
        }

        public Builder setTarget(ItemScope target) {
            this.amN = (nt) target;
            this.alR.add(6);
            return this;
        }

        public Builder setType(String type) {
            this.uO = type;
            this.alR.add(7);
            return this;
        }
    }

    String getId();

    ItemScope getResult();

    String getStartDate();

    ItemScope getTarget();

    String getType();

    boolean hasId();

    boolean hasResult();

    boolean hasStartDate();

    boolean hasTarget();

    boolean hasType();
}
