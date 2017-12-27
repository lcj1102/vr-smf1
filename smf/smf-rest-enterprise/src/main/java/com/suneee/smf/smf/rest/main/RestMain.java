package com.suneee.smf.smf.rest.main;

import com.alibaba.dubbo.container.Main;
import com.suneee.resteasy.util.StartArgsUtils;

/**
 * Created by Administrator on 2017/6/2.
 */
public class RestMain {

    public static void main(String[] args){
        StartArgsUtils.setArgs(args);
        System.setProperty("dubbo.spring.javaconfig","com.suneee.smf.smf.rest.config");
        String[] startArgs = {"javaconfig"};
        Main.main(startArgs);
    }
}
