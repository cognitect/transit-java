// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl;

import com.cognitect.transit.Ratio;

import java.math.BigDecimal;
import java.math.BigInteger;

public class RatioImpl implements Ratio {

    private final BigInteger numerator;
    private final BigInteger denominator;

    public RatioImpl(BigInteger numerator, BigInteger denominator) {
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
    public BigDecimal value() {
        BigDecimal n = new BigDecimal(numerator);
        BigDecimal d = new BigDecimal(denominator);
        return n.divide(d);
    }

    @Override
    public BigInteger numerator() {
        return numerator;
    }

    @Override
    public BigInteger denominator() {
        return denominator;
    }

    @Override
    public int compareTo(Ratio o) {
        return value().compareTo(o.value());
    }
}
