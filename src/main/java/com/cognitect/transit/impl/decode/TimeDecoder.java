package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;
import com.cognitect.transit.impl.JsonParser;

import java.text.ParseException;

public class TimeDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        try {
            return JsonParser.dateTimeFormat.parse((String)encodedVal);
        } catch(ParseException e) {
            // TODO: What should happen here
            System.out.println("WARNING: Could not decode time: " + encodedVal);
            return "~t" + encodedVal;
        }
    }
}
