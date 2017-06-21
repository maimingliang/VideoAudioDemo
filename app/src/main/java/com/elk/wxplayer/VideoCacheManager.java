package com.elk.wxplayer;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * 类       名:
 * 说       明:
 * 修 改 记 录:
 * 版 权 所 有:   Copyright © 2017
 * 公       司:   深圳市旅联网络科技有限公司
 * version   0.1
 * date   2017/6/21
 * author   maimingliang
 */


public class VideoCacheManager {

    private  HttpProxyCacheServer mProxyCacheServer;

    private static VideoCacheManager instance;

    private VideoCacheManager(){}


    public static VideoCacheManager getInstance(){

        if(instance == null){
            instance = new VideoCacheManager();
        }
        return instance;
    }


    public  HttpProxyCacheServer getProxy(Context context){
        if(mProxyCacheServer == null){
            mProxyCacheServer = newProxy(context);
        }

        return mProxyCacheServer;
    }

    private  HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer.Builder(context.getApplicationContext())
                .maxCacheSize(100 * 1024 * 1024)
                .build();
    }
}
