package it.adrian.fixme.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.adrian.fixme.R;
import it.adrian.fixme.model.Car;
import it.adrian.fixme.tb.BTDeviceHelper;
import it.adrian.fixme.obd.ObdHelper;

public class RequestActivity extends AppCompatActivity {

    private static final String TAG = RequestActivity.class.getSimpleName();

    private Context c;
    private Button sendBtn;
    private ProgressBar progressBar;
    private Spinner deviceSpinner;
    private Spinner languageSpinner;
    private BTDeviceHelper btDeviceHelper;
    private Boolean test = false;
    private ObdHelper obdHelper;
    private Spinner deviceTestSpinner;
    private Car car;

    private String chosenDevice;
    private String chosenLanguage;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        MyArrayAdapter adapter = (MyArrayAdapter) deviceSpinner.getAdapter();
                        if (adapter != null) {
                            adapter.clear();
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    case BluetoothAdapter.STATE_ON:
                        populateDeviceSpinner();
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        c = this.getApplicationContext();

        car = (Car) getIntent().getSerializableExtra("key");

        int bluetoothStatus;

        if (!test)
            bluetoothStatus = ObdHelper.checkBluetoothEnabled();

        else
            bluetoothStatus = BTDeviceHelper.checkBluetoothEnabled();

        if (bluetoothStatus == -1) {
            Log.d(TAG, "No BT support.");
        } else if (bluetoothStatus == 0) {
            Log.d(TAG, "Enabling bluetooth");
            makeBluetoothRequest();
        }

        initViewElements();

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    private void makeBluetoothRequest() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                ActivityCompat.requestPermissions(RequestActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
            }
        }
        startActivityForResult(enableBtIntent, 1234);
    }

    private void initViewElements() {
        sendBtn = (Button) findViewById(R.id.button);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        deviceSpinner = (Spinner) findViewById(R.id.device_spinner);
        deviceTestSpinner = (Spinner) findViewById(R.id.device_test_spinner);

        populateTestSpinner();

        if (!test) {
            if (ObdHelper.checkBluetoothEnabled() == 1) {
                populateDeviceSpinner();
            }
        } else {

            if (BTDeviceHelper.checkBluetoothEnabled() == 1) {
                populateDeviceSpinner();
            }
        }

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBtn.setEnabled(false);
                if (!test) {
                    obdHelper = new ObdHelper(mHandler, RequestActivity.this);
                    progressBar.setProgress(0);
                    obdHelper.connectToDevice();
                } else {
                    btDeviceHelper = new BTDeviceHelper(mHandler, RequestActivity.this);
                    progressBar.setProgress(0);
                    btDeviceHelper.connectToDevice();
                }

            }
        });
    }

    private void populateDeviceSpinner() {
        List<BluetoothDevice> devices = new ArrayList<>();
        Set<BluetoothDevice> deviceSet;
        if (!test) {
            deviceSet = ObdHelper.getPairedDevice(this);
        } else
            deviceSet = BTDeviceHelper.getPairedDevice();

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            {
                ActivityCompat.requestPermissions(RequestActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
            }
        }
        devices.addAll(deviceSet);
        final MyArrayAdapter deviceAdapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_1, devices);
        deviceSpinner.setAdapter(deviceAdapter);
        deviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                chosenDevice = ((BluetoothDevice) parent.getItemAtPosition(pos)).getAddress();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void populateTestSpinner() {
        List<String> testDevices = new ArrayList<>();
        testDevices.add("OBD2");
        testDevices.add("Android device");
        final ArrayAdapter testDeviceAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, testDevices);
        deviceTestSpinner.setAdapter(testDeviceAdapter);
        deviceTestSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 1) {
                    test = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    private class MyArrayAdapter extends ArrayAdapter {
        private MyArrayAdapter(Context context, int resource, List list) {
            super(context, resource, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            BluetoothDevice device = (BluetoothDevice) getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            TextView data = (TextView) convertView.findViewById(android.R.id.text1);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                {
                    ActivityCompat.requestPermissions(RequestActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                }

            }
            data.setText(device.getName());
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            BluetoothDevice device = (BluetoothDevice) getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            TextView data = (TextView) convertView.findViewById(android.R.id.text1);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                {
                    ActivityCompat.requestPermissions(RequestActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                }
            }
            data.setText(device.getName());
            return convertView;
        }
    }

    public String getChosenDevice() {
        return chosenDevice;
    }


    public void startResultActivity(String dtcs) {
        Intent intent = new Intent(c, ResponseActivity.class);
        Bundle extras = new Bundle();

        Log.d("RequestActivity",dtcs);

        extras.putString("EXTRA_DTCS", dtcs);
        intent.putExtras(extras);
        intent.putExtra("key", car);
        startActivity(intent);
        resetAcitivityState();
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    private static final int NO_BLUETOOTH_DEVICE_SELECTED = 0;
    private static final int CANNOT_CONNECT_TO_DEVICE = 1;
    private static final int NO_DATA = 3;
    private static final int OBD_COMMAND_FAILURE = 10;
    private static final int OBD_COMMAND_FAILURE_IO = 11;
    private static final int OBD_COMMAND_FAILURE_UTC = 12;
    private static final int OBD_COMMAND_FAILURE_IE = 13;
    private static final int OBD_COMMAND_FAILURE_MIS = 14;
    private static final int OBD_COMMAND_FAILURE_NODATA = 15;

    private Handler mHandler = new Handler(new Handler.Callback() {

        public boolean handleMessage(Message msg) {
            Log.d(TAG, "Message received on handler");
            switch (msg.what) {
                case NO_BLUETOOTH_DEVICE_SELECTED:
                    makeToast(getString(R.string.text_bluetooth_nodevice));
                    break;
                case CANNOT_CONNECT_TO_DEVICE:
                    makeToast(getString(R.string.text_bluetooth_error_connecting));
                    break;
                case OBD_COMMAND_FAILURE:
                    makeToast(getString(R.string.text_obd_command_failure));
                    break;
                case OBD_COMMAND_FAILURE_IO:
                    makeToast(getString(R.string.text_obd_command_failure) + " IO");
                    break;
                case OBD_COMMAND_FAILURE_IE:
                    makeToast(getString(R.string.text_obd_command_failure) + " IE");
                    break;
                case OBD_COMMAND_FAILURE_MIS:
                    makeToast(getString(R.string.text_obd_command_failure) + " MIS");
                    break;
                case OBD_COMMAND_FAILURE_UTC:
                    makeToast(getString(R.string.text_obd_command_failure) + " UTC");
                    break;
                case OBD_COMMAND_FAILURE_NODATA:
                    makeToastLong(getString(R.string.text_noerrors));
                    break;
                case NO_DATA:
                    makeToast(getString(R.string.text_dtc_no_data));
                    break;
            }
            resetAcitivityState();
            return false;
        }
    });

    private void resetAcitivityState() {
        progressBar.setProgress(0);
        sendBtn.setEnabled(true);
    }

    private void makeToast(String text) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void makeToastLong(String text) {
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
        toast.show();
    }
}