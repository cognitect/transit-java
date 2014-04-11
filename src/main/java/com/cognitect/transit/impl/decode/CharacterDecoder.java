// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;

public class CharacterDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        return ((String)encodedVal).charAt(0);
    }
}
