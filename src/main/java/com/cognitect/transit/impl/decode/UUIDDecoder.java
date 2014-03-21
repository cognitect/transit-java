package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;

import java.util.UUID;

public class UUIDDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        return UUID.fromString((String)encodedVal);
    }
}
