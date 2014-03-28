package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;
import com.cognitect.transit.Ratio;

import java.util.List;

public class RatioDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        List array = (List)encodedVal;

        return new Ratio((Long)array.get(0), (Long)array.get(1));

    }
}
