package com.google.android.gms.games.internal.request;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.n;
import com.google.android.gms.games.internal.constants.RequestUpdateResultOutcome;
import java.util.HashMap;
import java.util.Set;

/* loaded from: classes.dex */
public final class RequestUpdateOutcomes {
    private static final String[] abh = {"requestId", "outcome"};
    private final int HF;
    private final HashMap<String, Integer> abi;

    public static final class Builder {
        private HashMap<String, Integer> abi = new HashMap<>();
        private int HF = 0;

        public Builder dR(int i) {
            this.HF = i;
            return this;
        }

        public RequestUpdateOutcomes lw() {
            return new RequestUpdateOutcomes(this.HF, this.abi);
        }

        public Builder x(String str, int i) {
            if (RequestUpdateResultOutcome.isValid(i)) {
                this.abi.put(str, Integer.valueOf(i));
            }
            return this;
        }
    }

    private RequestUpdateOutcomes(int statusCode, HashMap<String, Integer> outcomeMap) {
        this.HF = statusCode;
        this.abi = outcomeMap;
    }

    public static RequestUpdateOutcomes V(DataHolder dataHolder) {
        Builder builder = new Builder();
        builder.dR(dataHolder.getStatusCode());
        int count = dataHolder.getCount();
        for (int i = 0; i < count; i++) {
            int iAr = dataHolder.ar(i);
            builder.x(dataHolder.c("requestId", i, iAr), dataHolder.b("outcome", i, iAr));
        }
        return builder.lw();
    }

    public Set<String> getRequestIds() {
        return this.abi.keySet();
    }

    public int getRequestOutcome(String requestId) {
        n.b(this.abi.containsKey(requestId), "Request " + requestId + " was not part of the update operation!");
        return this.abi.get(requestId).intValue();
    }
}
