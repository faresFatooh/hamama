package com.p1.uber.provides;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthProvides {
    FirebaseAuth mAuth;

    public AuthProvides(){
        mAuth = FirebaseAuth.getInstance();

    }

    public Task<AuthResult> register(String email, String password){
        return mAuth.createUserWithEmailAndPassword(email,password);

    };
    public Task<AuthResult> login(String email, String password){
        return mAuth.signInWithEmailAndPassword(email,password);

    }
    public void  logout(){
        mAuth.signOut();
    }
    public  String getId(){
        return mAuth.getCurrentUser().getUid();
    }
    public boolean existSesion(){
        boolean exist = false;
        if (mAuth.getCurrentUser() != null){
            exist = true;
        }
        return exist;
    }

}
