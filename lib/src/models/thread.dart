import 'recipient.dart';

enum ThreadType {common, broadcast}

class Thread {
  /// The id of the thread
  int id;
  /// If the thread is archived
  bool archived;
  /// The date at which the thread was created.
  DateTime? date;
  /// Indicates whether there is a transmission error in the thread.
  int? error;
  /// Indicates whether this thread contains any attachments.
  bool hasAttachment;
  /// The message count of the thread.
  int? messageCount;
  /// Indicates whether all messages of the thread have been read.
  bool read;
  /// The recipient IDs of the recipients of the message
  List<int> recipientIds;
  /// All recipients associated with the thread
  List<Recipient> recipients;
  /// The snippet of the latest message in the thread.
  String? snippet;
  /// The charset of the snippet.
  int? snippetCharset;
  /// Type of the thread, either COMMON_THREAD or BROADCAST_THREAD.
  ThreadType type;

  Thread({
    required this.id,
    this.archived = false,
    this.date,
    this.error,
    this.hasAttachment = false,
    this.messageCount,
    this.read = false,
    required this.recipientIds,
    required this.recipients,
    this.snippet,
    this.snippetCharset,
    required this.type,
  });

  factory Thread.fromMap(Map<String, dynamic> json) => Thread(
    id: json["_id"],
    archived: json["archived"] == 1,
    date: json["date"] == null ? null : DateTime.fromMillisecondsSinceEpoch(json["date"]),
    error: json["error"],
    hasAttachment: json["has_attachment"] == 1,
    messageCount: json["messageCount"],
    read: json["read"] == 1,
    recipientIds: json["recipient_ids"]?.toString().split(" ").map((e) => int.parse(e)).toList() ?? [],
    recipients: json["recipients"]?.map((e) => Recipient.fromMap(e.cast<String, dynamic>())).toList().cast<Recipient>() ?? [],
    snippet: json["snippet"],
    snippetCharset: json["snippet_cs"],
    type: getType(json["type"]),
  );

  static ThreadType getType(int? typeCode) {
    switch (typeCode) {
      case 0:
        return ThreadType.common;
      case 1:
        return ThreadType.broadcast;
      default:
        return ThreadType.common;
    }
  }
}