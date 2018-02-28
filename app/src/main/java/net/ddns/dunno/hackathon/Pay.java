package net.ddns.dunno.hackathon;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class Pay extends AppCompatActivity implements View.OnClickListener {

    private EditText codeText;
    private Button buttonPay;

    private DatabaseReference root;
    private DatabaseReference user;
    private FirebaseAuth firebaseAuth;
    private String code;
    private String comandaCurenta;
    private boolean stepTwo;
    private boolean firstChange;

    SurfaceView cameraPreview;
    TextView textResult;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;

    private ProgressDialog loadingDialog;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        loadingDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        codeText = findViewById(R.id.user_PumpCode);
        buttonPay = findViewById(R.id.buttonPay);

        buttonPay.setOnClickListener(this);

        cameraPreview = findViewById(R.id.cameraPreview);
        textResult = findViewById(R.id.textResult);
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Pay.this,
                            new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(qrcodes.size() != 0) {
                    textResult.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                            textResult.setText(qrcodes.valueAt(0).displayValue);
                        }
                    });
                }
            }
        });


    }

    @Override
    public void onClick(View view) {

        if (view == buttonPay) {
            code = codeText.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(this, "Please enter a code!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                root = FirebaseDatabase.getInstance().getReference().child("Pumps").child("ComandaCurenta");
                loadingDialog.setMessage("Working on it...");
                loadingDialog.show();
                root.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        comandaCurenta = dataSnapshot.getValue().toString();
                        if(comandaCurenta.equals("Ready")) {
                            Transaction noua = new Transaction();
                            noua.setUID(firebaseAuth.getUid());
                            user = FirebaseDatabase.getInstance().getReference().child("Pumps").child("Tranzactie");
                            user.setValue(noua);
                            root.setValue("Waiting");
                            Toast.makeText(Pay.this, "Poti alimenta!", Toast.LENGTH_SHORT).show();

                        } else {
                            root.removeEventListener(this);
                            Toast.makeText(Pay.this, "Incearca din nou sau alimenteaza normal!", Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.cancel();
                        /*


                        user = FirebaseDatabase.getInstance().getReference().child("Pumps").child(comandaCurenta);
                        stepTwo = false;
                        user.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Transaction t = dataSnapshot.getValue(Transaction.class);
                                if(!stepTwo) {
                                    if (TextUtils.isEmpty(t.getUID())) {
                                        t.setUID(firebaseAuth.getUid());
                                        user.setValue(t);
                                        stepTwo = true;
                                        firstChange = false;
                                        //user.removeEventListener(this);
                                    } else {
                                        if(!firstChange){
                                            firstChange = true;
                                            return;
                                        }
                                        Toast.makeText(Pay.this, "Comanda deja activata!", Toast.LENGTH_SHORT).show();
                                        user.removeEventListener(this);
                                        loadingDialog.cancel();
                                    }
                                } else {
                                    if(t.getStatus().equals("Completed")) {
                                        Toast.makeText(Pay.this, "Plata efectuata!", Toast.LENGTH_SHORT).show();
                                        user.removeEventListener(this);
                                        loadingDialog.cancel();
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        */
                        //root.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                /*
                if (!comandaCurenta.isEmpty()) {
                    user = FirebaseDatabase.getInstance().getReference().child("Pumps").child(comandaCurenta);
                    user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Transaction t = dataSnapshot.getValue(Transaction.class);
                            if (TextUtils.isEmpty(t.getUID())) {
                                t.setUID(firebaseAuth.getUid());
                                user.removeEventListener(this);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //loadingDialog.show();
                    //validate();
                }
                */



            /*
            code = codeText.getText().toString().trim();
            if (TextUtils.isEmpty(code)) {
                Toast.makeText(this, "Please enter a code!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                root = FirebaseDatabase.getInstance().getReference().child("Pumps").child(code);
                user = FirebaseDatabase.getInstance().getReference().child("UIDS").child(firebaseAuth.getUid()).child("Tranzactii");
                loadingDialog.show();
                validate();
            }
            */
            }
        }
    }
    private void validate(){
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                Transaction stare = dataSnapshot.getValue(Transaction.class);
                if(stare.getStatus().equals("Pending")){
                    Toast.makeText(Pay.this, "Payment Successful!", Toast.LENGTH_SHORT).show();
                    stare.setStatus("Completed");
                    root.setValue(stare);
                    user.child(stare.getTransactionID()).setValue(stare);
                    finish();
                    startActivity(new Intent(Pay.this, ProfileActivity.class));

                } else if(!stare.equals("Completed")) {
                    Toast.makeText(Pay.this, "Cod invalid!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Pay.this, "Plata deja efectuata!", Toast.LENGTH_SHORT).show();
                }
                loadingDialog.cancel();
                */

                root.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
