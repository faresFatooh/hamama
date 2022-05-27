package com.p1.uber.provides;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.p1.uber.modelos.Client;

import java.util.HashMap;
import java.util.Map;

public class ClientProvider {
    DatabaseReference mDatabase;
    public ClientProvider(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Clients");

    }
    public Task<Void> create(Client client){
        Map<String, Object>Map = new HashMap<>();
        Map.put("email",client.getEmail());
        Map.put("name",client.getName());
        return mDatabase.child(client.getId()).setValue(Map);

    }

    public DatabaseReference getClient(String idClient){
        return mDatabase.child(idClient);
    }

}
