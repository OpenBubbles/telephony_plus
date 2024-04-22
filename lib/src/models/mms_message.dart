import 'package:telephony_plus/src/models/message_impl.dart';

class MmsMessage extends Message {
  /// The content-class of the message.
  int? contentClass;
  /// The Content-Location of the message.
  String? contentLocation;
  /// The Content-Type of the message.
  String? contentType;
  /// The delivery-report of the message.
  int? deliveryReport;
  /// The delivery-time of the message.
  DateTime? deliveryTime;
  /// The expiry time of the message.
  DateTime? expiry;
  /// The class of the message.
  String? messageClass;
  /// The Message-ID of the message.
  String? messageId;
  /// The size of the message.
  int? messageSize;
  /// The type of the message defined by MMS spec.
  int? messageType;
  /// The version of the specification that this message conforms to.
  int? mmsVersion;
  /// The priority of the message.
  int? priority;
  /// The read-report of the message.
  bool readReport;
  /// The read-status of the message.
  int? readStatus;
  /// Is read report allowed?
  bool readReportAllowed;
  /// The response-status of the message.
  int? responseStatus;
  /// The response-text of the message.
  String? responseText;
  /// The retrieve-status of the message.
  int? retrieveStatus;
  /// The retrieve-text of the message.
  String? retrieveText;
  /// The character set of the retrieve-text.
  int? retrieveTextCharset;
  /// The character set of the subject, if present.
  int? subjectCharset;
  /// Does the message have only a text part (can also have a subject) with no picture, slideshow, sound, etc. parts?
  bool textOnly;
  /// The transaction-id of the message.
  String? transactionId;
  /// The data for the MMS message
  List<MmsData> data;

  MmsMessage({
    required super.id,
    super.address,
    this.contentClass,
    this.contentLocation,
    this.contentType,
    super.creator,
    super.date,
    super.dateSent,
    this.deliveryReport,
    this.deliveryTime,
    this.expiry,
    super.locked = false,
    this.messageClass,
    this.messageId,
    this.messageSize,
    this.messageType,
    this.mmsVersion,
    super.personId,
    this.priority,
    super.read = false,
    this.readReport = false,
    this.readStatus,
    this.readReportAllowed = false,
    this.responseStatus,
    this.responseText,
    this.retrieveStatus,
    this.retrieveTextCharset,
    super.seen = false,
    required super.status,
    super.subject,
    this.subjectCharset,
    super.subscriptionId,
    this.textOnly = false,
    super.threadId,
    this.transactionId,
    required super.type,
    required this.data,
  });

  factory MmsMessage.fromMap(Map<String, dynamic> json) {
    return MmsMessage(
      id: json['_id'],
      address: json['address'],
      contentClass: json['ct_cls'],
      contentLocation: json['ct_l'],
      contentType: json['ct_t'],
      creator: json['creator'],
      date: json['date'] == null ? null : DateTime.fromMillisecondsSinceEpoch(json['date']),
      dateSent: json['date_sent'] == null ? null : DateTime.fromMillisecondsSinceEpoch(json['date_sent']),
      deliveryReport: json['d_rpt'],
      deliveryTime: json['d_tm'] == null ? null : DateTime.fromMillisecondsSinceEpoch(json['d_tm']),
      expiry: json['exp'] == null ? null : DateTime.fromMillisecondsSinceEpoch(json['exp']),
      locked: json['locked'] == 1,
      messageId: json['m_id'],
      messageClass: json['m_cls'],
      messageSize: json['m_size'],
      messageType: json['m_type'],
      mmsVersion: json['v'],
      personId: json['person'],
      priority: json['pri'],
      read: json['read'] == 1,
      readReport: json['rr'] == 1,
      readStatus: json['read_status'],
      readReportAllowed: json['rpt_a'] == 1,
      responseStatus: json['resp_st'],
      responseText: json['resp_txt'],
      retrieveStatus: json['retr_st'],
      retrieveTextCharset: json['retr_txt'],
      seen: json['seen'] == 1,
      status: Message.getStatus(json['st']),
      subject: json['sub'],
      subjectCharset: json['sub_cs'],
      subscriptionId: json['sub_id'],
      textOnly: json['text_only'] == 1,
      threadId: json['thread_id'],
      transactionId: json['tr_id'],
      type: Message.getType(json['msg_box']),
      data: (json['mms_data'] as List?)
          ?.where((e) => e['ct'] != 'application/smil')
          .map((e) => MmsData.fromMap(e.cast<String, dynamic>())).toList() ?? []
    );
  }
}

class MmsData {
  String? contentLocation;
  String? contentType;
  String? text;

  MmsData({
    this.contentLocation,
    this.contentType,
    this.text,
  });

  factory MmsData.fromMap(Map<String, dynamic> json) {
    return MmsData(
      contentLocation: json['_data'],
      contentType: json['ct'],
      text: json['text'],
    );
  }
}