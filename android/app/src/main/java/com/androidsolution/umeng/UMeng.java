package com.androidsolution.umeng;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.support.annotation.Nullable;

import com.androidsolution.manager.Lcp;
import com.androidsolution.manager.ShareParm;
import com.androidsolution.manager.UMHelper;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.utils.Log;

import java.util.HashMap;

//import com.umeng.analytics.MobclickAgent;

/**
 * Created by wukai on 16/7/7.
 */
public class UMeng extends ReactContextBaseJavaModule {
    public static ReactApplicationContext reactApplicationContext;
    private UMShareAPI mShareAPI = null;
    private SHARE_MEDIA loginPlatform;
    private String platform;
    private Callback callback;
    private Activity currnetActivity = null;

    {
        PlatformConfig.setWeixin("wxa198b98018496d35", "24f9184cc820337ac6a2c2a72a20854f");
        PlatformConfig.setSinaWeibo("2877658122", "a358cf256ae7f0a5bb7787b1a5d230ea", "http://sns.whalecloud.com/sina2/callback");
        PlatformConfig.setQQZone("1105859116", "XvtVTzBg70WGgWCC");
    }

    public UMeng(ReactApplicationContext reactContext) {
        super(reactContext);
        reactApplicationContext = reactContext;
    }

    @Override
    public String getName() {
        return "UMeng";
    }

    //友盟配置
    @ReactMethod
    public void setAppKey() {
//        Bundle metaData = null;
//        ApplicationInfo appi;
//        try{
//            appi = reactApplicationContext.getPackageManager().getApplicationInfo(reactApplicationContext.getPackageName(), PackageManager.GET_META_DATA);
//            if(appi!=null)
//                metaData = appi.metaData;
//            if(metaData!=null){
//                metaData.putString("UMENG_APPKEY",appKey);
//            }
//
//        }catch (PackageManager.NameNotFoundException e){
//            e.printStackTrace();
//        }
    }

    @ReactMethod
    public void openLog(boolean isopen) {
        Log.LOG = isopen;
//        Config.IsToastTip = isopen;
    }


    @ReactMethod
    public void setWXAppIdSecretURL(String appid, String secret, String url) {
        Log.d("appid", appid);
        Log.d("secret", secret);
        PlatformConfig.setWeixin(appid, secret);
    }

    @ReactMethod
    public void setQQWithAppIdKeyURL(String appid, String appkey, String url) {
        PlatformConfig.setQQZone(appid, appkey);
    }

    @ReactMethod
    public void setQQSupportWebView(boolean issupport) {

    }

    @ReactMethod
    public void renrenOpenSSO() {

    }

    @ReactMethod
    public void setWechatSessionURL(String url) {

    }

    @ReactMethod
    public void setWechatTimelineURL(String url) {
    }

    @ReactMethod
    public void setQQURL(String url) {
    }

    @ReactMethod
    public void setQZoneURL(String url) {
    }

    @ReactMethod
    public void openNewSinaSSOWithAppKeySecretURL(String appkey, String secret, String url) {
        PlatformConfig.setSinaWeibo(appkey, secret, url);
    }

    //分享
    @ReactMethod
    public void share(String title, String desc, String url, String image, Callback callback) {
        Message msg = new Message();
        msg.what = 2;
        msg.obj = new ShareParm(title, desc, url, image, callback);
        UMHelper.sharehandler.sendMessage(msg);
    }

    private int getResourceDrawableId(Context context, @Nullable String name) {
        if (name == null || name.isEmpty()) {
            return 0;
        }
        name = name.toLowerCase().replace("-", "_");

        int id = context.getResources().getIdentifier(
                name,
                "drawable",
                context.getPackageName());
        return id;
    }

    //以下是友盟第三方登录
    @ReactMethod
    public void loginByPlatform(String p, Callback c) {
//        UMHelper.login(p,c);
        Message msg = new Message();
        msg.what = 1;
        msg.obj = new Lcp(p, c);
        UMHelper.handler.sendMessage(msg);
    }

    //以下是友盟统计
    @ReactMethod
    public void statisticsStartWithAppkey() {
//        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(getCurrentActivity(), "5823cc73aed17910890009dc", "Channel ID", MobclickAgent.EScenarioType.E_UM_NORMAL, true));

    }

    //账号的统计
    @ReactMethod
    public void statisticsProfileSignInWithPUID(String userId) {
//        MobclickAgent.onProfileSignIn(userId);
    }

    //页面统计
    @ReactMethod
    public void statisticsBeginLogPageView(String viewName) {
//        MobclickAgent.onPageStart(viewName);
    }

    @ReactMethod
    public void statisticsEndLogPageView(String viewName) {
//        MobclickAgent.onPageEnd(viewName);
    }

    @ReactMethod
    public void statisticsEvent(String eventType) {
        MobclickAgent.onEvent(reactApplicationContext, eventType);
    }

    @ReactMethod
    public void statisticsEventWithAttributes(String eventType, ReadableMap data) {
        MobclickAgent.onEvent(reactApplicationContext, eventType, convertReadMapToHashmap(data));
    }

    private HashMap<String, String> convertReadMapToHashmap(ReadableMap data) {
        HashMap<String, String> map = new HashMap<String, String>();
        ReadableMapKeySetIterator iterator = data.keySetIterator();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            switch (data.getType(key)) {
//                case Boolean:
//                    map.put(key, data.getBoolean(key)+"");
//                    break;
//                case Number:
//                    map.put(key, data.getDouble(key)+"");
//                    break;
                case String:
                    map.put(key, data.getString(key));
                    break;
//                case Map:
//                    map.put(key, convertReadMapToHashmap(data.getMap(key)));
//                    break;
//                case Array:
//                    map.put(key, convertReadArrayToArrayList(data.getArray(key)));
//                    break;
            }
        }
        return map;
    }
//    private ArrayList convertReadArrayToArrayList(ReadableArray readableArray){
//        ArrayList array = new ArrayList();
//        for (int i = 0; i < readableArray.size(); i++) {
//            switch (readableArray.getType(i)) {
//                case Null:
//                    break;
//                case Boolean:
//                    array.add(readableArray.getBoolean(i));
//                    break;
//                case Number:
//                    array.add(readableArray.getDouble(i));
//                    break;
//                case String:
//                    array.add(readableArray.getString(i));
//                    break;
//                case Map:
//                    array.add(convertReadMapToHashmap(readableArray.getMap(i)));
//                    break;
//                case Array:
//                    array.add(convertReadArrayToArrayList(readableArray.getArray(i)));
//                    break;
//            }
//        }
//        return array;
//    }
}

