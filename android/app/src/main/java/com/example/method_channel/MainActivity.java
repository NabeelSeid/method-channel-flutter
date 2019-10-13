package com.example.method_channel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

public class MainActivity extends FlutterActivity {
  private static final String CHANNEL = "method.channel/ussd";

  private Result resultLater;

  LocalBroadcastReceiver localBroadcastReceiver = new LocalBroadcastReceiver();

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    SharedPreferences prefs = getApplicationContext().getSharedPreferences("nativePrefs", MODE_PRIVATE);
    prefs.edit().putBoolean("serviceEnabled", true).apply();

    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);

    new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler((call, result) -> {
      // Note: this method is invoked on the main thread.

      if (call.method.equals("runUssd")) {
        runUssd(call.argument("ussdCode"));
        resultLater = result;
      } else if(call.method.equals("registerBroadcastReceiver")) {
        LocalBroadcastManager.getInstance(this).registerReceiver(localBroadcastReceiver,
                new IntentFilter("transaction-complete"));
      }
      else if(call.method.equals("unregisterBroadcastReceiver")) {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastReceiver);
      }
      else if(call.method.equals("accessibilityStatus")){
        result.success(isAccessibilitySettingsOn(this));
      } else if(call.method.equals("launchAccessibilitySettings")) {
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
      }
      else {
        result.notImplemented();
      }
    });

    HomeWatcher mHomeWatcher = new HomeWatcher(this);
    mHomeWatcher.setOnHomePressedListener(() -> prefs.edit().putBoolean("serviceEnabled", false).apply());
    mHomeWatcher.startWatch();

  }

  private class LocalBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      Log.e("TAG ERROR", intent.getStringExtra("ussdText"));
      resultLater.success(intent.getStringExtra("ussdText"));
      resultLater = null;
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  @Override
  protected void onDestroy() {
    SharedPreferences prefs = getApplicationContext().getSharedPreferences("nativePrefs", MODE_PRIVATE);
    prefs.edit().putBoolean("serviceEnabled", false).apply();
    super.onDestroy();
  }

  private void runUssd(String ussdCode) {
    SharedPreferences prefs = getApplicationContext().getSharedPreferences("nativePrefs", MODE_PRIVATE);
    prefs.edit().putBoolean("serviceEnabled", true).apply();
    String encodedHash = Uri.encode("#");
    String ussd = "*" + ussdCode + encodedHash;
    startActivityForResult(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussd)), 1);
  }

  // To check if service is enabled
  private boolean isAccessibilitySettingsOn(Context mContext) {
    int accessibilityEnabled = 0;
    final String service = getPackageName() + "/" + USSDService.class.getCanonicalName();
    try {
      accessibilityEnabled = Settings.Secure.getInt(
              mContext.getApplicationContext().getContentResolver(),
              android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
    } catch (Settings.SettingNotFoundException e) {
    }
    TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

    if (accessibilityEnabled == 1) {
      String settingValue = Settings.Secure.getString(
              mContext.getApplicationContext().getContentResolver(),
              Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
      if (settingValue != null) {
        mStringColonSplitter.setString(settingValue);
        while (mStringColonSplitter.hasNext()) {
          String accessibilityService = mStringColonSplitter.next();

          if (accessibilityService.equalsIgnoreCase(service)) {
            return true;
          }
        }
      }
    }

    return false;
  }

}