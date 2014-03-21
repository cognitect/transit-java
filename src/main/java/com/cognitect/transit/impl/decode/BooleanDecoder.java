package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;

public class BooleanDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        return encodedVal.equals("t");
    }
}
