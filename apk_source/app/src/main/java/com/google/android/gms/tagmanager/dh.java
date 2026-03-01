package com.google.android.gms.tagmanager;

/* loaded from: classes.dex */
class dh extends Number implements Comparable<dh> {
    private double arG;
    private long arH;
    private boolean arI = false;

    private dh(double d) {
        this.arG = d;
    }

    private dh(long j) {
        this.arH = j;
    }

    public static dh a(Double d) {
        return new dh(d.doubleValue());
    }

    public static dh cT(String str) throws NumberFormatException {
        try {
            return new dh(Long.parseLong(str));
        } catch (NumberFormatException e) {
            try {
                return new dh(Double.parseDouble(str));
            } catch (NumberFormatException e2) {
                throw new NumberFormatException(str + " is not a valid TypedNumber");
            }
        }
    }

    public static dh z(long j) {
        return new dh(j);
    }

    @Override // java.lang.Comparable
    /* renamed from: a, reason: merged with bridge method [inline-methods] */
    public int compareTo(dh dhVar) {
        return (py() && dhVar.py()) ? new Long(this.arH).compareTo(Long.valueOf(dhVar.arH)) : Double.compare(doubleValue(), dhVar.doubleValue());
    }

    @Override // java.lang.Number
    public byte byteValue() {
        return (byte) longValue();
    }

    @Override // java.lang.Number
    public double doubleValue() {
        return py() ? this.arH : this.arG;
    }

    public boolean equals(Object other) {
        return (other instanceof dh) && compareTo((dh) other) == 0;
    }

    @Override // java.lang.Number
    public float floatValue() {
        return (float) doubleValue();
    }

    public int hashCode() {
        return new Long(longValue()).hashCode();
    }

    @Override // java.lang.Number
    public int intValue() {
        return pA();
    }

    @Override // java.lang.Number
    public long longValue() {
        return pz();
    }

    public int pA() {
        return (int) longValue();
    }

    public short pB() {
        return (short) longValue();
    }

    public boolean px() {
        return !py();
    }

    public boolean py() {
        return this.arI;
    }

    public long pz() {
        return py() ? this.arH : (long) this.arG;
    }

    @Override // java.lang.Number
    public short shortValue() {
        return pB();
    }

    public String toString() {
        return py() ? Long.toString(this.arH) : Double.toString(this.arG);
    }
}
