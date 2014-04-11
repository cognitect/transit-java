// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;

public class NullDecoder implements Decoder {

    @Override
    public Object decode(Object ignored) {
        return null;
    }
}
