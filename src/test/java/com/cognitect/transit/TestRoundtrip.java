// Copyright (c) Cognitect, Inc.
// All rights reserved.

package com.cognitect.transit;

public class TestRoundtrip {

    public static void main(String [] args) throws Exception {

        String encoding = args[0];

        IReader reader;
        IWriter writer;

        if(encoding.equals("msgpack")) {
            reader = Reader.instance(Reader.Format.MSGPACK, System.in, Reader.defaultDecoders());
            writer = AWriter.getMsgpackInstance(System.out, AWriter.defaultHandlers());
        }
        else {
            reader = Reader.instance(Reader.Format.JSON, System.in, Reader.defaultDecoders());
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
