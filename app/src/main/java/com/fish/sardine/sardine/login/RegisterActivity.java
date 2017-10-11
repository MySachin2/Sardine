package com.fish.sardine.sardine.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fish.sardine.sardine.MainActivity;
import com.fish.sardine.sardine.R;
import com.fish.sardine.sardine.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity  {



    // UI reference
    private EditText mPhone,confirm_phone_edit,address_edit,name_edit;
    private View mLoginFormView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    MessageDigest md;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String TAG = "Register";
    private String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DatabaseReference mRef;
    String uid, role , phone;
    boolean registered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        sharedPreferences = getSharedPreferences(Utils.pref,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mRef = FirebaseDatabase.getInstance().getReference();
        if(sharedPreferences.contains("address"))
        {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        }
        name_edit = (EditText) findViewById(R.id.name);
        address_edit = (EditText) findViewById(R.id.address);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mLoginFormView = findViewById(R.id.login_form);
        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Checking for Address");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if(sharedPreferences.contains("uid")) {
            uid = sharedPreferences.getString("uid", "");
            phone = sharedPreferences.getString("phone", "");
            Log.d("UID_Register",uid);
            mRef.child("Users").orderByKey().equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    {
                        address_edit.setText(postSnapshot.child("Address").getValue(String.class));
                        name_edit.setText(postSnapshot.child("Name").getValue(String.class));
                        if(postSnapshot.hasChild("Role"))
                        {
                            role = postSnapshot.child("Role").getValue(String.class);
                            editor.putString("role", role);
                            editor.commit();
                        }
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            editor.clear();
            editor.commit();
            finish();
        }
        mEmailSignInButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                final Map<String,String>  map = new HashMap<String, String>();
                map.put("Address",address_edit.getText().toString());
                map.put("Name",name_edit.getText().toString());
                map.put("Phone",sharedPreferences.getString("phone",""));
                map.put("Role",sharedPreferences.getString("role","Customer"));
                mRef.child("Phone Reference").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(phone))
                        {
                            mRef.child("Users").child(uid).child("Address").setValue(address_edit.getText().toString(), new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    editor.putString("address",address_edit.getText().toString());
                                    mRef.child("Users").child(uid).child("Name").setValue(name_edit.getText().toString(), new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            editor.putString("name",address_edit.getText().toString());
                                            editor.commit();
                                            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                                        }
                                    });
                                }
                            });
                        }
                        else
                        {
                            mRef.child("Users").child(uid).setValue(map, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    editor.putString("address",address_edit.getText().toString());
                                    editor.putString("name",name_edit.getText().toString());
                                    editor.commit();
                                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }
        });

    }


}

