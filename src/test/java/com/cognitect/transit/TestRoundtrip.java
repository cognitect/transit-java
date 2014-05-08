// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

public class TestRoundtrip {

    public static void main(String [] args) throws Exception {

        String encoding = args[0];

        AReader reader;
        AWriter writer;

        if(encoding.equals("msgpack")) {
            reader = AReader.getMsgpackInstance(System.in, AReader.defaultDecoders());
            writer = AWriter.getMsgpackInstance(System.out, AWriter.defaultHandlers());
        }
        else {
            reader = AReader.getJsonInstance(System.in, AReader.defaultDecoders());
            writer = AWriter.getJsonInstance(System.out, AWriter.defaultHandlers());
        }

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
