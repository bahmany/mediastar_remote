package com.google.android.gms.internal;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.games.multiplayer.Multiplayer;

/* loaded from: classes.dex */
public final class mo implements SafeParcelable {
    final int BR;
    final String uO;
    public static final mo afr = bW("accounting");
    public static final mo afs = bW("airport");
    public static final mo aft = bW("amusement_park");
    public static final mo afu = bW("aquarium");
    public static final mo afv = bW("art_gallery");
    public static final mo afw = bW("atm");
    public static final mo afx = bW("bakery");
    public static final mo afy = bW("bank");
    public static final mo afz = bW("bar");
    public static final mo afA = bW("beauty_salon");
    public static final mo afB = bW("bicycle_store");
    public static final mo afC = bW("book_store");
    public static final mo afD = bW("bowling_alley");
    public static final mo afE = bW("bus_station");
    public static final mo afF = bW("cafe");
    public static final mo afG = bW("campground");
    public static final mo afH = bW("car_dealer");
    public static final mo afI = bW("car_rental");
    public static final mo afJ = bW("car_repair");
    public static final mo afK = bW("car_wash");
    public static final mo afL = bW("casino");
    public static final mo afM = bW("cemetery");
    public static final mo afN = bW("church");
    public static final mo afO = bW("city_hall");
    public static final mo afP = bW("clothing_store");
    public static final mo afQ = bW("convenience_store");
    public static final mo afR = bW("courthouse");
    public static final mo afS = bW("dentist");
    public static final mo afT = bW("department_store");
    public static final mo afU = bW("doctor");
    public static final mo afV = bW("electrician");
    public static final mo afW = bW("electronics_store");
    public static final mo afX = bW("embassy");
    public static final mo afY = bW("establishment");
    public static final mo afZ = bW("finance");
    public static final mo aga = bW("fire_station");
    public static final mo agb = bW("florist");
    public static final mo agc = bW("food");
    public static final mo agd = bW("funeral_home");
    public static final mo age = bW("furniture_store");
    public static final mo agf = bW("gas_station");
    public static final mo agg = bW("general_contractor");
    public static final mo agh = bW("grocery_or_supermarket");
    public static final mo agi = bW("gym");
    public static final mo agj = bW("hair_care");
    public static final mo agk = bW("hardware_store");
    public static final mo agl = bW("health");
    public static final mo agm = bW("hindu_temple");
    public static final mo agn = bW("home_goods_store");
    public static final mo ago = bW("hospital");
    public static final mo agp = bW("insurance_agency");
    public static final mo agq = bW("jewelry_store");
    public static final mo agr = bW("laundry");
    public static final mo ags = bW("lawyer");
    public static final mo agt = bW("library");
    public static final mo agu = bW("liquor_store");
    public static final mo agv = bW("local_government_office");
    public static final mo agw = bW("locksmith");
    public static final mo agx = bW("lodging");
    public static final mo agy = bW("meal_delivery");
    public static final mo agz = bW("meal_takeaway");
    public static final mo agA = bW("mosque");
    public static final mo agB = bW("movie_rental");
    public static final mo agC = bW("movie_theater");
    public static final mo agD = bW("moving_company");
    public static final mo agE = bW("museum");
    public static final mo agF = bW("night_club");
    public static final mo agG = bW("painter");
    public static final mo agH = bW("park");
    public static final mo agI = bW("parking");
    public static final mo agJ = bW("pet_store");
    public static final mo agK = bW("pharmacy");
    public static final mo agL = bW("physiotherapist");
    public static final mo agM = bW("place_of_worship");
    public static final mo agN = bW("plumber");
    public static final mo agO = bW("police");
    public static final mo agP = bW("post_office");
    public static final mo agQ = bW("real_estate_agency");
    public static final mo agR = bW("restaurant");
    public static final mo agS = bW("roofing_contractor");
    public static final mo agT = bW("rv_park");
    public static final mo agU = bW("school");
    public static final mo agV = bW("shoe_store");
    public static final mo agW = bW("shopping_mall");
    public static final mo agX = bW("spa");
    public static final mo agY = bW("stadium");
    public static final mo agZ = bW("storage");
    public static final mo aha = bW("store");
    public static final mo ahb = bW("subway_station");
    public static final mo ahc = bW("synagogue");
    public static final mo ahd = bW("taxi_stand");
    public static final mo ahe = bW("train_station");
    public static final mo ahf = bW("travel_agency");
    public static final mo ahg = bW("university");
    public static final mo ahh = bW("veterinary_care");
    public static final mo ahi = bW("zoo");
    public static final mo ahj = bW("administrative_area_level_1");
    public static final mo ahk = bW("administrative_area_level_2");
    public static final mo ahl = bW("administrative_area_level_3");
    public static final mo ahm = bW("colloquial_area");
    public static final mo ahn = bW("country");
    public static final mo aho = bW("floor");
    public static final mo ahp = bW("geocode");
    public static final mo ahq = bW("intersection");
    public static final mo ahr = bW("locality");
    public static final mo ahs = bW("natural_feature");
    public static final mo aht = bW("neighborhood");
    public static final mo ahu = bW("political");
    public static final mo ahv = bW("point_of_interest");
    public static final mo ahw = bW("post_box");
    public static final mo ahx = bW("postal_code");
    public static final mo ahy = bW("postal_code_prefix");
    public static final mo ahz = bW("postal_town");
    public static final mo ahA = bW("premise");
    public static final mo ahB = bW(Multiplayer.EXTRA_ROOM);
    public static final mo ahC = bW("route");
    public static final mo ahD = bW("street_address");
    public static final mo ahE = bW("sublocality");
    public static final mo ahF = bW("sublocality_level_1");
    public static final mo ahG = bW("sublocality_level_2");
    public static final mo ahH = bW("sublocality_level_3");
    public static final mo ahI = bW("sublocality_level_4");
    public static final mo ahJ = bW("sublocality_level_5");
    public static final mo ahK = bW("subpremise");
    public static final mo ahL = bW("transit_station");
    public static final mo ahM = bW(FitnessActivities.OTHER_STRING);
    public static final mp CREATOR = new mp();

    mo(int i, String str) {
        com.google.android.gms.common.internal.n.aZ(str);
        this.BR = i;
        this.uO = str;
    }

    public static mo bW(String str) {
        return new mo(0, str);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        mp mpVar = CREATOR;
        return 0;
    }

    public boolean equals(Object o) {
        return (o instanceof mo) && this.uO.equals(((mo) o).uO);
    }

    public int hashCode() {
        return this.uO.hashCode();
    }

    public String toString() {
        return this.uO;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        mp mpVar = CREATOR;
        mp.a(this, parcel, flags);
    }
}
