
import 'dart:async';

import 'package:permission_handler/permission_handler.dart';

import 'src/models/attachment.dart';
import 'src/models/message_impl.dart';
import 'src/models/recipient.dart';
import 'src/models/thread.dart';
import 'telephony_plus_platform_interface.dart';

export 'src/models/mms_message.dart';
export 'src/models/sms_message.dart';

class TelephonyPlus {
  void initialize() => TelephonyPlusPlatform.instance.initialize();

  Future<List<Message>> queryMessages({int? threadId, bool getMmsData = false, bool getMmsAddress = false}) {
    return TelephonyPlusPlatform.instance.queryMessages(threadId: threadId, getMmsAddress: getMmsAddress, getMmsData: getMmsData);
  }

  Future<List<Thread>> queryThreads() {
    return TelephonyPlusPlatform.instance.queryThreads();
  }

  Future<List<Recipient>> queryRecipients({required List<int> ids}) {
    return TelephonyPlusPlatform.instance.queryRecipients(ids: ids);
  }

  Future<int> sendSMS({required String address, required String message, int? threadId}) async {
    return TelephonyPlusPlatform.instance.sendSMS(address: address, message: message, threadId: threadId);
  }

  Future<int> sendMMS({required List<String> addresses, int? threadId, String? message, String? subject, List<Attachment>? attachments}) async {
    return TelephonyPlusPlatform.instance.sendMMS(addresses: addresses, threadId: threadId, message: message, subject: subject, attachments: attachments);
  }

  Future<String> getNumber() {
    return TelephonyPlusPlatform.instance.getNumber();
  } 

  Future<bool> requestPermissions() async {
    var status = await Permission.sms.request();
    
    return status.isGranted;
  }
}
