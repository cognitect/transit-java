// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

public class TestRoundtrip {

    public static void main(String [] args) throws Exception {

        String encoding = args[0];

        TransitFactory.Format format = encoding.equals("msgpack") ? TransitFactory.Format.MSGPACK : TransitFactory.Format.JSON;

        Reader reader = TransitFactory.reader(TransitFactory.Format.MSGPACK, System.in);
        Writer writer = TransitFactory.writer(TransitFactory.Format.MSGPACK, System.out);

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
