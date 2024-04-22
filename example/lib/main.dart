import 'dart:developer';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:telephony_plus/telephony_plus.dart';
import 'package:telephony_plus/telephony_plus_platform_interface.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  TelephonyPlus().initialize();
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _telephonyPlusPlugin = TelephonyPlus();

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: GestureDetector(
            onTap: () async {
              final messages = await TelephonyPlus().sendSMS(address: "5555215556", message: "Test");
            },
            child: Column(
              children: [
                CircularProgressIndicator(),
                Text('Running on: $_platformVersion\n'),
              ],
            )
          ),
        ),
      ),
    );
  }
}
