package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CmapDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        List array = (List)encodedVal;
        Map m = new HashMap();

        for(int i=0;i<array.size();i++) {
            m.put(array.get(i), array.get(++i));
        }

        return m;
    }
}
