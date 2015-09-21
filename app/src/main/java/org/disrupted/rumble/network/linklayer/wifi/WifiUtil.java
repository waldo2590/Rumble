/*
 * Copyright (C) 2014 Disrupted Systems
 * This file is part of Rumble.
 * Rumble is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rumble is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Rumble.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.disrupted.rumble.network.linklayer.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;

import org.disrupted.rumble.app.RumbleApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

/**
 * @author Marlinski
 */
public class WifiUtil {

    public static String TAG = "WifiUtil";

    public static WifiManager getWifiManager() {
        return (WifiManager) RumbleApplication.getContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static boolean isEnabled() {
        return (getWifiManager().isWifiEnabled() || isWiFiApEnabled());
    }

    public static String getIPAddress() {
        WifiInfo wifiInfo = getWifiManager().getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            return InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            return null;
        }
    }

    public static void enableWifi() {
        getWifiManager().setWifiEnabled(true);
    }


    public static boolean isWiFiApEnabled()
    {
        try
        {
            final Method method = getWifiManager().getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(getWifiManager());
        }
        catch (final Throwable ignored)
        {
        }
        return false;
    }

    public static void enableAP() {
        Method[] wmMethods = getWifiManager().getClass().getDeclaredMethods();
        boolean methodFound = false;
        for (Method method: wmMethods) {
            Log.d(TAG, method.getName());
            if (method.getName().equals("setWifiApEnabled")) {
                methodFound = true;
                WifiConfiguration netConfig = new WifiConfiguration();
                netConfig.SSID = "Rumble";
                netConfig.allowedAuthAlgorithms.set(
                        WifiConfiguration.AuthAlgorithm.OPEN);
                try {
                    boolean apstatus = (Boolean) method.invoke( getWifiManager(), netConfig, true);

                    for (Method isWifiApEnabledmethod: wmMethods) {
                        if (isWifiApEnabledmethod.getName().equals("isWifiApEnabled")) {

                            while (!(Boolean) isWifiApEnabledmethod.invoke( getWifiManager())) {};
                            for (Method method1: wmMethods) {
                                if (method1.getName().equals("getWifiApState")) {
                                    int apstate;
                                    apstate = (Integer) method1.invoke(getWifiManager());
                                }
                            }
                        }
                    }

                    if (apstatus) {
                        Log.d(TAG, "Access Point created");
                    } else {
                        Log.d(TAG, "Access Point creation failed");
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!methodFound) {
            Log.d("Splash Activity",
                    "cannot configure an access point");
        }
    }

}