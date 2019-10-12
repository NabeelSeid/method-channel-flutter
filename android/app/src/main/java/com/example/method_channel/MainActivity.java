package com.example.method_channel;


import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;

public class MainActivity extends FlutterActivity {
  private static final String CHANNEL = "method.channel/ussd";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);

    new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(new MethodCallHandler() {
      @Override
      public void onMethodCall(MethodCall call, Result result) {
        // Note: this method is invoked on the main thread.

        if (call.method.equals("runUssd")) {
          runUssd(call.argument("ussdCode"));
        } else {
          result.notImplemented();
        }
      }
    });

  }

  private void runUssd(String ussdCode) {
    String encodedHash = Uri.encode("#");
    String ussd = "*" + ussdCode + encodedHash;
    startActivityForResult(new Intent("android.intent.action.CALL", Uri.parse("tel:" + ussd)), 1);
  }
}
