package com.p1.uber.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.p1.uber.activities.driver.MapDriverBookingActivity;
import com.p1.uber.provides.AuthProvides;
import com.p1.uber.provides.ClientBookingProvider;
import com.p1.uber.provides.GeofireProvider;


public class AcceptReceiver extends BroadcastReceiver {

    private ClientBookingProvider mClientBookingProvider;
    private GeofireProvider mGeofireProvider;
    private AuthProvides mAuthProvider;



    @Override
    public void onReceive(Context context, Intent intent) {
        mAuthProvider =  new AuthProvides();
        mGeofireProvider = new GeofireProvider("active_driver");
        mGeofireProvider.removeLocation(mAuthProvider.getId());

        String IdClient = intent.getExtras().getString("IdClient");
        mClientBookingProvider = new ClientBookingProvider();
        mClientBookingProvider.updateStatus(IdClient, "accept");

        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.cancel(2);

        Intent intent1 = new Intent(context, MapDriverBookingActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        intent1.putExtra("IdClient",IdClient);
        context.startActivity(intent1);

    }


}
