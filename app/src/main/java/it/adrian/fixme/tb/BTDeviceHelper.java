package it.adrian.fixme.tb;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import it.adrian.fixme.ui.RequestActivity;

/**
 * Created by adrian on 19/07/2017.
 */

public class BTDeviceHelper {

    private static final String TAG = BTDeviceHelper.class.getSimpleName();
    private static final UUID MY_UUID = UUID.fromString("ec79da00-853f-11e4-b4a9-0800200c9a66");

    private static final int CANNOT_CONNECT_TO_DEVICE = 1;

    private RequestActivity requestActivity;
    private ProgressBar progressBar;
    private Handler mHandler;

    private static BluetoothAdapter bluetoothAdapter;
    private static Set<BluetoothDevice> pairedDevices;

    private GetTroubleCodesTask gtct;

    private final Runnable mQueueCommands = new Runnable() {
        public void run() {
            new Handler().postDelayed(mQueueCommands, 400);
        }
    };

    public BTDeviceHelper(Handler mHandler, RequestActivity requestActivity) {
        this.requestActivity = requestActivity;
        this.mHandler = mHandler;
        gtct = new BTDeviceHelper.GetTroubleCodesTask();
    }

    public void connectToDevice() {
        String remoteDevice = requestActivity.getChosenDevice();
        if (remoteDevice == null | "".equals(remoteDevice))
            Log.e(TAG, "No bt device is paired.");
        else
            gtct.execute(remoteDevice);
    }

    private static void connectViaBluetooth() {
        if (bluetoothAdapter == null)
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static int checkBluetoothEnabled() {
        connectViaBluetooth();
        if (bluetoothAdapter == null)
            return -1;
        else if (!bluetoothAdapter.isEnabled()) {
            return 0;
        } else {
            return 1;
        }
    }

    public static Set<BluetoothDevice> getPairedDevice() {
        if (bluetoothAdapter == null)
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON)
            pairedDevices = bluetoothAdapter.getBondedDevices();
        return pairedDevices;
    }

    private class GetTroubleCodesTask extends AsyncTask<String, Integer, String> {

        private InputStream mmInStream;
        private OutputStream mmOutStream;

        @Override
        protected void onPreExecute() {
            progressBar = requestActivity.getProgressBar();
            progressBar.setMax(8);
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "";
            BluetoothDevice dev;

            synchronized (this) {
                Log.d(TAG, "Starting service..");
                final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                dev = btAdapter.getRemoteDevice(params[0]);

                Log.d(TAG, "Stopping Bluetooth discovery.");
                btAdapter.cancelDiscovery();

                Log.d(TAG, "Starting OBD connection..");
                BluetoothSocket sock;
                // Instantiate a BluetoothSocket for the remote device and connect it.
                try {
                    sock = connect(dev);
                } catch (Exception e) {
                    Log.e(TAG, "There was an error while establishing connection. -> " + e.getMessage());
                    Log.d(TAG, "Message received on handler here");
                    mHandler.obtainMessage(CANNOT_CONNECT_TO_DEVICE).sendToTarget();
                    return null;
                }

                try {
                    InputStream tmpIn = null;
                    OutputStream tmpOut = null;

                    // Get the BluetoothSocket input and output streams
                    try {
                        tmpIn = sock.getInputStream();
                        tmpOut = sock.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mmInStream = tmpIn;
                    mmOutStream = tmpOut;

                    byte[] buffer = new byte[5];
                    int bytes = 0;

                    try {
                        // Read from the InputStream
                        bytes = mmInStream.read(buffer);
                        String value = new String(buffer);
                        Log.d(TAG, "Letti "+bytes+" bytes: "+value);
                        result = value;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            Log.d(TAG, "result = "+result);
            return result;
        }

        private void closeSocket(BluetoothSocket sock) {
            if (sock != null)
                try {
                    sock.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d(TAG, result);
                requestActivity.startResultActivity(result);
            } else {
                Log.e(TAG, "No result (Nullpointer).");
            }
        }
    }


    /**
     * Instantiates a BluetoothSocket for the remote device and connects it.
     * See http://stackoverflow.com/questions/18657427/ioexception-read-failed-socket-might-closed-bluetooth-on-android-4-3/18786701#18786701
     *
     * @param dev The remote device to connect to
     * @return The BluetoothSocket
     * @throws IOException
     */
    private static BluetoothSocket connect(BluetoothDevice dev) throws IOException {
        BluetoothSocket sock = null;
        BluetoothSocket sockFallback;

        Log.d(TAG, "Starting Bluetooth connection..");
        try {
            sock = dev.createRfcommSocketToServiceRecord(MY_UUID);
            sock.connect();
        } catch (Exception e1) {
            Log.e(TAG, "There was an error while establishing Bluetooth connection. Falling back..", e1);
            if (sock != null) {
                Class<?> clazz = sock.getRemoteDevice().getClass();
                Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
                try {
                    Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                    Object[] params = new Object[]{Integer.valueOf(1)};
                    sockFallback = (BluetoothSocket) m.invoke(sock.getRemoteDevice(), params);
                    sockFallback.connect();
                    sock = sockFallback;
                } catch (Exception e2) {
                    Log.e(TAG, "Couldn't fallback while establishing Bluetooth connection.", e2);
                    throw new IOException(e2.getMessage());
                }
            }
        }
        return sock;
    }

}
