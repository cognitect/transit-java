package com.cognitect.transit.impl;

/**
 * Created by fogus on 4/2/14.
 */
public class Util {

    public static long numberToPrimitiveLong(Object o) throws Exception {
        long i;

        if(o instanceof Long)
            i = ((Long)o).longValue();
        else if(o instanceof Integer)
            i = ((Integer)o).longValue();
        else if(o instanceof Short)
            i = ((Short)o).longValue();
        else if(o instanceof Byte)
            i = ((Byte)o).longValue();
        else
            throw new Exception("Unknown integer type: " + o.getClass());

        return i;
    }
}
