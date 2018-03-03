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
    Card card;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_check);
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null) {
            change(LoginActivity.class);
        }

        ref = FirebaseDatabase.getInstance().getReference().child("UIDS").child(firebaseAuth.getUid()).child("card");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                card = dataSnapshot.getValue(Card.class);
                if(card.getName().isEmpty()){
                    change(CardFormActivity.class);
                } else {
                    change(ProfileActivity.class);
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
