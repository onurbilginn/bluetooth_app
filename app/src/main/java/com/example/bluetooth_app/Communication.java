package com.example.bluetooth_app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.UUID;

public class Communication extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;

    BluetoothSocket btsocket = null;
    BluetoothDevice btdevice;
    BluetoothServerSocket mServer;

    Button LedOn, LedOff;
    ToggleButton Locked;


    private boolean isbtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        Intent newint = getIntent();

        LedOn = findViewById(R.id.btnLedOn);
        LedOff = findViewById(R.id.btnLedOff);
        Locked = findViewById(R.id.btnLocked);
        address = newint.getStringExtra(MainActivity.EXTRA_ADRESS);

        new BTbaglan().execute();
        try {
            if (myBluetooth.getState()==btsocket.getInputStream().read("1".getBytes())) {
                Locked.setChecked(true);
            }
            else if (myBluetooth.getState()==btsocket.getInputStream().read("2".getBytes())){
                Locked.setChecked(false);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        Locked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                if (b && (btsocket != null)) {

                    try {
                        btsocket.getOutputStream().write("1".getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    try {

                        btsocket.getOutputStream().write("2".getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });


        /*Locked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btsocket!=null){
                    try {
                        btsocket.getOutputStream().write("1".getBytes());
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if (btsocket!=null){
                    try {
                        btsocket.getOutputStream().write("2".getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });*/


        LedOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btsocket != null) {
                    try {
                        btsocket.getOutputStream().write("1".getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        LedOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btsocket != null) {
                    try {
                        btsocket.getOutputStream().write("2".getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void Disconnect() {

        if (btsocket != null) {
            try {
                btsocket.close();
            } catch (IOException e) {
                //msg("Error");
            }
        }

        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Disconnect();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }


    private class BTbaglan extends AsyncTask<Void, Void, Void> {

        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Communication.this, "Bağlanıyor...", "Lütfen Bekleyin");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (btsocket == null || isbtConnected) {

                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    btdevice = myBluetooth.getRemoteDevice(address);
                    btsocket = btdevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btsocket.connect();


                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!ConnectSuccess) {
                Toast.makeText(Communication.this, "Bağlantı Hatası Tekrar Deneyiniz", Toast.LENGTH_SHORT).show();

                finish();
            } else {
                Toast.makeText(Communication.this, "Bağlanı Başarılı", Toast.LENGTH_SHORT).show();
                isbtConnected = true;
            }
            progress.dismiss();
        }
    }
}