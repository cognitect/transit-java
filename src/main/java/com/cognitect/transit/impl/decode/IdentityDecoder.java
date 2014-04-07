package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;

import java.math.BigDecimal;

public class IdentityDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {
        return encodedVal;
    }
}
