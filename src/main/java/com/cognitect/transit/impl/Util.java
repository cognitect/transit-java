package com.cognitect.transit.impl;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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

	static String maybePrefix(String prefix, String tag, String s){
		if(prefix == null && tag == null)
			return s;
		prefix = (prefix == null)?"":prefix;
		tag = (tag == null)?"":tag;
		StringBuilder sb = new StringBuilder(prefix.length()+tag.length()+s.length());
		return sb.append(prefix).append(tag).append(s).toString();
	}

	public static long arraySize(Object a) {
	    if(a instanceof Collection)
	        return ((Collection)a).size();
	    else if (a.getClass().isArray())
	        return Array.getLength(a);
	    else if (a instanceof Iterable) {
	        int i = 0;
	        for (Object o : (Iterable) a) {
	            i++;
	        }
	        return i;
	    }
	    else
	        throw new UnsupportedOperationException("arraySize not supported on this type " + a.getClass().getSimpleName());

	}

	public static long mapSize(Object m) {
	    if(m instanceof Collection)
	        return ((Collection) m).size();
	    else
	        throw new UnsupportedOperationException("mapSize not supported on this type " + m.getClass().getSimpleName());
	}
}
