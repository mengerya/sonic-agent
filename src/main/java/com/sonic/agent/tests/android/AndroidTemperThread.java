package com.sonic.agent.tests.android;

import com.alibaba.fastjson.JSONObject;
import com.android.ddmlib.IDevice;
import com.sonic.agent.bridge.android.AndroidDeviceBridgeTool;
import com.sonic.agent.netty.NettyThreadPool;

import java.util.ArrayList;
import java.util.List;

public class AndroidTemperThread extends Thread {
    @Override
    public void run() {
        while (AndroidDeviceBridgeTool.androidDebugBridge != null) {
            IDevice[] deviceList = AndroidDeviceBridgeTool.getRealOnLineDevices();
            List<JSONObject> detail = new ArrayList<>();
            for (IDevice iDevice : deviceList) {
                JSONObject jsonObject = new JSONObject();
                String temper = AndroidDeviceBridgeTool
                        .executeCommand(iDevice, "dumpsys battery");
                if (temper != null && temper.length() > 0) {
                    String real = temper.substring(temper.indexOf("temperature")).trim();
                    int total = Integer.parseInt(real.substring(13, real.indexOf("\n")));
                    jsonObject.put("udId", iDevice.getSerialNumber());
                    jsonObject.put("tem", total);
                    detail.add(jsonObject);
                }
            }
            JSONObject result = new JSONObject();
            result.put("msg", "temperature");
            result.put("detail", detail);
            NettyThreadPool.send(result);
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}