import 'package:flutter/material.dart';
import 'package:method_channel/method_channels.dart';

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
  MethodChannels _methodChannels = MethodChannels();
  String _ussdResponse = '';

  @override
  void initState() {
    _methodChannels.registerBroadcastReceiver();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: () => _methodChannels.unregisterBroadcastReveiver(),
      child: Material(
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
                    if (await _methodChannels.accessibilityStatus()) {
                      _methodChannels.runUssd().then((value) {
                        setState(() {
                          _ussdResponse = value;
                        });
                      });
                    } else {
                      showDialog(
                        context: context,
                        builder: (_) => Dialog(
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(10.0),
                          ),
                          child: Padding(
                            padding: const EdgeInsets.symmetric(
                                vertical: 10.0, horizontal: 10.0),
                            child: Column(
                              mainAxisSize: MainAxisSize.min,
                              children: <Widget>[
                                Text(
                                    'Enable accessibility for method_channel to precede'),
                                SizedBox(
                                  height: 10.0,
                                ),
                                RaisedButton(
                                  child: Text('Accessibility'),
                                  onPressed: () {
                                    _methodChannels
                                        .launchAccessibilitySettings();
                                    Navigator.pop(context);
                                  },
                                ),
                              ],
                            ),
                          ),
                        ),
                      );
                    }
                  }
                },
              ),
              Text(_ussdResponse),
              FutureBuilder(
                future: _methodChannels.accessibilityStatus(),
                builder: (_, snapshot) {
                  if (snapshot.hasData) {
                    if (snapshot.data) {
                      return Text('Accessibility Enabled');
                    } else {
                      return Text('Accessibility Disabled');
                    }
                  }
                  return Container();
                },
              )
            ],
          ),
        ),
      ),
    );
  }
}
