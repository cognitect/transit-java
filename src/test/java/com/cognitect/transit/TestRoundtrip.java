// Copyright 2014 Cognitect. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS-IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
