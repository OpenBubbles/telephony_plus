import 'dart:typed_data';

class Attachment {
  final Uint8List bytes;
  final String mimeType;
  final String filename;

  Attachment({
    required this.bytes,
    required this.mimeType,
    required this.filename
  });

  Map<String, dynamic> toMap() => {
    'bytes': bytes,
    'mimeType': mimeType,
    'filename': filename,
  };
}