package utils;

/**
 * Binomial function from: https://rosettacode.org/wiki/Evaluate_binomial_coefficients#Java
 */
public class Binomial {
    public static long binomial(int n, int k)
    {
        if (k>n-k)
            k=n-k;

        long b=1;
        for (int i=1, m=n; i<=k; i++, m--)
            b=b*m/i;
        return b;
    }
}
