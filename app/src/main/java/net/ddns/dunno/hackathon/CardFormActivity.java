package net.ddns.dunno.hackathon;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CardFormActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference ref;
    Card myCard;

    EditText cardName;
    EditText cardNumber;
    EditText cvc;
    EditText expiryDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_form);

        cardName = findViewById(com.craftman.cardform.R.id.card_name);
        cardNumber = findViewById(com.craftman.cardform.R.id.card_number);
        cvc = findViewById(com.craftman.cardform.R.id.cvc);
        expiryDate = findViewById(com.craftman.cardform.R.id.expiry_date);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            return;
        }
        ref = FirebaseDatabase.getInstance().getReference().child("UIDS").child(firebaseAuth.getUid()).child("card");



        final CardForm cardForm = findViewById(R.id.cardform);
        TextView textDes = findViewById(R.id.payment_amount);
        Button btnPay = findViewById(R.id.btn_pay);

        textDes.setText("");
        btnPay.setText("Add card");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    return;
                }
                myCard = dataSnapshot.getValue(Card.class);
                cardName.setHint(myCard.getName());
                cardNumber.setHint(myCard.getNumber());
                cvc.setHint(myCard.getCVC());
                expiryDate.setHint(myCard.getExpMonth() + "/" + myCard.getExpYear());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(Card card) {
                ref.setValue(card);
                Toast.makeText(CardFormActivity.this, "Name :" + card.getName(), Toast.LENGTH_SHORT).show();
                change(ProfileActivity.class);
            }
        });

    }
    private void change(Class myClass) {
        startActivity(new Intent(this, myClass));
    }
}
