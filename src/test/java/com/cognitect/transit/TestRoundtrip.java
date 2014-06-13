// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

public class TestRoundtrip {

    public static void main(String [] args) throws Exception {

        String formatArg = args[0];

        TransitFactory.Format format;
        if(formatArg.equals("msgpack"))
            format = TransitFactory.Format.MSGPACK;
        else if(formatArg.equals("json-verbose"))
            format = TransitFactory.Format.JSON_VERBOSE;
        else if(formatArg.equals("json"))
            format = TransitFactory.Format.JSON;
        else
            throw new IllegalArgumentException("No format '" + formatArg + "'");

        Reader reader = TransitFactory.reader(format, System.in);
        Writer writer = TransitFactory.writer(format, System.out);

        try {
            while(true) {
                Object o = reader.read();
                writer.write(o);
            }
        }
        catch(Exception e) {
            // exit
        }
    }
}
