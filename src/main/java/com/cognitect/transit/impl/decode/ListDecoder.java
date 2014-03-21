package com.cognitect.transit.impl.decode;

import com.cognitect.transit.Decoder;

import java.util.*;

public class ListDecoder implements Decoder {

    @Override
    public Object decode(Object encodedVal) {

        List list = new LinkedList();
        List array = (List)encodedVal;

        Iterator i = array.iterator();
        while(i.hasNext()) {
            list.add(i.next());
        }

        return list;
    }
}
