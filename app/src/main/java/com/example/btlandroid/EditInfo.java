package com.example.btlandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditInfo extends AppCompatActivity {

    private static final int GALLERY_INTENT_CODE = 1023 ;

    TextInputLayout company, position,id, address;
    Button logout,update, resetPassLocal,changeProfileImage;
    FirebaseUser user;
    FirebaseAuth mAuth;


    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");



        company = findViewById(R.id.full_name_profile);
        position = findViewById(R.id.email_profile);
        id = findViewById(R.id.phone_no_profile);
        address = findViewById(R.id.password_profile);
        update = findViewById(R.id.btnUpdate);


        update.setOnClickListener(v -> {
            update();
        });

    }

    public void update() {
        Map<String, Object> mapValue = new HashMap<>();
        mapValue.put("Company", company.getEditText().getText().toString());
        mapValue.put("Position", position.getEditText().getText().toString());
        mapValue.put("Address", address.getEditText().getText().toString());
        mapValue.put("ID", id.getEditText().getText().toString());
        DatabaseReference dbf = reference.child(mAuth.getCurrentUser().getUid());
        dbf.updateChildren(mapValue);
        Toast.makeText(EditInfo.this, "Đã lưu.", Toast.LENGTH_SHORT).show();
    }
}