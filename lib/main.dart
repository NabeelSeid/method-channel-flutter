import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:permission_handler/permission_handler.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = const MethodChannel('method.channel/ussd');

  Future<void> _getBatteryLevel() async {
    String batteryLevel;
    try {
      final int result =
          await platform.invokeMethod('runUssd', <String, dynamic>{
        "ussdCode": "804",
      });
    } on PlatformException catch (e) {
      batteryLevel = "Failed to run ussd: '${e.message}'.";
    }
  }

  @override
  Widget build(BuildContext context) {
    return Material(
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            RaisedButton(
              child: Text('Run *804#'),
              onPressed: () async {
                Map<PermissionGroup, PermissionStatus> permissions =
                    await PermissionHandler()
                        .requestPermissions([PermissionGroup.phone]);
                if (permissions[PermissionGroup.phone] ==
                    PermissionStatus.granted) {
                  _getBatteryLevel();
                }
              },
            ),
          ],
        ),
      ),
    );
  }
}
