package com.google.android.gms.analytics;

import android.text.TextUtils;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.google.android.gms.analytics.ecommerce.Promotion;
import com.google.android.gms.analytics.t;
import com.google.android.gms.tagmanager.DataLayer;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class HitBuilders {

    @Deprecated
    public static class AppViewBuilder extends HitBuilder<AppViewBuilder> {
        public AppViewBuilder() {
            t.eq().a(t.a.CONSTRUCT_APP_VIEW);
            set("&t", "screenview");
        }

        @Override // com.google.android.gms.analytics.HitBuilders.HitBuilder
        public /* bridge */ /* synthetic */ Map build() {
            return super.build();
        }
    }

    public static class EventBuilder extends HitBuilder<EventBuilder> {
        public EventBuilder() {
            t.eq().a(t.a.CONSTRUCT_EVENT);
            set("&t", DataLayer.EVENT_KEY);
        }

        public EventBuilder(String category, String action) {
            this();
            setCategory(category);
            setAction(action);
        }

        @Override // com.google.android.gms.analytics.HitBuilders.HitBuilder
        public /* bridge */ /* synthetic */ Map build() {
            return super.build();
        }

        public EventBuilder setAction(String action) {
            set("&ea", action);
            return this;
        }

        public EventBuilder setCategory(String category) {
            set("&ec", category);
            return this;
        }

        public EventBuilder setLabel(String label) {
            set("&el", label);
            return this;
        }

        public EventBuilder setValue(long value) {
            set("&ev", Long.toString(value));
            return this;
        }
    }

    public static class ExceptionBuilder extends HitBuilder<ExceptionBuilder> {
        public ExceptionBuilder() {
            t.eq().a(t.a.CONSTRUCT_EXCEPTION);
            set("&t", "exception");
        }

        @Override // com.google.android.gms.analytics.HitBuilders.HitBuilder
        public /* bridge */ /* synthetic */ Map build() {
            return super.build();
        }

        public ExceptionBuilder setDescription(String description) {
            set("&exd", description);
            return this;
        }

        public ExceptionBuilder setFatal(boolean fatal) {
            set("&exf", aj.C(fatal));
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static class HitBuilder<T extends HitBuilder> {
        ProductAction AJ;
        private Map<String, String> AI = new HashMap();
        Map<String, List<Product>> AK = new HashMap();
        List<Promotion> AL = new ArrayList();
        List<Product> AM = new ArrayList();

        protected HitBuilder() {
        }

        public T addImpression(Product product, String impressionList) {
            if (product == null) {
                z.W("product should be non-null");
            } else {
                if (impressionList == null) {
                    impressionList = "";
                }
                if (!this.AK.containsKey(impressionList)) {
                    this.AK.put(impressionList, new ArrayList());
                }
                this.AK.get(impressionList).add(product);
            }
            return this;
        }

        public T addProduct(Product product) {
            if (product == null) {
                z.W("product should be non-null");
            } else {
                this.AM.add(product);
            }
            return this;
        }

        public T addPromotion(Promotion promotion) {
            if (promotion == null) {
                z.W("promotion should be non-null");
            } else {
                this.AL.add(promotion);
            }
            return this;
        }

        public Map<String, String> build() {
            HashMap map = new HashMap(this.AI);
            if (this.AJ != null) {
                map.putAll(this.AJ.build());
            }
            Iterator<Promotion> it = this.AL.iterator();
            int i = 1;
            while (it.hasNext()) {
                map.putAll(it.next().aq(n.A(i)));
                i++;
            }
            Iterator<Product> it2 = this.AM.iterator();
            int i2 = 1;
            while (it2.hasNext()) {
                map.putAll(it2.next().aq(n.z(i2)));
                i2++;
            }
            int i3 = 1;
            for (Map.Entry<String, List<Product>> entry : this.AK.entrySet()) {
                List<Product> value = entry.getValue();
                String strC = n.C(i3);
                Iterator<Product> it3 = value.iterator();
                int i4 = 1;
                while (it3.hasNext()) {
                    map.putAll(it3.next().aq(strC + n.B(i4)));
                    i4++;
                }
                if (!TextUtils.isEmpty(entry.getKey())) {
                    map.put(strC + "nm", entry.getKey());
                }
                i3++;
            }
            return map;
        }

        protected String get(String paramName) {
            return this.AI.get(paramName);
        }

        public final T set(String paramName, String paramValue) {
            t.eq().a(t.a.MAP_BUILDER_SET);
            if (paramName != null) {
                this.AI.put(paramName, paramValue);
            } else {
                z.W(" HitBuilder.set() called with a null paramName.");
            }
            return this;
        }

        public final T setAll(Map<String, String> params) {
            t.eq().a(t.a.MAP_BUILDER_SET_ALL);
            if (params != null) {
                this.AI.putAll(new HashMap(params));
            }
            return this;
        }

        public T setCampaignParamsFromUrl(String utmParams) throws UnsupportedEncodingException {
            t.eq().a(t.a.MAP_BUILDER_SET_CAMPAIGN_PARAMS);
            String strAo = aj.ao(utmParams);
            if (!TextUtils.isEmpty(strAo)) {
                Map<String, String> mapAn = aj.an(strAo);
                set("&cc", mapAn.get("utm_content"));
                set("&cm", mapAn.get("utm_medium"));
                set("&cn", mapAn.get("utm_campaign"));
                set("&cs", mapAn.get("utm_source"));
                set("&ck", mapAn.get("utm_term"));
                set("&ci", mapAn.get("utm_id"));
                set("&gclid", mapAn.get("gclid"));
                set("&dclid", mapAn.get("dclid"));
                set("&gmob_t", mapAn.get("gmob_t"));
            }
            return this;
        }

        public T setCustomDimension(int index, String dimension) {
            set(n.x(index), dimension);
            return this;
        }

        public T setCustomMetric(int index, float metric) {
            set(n.y(index), Float.toString(metric));
            return this;
        }

        protected T setHitType(String hitType) {
            set("&t", hitType);
            return this;
        }

        public T setNewSession() {
            set("&sc", "start");
            return this;
        }

        public T setNonInteraction(boolean nonInteraction) {
            set("&ni", aj.C(nonInteraction));
            return this;
        }

        public T setProductAction(ProductAction action) {
            this.AJ = action;
            return this;
        }

        public T setPromotionAction(String action) {
            this.AI.put("&promoa", action);
            return this;
        }
    }

    @Deprecated
    public static class ItemBuilder extends HitBuilder<ItemBuilder> {
        public ItemBuilder() {
            t.eq().a(t.a.CONSTRUCT_ITEM);
            set("&t", "item");
        }

        @Override // com.google.android.gms.analytics.HitBuilders.HitBuilder
        public /* bridge */ /* synthetic */ Map build() {
            return super.build();
        }

        public ItemBuilder setCategory(String category) {
            set("&iv", category);
            return this;
        }

        public ItemBuilder setCurrencyCode(String currencyCode) {
            set("&cu", currencyCode);
            return this;
        }

        public ItemBuilder setName(String name) {
            set("&in", name);
            return this;
        }

        public ItemBuilder setPrice(double price) {
            set("&ip", Double.toString(price));
            return this;
        }

        public ItemBuilder setQuantity(long quantity) {
            set("&iq", Long.toString(quantity));
            return this;
        }

        public ItemBuilder setSku(String sku) {
            set("&ic", sku);
            return this;
        }

        public ItemBuilder setTransactionId(String transactionid) {
            set("&ti", transactionid);
            return this;
        }
    }

    public static class ScreenViewBuilder extends HitBuilder<ScreenViewBuilder> {
        public ScreenViewBuilder() {
            t.eq().a(t.a.CONSTRUCT_APP_VIEW);
            set("&t", "screenview");
        }

        @Override // com.google.android.gms.analytics.HitBuilders.HitBuilder
        public /* bridge */ /* synthetic */ Map build() {
            return super.build();
        }
    }

    public static class SocialBuilder extends HitBuilder<SocialBuilder> {
        public SocialBuilder() {
            t.eq().a(t.a.CONSTRUCT_SOCIAL);
            set("&t", "social");
        }

        @Override // com.google.android.gms.analytics.HitBuilders.HitBuilder
        public /* bridge */ /* synthetic */ Map build() {
            return super.build();
        }

        public SocialBuilder setAction(String action) {
            set("&sa", action);
            return this;
        }

        public SocialBuilder setNetwork(String network) {
            set("&sn", network);
            return this;
        }

        public SocialBuilder setTarget(String target) {
            set("&st", target);
            return this;
        }
    }

    public static class TimingBuilder extends HitBuilder<TimingBuilder> {
        public TimingBuilder() {
            t.eq().a(t.a.CONSTRUCT_TIMING);
            set("&t", "timing");
        }

        public TimingBuilder(String category, String variable, long value) {
            this();
            setVariable(variable);
            setValue(value);
            setCategory(category);
        }

        @Override // com.google.android.gms.analytics.HitBuilders.HitBuilder
        public /* bridge */ /* synthetic */ Map build() {
            return super.build();
        }

        public TimingBuilder setCategory(String category) {
            set("&utc", category);
            return this;
        }

        public TimingBuilder setLabel(String label) {
            set("&utl", label);
            return this;
        }

        public TimingBuilder setValue(long value) {
            set("&utt", Long.toString(value));
            return this;
        }

        public TimingBuilder setVariable(String variable) {
            set("&utv", variable);
            return this;
        }
    }

    @Deprecated
    public static class TransactionBuilder extends HitBuilder<TransactionBuilder> {
        public TransactionBuilder() {
            t.eq().a(t.a.CONSTRUCT_TRANSACTION);
            set("&t", "transaction");
        }

        @Override // com.google.android.gms.analytics.HitBuilders.HitBuilder
        public /* bridge */ /* synthetic */ Map build() {
            return super.build();
        }

        public TransactionBuilder setAffiliation(String affiliation) {
            set("&ta", affiliation);
            return this;
        }

        public TransactionBuilder setCurrencyCode(String currencyCode) {
            set("&cu", currencyCode);
            return this;
        }

        public TransactionBuilder setRevenue(double revenue) {
            set("&tr", Double.toString(revenue));
            return this;
        }

        public TransactionBuilder setShipping(double shipping) {
            set("&ts", Double.toString(shipping));
            return this;
        }

        public TransactionBuilder setTax(double tax) {
            set("&tt", Double.toString(tax));
            return this;
        }

        public TransactionBuilder setTransactionId(String transactionid) {
            set("&ti", transactionid);
            return this;
        }
    }
}
