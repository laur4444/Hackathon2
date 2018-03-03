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
    private String comandaCurenta;
    private boolean handShake = true;
    private boolean stepTwo;
    private boolean firstChange = true;
    private int noOfChanges = 0;

    private String code;

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
        buttonPay = findViewById(R.id.buttonPay);

        buttonPay.setOnClickListener(this);

        cameraPreview = findViewById(R.id.cameraPreview);

        textResult = findViewById(R.id.textResult);
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1000, 1000)
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

                            code = qrcodes.valueAt(0).displayValue;
                            //if (isValidCode(code))
                            textResult.setText(code);
                            if(code.equals("Pump1") && handShake){
                                handShake = false;
                                firstChange = true;
                                tryToPay();
                            }
                        }
                    });
                }
            }
        });


    }

    private boolean isValidCode(String code){
        String IDS, IDP;
        IDS = code.substring(0, 3);
        IDP = code.substring(5, 6);
        root = FirebaseDatabase.getInstance().getReference().child("Pumps").child(IDS).child(IDP);
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return true;
    }

    private void tryToPay() {
        loadingDialog.setMessage("Working on it...");
        loadingDialog.show();
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                comandaCurenta = dataSnapshot.getValue().toString();
                if (comandaCurenta.equals("Ready")) {
                    if(firstChange) {
                        Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(1000);
                        firstChange = false;
                        Transaction noua = new Transaction();
                        noua.setUID(firebaseAuth.getUid());
                        user = FirebaseDatabase.getInstance().getReference().child(code).child("Transaction");
                        user.setValue(noua);
                        root.setValue("Waiting");
                        Toast.makeText(Pay.this, "Poti alimenta!", Toast.LENGTH_SHORT).show();
                    } else {
                        handShake = true;
                        Toast.makeText(Pay.this, "Plata Efectuata!", Toast.LENGTH_SHORT).show();
                        root.removeEventListener(this);
                    }
                } else {
                    if(firstChange) {
                        root.removeEventListener(this);
                        Toast.makeText(Pay.this, "Incearca din nou sau alimenteaza normal!", Toast.LENGTH_SHORT).show();
                    }
                }
                loadingDialog.cancel();




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        if (view == buttonPay) {
                Transaction newTransaction = new Transaction();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("UIDS").child(firebaseAuth.getUid()).child("Tranzactii").child("Test");
                ref.setValue(newTransaction);
        }
    }
}
