package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;
import com.cognitect.transit.Symbol;

public class SymbolDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {
        return new Symbol((String)encodedVal);
    }
}
