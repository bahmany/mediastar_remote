package javax.mail.internet;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class MailDateFormat extends SimpleDateFormat {
    private static final long serialVersionUID = -8148227605210628779L;
    static boolean debug = false;
    private static TimeZone tz = TimeZone.getTimeZone("GMT");
    private static Calendar cal = new GregorianCalendar(tz);

    public MailDateFormat() {
        super("EEE, d MMM yyyy HH:mm:ss 'XXXXX' (z)", Locale.US);
    }

    @Override // java.text.SimpleDateFormat, java.text.DateFormat
    public StringBuffer format(Date date, StringBuffer dateStrBuf, FieldPosition fieldPosition) {
        int pos;
        int start = dateStrBuf.length();
        super.format(date, dateStrBuf, fieldPosition);
        int pos2 = start + 25;
        while (dateStrBuf.charAt(pos2) != 'X') {
            pos2++;
        }
        this.calendar.clear();
        this.calendar.setTime(date);
        int offset = this.calendar.get(15) + this.calendar.get(16);
        if (offset < 0) {
            dateStrBuf.setCharAt(pos2, '-');
            offset = -offset;
            pos = pos2 + 1;
        } else {
            dateStrBuf.setCharAt(pos2, '+');
            pos = pos2 + 1;
        }
        int rawOffsetInMins = (offset / 60) / 1000;
        int offsetInHrs = rawOffsetInMins / 60;
        int offsetInMins = rawOffsetInMins % 60;
        int pos3 = pos + 1;
        dateStrBuf.setCharAt(pos, Character.forDigit(offsetInHrs / 10, 10));
        int pos4 = pos3 + 1;
        dateStrBuf.setCharAt(pos3, Character.forDigit(offsetInHrs % 10, 10));
        int pos5 = pos4 + 1;
        dateStrBuf.setCharAt(pos4, Character.forDigit(offsetInMins / 10, 10));
        int i = pos5 + 1;
        dateStrBuf.setCharAt(pos5, Character.forDigit(offsetInMins % 10, 10));
        return dateStrBuf;
    }

    @Override // java.text.SimpleDateFormat, java.text.DateFormat
    public Date parse(String text, ParsePosition pos) {
        return parseDate(text.toCharArray(), pos, isLenient());
    }

    private static Date parseDate(char[] orig, ParsePosition pos, boolean lenient) {
        int seconds = 0;
        int offset = 0;
        try {
            MailDateParser p = new MailDateParser(orig);
            p.skipUntilNumber();
            int day = p.parseNumber();
            if (!p.skipIfChar('-')) {
                p.skipWhiteSpace();
            }
            int month = p.parseMonth();
            if (!p.skipIfChar('-')) {
                p.skipWhiteSpace();
            }
            int year = p.parseNumber();
            if (year < 50) {
                year += 2000;
            } else if (year < 100) {
                year += 1900;
            }
            p.skipWhiteSpace();
            int hours = p.parseNumber();
            p.skipChar(':');
            int minutes = p.parseNumber();
            if (p.skipIfChar(':')) {
                seconds = p.parseNumber();
            }
            try {
                p.skipWhiteSpace();
                offset = p.parseTimeZone();
            } catch (java.text.ParseException e) {
                if (debug) {
                    System.out.println("No timezone? : '" + new String(orig) + "'");
                }
            }
            pos.setIndex(p.getIndex());
            return ourUTC(year, month, day, hours, minutes, seconds, offset, lenient);
        } catch (Exception e2) {
            if (debug) {
                System.out.println("Bad date: '" + new String(orig) + "'");
                e2.printStackTrace();
            }
            pos.setIndex(1);
            return null;
        }
    }

    private static synchronized Date ourUTC(int year, int mon, int mday, int hour, int min, int sec, int tzoffset, boolean lenient) {
        cal.clear();
        cal.setLenient(lenient);
        cal.set(1, year);
        cal.set(2, mon);
        cal.set(5, mday);
        cal.set(11, hour);
        cal.set(12, min + tzoffset);
        cal.set(13, sec);
        return cal.getTime();
    }

    @Override // java.text.DateFormat
    public void setCalendar(Calendar newCalendar) {
        throw new RuntimeException("Method setCalendar() shouldn't be called");
    }

    @Override // java.text.DateFormat
    public void setNumberFormat(NumberFormat newNumberFormat) {
        throw new RuntimeException("Method setNumberFormat() shouldn't be called");
    }
}
