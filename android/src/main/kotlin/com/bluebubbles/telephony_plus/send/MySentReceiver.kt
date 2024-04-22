package com.bluebubbles.telephony_plus.send

import android.content.Context
import android.content.Intent
import android.util.Log
import com.klinker.android.send_message.SentReceiver
import io.flutter.plugin.common.MethodChannel.Result

class MySentReceiver : SentReceiver() {

    companion object {
        val callbacks: HashMap<Int, Result> = HashMap()
        var cbCount = 0

        fun register(result: Result): Int {
            cbCount += 1
            callbacks[cbCount] = result;
            return cbCount
        }

    }

    override fun onMessageStatusUpdated(
        context: Context?,
        intent: Intent?,
        receiverResultCode: Int
    ) {
        Log.v(
            "TEST",
            "SMS Receiver Result Code = [$receiverResultCode]"
        )

        val reqId = intent?.getIntExtra("reqId", 0) ?: 0
        if (reqId == 0) {
            return
        }
        callbacks.remove(reqId)?.success(receiverResultCode)
    }

}