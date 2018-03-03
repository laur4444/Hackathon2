package net.ddns.dunno.hackathon;

import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Laurentiu on 2/27/2018.
 */

public class FirebaseHelper {
    DatabaseReference db;
    Boolean saved;
    ArrayList<Transaction> transactions = new ArrayList<>();
    CustomAdaptor adaptor;

    public void FirebaseHelper(DatabaseReference db, CustomAdaptor adaptor) {
        this.db = db;
        this.adaptor = adaptor;
    }

    public void saveTransaction(Transaction transaction) {
        if(transaction == null) {
            saved = false;
        } else {
            try {
                db.child("Tranzactii").setValue(" ");
            } catch (DatabaseException e) {
                e.printStackTrace();
            }
        }

    }
    private void fetchData(DataSnapshot dataSnapshot) {
        transactions.clear();
        for(DataSnapshot t: dataSnapshot.getChildren()) {
            Transaction temp = t.getValue(Transaction.class);
            transactions.add(temp);
        }
    }
    public ArrayList<Transaction> retrieveData() {
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    transactions.add(transaction);
                    //adaptor.notifyDataSetChanged();
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
        return transactions;
    }



}
