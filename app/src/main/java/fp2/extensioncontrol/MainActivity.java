package fp2.extensioncontrol;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.util.Map;

/**
 * Control the Arduino board connected to the phone.
 */
public class MainActivity extends AppCompatActivity {
    private static final String ACTION_USB_PERMISSION = "fp2.extensioncontrol.USB_PERMISSION";
    private static final int ARDUINO_VENDOR_ID = 9025;
    private TempSensorFragment tempSensorFragment;

    // USB serial library
    private UsbManager usbManager;
    private UsbDevice device;
    private UsbSerialDevice serialPort;
    private UsbDeviceConnection connection;

    /**
     * Broadcast receiver to automatically start and stop the serial connection.
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                Bundle extras = intent.getExtras();
                boolean granted = extras != null && extras.getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);

                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);

                    if (serialPort != null) {
                        if (serialPort.open()) {
                            // Set Serial Connection Parameters.
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(tempSensorFragment.receiveCallback);

                            Toast.makeText(getApplicationContext(), "Serial port opened.", Toast.LENGTH_LONG).show();
                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                onClickConnect();
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                onClickDisconnect();
            }
        }
    };

    /**
     * Load all UI components and the intent filter.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        // Setup the view pager which contains the tabs.
        ViewPager viewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        usbManager = (UsbManager) getSystemService(USB_SERVICE);

        // Intent filter for USB device connect/disconnect.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_USB_PERMISSION);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, intentFilter);

        // Add all fragments as tabs
        viewPagerAdapter.addFrag(new LedFragment(), "LED");
        viewPagerAdapter.addFrag(new ColorPickerFragment(), "Color picker");
        tempSensorFragment = new TempSensorFragment();
        viewPagerAdapter.addFrag(tempSensorFragment, "Temp Sensor");
        viewPagerAdapter.addFrag(new IRRemoteFragment(), "IR Remote");
        viewPagerAdapter.notifyDataSetChanged();
    }

    /**
     * Add the serial connect switch to the menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Add the serial connect switch.
        MenuItem item = menu.findItem(R.id.menu_switch);
        SwitchCompat serialSwitch = item.getActionView().findViewById(R.id.serial_switch);
        serialSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                onClickConnect();
            } else {
                onClickDisconnect();
            }
        });
        return true;
    }

    /**
     * Request the usb permission for all Arduino USB devices.
     */
    private void onClickConnect() {
        Map<String, UsbDevice> usbDevices = usbManager.getDeviceList();

        for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
            device = entry.getValue();

            if (device.getVendorId() == ARDUINO_VENDOR_ID) {
                PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                usbManager.requestPermission(device, pi);
                break;
            } else {
                connection = null;
                device = null;
            }
        }
    }

    /**
     * Close the serial port and show a toast.
     */
    private void onClickDisconnect() {
        serialPort.close();
        serialPort = null;
        Toast.makeText(getApplicationContext(), "Serial port closed.", Toast.LENGTH_LONG).show();
    }

    /**
     * Get the usb serial device.
     */
    public UsbSerialDevice getSerialPort() {
        return serialPort;
    }
}