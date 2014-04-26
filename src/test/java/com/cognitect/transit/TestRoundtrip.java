// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

public class TestRoundtrip {

    public static void main(String [] args) throws Exception {

        String encoding = args[0];

        Reader reader;
        Writer writer;

        if(encoding.equals("msgpack")) {
            reader = Reader.getMsgpackInstance(System.in, Reader.defaultDecoders());
            writer = Writer.getMsgpackInstance(System.out, Writer.defaultHandlers());
        }
        else {
            reader = Reader.getJsonInstance(System.in, Reader.defaultDecoders());
            writer = Writer.getJsonInstance(System.out, Writer.defaultHandlers());
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
