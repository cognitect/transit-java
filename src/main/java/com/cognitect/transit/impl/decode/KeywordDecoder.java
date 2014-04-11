// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;
import com.cognitect.transit.Keyword;

public class KeywordDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {
        return new Keyword((String)encodedVal);
    }
}
