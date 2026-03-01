package com.google.android.gms.fitness.data;

/* loaded from: classes.dex */
public class DataTypes {
    public static final DataType STEP_COUNT_DELTA = new DataType("com.google.step_count.delta", Fields.STEPS);
    public static final DataType STEP_COUNT_CUMULATIVE = new DataType("com.google.step_count.cumulative", Fields.STEPS);
    public static final DataType STEP_COUNT_CADENCE = new DataType("com.google.step_count.cadence", Fields.RPM);
    public static final DataType ACTIVITY_SEGMENT = new DataType("com.google.activity.segment", Fields.ACTIVITY);
    public static final DataType CALORIES_CONSUMED = new DataType("com.google.calories.consumed", Fields.CALORIES);
    public static final DataType CALORIES_EXPENDED = new DataType("com.google.calories.expended", Fields.CALORIES);
    public static final DataType POWER_SAMPLE = new DataType("com.google.power.sample", Fields.WATTS);
    public static final DataType ACTIVITY_SAMPLE = new DataType("com.google.activity.sample", Fields.ACTIVITY, Fields.CONFIDENCE);
    public static final DataType ACTIVITY_EDGE = new DataType("com.google.activity.edge", Fields.ACTIVITY, Fields.SU);
    public static final DataType SO = new DataType("com.google.accelerometer", Fields.SV, Fields.SW, Fields.SX);
    public static final DataType HEART_RATE_BPM = new DataType("com.google.heart_rate.bpm", Fields.BPM);
    public static final DataType LOCATION_SAMPLE = new DataType("com.google.location.sample", Fields.LATITUDE, Fields.LONGITUDE, Fields.ACCURACY, Fields.ALTITUDE);
    public static final DataType SP = new DataType("com.google.location", Fields.LATITUDE, Fields.LONGITUDE, Fields.ACCURACY);
    public static final DataType DISTANCE_DELTA = new DataType("com.google.distance.delta", Fields.DISTANCE);
    public static final DataType DISTANCE_CUMULATIVE = new DataType("com.google.distance.cumulative", Fields.DISTANCE);
    public static final DataType SPEED = new DataType("com.google.speed", Fields.SPEED);
    public static final DataType CYCLING_WHEEL_REVOLUTION = new DataType("com.google.cycling.wheel_revolution.cumulative", Fields.REVOLUTIONS);
    public static final DataType CYCLING_WHEEL_RPM = new DataType("com.google.cycling.wheel_revolution.rpm", Fields.RPM);
    public static final DataType CYCLING_PEDALING_CUMULATIVE = new DataType("com.google.cycling.pedaling.cumulative", Fields.REVOLUTIONS);
    public static final DataType CYCLING_PEDALING_CADENCE = new DataType("com.google.cycling.pedaling.cadence", Fields.RPM);
    public static final DataType HEIGHT = new DataType("com.google.height", Fields.HEIGHT);
    public static final DataType WEIGHT = new DataType("com.google.weight", Fields.WEIGHT);
    public static final DataType[] Sm = {SO, ACTIVITY_EDGE, ACTIVITY_SAMPLE, ACTIVITY_SEGMENT, CALORIES_CONSUMED, CALORIES_EXPENDED, CYCLING_PEDALING_CADENCE, CYCLING_PEDALING_CUMULATIVE, CYCLING_WHEEL_REVOLUTION, CYCLING_WHEEL_RPM, DISTANCE_CUMULATIVE, DISTANCE_DELTA, HEART_RATE_BPM, HEIGHT, SP, LOCATION_SAMPLE, POWER_SAMPLE, SPEED, STEP_COUNT_CADENCE, STEP_COUNT_CUMULATIVE, STEP_COUNT_DELTA, WEIGHT};
    public static final String[] Sn = {SO.getName(), ACTIVITY_EDGE.getName(), ACTIVITY_SAMPLE.getName(), ACTIVITY_SEGMENT.getName(), CALORIES_CONSUMED.getName(), CALORIES_EXPENDED.getName(), CYCLING_PEDALING_CADENCE.getName(), CYCLING_PEDALING_CUMULATIVE.getName(), CYCLING_WHEEL_REVOLUTION.getName(), CYCLING_WHEEL_RPM.getName(), DISTANCE_CUMULATIVE.getName(), DISTANCE_DELTA.getName(), HEART_RATE_BPM.getName(), HEIGHT.getName(), SP.getName(), LOCATION_SAMPLE.getName(), POWER_SAMPLE.getName(), SPEED.getName(), STEP_COUNT_CADENCE.getName(), STEP_COUNT_CUMULATIVE.getName(), STEP_COUNT_DELTA.getName(), WEIGHT.getName()};

    private DataTypes() {
    }
}
