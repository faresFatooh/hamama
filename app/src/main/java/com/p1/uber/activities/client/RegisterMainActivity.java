package com.p1.uber.activities.client;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.p1.uber.R;
import com.p1.uber.include.MyToolbar;
import com.p1.uber.modelos.Client;
import com.p1.uber.provides.AuthProvides;
import com.p1.uber.provides.ClientProvider;

import dmax.dialog.SpotsDialog;

public class RegisterMainActivity extends AppCompatActivity {


    AuthProvides mAuthProvider;
    ClientProvider mClientProvider;

    //VIEWS
    Button mButtonRegister;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    TextInputEditText mTextInputName;


    AlertDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_main);

        MyToolbar.show(this, "Registro de usuario",true);


        mDialog = new SpotsDialog.Builder().setContext(RegisterMainActivity.this).setMessage("Espere un momento").build();
        mAuthProvider = new AuthProvides();
        mClientProvider = new ClientProvider();


        mButtonRegister = findViewById(R.id.btnRegister);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputName = findViewById(R.id.textInputName);
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRegister();
            }
        });
    }
    void clickRegister(){
        final String name = mTextInputName.getText().toString();
        final String email = mTextInputEmail.getText().toString();
        final String password = mTextInputPassword.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            if (password.length()>= 6) {
                mDialog.show();
                register(name,email,password);
            }


                else{
                    Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
            }
        }

        void register(final String name, final String email, String password){

            mAuthProvider.register(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    mDialog.hide();
                    if (task.isSuccessful()) {
                        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Client client =new Client(id,name,email);
                        create(client);
                    }
                    else {
                        Toast.makeText(RegisterMainActivity.this, "No se pudoregistrar el ususario", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        void create(Client client){
        mClientProvider.create(client).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                   // Toast.makeText(RegisterMainActivity.this, "El registro se realizo exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent =new Intent(RegisterMainActivity.this, MapClientActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(RegisterMainActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });

        }

        /*
        void saveUser(String id,String name, String email){
            String selectedUser= mPref.getString("user","");
            User user = new User();
            user.setEmail(email);
            user.setName(name);


            if (selectedUser.equals("driver")){
                    mDatabase.child("User").child("Drivers").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(RegisterMainActivity.this, "Registro exitoso",Toast.LENGTH_SHORT).show();

                            }
                            else {
                                Toast.makeText(RegisterMainActivity.this, "Fallo el registro",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }else if (selectedUser.equals("client")){
                mDatabase.child("User").child("Clients").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterMainActivity.this, "Registro exitoso",Toast.LENGTH_SHORT).show();

                        }
                        else {
                            Toast.makeText(RegisterMainActivity.this, "Fallo el registro",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                }


        }*/

    }
