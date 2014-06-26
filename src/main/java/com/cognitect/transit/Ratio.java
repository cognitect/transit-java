// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface Ratio extends Comparable<Ratio> {
    public BigDecimal value();
    public BigInteger numerator();
    public BigInteger denominator();
}
