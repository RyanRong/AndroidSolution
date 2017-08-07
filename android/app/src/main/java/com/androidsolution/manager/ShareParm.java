package com.androidsolution.manager;

import com.facebook.react.bridge.Callback;

/**
 * Created by wukai on 16/7/19.
 */
public class ShareParm {

    public String title;
    public String desc;
    public String url;
    public String image;
    public Callback callback;


    public ShareParm(String title, String desc, String url, String image, Callback callback) {
        this.title = title;
        this.desc = desc;
        this.url = url;
        this.image = image;
        this.callback = callback;
    }
}
