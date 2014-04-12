// Copyright (c) Cognitect, Inc.
// All rights reserved.

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

    @Override
    public boolean equals(Object o) {

        if(o instanceof Ratio && ((Ratio)o).numerator == numerator && ((Ratio)o).denominator == denominator)
            return true;
        else
            return false;
    }

    // TODO: implement comparator interface
}
