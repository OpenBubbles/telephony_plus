import 'package:telephony_plus/src/models/message_impl.dart';

class SmsMessage extends Message {
  /// The body of the message.
  String? body;
  /// Error code associated with sending or receiving this message
  int? errorCode;
  /// The protocol identifier code.
  int? protocol;
  /// Is the TP-Reply-Path flag set?
  bool replyPathPresent;
  /// The service center (SC) through which to send the message, if present.
  String? serviceCenter;

  SmsMessage({
    required super.id,
    super.address,
    this.body,
    super.creator,
    super.date,
    super.dateSent,
    this.errorCode,
    super.locked = false,
    super.personId,
    this.protocol,
    super.read = false,
    this.replyPathPresent = false,
    super.seen = false,
    this.serviceCenter,
    required super.status,
    super.subject,
    super.subscriptionId,
    super.threadId,
    required super.type,
  });

  factory SmsMessage.fromMap(Map<String, dynamic> json) {
    return SmsMessage(
      id: json['_id'],
      address: json['address'],
      body: json['body'],
      creator: json['creator'],
      date: json['date'] == null ? null : DateTime.fromMillisecondsSinceEpoch(json['date']),
      dateSent: json['date_sent'] == null ? null : DateTime.fromMillisecondsSinceEpoch(json['date_sent']),
      errorCode: json['error_code'],
      locked: json['locked'] == 1,
      personId: json['person'],
      protocol: json['protocol'],
      read: json['read'] == 1,
      replyPathPresent: json['reply_path_present'] == 1,
      seen: json['seen'] == 1,
      serviceCenter: json['service_center'],
      status: Message.getStatus(json['status']),
      subject: json['subject'],
      subscriptionId: json['sub_id'],
      threadId: json['thread_id'],
      type: Message.getType(json['type']),
    );
  }
}