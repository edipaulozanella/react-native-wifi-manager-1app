package com.skierkowski.WifiManager;

import com.facebook.react.uimanager.*;
import com.facebook.react.bridge.*;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;

import android.content.res.Configuration;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.util.Log;

import java.net.URL;
import java.util.List;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;

import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class WifiManagerModule extends ReactContextBaseJavaModule {
  public WifiManagerModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "WifiManager";
  }

  @ReactMethod
  public void list(Callback successCallback, Callback errorCallback) {
    try {
      WifiManager mWifiManager = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);
      List < ScanResult > results = mWifiManager.getScanResults();
      WritableArray wifiArray =  Arguments.createArray();
      for (ScanResult result: results) {
        if(!result.SSID.equals("")){
          wifiArray.pushString(result.SSID);
        }
      }
      successCallback.invoke(wifiArray);
    } catch (IllegalViewOperationException e) {
      errorCallback.invoke(e.getMessage());
    }
  }

  @ReactMethod
  public void loadWifiList(final Callback successCallback,final Callback errorCallback) {

      Thread thread = new Thread() {
          @Override
          public void run() {
              try {
                  WifiManager mWifiManager = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);
                  mWifiManager.startScan();
                  List < ScanResult > results = mWifiManager.getScanResults();
                  JSONArray wifiArray = new JSONArray();

                  for (ScanResult result: results) {
                      JSONObject wifiObject = new JSONObject();
                      if(!result.SSID.equals("")){
                          try {
                              wifiObject.put("SSID", result.SSID);
                              wifiObject.put("BSSID", result.BSSID);
                              wifiObject.put("capabilities", result.capabilities);
                              wifiObject.put("frequency", result.frequency);
                              wifiObject.put("level", result.level);
                              //Other fields not added
                              //wifiObject.put("operatorFriendlyName", result.operatorFriendlyName);
                              //wifiObject.put("venueName", result.venueName);
                              //wifiObject.put("centerFreq0", result.centerFreq0);
                              //wifiObject.put("centerFreq1", result.centerFreq1);
                              //wifiObject.put("channelWidth", result.channelWidth);
                          } catch (JSONException e) {
                              errorCallback.invoke(e.getMessage());
                          }
                          wifiArray.put(wifiObject);
                      }
                  }
                  successCallback.invoke(wifiArray.toString());
              } catch (IllegalViewOperationException e) {
                  errorCallback.invoke(e.getMessage());
              }
          }
      };

      thread.start();

  }
    @ReactMethod
    public void getSSID(final Callback callback) {
//        WifiManager mWifiManager = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);
//
//        WifiInfo info = mWifiManager.getConnectionInfo();
//
//        // This value should be wrapped in double quotes, so we need to unwrap it.
//        String ssid = info.getSSID();
//        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
//            ssid = ssid.substring(1, ssid.length() - 1);
//        }
//
//        callback.invoke(ssid);
//        class Conectar extends AsyncTask<String, Integer, String> {
//            protected String doInBackground(String... config) {
//                        WifiManager mWifiManager = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);
//
//        WifiInfo info = mWifiManager.getConnectionInfo();
//
//        // This value should be wrapped in double quotes, so we need to unwrap it.
//        String ssid = info.getSSID();
//        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
//            ssid = ssid.substring(1, ssid.length() - 1);
//        }
//
////                ConnectivityManager connManager = (ConnectivityManager) getReactApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
////                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
////                callback.invoke(ssid,mWifi.getState().toString());z
//
//                return ssid;
//            }
//
//            protected void onProgressUpdate(Integer... progress) {
//
//            }
//
//            protected void onPostExecute(String result) {
//                callback.invoke(result);
//            }
//        }
//        new Conectar().execute("");



        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    WifiManager mWifiManager = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);

                    WifiInfo info = mWifiManager.getConnectionInfo();

                    // This value should be wrapped in double quotes, so we need to unwrap it.
                    String ssid = info.getSSID();
                    if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                        ssid = ssid.substring(1, ssid.length() - 1);
                    }
                    callback.invoke(ssid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();


    }

  @ReactMethod
  public void connect(String ssid, String password,final Callback callback) {
    WifiManager mWifiManager = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);
    List < ScanResult > results = mWifiManager.getScanResults();
      try {
          for (ScanResult result: results) {
              if (ssid.equals(result.SSID)) {
                  connectTo(result, password, ssid,callback);
              }
          }
      }catch (Exception e){

      }

  }
   public static String ssidGlobal = "";
  public void connectTo(ScanResult result, String password, final String ssid,final Callback callback) {
    //Make new configuration
      ssidGlobal = ssid;
   final  WifiConfiguration conf = new WifiConfiguration();
    conf.SSID = "\"" + ssid + "\"";
    String Capabilities = result.capabilities;
    if (Capabilities.contains("WPA2")) {
      conf.preSharedKey = "\"" + password + "\"";
    } else if (Capabilities.contains("WPA")) {
      conf.preSharedKey = "\"" + password + "\"";
    } else if (Capabilities.contains("WEP")) {
      conf.wepKeys[0] = "\"" + password + "\"";
      conf.wepTxKeyIndex = 0;
      conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
      conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
    } else {
      conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
    }
     // Log.i("d",conf.toString());
    //Remove the existing configuration for this netwrok
    WifiManager mWifiManager = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);
    List<WifiConfiguration> mWifiConfigList = mWifiManager.getConfiguredNetworks();
    String comparableSSID = ('"' + ssid + '"'); //Add quotes because wifiConfig.SSID has them
    for(WifiConfiguration wifiConfig : mWifiConfigList){
      if(wifiConfig.SSID.equals(comparableSSID)){
        int networkId = wifiConfig.networkId;
        mWifiManager.removeNetwork(networkId);
        mWifiManager.saveConfiguration();
      }
    }
    //Add configuration to Android wifi manager settings...
//     WifiManager wifiManager = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);
//     mWifiManager.addNetwork(conf);
//    //Enable it so that android can connect
//    List < WifiConfiguration > list = mWifiManager.getConfiguredNetworks();
//    for (WifiConfiguration i: list) {
//      if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
//        wifiManager.disconnect();
//        wifiManager.enableNetwork(i.networkId, true);
//        wifiManager.reconnect();
//        break;
//      }
//    }

     // new ConectarWifi().execute(conf);

      Thread thread = new Thread() {
          @Override
          public void run() {
              try {
                 // WifiConfiguration conf = config[0];
                  WifiManager mWifiManager = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);

                  WifiManager wifiManager = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);
                  mWifiManager.addNetwork(conf);
                  //Enable it so that android can connect
                  List < WifiConfiguration > list = mWifiManager.getConfiguredNetworks();
                  for (WifiConfiguration i: list) {
                      if (i.SSID != null && i.SSID.equals("\"" + ssidGlobal + "\"")) {
                          wifiManager.disconnect();
                          wifiManager.enableNetwork(i.networkId, true);
                          if(wifiManager.reconnect()){
                            this.sleep(5000);
                            callback.invoke();
                          };
                          
                          break;
                      }
                  }

                
                  
                  
                  Log.i("ss"," conectou agora "+ssidGlobal);

              } catch (Exception e) {
                  e.printStackTrace();
              }
          }
      };


      

      thread.start();

      Log.i("ss"," conectou wifi");
  }
    public class ConectarWifi extends AsyncTask<WifiConfiguration, Integer, Long> {
        protected Long doInBackground(WifiConfiguration... config) {
            Log.i("ss"," conectou agora "+ssidGlobal);

            try{
                WifiConfiguration conf = config[0];
                WifiManager mWifiManager = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);

                WifiManager wifiManager = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);
                mWifiManager.addNetwork(conf);
                //Enable it so that android can connect
                List < WifiConfiguration > list = mWifiManager.getConfiguredNetworks();
                for (WifiConfiguration i: list) {
                    if (i.SSID != null && i.SSID.equals("\"" + ssidGlobal + "\"")) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(i.networkId, false);
                        wifiManager.reconnect();
                        break;
                    }
                }
                Log.i("ss"," conectou ok "+ssidGlobal);

                return 0l;
            }catch (Exception e){
                e.printStackTrace();
                Log.i("ss"," conectou erro "+ssidGlobal);

                return 0l;
            }


        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {

        }
    }
  @ReactMethod
  public void status(final Callback callback) {


//
//      class ConectarS extends AsyncTask<String, Integer, String> {
//          protected String doInBackground(String... config) {
//              ConnectivityManager connManager = (ConnectivityManager) getReactApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//              NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//              return mWifi.getState().toString();
//          }
//
//          protected void onProgressUpdate(Integer... progress) {
//
//          }
//
//          protected void onPostExecute(String result) {
//              callback.invoke(result);
//          }
//      }
//      new ConectarS().execute("");

      Thread thread = new Thread() {
          @Override
          public void run() {
              try {
                  ConnectivityManager connManager = (ConnectivityManager) getReactApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                  NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                   callback.invoke(mWifi.getState().toString());
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }
      };

      thread.start();

  }

    @ReactMethod
    public void disconnect(final Callback callback) {



        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    WifiManager  wifi = (WifiManager)getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifi.disconnect();
                    callback.invoke(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }


    @ReactMethod
    public void clear(final String ssid,final Callback callback) {



        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    WifiManager mWifiManager = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    List<WifiConfiguration> mWifiConfigList = mWifiManager.getConfiguredNetworks();
                    String comparableSSID = ('"' + ssid + '"'); //Add quotes because wifiConfig.SSID has them
                    for(WifiConfiguration wifiConfig : mWifiConfigList){
                        if(wifiConfig.SSID.contains(comparableSSID) || wifiConfig.SSID.contains(ssid)){
                            int networkId = wifiConfig.networkId;
                            mWifiManager.removeNetwork(networkId);
                           /// mWifiManager.saveConfiguration();
                        }
                    }
                    mWifiManager.getScanResults();
                    callback.invoke(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }

    @ReactMethod
    public void clearAll() {


        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    WifiManager mWifiManager = (WifiManager) getReactApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    List<WifiConfiguration> mWifiConfigList = mWifiManager.getConfiguredNetworks();
                    for(WifiConfiguration wifiConfig : mWifiConfigList){
                            int networkId = wifiConfig.networkId;
                            mWifiManager.removeNetwork(networkId);
                            /// mWifiManager.saveConfiguration();
                    }
                    mWifiManager.getScanResults();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }



    private static Integer findNetworkInExistingConfig(WifiManager wifiManager, String ssid) {
   List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
   for (WifiConfiguration existingConfig : existingConfigs) {
     if (existingConfig.SSID.equals(ssid)) {
       return existingConfig.networkId;
     }
   }
   return null;
 }
}