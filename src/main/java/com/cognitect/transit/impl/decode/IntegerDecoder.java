package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;
import java.math.BigInteger;

public class IntegerDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        String enc = (String)encodedVal;
        try {
            return Long.parseLong(enc);
        }catch(NumberFormatException e) {
            return new BigInteger(enc);
        }
    }
}
