package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;
import com.cognitect.transit.impl.JsonParser;

import java.text.ParseException;
import java.util.Date;

public class TimeDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        if(encodedVal instanceof String) {
            try {
                return JsonParser.dateTimeFormat.parse((String)encodedVal);
            } catch(ParseException e) {
                // TODO: What should happen here?
                System.out.println("WARNING: Could not decode time: " + encodedVal);
                return "~t" + encodedVal;
            }
        }
        else {
            return new Date((Long)encodedVal);
        }

    }
}
