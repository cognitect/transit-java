package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;
import org.apache.commons.codec.binary.Base64;

public class BinaryDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        return Base64.decodeBase64(((String)encodedVal).getBytes());
    }
}
