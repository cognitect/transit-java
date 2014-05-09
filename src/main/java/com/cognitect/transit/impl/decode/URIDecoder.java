// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Writer;
import com.cognitect.transit.Decoder;
import com.cognitect.transit.impl.Constants;

import java.net.URI;
import java.net.URISyntaxException;

public class URIDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        try {
            return new URI((String)encodedVal);
        } catch (URISyntaxException e) {
            // TODO: What should happen here
            System.out.println("WARNING: Could not decode URI: " + encodedVal);
            return Constants.ESC + "r" + encodedVal;
        }
    }
}
