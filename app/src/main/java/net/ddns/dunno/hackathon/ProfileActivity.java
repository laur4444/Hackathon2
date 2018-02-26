package net.ddns.dunno.hackathon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;

    private TextView viewUserEmail;
    private Button buttonLogout;

    private TextView viewPhone;

    private TextView viewCard;
    private Button insertCard;
    private Button goToPay;
    private Button goToTransactions;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef;
    DatabaseReference mInsertCard;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        viewUserEmail = findViewById(R.id.user_name);
        buttonLogout = findViewById(R.id.user_LogOut);

        viewPhone = findViewById(R.id.user_referred);
        viewCard = findViewById(R.id.user_card);
        insertCard = findViewById(R.id.user_AddCard);
        goToPay = findViewById(R.id.user_Pay);
        goToTransactions = findViewById(R.id.user_ViewTransactions);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        viewUserEmail.setText("Welcome " + user.getEmail().toString());

        mConditionRef = mRootRef.child("UIDS").child(user.getUid().toString()).child("Referral");
        mInsertCard = mRootRef.child("UIDS").child(user.getUid().toString()).child("Card");

        buttonLogout.setOnClickListener(this);
        insertCard.setOnClickListener(this);
        goToPay.setOnClickListener(this);
        goToTransactions.setOnClickListener(this);




    }
    protected void onStart() {
        super.onStart();

        mConditionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue(String.class);
                if(TextUtils.isEmpty(text)){
                    viewPhone.setText("Referral: Nu ai folosit un email valid!");
                } else {
                    viewPhone.setText("Referral: " + text);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mInsertCard.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue(String.class);
                if(TextUtils.isEmpty(text)){
                    //nothing
                } else {
                    insertCard.setVisibility(View.INVISIBLE);
                    viewCard.setText("Card: " + text);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    @Override
    public void onClick(View view) {
        if(view == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        if(view == insertCard) {
            mInsertCard.setValue("12345678");
            insertCard.setVisibility(View.INVISIBLE);
        }

        if(view == goToPay) {
            finish();
            startActivity(new Intent(this, Pay.class));
        }

        if(view == goToTransactions) {
           // finish();
            //startActivity(new Intent(this, TransactionsActivity.class));
            DatabaseReference transactions = FirebaseDatabase.getInstance().getReference().child("Test");
            net.ddns.dunno.hackathon.Transaction test = new net.ddns.dunno.hackathon.Transaction();
            test.setPrice("10");
            test.setTransactionID("1000");
            try {
                transactions.child("numeIrelevant").setValue(test);
            } catch (DatabaseException e) {
                e.printStackTrace();
            }

        }

    }
}
