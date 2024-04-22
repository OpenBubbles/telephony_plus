
class Recipient {
  /// The ID of the recipient
  int id;
  /// The address of the recipient
  String address;

  Recipient({
    required this.id,
    required this.address,
  });

  factory Recipient.fromMap(Map<String, dynamic> json) => Recipient(
    id: json["_id"],
    address: json["address"],
  );
}