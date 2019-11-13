package com.example.bluetooth_app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter myBluetooth;
    private Set<BluetoothDevice> pairedDevices;
    Button btn_Toggle;
    Button btn_Paired;
    ListView pairedList;
    public static String EXTRA_ADRESS = "device_address";
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        btn_Toggle = findViewById(R.id.btnToggle);
        pairedList = findViewById(R.id.lvdevices);

        btn_Toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleBluetooth();
            }

        });

        btn_Paired = findViewById(R.id.btnPaired);

        btn_Paired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDevice();
            }

        });
    }

    private void toggleBluetooth() {

        if (myBluetooth == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth Cihazı Yok!1", Toast.LENGTH_LONG).show();
        }

        if (myBluetooth.isEnabled()) {
            Intent enableBTintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTintent);
        }
        if (!myBluetooth.isEnabled()) {
            myBluetooth.disable();
        }
    }

    private void listDevice() {

        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        } else {
            Toast.makeText(getApplicationContext(), "Eşleşmiş Cihaz Yok", Toast.LENGTH_SHORT).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, list);

        pairedList.setAdapter(adapter);

        pairedList.setOnItemClickListener(selectDevice);


    }

    public AdapterView.OnItemClickListener selectDevice = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);

            Intent comintent = new Intent(MainActivity.this, Communication.class);

            comintent.putExtra(EXTRA_ADRESS, address);
            startActivity(comintent);
        }

    };


}
