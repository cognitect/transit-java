// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SetDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        Set set = new HashSet();
        List list = (List)encodedVal;

        Iterator i = list.iterator();
        while(i.hasNext()) {
            set.add(i.next());
        }

        return set;
    }
}
