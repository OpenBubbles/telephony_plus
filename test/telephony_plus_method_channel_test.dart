import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:telephony_plus/telephony_plus_method_channel.dart';

void main() {
  MethodChannelTelephonyPlus platform = MethodChannelTelephonyPlus();
  const MethodChannel channel = MethodChannel('telephony_plus');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
