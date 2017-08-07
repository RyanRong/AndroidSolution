package com.androidsolution.manager;

import com.facebook.react.bridge.Callback;

/**
 * Created by wukai on 16/7/18.
 */
public class Lcp {
    public String platform;
    public Callback callback;
    public Lcp(String p, Callback c){
        platform = p;
        callback = c;
    }
}
