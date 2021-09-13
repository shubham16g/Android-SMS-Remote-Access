package com.acoder.smstrigger;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Toast.makeText(context, "SMS Controller Started", Toast.LENGTH_SHORT).show();
            ApManager.onHotspot(context);
        }

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody().trim().toLowerCase();

                        Log.d("tagtag", msgBody + " = " + msg_from);

                        String result = null;
                        switch (msgBody) {
                            case "hotspot on":
                            case "hn":
                                result = ApManager.onHotspot(context);
                                break;
                            case "hotspot off":
                            case "hf":
                                result = ApManager.offHotspot(context);
                                break;
                            case "status":
                            case "st":
                                result = ApManager.getStatus(context);
                                break;
                        }

                        if (result != null)
                            sendSMS(context, msg_from, result);
                    }
                } catch (Exception e) {
//                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }

    public void sendSMS(Context context, String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(context, "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}