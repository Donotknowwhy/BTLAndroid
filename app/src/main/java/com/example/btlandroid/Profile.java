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
import com.example.btlandroid.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
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


public class Profile extends AppCompatActivity {

    private static final int GALLERY_INTENT_CODE = 1023 ;
    TextView name, mail, fullName1,email1,phone1,verifyMsg1;
    TextInputLayout fullName,email,phoneNo,password;
    Button logout,update, resetPassLocal,changeProfileImage;
    ImageView img;
    FirebaseUser user;
    FirebaseAuth mAuth;
    Uri photoUrl;
    private GoogleSignInClient mGoogleSignInClient;
    String _USERNAME, _EMAIL, _PHONENO, _PASSWORD, _NAME;

    DatabaseReference reference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");
        changeProfileImage = findViewById(R.id.changeProfile);
        resetPassLocal = findViewById(R.id.resetPasswordLocal);

        logout = findViewById(R.id.logout);
        name = findViewById(R.id.name);
        mail = findViewById(R.id.mail);
        img = (ImageView) findViewById(R.id.profile_image);


        fullName = findViewById(R.id.full_name_profile);
        email = findViewById(R.id.email_profile);
        phoneNo = findViewById(R.id.phone_no_profile);
        password = findViewById(R.id.password_profile);
        update = findViewById(R.id.btnUpdate);

        storageReference = FirebaseStorage.getInstance().getReference();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            StorageReference profileRef = storageReference.child("users/"+user.getUid()+"/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Log.d("AppLog",uri.toString());
                    Picasso.get().load(uri).into(img);
                }
            });
            name.setText(user.getDisplayName());
            mail.setText(user.getEmail());

        }else {

            GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
            if (signInAccount != null) {
                name.setText(signInAccount.getDisplayName());
                mail.setText(signInAccount.getEmail());
                photoUrl = signInAccount.getPhotoUrl();
                Glide
                        .with(this)
                        .load(photoUrl)
                        .centerCrop()
                        .into(img);

                Log.d("url", String.valueOf(photoUrl));
//            fullName.setTe(signInAccount.getDisplayName());
            }
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



        changeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                Intent i = new Intent(v.getContext(),EditProfile.class);
            i.putExtra("fullName", name.getText().toString());
            i.putExtra("email", mail.getText().toString());
            i.putExtra("phone","1234");

                startActivity(i);
            }
        });

        resetPassLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetPassword = new EditText(v.getContext());

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter New Password > 6 Characters long.");
                passwordResetDialog.setView(resetPassword);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        Log.d("user", String.valueOf(user));
                        String newPassword = resetPassword.getText().toString();
                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Profile.this, "Password Reset Successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Profile.this, "Password Reset Failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close
                    }
                });

                passwordResetDialog.create().show();

            }
        });

    }


    public void update() {
        Map<String, Object> mapValue = new HashMap<>();
        mapValue.put("Company", fullName.getEditText().getText().toString());
        mapValue.put("Position", email.getEditText().getText().toString());
        mapValue.put("Address", password.getEditText().getText().toString());
        mapValue.put("ID", phoneNo.getEditText().getText().toString());
        DatabaseReference dbf = reference.child(mAuth.getCurrentUser().getUid());
        dbf.updateChildren(mapValue);
        Toast.makeText(Profile.this, "Saved.", Toast.LENGTH_SHORT).show();
    }
}