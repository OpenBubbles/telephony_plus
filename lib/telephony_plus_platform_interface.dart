import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'src/models/attachment.dart';
import 'src/models/message_impl.dart';
import 'src/models/recipient.dart';
import 'src/models/thread.dart';
import 'telephony_plus_method_channel.dart';

abstract class TelephonyPlusPlatform extends PlatformInterface {
  /// Constructs a TelephonyPlusPlatform.
  TelephonyPlusPlatform() : super(token: _token);

  static final Object _token = Object();

  static TelephonyPlusPlatform _instance = MethodChannelTelephonyPlus();

  /// The default instance of [TelephonyPlusPlatform] to use.
  ///
  /// Defaults to [MethodChannelTelephonyPlus].
  static TelephonyPlusPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [TelephonyPlusPlatform] when
  /// they register themselves.
  static set instance(TelephonyPlusPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  void initialize() {}

  Future<List<Message>> queryMessages({int? threadId, bool getMmsData = false, bool getMmsAddress = false}) {
    throw UnimplementedError('queryMessages() has not been implemented.');
  }

  Future<List<Thread>> queryThreads() {
    throw UnimplementedError('queryThreads() has not been implemented.');
  }

  Future<List<Recipient>> queryRecipients({required List<int> ids}) {
    throw UnimplementedError('queryRecipients() has not been implemented.');
  }

  Future<int> sendSMS({required String address, required String message, int? threadId}) {
    throw UnimplementedError('sendSMS() has not been implemented.');
  }

  Future<int> sendMMS({required List<String> addresses, int? threadId, String? message, String? subject, List<Attachment>? attachments}) {
    throw UnimplementedError('sendMMS() has not been implemented.');
  }

  Future<String> getNumber() {
    throw UnimplementedError('getNumber() has not been implemented.');
  }
}
