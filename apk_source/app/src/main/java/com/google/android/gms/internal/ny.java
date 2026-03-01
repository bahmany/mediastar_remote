package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.internal.ji;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.people.Person;
import com.hisilicon.dlna.dmc.data.PlaylistSQLiteHelper;
import com.iflytek.cloud.SpeechConstant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public final class ny extends jj implements Person {
    public static final nz CREATOR = new nz();
    private static final HashMap<String, ji.a<?, ?>> alQ = new HashMap<>();
    String BL;
    final int BR;
    String Fc;
    String Nz;
    final Set<Integer> alR;
    String amP;
    a amQ;
    String amR;
    String amS;
    int amT;
    b amU;
    String amV;
    c amW;
    boolean amX;
    d amY;
    String amZ;
    int ana;
    List<f> anb;
    List<g> anc;
    int and;
    int ane;
    String anf;
    List<h> ang;
    boolean anh;
    int om;
    String uR;

    public static final class a extends jj implements Person.AgeRange {
        public static final oa CREATOR = new oa();
        private static final HashMap<String, ji.a<?, ?>> alQ = new HashMap<>();
        final int BR;
        final Set<Integer> alR;
        int ani;
        int anj;

        static {
            alQ.put("max", ji.a.i("max", 2));
            alQ.put("min", ji.a.i("min", 3));
        }

        public a() {
            this.BR = 1;
            this.alR = new HashSet();
        }

        a(Set<Integer> set, int i, int i2, int i3) {
            this.alR = set;
            this.BR = i;
            this.ani = i2;
            this.anj = i3;
        }

        @Override // com.google.android.gms.internal.ji
        protected boolean a(ji.a aVar) {
            return this.alR.contains(Integer.valueOf(aVar.hm()));
        }

        @Override // com.google.android.gms.internal.ji
        protected Object b(ji.a aVar) {
            switch (aVar.hm()) {
                case 2:
                    return Integer.valueOf(this.ani);
                case 3:
                    return Integer.valueOf(this.anj);
                default:
                    throw new IllegalStateException("Unknown safe parcelable id=" + aVar.hm());
            }
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            oa oaVar = CREATOR;
            return 0;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof a)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            a aVar = (a) obj;
            for (ji.a<?, ?> aVar2 : alQ.values()) {
                if (a(aVar2)) {
                    if (aVar.a(aVar2) && b(aVar2).equals(aVar.b(aVar2))) {
                    }
                    return false;
                }
                if (aVar.a(aVar2)) {
                    return false;
                }
            }
            return true;
        }

        @Override // com.google.android.gms.plus.model.people.Person.AgeRange
        public int getMax() {
            return this.ani;
        }

        @Override // com.google.android.gms.plus.model.people.Person.AgeRange
        public int getMin() {
            return this.anj;
        }

        @Override // com.google.android.gms.plus.model.people.Person.AgeRange
        public boolean hasMax() {
            return this.alR.contains(2);
        }

        @Override // com.google.android.gms.plus.model.people.Person.AgeRange
        public boolean hasMin() {
            return this.alR.contains(3);
        }

        public int hashCode() {
            int iHashCode = 0;
            Iterator<ji.a<?, ?>> it = alQ.values().iterator();
            while (true) {
                int i = iHashCode;
                if (!it.hasNext()) {
                    return i;
                }
                ji.a<?, ?> next = it.next();
                if (a(next)) {
                    iHashCode = b(next).hashCode() + i + next.hm();
                } else {
                    iHashCode = i;
                }
            }
        }

        @Override // com.google.android.gms.internal.ji
        public HashMap<String, ji.a<?, ?>> hf() {
            return alQ;
        }

        @Override // com.google.android.gms.common.data.Freezable
        public boolean isDataValid() {
            return true;
        }

        @Override // com.google.android.gms.common.data.Freezable
        /* renamed from: nt, reason: merged with bridge method [inline-methods] */
        public a freeze() {
            return this;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            oa oaVar = CREATOR;
            oa.a(this, out, flags);
        }
    }

    public static final class b extends jj implements Person.Cover {
        public static final ob CREATOR = new ob();
        private static final HashMap<String, ji.a<?, ?>> alQ = new HashMap<>();
        final int BR;
        final Set<Integer> alR;
        a ank;
        C0085b anl;
        int anm;

        public static final class a extends jj implements Person.Cover.CoverInfo {
            public static final oc CREATOR = new oc();
            private static final HashMap<String, ji.a<?, ?>> alQ = new HashMap<>();
            final int BR;
            final Set<Integer> alR;
            int ann;
            int ano;

            static {
                alQ.put("leftImageOffset", ji.a.i("leftImageOffset", 2));
                alQ.put("topImageOffset", ji.a.i("topImageOffset", 3));
            }

            public a() {
                this.BR = 1;
                this.alR = new HashSet();
            }

            a(Set<Integer> set, int i, int i2, int i3) {
                this.alR = set;
                this.BR = i;
                this.ann = i2;
                this.ano = i3;
            }

            @Override // com.google.android.gms.internal.ji
            protected boolean a(ji.a aVar) {
                return this.alR.contains(Integer.valueOf(aVar.hm()));
            }

            @Override // com.google.android.gms.internal.ji
            protected Object b(ji.a aVar) {
                switch (aVar.hm()) {
                    case 2:
                        return Integer.valueOf(this.ann);
                    case 3:
                        return Integer.valueOf(this.ano);
                    default:
                        throw new IllegalStateException("Unknown safe parcelable id=" + aVar.hm());
                }
            }

            @Override // android.os.Parcelable
            public int describeContents() {
                oc ocVar = CREATOR;
                return 0;
            }

            public boolean equals(Object obj) {
                if (!(obj instanceof a)) {
                    return false;
                }
                if (this == obj) {
                    return true;
                }
                a aVar = (a) obj;
                for (ji.a<?, ?> aVar2 : alQ.values()) {
                    if (a(aVar2)) {
                        if (aVar.a(aVar2) && b(aVar2).equals(aVar.b(aVar2))) {
                        }
                        return false;
                    }
                    if (aVar.a(aVar2)) {
                        return false;
                    }
                }
                return true;
            }

            @Override // com.google.android.gms.plus.model.people.Person.Cover.CoverInfo
            public int getLeftImageOffset() {
                return this.ann;
            }

            @Override // com.google.android.gms.plus.model.people.Person.Cover.CoverInfo
            public int getTopImageOffset() {
                return this.ano;
            }

            @Override // com.google.android.gms.plus.model.people.Person.Cover.CoverInfo
            public boolean hasLeftImageOffset() {
                return this.alR.contains(2);
            }

            @Override // com.google.android.gms.plus.model.people.Person.Cover.CoverInfo
            public boolean hasTopImageOffset() {
                return this.alR.contains(3);
            }

            public int hashCode() {
                int iHashCode = 0;
                Iterator<ji.a<?, ?>> it = alQ.values().iterator();
                while (true) {
                    int i = iHashCode;
                    if (!it.hasNext()) {
                        return i;
                    }
                    ji.a<?, ?> next = it.next();
                    if (a(next)) {
                        iHashCode = b(next).hashCode() + i + next.hm();
                    } else {
                        iHashCode = i;
                    }
                }
            }

            @Override // com.google.android.gms.internal.ji
            public HashMap<String, ji.a<?, ?>> hf() {
                return alQ;
            }

            @Override // com.google.android.gms.common.data.Freezable
            public boolean isDataValid() {
                return true;
            }

            @Override // com.google.android.gms.common.data.Freezable
            /* renamed from: nv, reason: merged with bridge method [inline-methods] */
            public a freeze() {
                return this;
            }

            @Override // android.os.Parcelable
            public void writeToParcel(Parcel out, int flags) {
                oc ocVar = CREATOR;
                oc.a(this, out, flags);
            }
        }

        /* renamed from: com.google.android.gms.internal.ny$b$b, reason: collision with other inner class name */
        public static final class C0085b extends jj implements Person.Cover.CoverPhoto {
            public static final od CREATOR = new od();
            private static final HashMap<String, ji.a<?, ?>> alQ = new HashMap<>();
            final int BR;
            final Set<Integer> alR;
            int lf;
            int lg;
            String uR;

            static {
                alQ.put("height", ji.a.i("height", 2));
                alQ.put("url", ji.a.l("url", 3));
                alQ.put("width", ji.a.i("width", 4));
            }

            public C0085b() {
                this.BR = 1;
                this.alR = new HashSet();
            }

            C0085b(Set<Integer> set, int i, int i2, String str, int i3) {
                this.alR = set;
                this.BR = i;
                this.lg = i2;
                this.uR = str;
                this.lf = i3;
            }

            @Override // com.google.android.gms.internal.ji
            protected boolean a(ji.a aVar) {
                return this.alR.contains(Integer.valueOf(aVar.hm()));
            }

            @Override // com.google.android.gms.internal.ji
            protected Object b(ji.a aVar) {
                switch (aVar.hm()) {
                    case 2:
                        return Integer.valueOf(this.lg);
                    case 3:
                        return this.uR;
                    case 4:
                        return Integer.valueOf(this.lf);
                    default:
                        throw new IllegalStateException("Unknown safe parcelable id=" + aVar.hm());
                }
            }

            @Override // android.os.Parcelable
            public int describeContents() {
                od odVar = CREATOR;
                return 0;
            }

            public boolean equals(Object obj) {
                if (!(obj instanceof C0085b)) {
                    return false;
                }
                if (this == obj) {
                    return true;
                }
                C0085b c0085b = (C0085b) obj;
                for (ji.a<?, ?> aVar : alQ.values()) {
                    if (a(aVar)) {
                        if (c0085b.a(aVar) && b(aVar).equals(c0085b.b(aVar))) {
                        }
                        return false;
                    }
                    if (c0085b.a(aVar)) {
                        return false;
                    }
                }
                return true;
            }

            @Override // com.google.android.gms.plus.model.people.Person.Cover.CoverPhoto
            public int getHeight() {
                return this.lg;
            }

            @Override // com.google.android.gms.plus.model.people.Person.Cover.CoverPhoto
            public String getUrl() {
                return this.uR;
            }

            @Override // com.google.android.gms.plus.model.people.Person.Cover.CoverPhoto
            public int getWidth() {
                return this.lf;
            }

            @Override // com.google.android.gms.plus.model.people.Person.Cover.CoverPhoto
            public boolean hasHeight() {
                return this.alR.contains(2);
            }

            @Override // com.google.android.gms.plus.model.people.Person.Cover.CoverPhoto
            public boolean hasUrl() {
                return this.alR.contains(3);
            }

            @Override // com.google.android.gms.plus.model.people.Person.Cover.CoverPhoto
            public boolean hasWidth() {
                return this.alR.contains(4);
            }

            public int hashCode() {
                int iHashCode = 0;
                Iterator<ji.a<?, ?>> it = alQ.values().iterator();
                while (true) {
                    int i = iHashCode;
                    if (!it.hasNext()) {
                        return i;
                    }
                    ji.a<?, ?> next = it.next();
                    if (a(next)) {
                        iHashCode = b(next).hashCode() + i + next.hm();
                    } else {
                        iHashCode = i;
                    }
                }
            }

            @Override // com.google.android.gms.internal.ji
            public HashMap<String, ji.a<?, ?>> hf() {
                return alQ;
            }

            @Override // com.google.android.gms.common.data.Freezable
            public boolean isDataValid() {
                return true;
            }

            @Override // com.google.android.gms.common.data.Freezable
            /* renamed from: nw, reason: merged with bridge method [inline-methods] */
            public C0085b freeze() {
                return this;
            }

            @Override // android.os.Parcelable
            public void writeToParcel(Parcel out, int flags) {
                od odVar = CREATOR;
                od.a(this, out, flags);
            }
        }

        static {
            alQ.put("coverInfo", ji.a.a("coverInfo", 2, a.class));
            alQ.put("coverPhoto", ji.a.a("coverPhoto", 3, C0085b.class));
            alQ.put("layout", ji.a.a("layout", 4, new jf().h("banner", 0), false));
        }

        public b() {
            this.BR = 1;
            this.alR = new HashSet();
        }

        b(Set<Integer> set, int i, a aVar, C0085b c0085b, int i2) {
            this.alR = set;
            this.BR = i;
            this.ank = aVar;
            this.anl = c0085b;
            this.anm = i2;
        }

        @Override // com.google.android.gms.internal.ji
        protected boolean a(ji.a aVar) {
            return this.alR.contains(Integer.valueOf(aVar.hm()));
        }

        @Override // com.google.android.gms.internal.ji
        protected Object b(ji.a aVar) {
            switch (aVar.hm()) {
                case 2:
                    return this.ank;
                case 3:
                    return this.anl;
                case 4:
                    return Integer.valueOf(this.anm);
                default:
                    throw new IllegalStateException("Unknown safe parcelable id=" + aVar.hm());
            }
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            ob obVar = CREATOR;
            return 0;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof b)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            b bVar = (b) obj;
            for (ji.a<?, ?> aVar : alQ.values()) {
                if (a(aVar)) {
                    if (bVar.a(aVar) && b(aVar).equals(bVar.b(aVar))) {
                    }
                    return false;
                }
                if (bVar.a(aVar)) {
                    return false;
                }
            }
            return true;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Cover
        public Person.Cover.CoverInfo getCoverInfo() {
            return this.ank;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Cover
        public Person.Cover.CoverPhoto getCoverPhoto() {
            return this.anl;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Cover
        public int getLayout() {
            return this.anm;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Cover
        public boolean hasCoverInfo() {
            return this.alR.contains(2);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Cover
        public boolean hasCoverPhoto() {
            return this.alR.contains(3);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Cover
        public boolean hasLayout() {
            return this.alR.contains(4);
        }

        public int hashCode() {
            int iHashCode = 0;
            Iterator<ji.a<?, ?>> it = alQ.values().iterator();
            while (true) {
                int i = iHashCode;
                if (!it.hasNext()) {
                    return i;
                }
                ji.a<?, ?> next = it.next();
                if (a(next)) {
                    iHashCode = b(next).hashCode() + i + next.hm();
                } else {
                    iHashCode = i;
                }
            }
        }

        @Override // com.google.android.gms.internal.ji
        public HashMap<String, ji.a<?, ?>> hf() {
            return alQ;
        }

        @Override // com.google.android.gms.common.data.Freezable
        public boolean isDataValid() {
            return true;
        }

        @Override // com.google.android.gms.common.data.Freezable
        /* renamed from: nu, reason: merged with bridge method [inline-methods] */
        public b freeze() {
            return this;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            ob obVar = CREATOR;
            ob.a(this, out, flags);
        }
    }

    public static final class c extends jj implements Person.Image {
        public static final oe CREATOR = new oe();
        private static final HashMap<String, ji.a<?, ?>> alQ = new HashMap<>();
        final int BR;
        final Set<Integer> alR;
        String uR;

        static {
            alQ.put("url", ji.a.l("url", 2));
        }

        public c() {
            this.BR = 1;
            this.alR = new HashSet();
        }

        public c(String str) {
            this.alR = new HashSet();
            this.BR = 1;
            this.uR = str;
            this.alR.add(2);
        }

        c(Set<Integer> set, int i, String str) {
            this.alR = set;
            this.BR = i;
            this.uR = str;
        }

        @Override // com.google.android.gms.internal.ji
        protected boolean a(ji.a aVar) {
            return this.alR.contains(Integer.valueOf(aVar.hm()));
        }

        @Override // com.google.android.gms.internal.ji
        protected Object b(ji.a aVar) {
            switch (aVar.hm()) {
                case 2:
                    return this.uR;
                default:
                    throw new IllegalStateException("Unknown safe parcelable id=" + aVar.hm());
            }
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            oe oeVar = CREATOR;
            return 0;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof c)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            c cVar = (c) obj;
            for (ji.a<?, ?> aVar : alQ.values()) {
                if (a(aVar)) {
                    if (cVar.a(aVar) && b(aVar).equals(cVar.b(aVar))) {
                    }
                    return false;
                }
                if (cVar.a(aVar)) {
                    return false;
                }
            }
            return true;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Image
        public String getUrl() {
            return this.uR;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Image
        public boolean hasUrl() {
            return this.alR.contains(2);
        }

        public int hashCode() {
            int iHashCode = 0;
            Iterator<ji.a<?, ?>> it = alQ.values().iterator();
            while (true) {
                int i = iHashCode;
                if (!it.hasNext()) {
                    return i;
                }
                ji.a<?, ?> next = it.next();
                if (a(next)) {
                    iHashCode = b(next).hashCode() + i + next.hm();
                } else {
                    iHashCode = i;
                }
            }
        }

        @Override // com.google.android.gms.internal.ji
        public HashMap<String, ji.a<?, ?>> hf() {
            return alQ;
        }

        @Override // com.google.android.gms.common.data.Freezable
        public boolean isDataValid() {
            return true;
        }

        @Override // com.google.android.gms.common.data.Freezable
        /* renamed from: nx, reason: merged with bridge method [inline-methods] */
        public c freeze() {
            return this;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            oe oeVar = CREATOR;
            oe.a(this, out, flags);
        }
    }

    public static final class d extends jj implements Person.Name {
        public static final of CREATOR = new of();
        private static final HashMap<String, ji.a<?, ?>> alQ = new HashMap<>();
        final int BR;
        final Set<Integer> alR;
        String amp;
        String ams;
        String anp;
        String anq;
        String anr;
        String ans;

        static {
            alQ.put("familyName", ji.a.l("familyName", 2));
            alQ.put("formatted", ji.a.l("formatted", 3));
            alQ.put("givenName", ji.a.l("givenName", 4));
            alQ.put("honorificPrefix", ji.a.l("honorificPrefix", 5));
            alQ.put("honorificSuffix", ji.a.l("honorificSuffix", 6));
            alQ.put("middleName", ji.a.l("middleName", 7));
        }

        public d() {
            this.BR = 1;
            this.alR = new HashSet();
        }

        d(Set<Integer> set, int i, String str, String str2, String str3, String str4, String str5, String str6) {
            this.alR = set;
            this.BR = i;
            this.amp = str;
            this.anp = str2;
            this.ams = str3;
            this.anq = str4;
            this.anr = str5;
            this.ans = str6;
        }

        @Override // com.google.android.gms.internal.ji
        protected boolean a(ji.a aVar) {
            return this.alR.contains(Integer.valueOf(aVar.hm()));
        }

        @Override // com.google.android.gms.internal.ji
        protected Object b(ji.a aVar) {
            switch (aVar.hm()) {
                case 2:
                    return this.amp;
                case 3:
                    return this.anp;
                case 4:
                    return this.ams;
                case 5:
                    return this.anq;
                case 6:
                    return this.anr;
                case 7:
                    return this.ans;
                default:
                    throw new IllegalStateException("Unknown safe parcelable id=" + aVar.hm());
            }
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            of ofVar = CREATOR;
            return 0;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof d)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            d dVar = (d) obj;
            for (ji.a<?, ?> aVar : alQ.values()) {
                if (a(aVar)) {
                    if (dVar.a(aVar) && b(aVar).equals(dVar.b(aVar))) {
                    }
                    return false;
                }
                if (dVar.a(aVar)) {
                    return false;
                }
            }
            return true;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Name
        public String getFamilyName() {
            return this.amp;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Name
        public String getFormatted() {
            return this.anp;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Name
        public String getGivenName() {
            return this.ams;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Name
        public String getHonorificPrefix() {
            return this.anq;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Name
        public String getHonorificSuffix() {
            return this.anr;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Name
        public String getMiddleName() {
            return this.ans;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Name
        public boolean hasFamilyName() {
            return this.alR.contains(2);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Name
        public boolean hasFormatted() {
            return this.alR.contains(3);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Name
        public boolean hasGivenName() {
            return this.alR.contains(4);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Name
        public boolean hasHonorificPrefix() {
            return this.alR.contains(5);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Name
        public boolean hasHonorificSuffix() {
            return this.alR.contains(6);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Name
        public boolean hasMiddleName() {
            return this.alR.contains(7);
        }

        public int hashCode() {
            int iHashCode = 0;
            Iterator<ji.a<?, ?>> it = alQ.values().iterator();
            while (true) {
                int i = iHashCode;
                if (!it.hasNext()) {
                    return i;
                }
                ji.a<?, ?> next = it.next();
                if (a(next)) {
                    iHashCode = b(next).hashCode() + i + next.hm();
                } else {
                    iHashCode = i;
                }
            }
        }

        @Override // com.google.android.gms.internal.ji
        public HashMap<String, ji.a<?, ?>> hf() {
            return alQ;
        }

        @Override // com.google.android.gms.common.data.Freezable
        public boolean isDataValid() {
            return true;
        }

        @Override // com.google.android.gms.common.data.Freezable
        /* renamed from: ny, reason: merged with bridge method [inline-methods] */
        public d freeze() {
            return this;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            of ofVar = CREATOR;
            of.a(this, out, flags);
        }
    }

    public static class e {
        public static int cf(String str) {
            if (str.equals("person")) {
                return 0;
            }
            if (str.equals("page")) {
                return 1;
            }
            throw new IllegalArgumentException("Unknown objectType string: " + str);
        }
    }

    public static final class f extends jj implements Person.Organizations {
        public static final og CREATOR = new og();
        private static final HashMap<String, ji.a<?, ?>> alQ = new HashMap<>();
        final int BR;
        int FD;
        String No;
        String Tg;
        final Set<Integer> alR;
        String amE;
        String amo;
        String ant;
        String anu;
        boolean anv;
        String mName;

        static {
            alQ.put("department", ji.a.l("department", 2));
            alQ.put(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_DESCRIPTION, ji.a.l(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_DESCRIPTION, 3));
            alQ.put("endDate", ji.a.l("endDate", 4));
            alQ.put("location", ji.a.l("location", 5));
            alQ.put("name", ji.a.l("name", 6));
            alQ.put("primary", ji.a.k("primary", 7));
            alQ.put("startDate", ji.a.l("startDate", 8));
            alQ.put("title", ji.a.l("title", 9));
            alQ.put(PlaylistSQLiteHelper.COL_TYPE, ji.a.a(PlaylistSQLiteHelper.COL_TYPE, 10, new jf().h("work", 0).h("school", 1), false));
        }

        public f() {
            this.BR = 1;
            this.alR = new HashSet();
        }

        f(Set<Integer> set, int i, String str, String str2, String str3, String str4, String str5, boolean z, String str6, String str7, int i2) {
            this.alR = set;
            this.BR = i;
            this.ant = str;
            this.Tg = str2;
            this.amo = str3;
            this.anu = str4;
            this.mName = str5;
            this.anv = z;
            this.amE = str6;
            this.No = str7;
            this.FD = i2;
        }

        @Override // com.google.android.gms.internal.ji
        protected boolean a(ji.a aVar) {
            return this.alR.contains(Integer.valueOf(aVar.hm()));
        }

        @Override // com.google.android.gms.internal.ji
        protected Object b(ji.a aVar) {
            switch (aVar.hm()) {
                case 2:
                    return this.ant;
                case 3:
                    return this.Tg;
                case 4:
                    return this.amo;
                case 5:
                    return this.anu;
                case 6:
                    return this.mName;
                case 7:
                    return Boolean.valueOf(this.anv);
                case 8:
                    return this.amE;
                case 9:
                    return this.No;
                case 10:
                    return Integer.valueOf(this.FD);
                default:
                    throw new IllegalStateException("Unknown safe parcelable id=" + aVar.hm());
            }
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            og ogVar = CREATOR;
            return 0;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof f)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            f fVar = (f) obj;
            for (ji.a<?, ?> aVar : alQ.values()) {
                if (a(aVar)) {
                    if (fVar.a(aVar) && b(aVar).equals(fVar.b(aVar))) {
                    }
                    return false;
                }
                if (fVar.a(aVar)) {
                    return false;
                }
            }
            return true;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public String getDepartment() {
            return this.ant;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public String getDescription() {
            return this.Tg;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public String getEndDate() {
            return this.amo;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public String getLocation() {
            return this.anu;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public String getName() {
            return this.mName;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public String getStartDate() {
            return this.amE;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public String getTitle() {
            return this.No;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public int getType() {
            return this.FD;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public boolean hasDepartment() {
            return this.alR.contains(2);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public boolean hasDescription() {
            return this.alR.contains(3);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public boolean hasEndDate() {
            return this.alR.contains(4);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public boolean hasLocation() {
            return this.alR.contains(5);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public boolean hasName() {
            return this.alR.contains(6);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public boolean hasPrimary() {
            return this.alR.contains(7);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public boolean hasStartDate() {
            return this.alR.contains(8);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public boolean hasTitle() {
            return this.alR.contains(9);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public boolean hasType() {
            return this.alR.contains(10);
        }

        public int hashCode() {
            int iHashCode = 0;
            Iterator<ji.a<?, ?>> it = alQ.values().iterator();
            while (true) {
                int i = iHashCode;
                if (!it.hasNext()) {
                    return i;
                }
                ji.a<?, ?> next = it.next();
                if (a(next)) {
                    iHashCode = b(next).hashCode() + i + next.hm();
                } else {
                    iHashCode = i;
                }
            }
        }

        @Override // com.google.android.gms.internal.ji
        public HashMap<String, ji.a<?, ?>> hf() {
            return alQ;
        }

        @Override // com.google.android.gms.common.data.Freezable
        public boolean isDataValid() {
            return true;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Organizations
        public boolean isPrimary() {
            return this.anv;
        }

        @Override // com.google.android.gms.common.data.Freezable
        /* renamed from: nz, reason: merged with bridge method [inline-methods] */
        public f freeze() {
            return this;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            og ogVar = CREATOR;
            og.a(this, out, flags);
        }
    }

    public static final class g extends jj implements Person.PlacesLived {
        public static final oh CREATOR = new oh();
        private static final HashMap<String, ji.a<?, ?>> alQ = new HashMap<>();
        final int BR;
        final Set<Integer> alR;
        boolean anv;
        String mValue;

        static {
            alQ.put("primary", ji.a.k("primary", 2));
            alQ.put("value", ji.a.l("value", 3));
        }

        public g() {
            this.BR = 1;
            this.alR = new HashSet();
        }

        g(Set<Integer> set, int i, boolean z, String str) {
            this.alR = set;
            this.BR = i;
            this.anv = z;
            this.mValue = str;
        }

        @Override // com.google.android.gms.internal.ji
        protected boolean a(ji.a aVar) {
            return this.alR.contains(Integer.valueOf(aVar.hm()));
        }

        @Override // com.google.android.gms.internal.ji
        protected Object b(ji.a aVar) {
            switch (aVar.hm()) {
                case 2:
                    return Boolean.valueOf(this.anv);
                case 3:
                    return this.mValue;
                default:
                    throw new IllegalStateException("Unknown safe parcelable id=" + aVar.hm());
            }
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            oh ohVar = CREATOR;
            return 0;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof g)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            g gVar = (g) obj;
            for (ji.a<?, ?> aVar : alQ.values()) {
                if (a(aVar)) {
                    if (gVar.a(aVar) && b(aVar).equals(gVar.b(aVar))) {
                    }
                    return false;
                }
                if (gVar.a(aVar)) {
                    return false;
                }
            }
            return true;
        }

        @Override // com.google.android.gms.plus.model.people.Person.PlacesLived
        public String getValue() {
            return this.mValue;
        }

        @Override // com.google.android.gms.plus.model.people.Person.PlacesLived
        public boolean hasPrimary() {
            return this.alR.contains(2);
        }

        @Override // com.google.android.gms.plus.model.people.Person.PlacesLived
        public boolean hasValue() {
            return this.alR.contains(3);
        }

        public int hashCode() {
            int iHashCode = 0;
            Iterator<ji.a<?, ?>> it = alQ.values().iterator();
            while (true) {
                int i = iHashCode;
                if (!it.hasNext()) {
                    return i;
                }
                ji.a<?, ?> next = it.next();
                if (a(next)) {
                    iHashCode = b(next).hashCode() + i + next.hm();
                } else {
                    iHashCode = i;
                }
            }
        }

        @Override // com.google.android.gms.internal.ji
        public HashMap<String, ji.a<?, ?>> hf() {
            return alQ;
        }

        @Override // com.google.android.gms.common.data.Freezable
        public boolean isDataValid() {
            return true;
        }

        @Override // com.google.android.gms.plus.model.people.Person.PlacesLived
        public boolean isPrimary() {
            return this.anv;
        }

        @Override // com.google.android.gms.common.data.Freezable
        /* renamed from: nA, reason: merged with bridge method [inline-methods] */
        public g freeze() {
            return this;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            oh ohVar = CREATOR;
            oh.a(this, out, flags);
        }
    }

    public static final class h extends jj implements Person.Urls {
        public static final oi CREATOR = new oi();
        private static final HashMap<String, ji.a<?, ?>> alQ = new HashMap<>();
        final int BR;
        int FD;
        final Set<Integer> alR;
        String anw;
        private final int anx;
        String mValue;

        static {
            alQ.put(PlusShare.KEY_CALL_TO_ACTION_LABEL, ji.a.l(PlusShare.KEY_CALL_TO_ACTION_LABEL, 5));
            alQ.put(PlaylistSQLiteHelper.COL_TYPE, ji.a.a(PlaylistSQLiteHelper.COL_TYPE, 6, new jf().h("home", 0).h("work", 1).h("blog", 2).h(Scopes.PROFILE, 3).h(FitnessActivities.OTHER_STRING, 4).h("otherProfile", 5).h("contributor", 6).h("website", 7), false));
            alQ.put("value", ji.a.l("value", 4));
        }

        public h() {
            this.anx = 4;
            this.BR = 1;
            this.alR = new HashSet();
        }

        h(Set<Integer> set, int i, String str, int i2, String str2, int i3) {
            this.anx = 4;
            this.alR = set;
            this.BR = i;
            this.anw = str;
            this.FD = i2;
            this.mValue = str2;
        }

        @Override // com.google.android.gms.internal.ji
        protected boolean a(ji.a aVar) {
            return this.alR.contains(Integer.valueOf(aVar.hm()));
        }

        @Override // com.google.android.gms.internal.ji
        protected Object b(ji.a aVar) {
            switch (aVar.hm()) {
                case 4:
                    return this.mValue;
                case 5:
                    return this.anw;
                case 6:
                    return Integer.valueOf(this.FD);
                default:
                    throw new IllegalStateException("Unknown safe parcelable id=" + aVar.hm());
            }
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            oi oiVar = CREATOR;
            return 0;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof h)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            h hVar = (h) obj;
            for (ji.a<?, ?> aVar : alQ.values()) {
                if (a(aVar)) {
                    if (hVar.a(aVar) && b(aVar).equals(hVar.b(aVar))) {
                    }
                    return false;
                }
                if (hVar.a(aVar)) {
                    return false;
                }
            }
            return true;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Urls
        public String getLabel() {
            return this.anw;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Urls
        public int getType() {
            return this.FD;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Urls
        public String getValue() {
            return this.mValue;
        }

        @Override // com.google.android.gms.plus.model.people.Person.Urls
        public boolean hasLabel() {
            return this.alR.contains(5);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Urls
        public boolean hasType() {
            return this.alR.contains(6);
        }

        @Override // com.google.android.gms.plus.model.people.Person.Urls
        public boolean hasValue() {
            return this.alR.contains(4);
        }

        public int hashCode() {
            int iHashCode = 0;
            Iterator<ji.a<?, ?>> it = alQ.values().iterator();
            while (true) {
                int i = iHashCode;
                if (!it.hasNext()) {
                    return i;
                }
                ji.a<?, ?> next = it.next();
                if (a(next)) {
                    iHashCode = b(next).hashCode() + i + next.hm();
                } else {
                    iHashCode = i;
                }
            }
        }

        @Override // com.google.android.gms.internal.ji
        public HashMap<String, ji.a<?, ?>> hf() {
            return alQ;
        }

        @Override // com.google.android.gms.common.data.Freezable
        public boolean isDataValid() {
            return true;
        }

        @Deprecated
        public int nB() {
            return 4;
        }

        @Override // com.google.android.gms.common.data.Freezable
        /* renamed from: nC, reason: merged with bridge method [inline-methods] */
        public h freeze() {
            return this;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            oi oiVar = CREATOR;
            oi.a(this, out, flags);
        }
    }

    static {
        alQ.put("aboutMe", ji.a.l("aboutMe", 2));
        alQ.put("ageRange", ji.a.a("ageRange", 3, a.class));
        alQ.put("birthday", ji.a.l("birthday", 4));
        alQ.put("braggingRights", ji.a.l("braggingRights", 5));
        alQ.put("circledByCount", ji.a.i("circledByCount", 6));
        alQ.put("cover", ji.a.a("cover", 7, b.class));
        alQ.put("currentLocation", ji.a.l("currentLocation", 8));
        alQ.put("displayName", ji.a.l("displayName", 9));
        alQ.put("gender", ji.a.a("gender", 12, new jf().h("male", 0).h("female", 1).h(FitnessActivities.OTHER_STRING, 2), false));
        alQ.put("id", ji.a.l("id", 14));
        alQ.put("image", ji.a.a("image", 15, c.class));
        alQ.put("isPlusUser", ji.a.k("isPlusUser", 16));
        alQ.put(SpeechConstant.LANGUAGE, ji.a.l(SpeechConstant.LANGUAGE, 18));
        alQ.put("name", ji.a.a("name", 19, d.class));
        alQ.put("nickname", ji.a.l("nickname", 20));
        alQ.put("objectType", ji.a.a("objectType", 21, new jf().h("person", 0).h("page", 1), false));
        alQ.put("organizations", ji.a.b("organizations", 22, f.class));
        alQ.put("placesLived", ji.a.b("placesLived", 23, g.class));
        alQ.put("plusOneCount", ji.a.i("plusOneCount", 24));
        alQ.put("relationshipStatus", ji.a.a("relationshipStatus", 25, new jf().h("single", 0).h("in_a_relationship", 1).h("engaged", 2).h("married", 3).h("its_complicated", 4).h("open_relationship", 5).h("widowed", 6).h("in_domestic_partnership", 7).h("in_civil_union", 8), false));
        alQ.put("tagline", ji.a.l("tagline", 26));
        alQ.put("url", ji.a.l("url", 27));
        alQ.put("urls", ji.a.b("urls", 28, h.class));
        alQ.put("verified", ji.a.k("verified", 29));
    }

    public ny() {
        this.BR = 1;
        this.alR = new HashSet();
    }

    public ny(String str, String str2, c cVar, int i, String str3) {
        this.BR = 1;
        this.alR = new HashSet();
        this.Nz = str;
        this.alR.add(9);
        this.BL = str2;
        this.alR.add(14);
        this.amW = cVar;
        this.alR.add(15);
        this.ana = i;
        this.alR.add(21);
        this.uR = str3;
        this.alR.add(27);
    }

    ny(Set<Integer> set, int i, String str, a aVar, String str2, String str3, int i2, b bVar, String str4, String str5, int i3, String str6, c cVar, boolean z, String str7, d dVar, String str8, int i4, List<f> list, List<g> list2, int i5, int i6, String str9, String str10, List<h> list3, boolean z2) {
        this.alR = set;
        this.BR = i;
        this.amP = str;
        this.amQ = aVar;
        this.amR = str2;
        this.amS = str3;
        this.amT = i2;
        this.amU = bVar;
        this.amV = str4;
        this.Nz = str5;
        this.om = i3;
        this.BL = str6;
        this.amW = cVar;
        this.amX = z;
        this.Fc = str7;
        this.amY = dVar;
        this.amZ = str8;
        this.ana = i4;
        this.anb = list;
        this.anc = list2;
        this.and = i5;
        this.ane = i6;
        this.anf = str9;
        this.uR = str10;
        this.ang = list3;
        this.anh = z2;
    }

    public static ny i(byte[] bArr) {
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.unmarshall(bArr, 0, bArr.length);
        parcelObtain.setDataPosition(0);
        ny nyVarCreateFromParcel = CREATOR.createFromParcel(parcelObtain);
        parcelObtain.recycle();
        return nyVarCreateFromParcel;
    }

    @Override // com.google.android.gms.internal.ji
    protected boolean a(ji.a aVar) {
        return this.alR.contains(Integer.valueOf(aVar.hm()));
    }

    @Override // com.google.android.gms.internal.ji
    protected Object b(ji.a aVar) {
        switch (aVar.hm()) {
            case 2:
                return this.amP;
            case 3:
                return this.amQ;
            case 4:
                return this.amR;
            case 5:
                return this.amS;
            case 6:
                return Integer.valueOf(this.amT);
            case 7:
                return this.amU;
            case 8:
                return this.amV;
            case 9:
                return this.Nz;
            case 10:
            case 11:
            case 13:
            case 17:
            default:
                throw new IllegalStateException("Unknown safe parcelable id=" + aVar.hm());
            case 12:
                return Integer.valueOf(this.om);
            case 14:
                return this.BL;
            case 15:
                return this.amW;
            case 16:
                return Boolean.valueOf(this.amX);
            case 18:
                return this.Fc;
            case 19:
                return this.amY;
            case 20:
                return this.amZ;
            case 21:
                return Integer.valueOf(this.ana);
            case 22:
                return this.anb;
            case 23:
                return this.anc;
            case 24:
                return Integer.valueOf(this.and);
            case 25:
                return Integer.valueOf(this.ane);
            case 26:
                return this.anf;
            case 27:
                return this.uR;
            case 28:
                return this.ang;
            case 29:
                return Boolean.valueOf(this.anh);
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        nz nzVar = CREATOR;
        return 0;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ny)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        ny nyVar = (ny) obj;
        for (ji.a<?, ?> aVar : alQ.values()) {
            if (a(aVar)) {
                if (nyVar.a(aVar) && b(aVar).equals(nyVar.b(aVar))) {
                }
                return false;
            }
            if (nyVar.a(aVar)) {
                return false;
            }
        }
        return true;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public String getAboutMe() {
        return this.amP;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public Person.AgeRange getAgeRange() {
        return this.amQ;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public String getBirthday() {
        return this.amR;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public String getBraggingRights() {
        return this.amS;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public int getCircledByCount() {
        return this.amT;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public Person.Cover getCover() {
        return this.amU;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public String getCurrentLocation() {
        return this.amV;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public String getDisplayName() {
        return this.Nz;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public int getGender() {
        return this.om;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public String getId() {
        return this.BL;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public Person.Image getImage() {
        return this.amW;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public String getLanguage() {
        return this.Fc;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public Person.Name getName() {
        return this.amY;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public String getNickname() {
        return this.amZ;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public int getObjectType() {
        return this.ana;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public List<Person.Organizations> getOrganizations() {
        return (ArrayList) this.anb;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public List<Person.PlacesLived> getPlacesLived() {
        return (ArrayList) this.anc;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public int getPlusOneCount() {
        return this.and;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public int getRelationshipStatus() {
        return this.ane;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public String getTagline() {
        return this.anf;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public String getUrl() {
        return this.uR;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public List<Person.Urls> getUrls() {
        return (ArrayList) this.ang;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasAboutMe() {
        return this.alR.contains(2);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasAgeRange() {
        return this.alR.contains(3);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasBirthday() {
        return this.alR.contains(4);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasBraggingRights() {
        return this.alR.contains(5);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasCircledByCount() {
        return this.alR.contains(6);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasCover() {
        return this.alR.contains(7);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasCurrentLocation() {
        return this.alR.contains(8);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasDisplayName() {
        return this.alR.contains(9);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasGender() {
        return this.alR.contains(12);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasId() {
        return this.alR.contains(14);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasImage() {
        return this.alR.contains(15);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasIsPlusUser() {
        return this.alR.contains(16);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasLanguage() {
        return this.alR.contains(18);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasName() {
        return this.alR.contains(19);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasNickname() {
        return this.alR.contains(20);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasObjectType() {
        return this.alR.contains(21);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasOrganizations() {
        return this.alR.contains(22);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasPlacesLived() {
        return this.alR.contains(23);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasPlusOneCount() {
        return this.alR.contains(24);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasRelationshipStatus() {
        return this.alR.contains(25);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasTagline() {
        return this.alR.contains(26);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasUrl() {
        return this.alR.contains(27);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasUrls() {
        return this.alR.contains(28);
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean hasVerified() {
        return this.alR.contains(29);
    }

    public int hashCode() {
        int iHashCode = 0;
        Iterator<ji.a<?, ?>> it = alQ.values().iterator();
        while (true) {
            int i = iHashCode;
            if (!it.hasNext()) {
                return i;
            }
            ji.a<?, ?> next = it.next();
            if (a(next)) {
                iHashCode = b(next).hashCode() + i + next.hm();
            } else {
                iHashCode = i;
            }
        }
    }

    @Override // com.google.android.gms.internal.ji
    public HashMap<String, ji.a<?, ?>> hf() {
        return alQ;
    }

    @Override // com.google.android.gms.common.data.Freezable
    public boolean isDataValid() {
        return true;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean isPlusUser() {
        return this.amX;
    }

    @Override // com.google.android.gms.plus.model.people.Person
    public boolean isVerified() {
        return this.anh;
    }

    @Override // com.google.android.gms.common.data.Freezable
    /* renamed from: ns, reason: merged with bridge method [inline-methods] */
    public ny freeze() {
        return this;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        nz nzVar = CREATOR;
        nz.a(this, out, flags);
    }
}
