import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'src/models/attachment.dart';
import 'src/models/message_impl.dart';
import 'src/models/recipient.dart';
import 'src/models/thread.dart';
import 'telephony_plus_platform_interface.dart';

/// An implementation of [TelephonyPlusPlatform] that uses method channels.
class MethodChannelTelephonyPlus extends TelephonyPlusPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('telephony_plus');

  @override
  void initialize() {
    methodChannel.setMethodCallHandler(_callHandler);
  }

  Future<dynamic> _callHandler(MethodCall call) async {
    switch (call.method) {
      case "SMS_SENT":
        print(call.arguments);
        return;
      case "SMS_DELIVERED":
        print(call.arguments);
        return;
      case "MMS_SENT":
        print(call.arguments);
        return;
      default:
        return;
    }
  }

  @override
  Future<List<Message>> queryMessages({int? threadId, bool getMmsData = false, bool getMmsAddress = false}) async {
    final List response = await methodChannel.invokeMethod(
        'message-query',
        {"threadId": threadId, "mmsData": getMmsAddress, "mmsAddress": getMmsAddress}
    ) ?? [];
    return response.map((e) => Message.fromMap(e.cast<String, dynamic>())).toList();
  }

  @override
  Future<List<Thread>> queryThreads() async {
    final List response = await methodChannel.invokeMethod('thread-query') ?? [];
    return response.map((e) => Thread.fromMap(e.cast<String, dynamic>())).toList();
  }

  @override
  Future<List<Recipient>> queryRecipients({required List<int> ids}) async {
    final List response = await methodChannel.invokeMethod('recipient-query', {'ids': ids}) ?? [];
    return response.map((e) => Recipient.fromMap(e.cast<String, dynamic>())).toList();
  }

  @override
  Future<int> sendSMS({required String address, required String message, int? threadId}) async {
    return await methodChannel.invokeMethod('send-message', {'addresses': [address], 'message': message, 'threadId': threadId});
  }

  @override
  Future<int> sendMMS({required List<String> addresses, int? threadId, String? message, String? subject, List<Attachment>? attachments}) async {
    final attachmentMap = attachments?.map((e) => e.toMap()).toList();
    return await methodChannel.invokeMethod('send-message', {'addresses': addresses, 'threadId': threadId, 'message': message, 'subject': subject, 'attachments': attachmentMap});
  }

  @override
  Future<String> getNumber() async {
    return await methodChannel.invokeMethod('number-query');
  }
}
