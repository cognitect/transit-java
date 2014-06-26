// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

import java.math.BigInteger;

/**
 * Represents a ratio using BigIntegers
 */
public interface Ratio extends Comparable<Ratio> {
    /**
     * The value of the ratio as double
     * @return a double
     */
    public Double getValue();

    /**
     * Gets the numerator
     * @return numerator
     */
    public BigInteger getNumerator();

    /**
     * Gets the denomninator
     * @return denominator
     */
    public BigInteger getDenominator();
}
