package com.example.btlandroid;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.btlandroid.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class Profile extends AppCompatActivity {


    TextView name, mail;
    TextInputLayout fullName,email,phoneNo,password;
    Button logout,update;
    ImageView img;
    Uri photoUrl;
    private GoogleSignInClient mGoogleSignInClient;
    String _USERNAME, _EMAIL, _PHONENO, _PASSWORD, _NAME;

    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        reference = FirebaseDatabase.getInstance().getReference("users");


        logout = findViewById(R.id.logout);
        name = findViewById(R.id.name);
        mail = findViewById(R.id.mail);
        img = (ImageView) findViewById(R.id.profile_image);


        fullName = findViewById(R.id.full_name_profile);
        email = findViewById(R.id.email_profile);
        phoneNo = findViewById(R.id.phone_no_profile);
        password = findViewById(R.id.password_profile);
        update = findViewById(R.id.btnUpdate);



        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount != null){
            name.setText(signInAccount.getDisplayName());
            mail.setText(signInAccount.getEmail());
            photoUrl = signInAccount.getPhotoUrl();
            Glide
                    .with(this)
                    .load(photoUrl)
                    .centerCrop()
                    .into(img);
//            fullName.setTe(signInAccount.getDisplayName());
        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("25474765805-e0j3e6jb03nskas1g39te5046gj6lffp.apps.googleusercontent.com")
                        .requestEmail()
                        .build();


                // Build a GoogleSignInClient with the options specified by gso.
                mGoogleSignInClient = GoogleSignIn.getClient(Profile.this, gso);

                mGoogleSignInClient.signOut();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        update.setOnClickListener(v -> {
            update();
        });

        showAllUserData();
    }

    private void showAllUserData(){
        Intent intent = getIntent();
        _USERNAME = intent.getStringExtra("username");
        _NAME = intent.getStringExtra("name");
        _EMAIL = intent.getStringExtra("email");
        _PHONENO = intent.getStringExtra("phoneNo");
        _PASSWORD = intent.getStringExtra("password");

        fullName.getEditText().setText(_NAME);
        email.getEditText().setText(_EMAIL);
        phoneNo.getEditText().setText(_PHONENO);
        password.getEditText().setText(_PASSWORD);


        

    }

    public void update(){

//        if(isNameChanged() || isPasswordChanged()){
            Map<String,Object> mapValue = new HashMap<>();
            mapValue.put("email", email.getEditText().getText().toString());
            mapValue.put("name",name.getText().toString());
            mapValue.put("password",password.getEditText().getText().toString());
            mapValue.put("phoneNo",phoneNo.getEditText().getText().toString());
            mapValue.put("username","username");
            DatabaseReference dbf = reference.child("testUpdate");
            dbf.updateChildren(mapValue);
//            Toast.makeText(this,"Data has been updated", Toast.LENGTH_LONG).show();
//        }
//        else Toast.makeText(this,"Data ise same and can not be update", Toast.LENGTH_LONG).show();


    }

    private boolean isNameChanged() {
        if( !_NAME.equals(fullName.getEditText().getText().toString())){
            reference.child("testUpdate").child("name").setValue(fullName.getEditText().getText().toString());
            return true;
        }else{
            return false;
        }
    }

    private boolean isPasswordChanged() {
        if( !_PASSWORD.equals(password.getEditText().getText().toString())){
            reference.child("testUpdate").child("password").setValue(password.getEditText().getText().toString());
            return true;
        }else{
            return false;
        }
    }


}