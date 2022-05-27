package com.p1.uber.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.p1.uber.provides.ClientBookingProvider;

public class CancelReceiver extends BroadcastReceiver {

    private ClientBookingProvider mClientBookingProvider;


    @Override
    public void onReceive(Context context, Intent intent) {

        String IdClient = intent.getExtras().getString("IdClient");
        mClientBookingProvider = new ClientBookingProvider();
        mClientBookingProvider.updateStatus(IdClient, "cancel");

        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.cancel(2);

    }

}
