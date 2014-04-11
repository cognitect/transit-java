// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;

public class DoubleDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        return new Double((String)encodedVal);
    }
}
