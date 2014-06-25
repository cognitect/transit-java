// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Ratio;

public class RatioImpl implements Ratio {

    private final long numerator;
    private final long denominator;

    public RatioImpl(long numerator, long denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    @Override
    public boolean equals(Object o) {

        if(o instanceof Ratio && ((Ratio)o).numerator() == numerator && ((Ratio)o).denominator() == denominator)
            return true;
        else
            return false;
    }

    @Override
    public Double value() {
        return (double)numerator / denominator;
    }

    @Override
    public long numerator() {
        return numerator;
    }

    @Override
    public long denominator() {
        return denominator;
    }

    @Override
    public int compareTo(Ratio o) {
        return value().compareTo(o.value());
    }
}
