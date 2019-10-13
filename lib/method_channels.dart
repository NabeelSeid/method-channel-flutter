import 'package:flutter/services.dart';

class MethodChannels {
  final platform = const MethodChannel('method.channel/ussd');

  Future<String> runUssd() async {
    String ussdResponse;
    try {
      ussdResponse = await platform.invokeMethod('runUssd', <String, dynamic>{
        "ussdCode": "804",
      });
    } on PlatformException catch (e) {
      ussdResponse = "Failed to run ussd: '${e.message}'.";
    }
    return ussdResponse;
  }

  Future<void> registerBroadcastReceiver() async {
    try {
      await platform.invokeMethod('registerBroadcastReceiver');
    } on PlatformException catch (e) {}
  }

  Future<void> unregisterBroadcastReveiver() async {
    try {
      await platform.invokeMethod('unregisterBroadcastReceiver');
    } on PlatformException catch (e) {}
  }

  Future<bool> accessibilityStatus() async {
    try {
      return await platform.invokeMethod('accessibilityStatus');
    } on PlatformException catch (e) {
      return false;
    }
  }

  Future<void> launchAccessibilitySettings() async {
    try {
      await platform.invokeMethod('launchAccessibilitySettings');
    } on PlatformException catch (e) {}
  }
}
