package com.p1.uber.provides;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.p1.uber.modelos.ClientBooking;

import java.util.HashMap;
import java.util.Map;

public class ClientBookingProvider {

    private DatabaseReference mDatabase;

    public ClientBookingProvider (){
        mDatabase= FirebaseDatabase.getInstance().getReference().child("ClientBooking");

    }

    public Task<Void> create(ClientBooking clientBooking){
        return mDatabase.child(clientBooking.getIdClient()).setValue(clientBooking);
    }

    public Task<Void> updateStatus(String IdClientBooking, String status){

        Map<String , Object> map = new HashMap<>();
        map.put("status", status);
        return mDatabase.child(IdClientBooking).updateChildren(map);

    }

    public Task<Void> updateIdHistoryBooking(String IdClientBooking){
        String idPush = mDatabase.push().getKey();
        Map<String , Object> map = new HashMap<>();
        map.put("idHistoryBooking", idPush);
        return mDatabase.child(IdClientBooking).updateChildren(map);

    }

    public  DatabaseReference getstatus(String idClientBooking) {
        return mDatabase.child(idClientBooking).child("status");
    }

    public  DatabaseReference getClientBooking(String idClientBooking) {
        return mDatabase.child(idClientBooking);
    }

    public  Task<Void> delete(String IdClientBooking){
        return mDatabase.child(IdClientBooking).removeValue();
    }
}
