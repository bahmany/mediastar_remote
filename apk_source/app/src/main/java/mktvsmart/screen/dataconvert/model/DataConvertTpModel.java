package mktvsmart.screen.dataconvert.model;

/* loaded from: classes.dex */
public class DataConvertTpModel {
    private int fec;
    private int freq;
    private char pol;
    private int satIndex;
    private int symRate;
    private int tpIndex;

    public int getTpIndex() {
        return this.tpIndex;
    }

    public void setTpIndex(int tpIndex) {
        this.tpIndex = tpIndex;
    }

    public int getSatIndex() {
        return this.satIndex;
    }

    public void setSatIndex(int satIndex) {
        this.satIndex = satIndex;
    }

    public int getFreq() {
        return this.freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public char getPol() {
        return this.pol;
    }

    public void setPol(char pol) {
        this.pol = pol;
    }

    public int getSymRate() {
        return this.symRate;
    }

    public void setSymRate(int symRate) {
        this.symRate = symRate;
    }

    public int getFec() {
        return this.fec;
    }

    public void setFec(int fec) {
        this.fec = fec;
    }
}
