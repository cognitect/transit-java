// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;

import java.util.List;
import java.util.UUID;

public class UUIDDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        if(encodedVal instanceof String) {
            return UUID.fromString((String)encodedVal);
        }
        else {
            List<Long> l = (List<Long>)encodedVal;
            return new UUID(l.get(0), l.get(1));
        }
    }
}
