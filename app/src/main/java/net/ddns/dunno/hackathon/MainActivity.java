package net.ddns.dunno.hackathon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Text;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText emailText;
    private EditText passwordText;
    private TextView buttonSignin;
    private ProgressDialog loadingDialog;
    private TextView referralEmailText;
    private String referralEmail;
    private String email;
    private String password;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRootRef;
    private DatabaseReference mChildRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();


        if(firebaseAuth.getCurrentUser() != null){
            // start profile activity
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        buttonRegister = findViewById(R.id.user_Register);
        buttonSignin = findViewById(R.id.user_Login);

        emailText = findViewById(R.id.user_Email);
        passwordText = findViewById(R.id.user_Password);
        referralEmailText = findViewById(R.id.referral_Email);

        loadingDialog = new ProgressDialog(this);



        buttonRegister.setOnClickListener(this);
        buttonSignin.setOnClickListener(this);

    }

    private void registerUser() {
        email = emailText.getText().toString().trim();
        password = passwordText.getText().toString().trim();
        referralEmail = referralEmailText.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingDialog.setMessage("Registering User...");
        loadingDialog.show();
        if(!TextUtils.isEmpty(referralEmail) && isValidEmaillId(referralEmail) == true){
            firebaseAuth.fetchProvidersForEmail(referralEmail).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                    if (task.getResult().getProviders().size() > 0) {
                        Toast.makeText(MainActivity.this, "Referral exists!", Toast.LENGTH_SHORT).show();
                        adduser(email, password);
                    } else {
                        Toast.makeText(MainActivity.this, "Referral doesn't exist!", Toast.LENGTH_SHORT).show();
                        //adduser(email, password);
                    }
                }
            });
        } else {
            referralEmail = "";
            adduser(email, password);
        }



    }

    private void adduser(String email, String password) {
        //loadingDialog.setMessage("Registering User...");
        //loadingDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                            mChildRef = mRootRef.child("UIDS").child(firebaseAuth.getUid().toString()).child("Referral");
                            mChildRef.setValue(referralEmail);
                            loadingDialog.cancel();
                        } else {
                            Toast.makeText(MainActivity.this, "Could not register!", Toast.LENGTH_SHORT).show();
                            loadingDialog.cancel();

                        }

                    }
                });
    }

    private boolean isValidEmaillId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public void onClick(View view){
        if(view == buttonRegister) {
            registerUser();
        }
        if(view == buttonSignin) {
            // go to sign in activity
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
