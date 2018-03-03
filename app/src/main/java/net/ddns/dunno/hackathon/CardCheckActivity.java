package net.ddns.dunno.hackathon;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.craftman.cardform.Card;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CardCheckActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_check);
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            change(LoginActivity.class);
        }

        ref = FirebaseDatabase.getInstance().getReference().child("UIDS").child(firebaseAuth.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("card")){
                    ref.removeEventListener(this);
                    change(ProfileActivity.class);
                } else {
                    ref.removeEventListener(this);
                    change(CardFormActivity.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private void change(Class myClass) {
        startActivity(new Intent(this, myClass));
    }
}
