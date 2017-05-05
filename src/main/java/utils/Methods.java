package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class to enable or disable comparison methods easily
 */
public class Methods {
    public final static Map<Integer, Boolean> methods = new HashMap<>();

    public final static int N_GRAMS = 0;
    public final static int WORD_GRAPHS = 1;
    public final static int PLACEHOLDER = 2;
    public final static int PLACEHOLDER_SS = 3;
    public final static int RANDOM = 4;

    public Methods() {
        // Set methods that you want to use for text comparison here
        methods.put(N_GRAMS, false);
        methods.put(WORD_GRAPHS, false);
        methods.put(PLACEHOLDER, true);
        methods.put(PLACEHOLDER_SS, false);
        methods.put(RANDOM, false);
    }

    public static boolean isEnabled(int method) {
        return methods.get(method);
    }
}
