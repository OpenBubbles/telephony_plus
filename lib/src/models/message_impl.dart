import 'package:telephony_plus/src/models/mms_message.dart';
import 'package:telephony_plus/src/models/sms_message.dart';

enum MessageStatus {complete, failed, none, pending}

enum MessageType {all, draft, failed, inbox, outbox, queued, sent}

abstract class Message {
  int id;
  /// The address of the other party.
  String? address;
  /// The identity of the sender of a sent message. It is usually the package name of the app which sends the message.
  String? creator;
  /// The date the message was received.
  DateTime? date;
  /// The date the message was sent.
  DateTime? dateSent;
  /// Is the message locked?
  bool locked;
  /// The ID of the sender of the conversation, if present. (reference to item in content://contacts/people)
  int? personId;
  /// Has the message been read?
  bool read;
  /// Has the message been seen by the user? The "seen" flag determines whether we need to show a notification.
  bool seen;
  /// TP-Status value for the message, or -1 if no status has been received.
  MessageStatus status;
  /// The subject of the message, if present.
  String? subject;
  /// The subscription to which the message belongs to. Its value will be < 0 if the sub id cannot be determined.
  int? subscriptionId;
  /// The thread ID of the message.
  int? threadId;
  /// The type of message.
  MessageType type;

  Message({
    required this.id,
    this.address,
    this.creator,
    this.date,
    this.dateSent,
    this.locked = false,
    this.personId,
    this.read = false,
    this.seen = false,
    required this.status,
    this.subject,
    this.subscriptionId,
    this.threadId,
    required this.type,
  });

  static Message fromMap(Map<String, dynamic> json) {
    if (json['ct_t'] == "application/vnd.wap.multipart.related") {
      return MmsMessage.fromMap(json);
    } else {
      return SmsMessage.fromMap(json);
    }
  }

  static MessageStatus getStatus(int? statusCode) {
    switch (statusCode) {
      case 0:
        return MessageStatus.complete;
      case 64:
        return MessageStatus.failed;
      case 32:
        return MessageStatus.pending;
      default:
        return MessageStatus.none;
    }
  }

  static MessageType getType(int? typeCode) {
    switch (typeCode) {
      case 0:
        return MessageType.all;
      case 3:
        return MessageType.draft;
      case 5:
        return MessageType.failed;
      case 1:
        return MessageType.inbox;
      case 4:
        return MessageType.outbox;
      case 6:
        return MessageType.queued;
      case 2:
        return MessageType.sent;
      default:
        return MessageType.all;
    }
  }
}