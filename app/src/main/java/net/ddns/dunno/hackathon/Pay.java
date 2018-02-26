package net.ddns.dunno.hackathon;

import android.content.Intent;
import android.renderscript.Sampler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Pay extends AppCompatActivity implements View.OnClickListener{

    private EditText codeText;
    private Button buttonPay;

    private DatabaseReference root;
    private DatabaseReference user;
    private FirebaseAuth firebaseAuth;
    private String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        firebaseAuth = FirebaseAuth.getInstance();

        codeText = findViewById(R.id.user_PumpCode);
        buttonPay = findViewById(R.id.buttonPay);

        buttonPay.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if(view == buttonPay) {
            code = codeText.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(this, "Please enter a code!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                root = FirebaseDatabase.getInstance().getReference().child("Pumps").child(code).child("Stare");
                user = FirebaseDatabase.getInstance().getReference().child("UIDS").child(firebaseAuth.getUid()).child("Tranzactii");
                validate();
            }
        }
    }
    private void validate(){
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String stare = dataSnapshot.getValue(String.class);
                if(stare.equals("Pending")){
                    Toast.makeText(Pay.this, "Payment Successful!", Toast.LENGTH_SHORT).show();
                    root.setValue("Completed");
                    user.child(code).child("Progres").setValue("Am reusit");
                    finish();
                    startActivity(new Intent(Pay.this, ProfileActivity.class));

                } else if(!stare.equals("Completed")) {
                    Toast.makeText(Pay.this, "Cod invalid!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Pay.this, "Plata deja efectuata!", Toast.LENGTH_SHORT).show();
                }
                root.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
