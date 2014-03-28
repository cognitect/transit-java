package com.cognitect.transit;

public class Ratio {

    public final long numerator;
    public final long denominator;

    public Ratio(long numerator, long denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public double doubleValue() {
        return (double)numerator / denominator;
    }
}
