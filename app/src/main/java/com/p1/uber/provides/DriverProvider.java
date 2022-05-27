package com.p1.uber.provides;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.p1.uber.modelos.Drive;

public class DriverProvider {

    DatabaseReference mDatabase;

    public DriverProvider(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
    }
    public Task<Void> create(Drive driver){
        return mDatabase.child(driver.getId()).setValue(driver);

    }

    public  DatabaseReference getDriver (String idDriver){
        return mDatabase.child(idDriver);
    }
}
