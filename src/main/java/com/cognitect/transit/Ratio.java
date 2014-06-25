// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

public interface Ratio extends Comparable<Ratio> {
    public Double value();
    public long numerator();
    public long denominator();
}
