import 'package:flutter_test/flutter_test.dart';
import 'package:telephony_plus/telephony_plus.dart';
import 'package:telephony_plus/telephony_plus_platform_interface.dart';
import 'package:telephony_plus/telephony_plus_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockTelephonyPlusPlatform
    with MockPlatformInterfaceMixin
    implements TelephonyPlusPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final TelephonyPlusPlatform initialPlatform = TelephonyPlusPlatform.instance;

  test('$MethodChannelTelephonyPlus is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelTelephonyPlus>());
  });

  test('getPlatformVersion', () async {
    TelephonyPlus telephonyPlusPlugin = TelephonyPlus();
    MockTelephonyPlusPlatform fakePlatform = MockTelephonyPlusPlatform();
    TelephonyPlusPlatform.instance = fakePlatform;

    expect(await telephonyPlusPlugin.getPlatformVersion(), '42');
  });
}
