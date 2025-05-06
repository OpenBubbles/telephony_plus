package com.bluebubbles.telephony_plus.receive

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.database.Cursor
import android.database.CursorIndexOutOfBoundsException
import android.net.Uri
import android.os.Handler
import android.provider.Telephony
import android.util.Log
import androidx.core.content.ContextCompat
import com.bluebubbles.telephony_plus.query.RecipientQuery
import java.io.FileNotFoundException
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.UUID

class SMSObserver(val context: Context, handler: Handler, val listener: (context: Context, message: HashMap<String, Any>) -> Unit): ContentObserver(handler) {

    companion object {
        fun init(context: Context, listener: (context: Context, message: HashMap<String, Any>) -> Unit) {
            Log.e("TEST", "INIT MSG")
            try {
                context.contentResolver.registerContentObserver(
                    Uri.parse("content://mms-sms/complete-conversations"),
                    true,
                    SMSObserver(context, Handler(context.mainLooper), listener)
                )
            } catch (e: SecurityException) {
                Log.e("TESTEX   ", e.message ?: "securityexception")
                e.printStackTrace()
            }
        }
    }

    var lastMms: Pair<Int, Boolean>? = null
    var lastId: Pair<Int, Boolean>? = null

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
            return

        var cursor = context.contentResolver.query(
            Uri.parse("content://mms-sms/complete-conversations"),
            arrayOf("ct_t", "_id", "m_type", "type", "body", "address", "thread_id", "date"), null, null, null)
                ?: return

//        cursor.moveToFirst()

        cursor.moveToLast()
//        Log.i("TESTDATE", cursor.getLong(cursor.getColumnIndexOrThrow("date")).toString())

        val dataObject = HashMap<String, Any>()
        try {
            // get columns in the table, their data types, and their values. Insert into the
            // data object for the message.
            for (columnName in cursor.columnNames) {
                val index = cursor.getColumnIndexOrThrow(columnName)
                val type = cursor.getType(index)
                val value: Any?
                value = when (type) {
                    Cursor.FIELD_TYPE_BLOB -> cursor.getBlob(index)
                    Cursor.FIELD_TYPE_FLOAT -> cursor.getFloat(index)
                    Cursor.FIELD_TYPE_INTEGER -> cursor.getInt(index)
                    Cursor.FIELD_TYPE_STRING -> cursor.getString(index)
                    Cursor.FIELD_TYPE_NULL -> null
                    else -> null
                }
                if (value != null) {
                    dataObject[columnName] = value
                }
            }
        } catch (e: CursorIndexOutOfBoundsException) {
            return
        }

        Log.i("TESTINIT", dataObject.toString())

        val isMMS = dataObject["ct_t"] == "application/vnd.wap.multipart.related" || dataObject["ct_t"] == "application/vnd.wap.multipart.mixed"

        if (lastId?.first == dataObject["_id"] && lastId?.second == isMMS) {
            return
        }
        if (!dataObject.containsKey("_id"))
            return
        // new message
        val id = dataObject["_id"] as Int
        lastId = Pair(id, isMMS)

        val body = ArrayList<HashMap<String, Any>>();
        var isFromMe = false
        if (isMMS) {
            if (dataObject["m_type"] == 128)
                isFromMe = true

            val singleMmsUri = Uri.parse("content://mms/part")
            // query the MMS Part table with the MMS ID
            val mmsCursor = context.contentResolver.query(singleMmsUri, null, "${Telephony.Mms.Part.MSG_ID}=${dataObject["_id"]}", null, null)

            while (mmsCursor != null && mmsCursor.moveToNext()) {
                val mmsDataObject = HashMap<String, Any>()
                // get columns in the table, their data types, and their values. Insert into the
                // data object for the MMS.
                for (columnName in mmsCursor.columnNames) {
                    val index = mmsCursor.getColumnIndexOrThrow(columnName)
                    val type = mmsCursor.getType(index)
                    val value : Any?
                    value = when (type) {
                        Cursor.FIELD_TYPE_BLOB -> mmsCursor.getBlob(index)
                        Cursor.FIELD_TYPE_FLOAT -> mmsCursor.getFloat(index)
                        Cursor.FIELD_TYPE_INTEGER -> mmsCursor.getInt(index)
                        Cursor.FIELD_TYPE_STRING -> mmsCursor.getString(index)
                        Cursor.FIELD_TYPE_NULL -> null
                        else -> null
                    }
                    if (value != null) {
                        mmsDataObject[columnName] = value
                    }
                }
                Log.i("TEST", mmsDataObject.toString())
                var bytes: ByteArray
                if (mmsDataObject.keys.contains("text")) {
                    bytes = (mmsDataObject["text"] as String).toByteArray()
                } else {
                    val partURI = Uri.parse("content://mms/part/${mmsDataObject["_id"]}")
                    try {
                        val inputStream = context.contentResolver.openInputStream(partURI)
                        bytes = inputStream?.readBytes() ?: ByteArray(0)
                        inputStream?.close()
                    } catch (e: FileNotFoundException) {
                        return
                    }
                }

                val md = MessageDigest.getInstance("SHA-256")
                md.update("$id$isMMS${body.size}".toByteArray())
                val data = md.digest()
                val buffer = ByteBuffer.wrap(data)
                val uuid = UUID(buffer.getLong(), buffer.getLong())

                val myBody = HashMap<String, Any>()
                myBody["contentType"] = mmsDataObject["ct"] as String
                myBody["body"] = bytes
                myBody["id"] = uuid.toString().uppercase()
                Log.i("SMSDAT", uuid.toString())
                body.add(myBody)
            }
            mmsCursor?.close()

            // query the MMS address table with the MMS ID and type as PduHeaders.FROM (131)
            val mmsAddrCursor = context.contentResolver.query(Uri.parse("content://mms/${dataObject["_id"]}/addr"), null, "${Telephony.Mms.Addr.MSG_ID}=${dataObject["_id"]} AND ${Telephony.Mms.Addr.TYPE}=137", null, null)
            // Get the MMS address and contact ID, and add to the base message data object
            while (mmsAddrCursor != null && mmsAddrCursor.moveToNext()) {
                val number = mmsAddrCursor.getString(mmsAddrCursor.getColumnIndexOrThrow(Telephony.Mms.Addr.ADDRESS))
                val contactId = mmsAddrCursor.getInt(mmsAddrCursor.getColumnIndexOrThrow(Telephony.Mms.Addr.CONTACT_ID))
                if (number != null) {
                    dataObject["address"] = number
                }
                dataObject["person"] = contactId
            }
            mmsAddrCursor?.close()
        } else {
            if (dataObject["type"] == 2)
                isFromMe = true

            if (dataObject["body"] == null)
                return

            val myBody = HashMap<String, Any>()

            val md = MessageDigest.getInstance("SHA-256")
            md.update("$id$isMMS${body.size}".toByteArray())
            val data = md.digest()
            val buffer = ByteBuffer.wrap(data)
            val uuid = UUID(buffer.getLong(), buffer.getLong())
            Log.i("SMSDAT", uuid.toString())

            myBody["contentType"] = "text/plain"
            myBody["id"] = uuid.toString().uppercase()
            myBody["body"] = (dataObject["body"] as String).toByteArray()
            body.add(myBody)
        }

        val messageMap = HashMap<String, Any>()

        if (!dataObject.containsKey("address"))
            return
        messageMap["sender"] = if (isFromMe) "me" else dataObject["address"] as String
        messageMap["body"] = body
        messageMap["thread_id"] = dataObject["thread_id"] as Int

        var tableUri = Uri.parse("content://mms-sms/conversations?simple=true")
        val cursor2 = context.contentResolver.query(tableUri, arrayOf("_id", "archived", "date", "error", "has_attachment", "message_count", "read", "recipient_ids", "snippet", "snippet_cs", "type"), "_id = ?", arrayOf((dataObject["thread_id"] as Int).toString()), null)
        while (cursor2 != null && cursor2.moveToNext()) {
            // get columns in the table, their data types, and their values. Insert into the
            // data object for the message.
            for (columnName in cursor2.columnNames) {
                val index = cursor2.getColumnIndexOrThrow(columnName)
                val type = cursor2.getType(index)
                val value : Any?
                value = when (type) {
                    Cursor.FIELD_TYPE_BLOB -> cursor2.getBlob(index)
                    Cursor.FIELD_TYPE_FLOAT -> cursor2.getFloat(index)
                    Cursor.FIELD_TYPE_INTEGER -> cursor2.getInt(index)
                    Cursor.FIELD_TYPE_STRING -> cursor2.getString(index)
                    Cursor.FIELD_TYPE_NULL -> null
                    else -> null
                }
                if (value != null) {
                    dataObject[columnName] = value
                }
            }
            messageMap["recipients"] = RecipientQuery().query(context, null, dataObject["recipient_ids"] as String)!!
        }
        cursor2?.close()

        Log.i("TEST", messageMap.toString())



        listener(
                context,
                messageMap
        )
        Log.i("TEST", messageMap.toString())

        cursor.close()
    }

}