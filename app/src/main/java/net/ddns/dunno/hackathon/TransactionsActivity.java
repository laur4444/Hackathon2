package net.ddns.dunno.hackathon;

import android.app.Activity;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TransactionsActivity extends AppCompatActivity {

    FirebaseHelper firebaseHelper;
    CustomAdaptor adaptor;
    DatabaseReference db;
    FirebaseAuth user;
    ArrayList<net.ddns.dunno.hackathon.Transaction> transactions = new ArrayList<>();



    ListView listViewTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_final);
        user = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference().child("UIDS").child(user.getUid().toString()).child("Tranzactii");
        //db = FirebaseDatabase.getInstance().getReference().child("Pumps");

        listViewTransactions = findViewById(R.id.listViewTransactions);
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.FirebaseHelper(db, adaptor);


        adaptor = new CustomAdaptor(this, firebaseHelper.retrieveData());

        listViewTransactions.setAdapter(adaptor);

        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                net.ddns.dunno.hackathon.Transaction transaction = dataSnapshot.getValue(net.ddns.dunno.hackathon.Transaction.class);
                transactions.add(transaction);
                adaptor.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }
}
