package net.ddns.dunno.hackathon;

import android.app.Activity;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    ListView listViewTransactions;
    DatabaseReference transactions;
    FirebaseAuth user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        user = FirebaseAuth.getInstance();
        listViewTransactions = findViewById(R.id.listViewTransactions);
        //transactions = FirebaseDatabase.getInstance().getReference().child("UIDS").child(user.getUid().toString()).child("Transactii");


    }

    protected void onStart(){
        super.onStart();

        transactions.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                for(DataSnapshot transactionSnapshot : dataSnapshot.getChildren()){

                }*/
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
