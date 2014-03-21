package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;
import java.math.BigDecimal;

public class BigDecimalDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {
        return new BigDecimal((String)encodedVal);
    }
}
