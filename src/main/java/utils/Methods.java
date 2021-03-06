package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to enable or disable comparison methods easily
 */
public class Methods {
    public final static Map<Integer, Boolean> methods = createMethodsMap();

    public final static int N_GRAMS = 0;
    public final static int WORD_GRAPHS = 1;
    public final static int PLACEHOLDER = 2; // replace non top / entity terms with a placeholder unit size word (eg "A")
    public final static int PLACEHOLDER_SS = 3; // placeholder, but lenght of plchldr matches the length of the word in replaces
    public final static int RANDOM = 4; // replace word characters with random ones
    public final static int COSINE = 5;
    public final static int PLACEHOLDER_EXTRA_WEIGHT = 6;

    public static Map<Integer, Boolean> createMethodsMap() {
        // Set methods that you want to use for text comparison here
        Map<Integer, Boolean> methods = new HashMap<>();
        methods.put(N_GRAMS, false);
        methods.put(WORD_GRAPHS, false);
        methods.put(PLACEHOLDER, true);
        methods.put(PLACEHOLDER_SS, false);
        methods.put(RANDOM, false);
        methods.put(COSINE, false);
        methods.put(PLACEHOLDER_EXTRA_WEIGHT, false);

        return methods;
    }

    public static boolean isEnabled(int method) {
        return methods.get(method);
    }
}
