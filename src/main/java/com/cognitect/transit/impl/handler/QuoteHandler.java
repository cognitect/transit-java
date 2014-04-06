package com.cognitect.transit.impl.handler;

import com.cognitect.transit.Handler;
import com.cognitect.transit.impl.HandlerAware;
import com.cognitect.transit.impl.Quote;

public class QuoteHandler implements Handler, HandlerAware {

    private Handler handler;

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public String tag(Object ignored) {
        return "'";
    }

    @Override
    public Object rep(Object o) {
        return ((Quote)o).o;
    }

    @Override
    public String stringRep(Object o) {
        System.out.println("THIS SHOULD NEVER BE CALLED");
        return handler.stringRep(o);
    }


}
