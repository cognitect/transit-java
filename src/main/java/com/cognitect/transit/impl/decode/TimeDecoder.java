// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.decode;

import com.cognitect.transit.AWriter;
import com.cognitect.transit.Decoder;

import java.util.Date;

public class TimeDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        if(encodedVal instanceof String) {
            try {
                return javax.xml.bind.DatatypeConverter.parseDateTime((String)encodedVal).getTime();
            } catch(Exception e) {
                // TODO: What should happen here?
                System.out.println("WARNING: Could not decode time: " + encodedVal);
                return AWriter.ESC_STR + "t" + encodedVal;
            }
        }
        else {
            return new Date((Long)encodedVal);
        }

    }
}
