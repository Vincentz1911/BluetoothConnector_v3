package com.vincentz.bluetoothconnectorv3;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.*;
import android.view.*;
import android.content.*;
import android.bluetooth.*;
import java.util.ArrayList;
import java.util.Set;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

    ImageView enable_bt;
    ListView BTDevices_list;

    private BluetoothAdapter BA;
    private BTDeviceAdapter mAdapter;

    public ArrayList<BTDeviceModel> BTDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BTDevices_list = findViewById(R.id.BTDevices_list);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {BA.startDiscovery();
            }
        });

        InitGUI();
        InitBluetooth();

        //Search for new BT
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);

        BA.startDiscovery();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_visible) {
            Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            startActivityForResult(getVisible, 0);
            makeText(MainActivity.this, "Visible for 2 min", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void InitGUI() {
        enable_bt = findViewById(R.id.enable_bt);

        enable_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (BA.isEnabled()) {
                    BA.disable();
                   // makeText(MainActivity.this, "Turning Bluetooth off", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intentOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intentOn, 0);
                    // makeText(MainActivity.this, "Turning Bluetooth On", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BTDevices_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                makeText(MainActivity.this, "Pairing bluetooth device " + id + "/" + position, Toast.LENGTH_SHORT).show();

                if (BA.isDiscovering()) {BA.cancelDiscovery();}

                BluetoothDevice bt = BTDevices.get(position).getDevice();
                bt.createBond();
            }
        });
    }


    private void InitBluetooth() {
        //Checking if have bluetooth
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            makeText(this, "Bluetooth not supported", Toast.LENGTH_LONG).show();
            finish();
        }
        //Enabling bluetooth
        BA = BluetoothAdapter.getDefaultAdapter();
        if (!BA.isEnabled()) {
            makeText(this, "Turning on Bluetooth", Toast.LENGTH_LONG).show();
            BA.enable();
        }
        //Naming local bluetooth
        if (BA.getName() == null) getSupportActionBar().setTitle(BA.getName());
        else getSupportActionBar().setTitle(BA.getName());
//        if (BA.getName() == null) name_bt.setText(BA.getAddress());
//        else name_bt.setText(BA.getName());

        if (BA.isEnabled()); //enable_bt.setChecked(true);
    }

    private void BondedDevices() {
        Set<BluetoothDevice> pairedSet = BA.getBondedDevices();
        for (BluetoothDevice bt : pairedSet) {
            boolean bonded = false;
            boolean connected = false;
            if (bt.getBondState() == 12) bonded = true;

            BTDevices.add(new BTDeviceModel(bt.getName(), bt.getAddress(), bt, true, connected, R.drawable.ic_action_bluetooth_paired));
        }
        UpdateListView();
    }

    private void UpdateListView() {
        mAdapter = new BTDeviceAdapter(this, BTDevices);
        BTDevices_list.setAdapter(mAdapter);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                makeText(MainActivity.this, "Scanning for bluetooth devices", Toast.LENGTH_LONG).show();
                BondedDevices();
                UpdateListView();

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
                makeText(MainActivity.this, "Finished scanning", Toast.LENGTH_LONG).show();
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                //foundDevicesSet = (Set<BluetoothDevice>) foundDevices;

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //if device is already in list break
                //if (foundNames.contains(device.getAddress())) return;
                if (BTDevices.contains(device.getAddress())) return;

                //Checks if Name is null and not already in deviceList
                String name = device.getName();
                if (name == null) name = device.getAddress();
                BTDevices.add(new BTDeviceModel(name, device.getAddress(), device, false, false, 0));
                makeText(MainActivity.this, "Found device " + device.getName(), Toast.LENGTH_SHORT).show();

                UpdateListView();
            }
        }
    };
}
