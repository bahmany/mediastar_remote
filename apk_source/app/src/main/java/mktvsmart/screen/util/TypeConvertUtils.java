package mktvsmart.screen.util;

/* loaded from: classes.dex */
public class TypeConvertUtils {
    public static int[] IntegerArrayToIntArray(Integer[] array) {
        if (array != null) {
            int[] intArray = new int[array.length];
            for (int i = 0; i < array.length; i++) {
                intArray[i] = array[i].intValue();
            }
            return intArray;
        }
        return null;
    }
}
