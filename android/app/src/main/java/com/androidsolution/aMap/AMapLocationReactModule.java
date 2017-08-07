package com.androidsolution.aMap;


import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by Ryan on 2017/3/23.
 */

public class AMapLocationReactModule extends ReactContextBaseJavaModule implements AMapLocationListener, PoiSearch.OnPoiSearchListener {
    private final String POI_TYPE = "汽车服务|地名地址信息|汽车销售|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|住宿服务|金融保险服务|公司企业";
    //声明AMapLocationClient类对象
    private final AMapLocationClient mLocationClient;
    //声明定位回调监听器
    private AMapLocationListener mLocationListener = this; // new AMapLocationListener();
    private LatLonPoint lp = null;
    private final ReactApplicationContext mReactContext;
    private String deepType = "";// poi搜索类型
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;
    private PoiResult poiResult; // poi返回的结果
    private List<PoiItem> poiItems;// poi数据
    private LatLng latlng;
    private boolean needMars = false;
    private boolean needDetail = false;
    private String cityCode;

    private void sendEvent(String eventName,
                           @Nullable WritableMap params) {
        if (mReactContext != null) {
            mReactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        }
    }

    private void sendArrayEvent(String eventName,
                                @Nullable WritableArray params) {
        if (mReactContext != null) {
            mReactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        }
    }


    public AMapLocationReactModule(ReactApplicationContext reactContext) {
        super(reactContext);
        //初始化定位
        this.mLocationClient = new AMapLocationClient(reactContext);
        //设置定位回调监听
        this.mLocationClient.setLocationListener(mLocationListener);
        mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return "AMapLocation";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        // constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
        // constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
        return constants;
    }

    // 开启位置监听
    @ReactMethod
    public void startLocation() {
        this.mLocationClient.startLocation();
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        // 默认值
        needMars = true;
        needDetail = true;
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setWifiActiveScan(true);
        mLocationOption.setMockEnable(false);
        mLocationOption.setInterval(2000);
        mLocationOption.setKillProcess(false);
        mLocationOption.setHttpTimeOut(30000);
        //给定位客户端对象设置定位参数
        this.mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        this.mLocationClient.startLocation();
    }

    // 停止位置监听
    @ReactMethod
    public void stopLocation() {
        this.mLocationClient.stopLocation();
    }

    // 坐标转换

    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != this.mLocationClient) {
            this.mLocationClient.onDestroy();
            this.mLocationClient = null;
        }
    }
    */


    // 获得定位结果
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            sendEvent("locationSuccess", amapLocationToObject(amapLocation));
            latlng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
            doSearchQuery();
        }
    }


    // Utils
    public static double transformLat(double x, double y) {
        double ret =
                -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y * Math.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    public static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret +=
                (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x / 30.0 * Math.PI)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * @param lat
     * @param lng
     * @return delta[0] 是纬度差，delta[1]是经度差
     */
    public static double[] delta(double lat, double lng) {
        double[] delta = new double[2];
        double a = 6378245.0;
        double ee = 0.00669342162296594323;
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double dLng = transformLon(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        delta[0] = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
        delta[1] = (dLng * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);
        return delta;
    }

    @Override
    public void onPoiSearched(PoiResult result, int code) {
        if (result != null) {
            sendArrayEvent("POISearchDone", amapPOiToObject(result, code));
        }


    }

    @ReactMethod
    public void searchUKeyWord(String keywords) {
        query = new PoiSearch.Query(keywords, "", cityCode);
        query.setPageSize(50);// 设置每页最多返回多少条poiitem
        int currentPage = 0;
        query.setPageNum(currentPage);//设置查询页码
        poiSearch = new PoiSearch(getCurrentActivity(), query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();// 异步搜索
    }

    //返回对象
    private WritableMap amapLocationToObject(AMapLocation amapLocation) {
        WritableMap map = Arguments.createMap();
        Integer errorCode = amapLocation.getErrorCode();
        if (errorCode > 0) {
            map.putString("errorCode", errorCode + "");
            map.putString("errorInfo", amapLocation.getErrorInfo());

        } else {
            WritableMap coordinateMap = Arguments.createMap();
            Double latitude = amapLocation.getLatitude();
            Double longitude = amapLocation.getLongitude();
            if (!needMars) {

                try {
                    CoordinateConverter converter = new CoordinateConverter(mReactContext);
                    //返回true代表当前位置在大陆、港澳地区，反之不在。
                    boolean isAMapDataAvailable = converter.isAMapDataAvailable(latitude, longitude);
                    if (isAMapDataAvailable) {
                        // 在中国境内，火星了
                        double[] deltas = delta(latitude, longitude);
                        latitude = latitude - deltas[0];
                        longitude = longitude - deltas[1];
                    }
                } catch (Exception ex) {
                    return null;
                }
            }

            map.putInt("locationType", amapLocation.getLocationType());
            map.putDouble("latitude", latitude);
            map.putDouble("longitude", longitude);
            coordinateMap.putDouble("latitude", latitude);
            coordinateMap.putDouble("longitude", longitude);
            if (needDetail) {
                // GPS Only
                map.putMap("coordinate", coordinateMap);
                map.putString("errorCode", "0");
                map.putDouble("accuracy", amapLocation.getAccuracy());
                map.putInt("satellites", amapLocation.getSatellites());
                map.putDouble("altitude", amapLocation.getAltitude());
                map.putDouble("speed", amapLocation.getSpeed());
                map.putDouble("bearing", amapLocation.getBearing());
                map.putString("address", amapLocation.getAddress());
                map.putString("adCode", amapLocation.getAdCode());
                map.putString("country", amapLocation.getCountry());
                map.putString("province", amapLocation.getProvince());
                map.putString("poiName", amapLocation.getPoiName());
                map.putString("provider", amapLocation.getProvider());
                map.putString("street", amapLocation.getStreet());
                map.putString("streetNum", amapLocation.getStreetNum());
                map.putString("city", amapLocation.getCity());
                map.putString("cityCode", amapLocation.getCityCode());
                cityCode = amapLocation.getCityCode();
                map.putString("country", amapLocation.getCountry());
                map.putString("district", amapLocation.getDistrict());

                // map.putString("aoiName", amapLocation.getAOIName());
            }

        }
        return map;
    }

    //返回数组
    private WritableArray amapPOiToObject(PoiResult result, int code) {
        WritableArray array = new WritableNativeArray();
        WritableMap map = null;
        WritableMap coordinateMap = null;
        if (code == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    for (PoiItem item : poiItems) {
                        map = Arguments.createMap();
                        coordinateMap = Arguments.createMap();
                        //获取经纬度对象
                        Log.d("PoiItem", "name" + item.getTitle());
                        map.putString("errorCode", "0");
                        map.putString("name", item.getTitle());
                        map.putString("cityCode", item.getCityCode());
                        map.putString("city", item.getCityName());
                        map.putString("district", item.getAdName());
                        map.putString("adCode", item.getAdCode());
                        map.putString("province", item.getProvinceName());
                        map.putString("address", item.getSnippet());
                        coordinateMap.putDouble("latitude", item.getLatLonPoint().getLatitude());
                        coordinateMap.putDouble("longitude", item.getLatLonPoint().getLongitude());
                        map.putMap("coordinate", coordinateMap);
                        array.pushMap(map);
                    }


                }
            }
        } else {
            map = Arguments.createMap();
            map.putString("errorCode", code + "");
        }
        return array;
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /**
     * 开始进行poi搜索
     */
    private void doSearchQuery() {
        query = new PoiSearch.Query("", POI_TYPE);
        query.setPageSize(50);// 设置每页最多返回多少条poiitem
        int currentPage = 0;
        query.setPageNum(currentPage);//设置查询页码
        lp = new LatLonPoint(latlng.latitude, latlng.longitude);
        poiSearch = new PoiSearch(getCurrentActivity(), query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(lp, 1000, true));
        poiSearch.searchPOIAsyn();// 异步搜索
    }


}
