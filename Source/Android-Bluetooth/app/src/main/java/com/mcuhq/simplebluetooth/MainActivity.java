package com.mcuhq.simplebluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import com.mcuhq.simplebluetooth.FeedReaderDbHelper;

import org.w3c.dom.Text;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static jxl.Workbook.*;

public class MainActivity extends AppCompatActivity {

    // GUI Components
    private TextView today;
    private TextView status;
    private TextView count;
    private Switch diemDanhThuCong;
    private Button dssv;
    private Button listStudent;
    private Button btnSave;
    private BluetoothAdapter mBTAdapter;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;

    //Search
    private EditText editMsssv;

    private final String TAG = MainActivity.class.getSimpleName();
    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    List<Students> listStd = new ArrayList<Students>();
    FeedReaderDbHelper mDbHelper;
    int numStudent;
    boolean manual;
    String strToday;
    int countValue;
    SharedPreferences sharedPref;
    List<String> columnDay = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        countValue = sharedPref.getInt("Count", 1);

        manual=false;
        editMsssv = (EditText)findViewById(R.id.editMssv);

        ///////////////////////////////////////////////////////
        //Create database
        mDbHelper = new FeedReaderDbHelper(this);
        mDbHelper.createDefaultStudents();

        //////////////////////////////////////////////////////////
        List<Students> list = mDbHelper.getAllNotes();
        listStd.addAll(list);

        count = (TextView)findViewById(R.id.count);
        today = (TextView)findViewById(R.id.today);
        status = (TextView)findViewById(R.id.status);
        diemDanhThuCong = (Switch) findViewById(R.id.manual);
        dssv = (Button)findViewById(R.id.dssv);
        listStudent = (Button)findViewById(R.id.listStudent);
        btnSave = (Button)findViewById(R.id.save);

        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        mDevicesListView = (ListView)findViewById(R.id.devicesListView);
        mDevicesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        //Diem danh thu cong
        mDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!manual) {
                    if (!listStd.get(i+1).isActive())
                        mDevicesListView.setItemChecked(i, false);
                    else
                        mDevicesListView.setItemChecked(i, true);
                }
                else
                {
                    if (!listStd.get(i+1).isActive()) {
                        numStudent++;
                        status.setText(getString(R.string.numStudent, numStudent, listStd.size()-1));

                        mDevicesListView.setItemChecked(i, true);
                        listStd.get(i+1).setActive(true);
                    }
                    else {
                        mDevicesListView.setItemChecked(i, true);
                    }
                }
            }
        });


        // Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);


        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                /*if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1)
                        mBluetoothStatus.setText("Connected to Device: " + (String)(msg.obj));
                    else
                        mBluetoothStatus.setText("Connection Failed");
                }*/
            }
        };

        diemDanhThuCong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    manual=true;
                } else {
                    manual=false;
                }
            }
        });

        //Today is a good day
        Calendar calendar = Calendar.getInstance();
        strToday = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+ "-" +String.valueOf(calendar.get(Calendar.MONTH) + 1) + "-" +
                String.valueOf(calendar.get(Calendar.YEAR) + " ");
        today.setText(strToday);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBTArrayAdapter.getCount()==0)
                {
                    Toast.makeText(getApplicationContext(), getString(R.string.saveNotif), Toast.LENGTH_SHORT).show();
                }
                else {
                    //Cheat
                    mDbHelper.updateDiemDanh(0, countValue, strToday);

                    //Save data
                    for(int i=1;i<listStd.size();i++)
                    {
                        if(listStd.get(i).isActive()==true)
                        {
                            mDbHelper.updateDiemDanh(listStd.get(i).getId(), countValue, strToday);
                        }
                    }

                    Toast.makeText(getApplicationContext(),  getString(R.string.saveSuccess, countValue), Toast.LENGTH_SHORT).show();

                    countValue++;
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("Count", countValue);
                    editor.commit();
                    count.setText(getString(R.string.count, countValue));
                    Reset();
                }
            }
        });

        listStudent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                listStudent(v);
            }
        });

        dssv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dssv(v);
            }
        });

        editMsssv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mBTArrayAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        status.setText(getString(R.string.numStudent, numStudent, listStd.size()-1));
        count.setText(getString(R.string.count,countValue));

        numStudent = 0;

    }

    private void ExportExcel()
    {
        //Request permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        List<Students> list = mDbHelper.getAllNotes();
        listStd.addAll(list);

        ////////////////////////////////
        final Cursor cursor = mDbHelper.getuser();

        File sd = Environment.getExternalStorageDirectory();
        String csvFile = "ĐiemDanh.xls";

        File directory = new File(sd.getAbsolutePath());
        //create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }

        try {
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet sheet = workbook.createSheet("CNPM Chuyên Sâu", 0);
            sheet.addCell(new Label(0, 0, "STT"));
            sheet.addCell(new Label(1, 0, "Mã số sinh viên"));
            sheet.addCell(new Label(2, 0, "Họ tên"));
            sheet.addCell(new Label(3, 0, listStd.get(0).getLan1()));
            sheet.addCell(new Label(4, 0, listStd.get(0).getLan2()));
            sheet.addCell(new Label(5, 0, listStd.get(0).getLan3()));
            sheet.addCell(new Label(6, 0, listStd.get(0).getLan4()));
            sheet.addCell(new Label(7, 0, listStd.get(0).getLan5()));
            sheet.addCell(new Label(8, 0, listStd.get(0).getLan6()));

            if (cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndex("Id"));
                    String mssv = cursor.getString(cursor.getColumnIndex("Mssv"));
                    String name = cursor.getString(cursor.getColumnIndex("Name"));
                    String lan1 = cursor.getString(cursor.getColumnIndex("Lan1"));
                    String lan2 = cursor.getString(cursor.getColumnIndex("Lan2"));
                    String lan3 = cursor.getString(cursor.getColumnIndex("Lan3"));
                    String lan4 = cursor.getString(cursor.getColumnIndex("Lan4"));
                    String lan5 = cursor.getString(cursor.getColumnIndex("Lan5"));
                    String lan6 = cursor.getString(cursor.getColumnIndex("Lan6"));

                    if(!mssv.isEmpty()) {
                        int i = cursor.getPosition() + 1;
                        sheet.addCell(new Label(0, i, id));
                        sheet.addCell(new Label(1, i, mssv));
                        sheet.addCell(new Label(2, i, name));
                        if (!lan1.isEmpty())
                            sheet.addCell(new Label(3, i, "x"));
                        if (!lan2.isEmpty())
                            sheet.addCell(new Label(4, i, "x"));
                        if (!lan3.isEmpty())
                            sheet.addCell(new Label(5, i, "x"));
                        if (!lan4.isEmpty())
                            sheet.addCell(new Label(6, i, "x"));
                        if (!lan5.isEmpty())
                            sheet.addCell(new Label(7, i, "x"));
                        if (!lan6.isEmpty())
                            sheet.addCell(new Label(8, i, "x"));
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();
            workbook.write();
            workbook.close();
            Toast.makeText(getApplicationContext(), getString(R.string.exportSuccess), Toast.LENGTH_SHORT).show();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.importExcel:
                Toast.makeText(getApplicationContext(),"Nhập excel",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.exportExcel:
                ExportExcel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void bluetoothOn(){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            //mBluetoothStatus.setText("Đã bật Bluetooth");
            Toast.makeText(getApplicationContext(),"Đã bật Bluetooth",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth đã được bật", Toast.LENGTH_SHORT).show();
        }
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data){
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                //mBluetoothStatus.setText("Đã bật");
            }
            else {
                //mBluetoothStatus.setText("Đã tắt");
            }
        }
    }

    private void bluetoothOff(){
        mBTAdapter.disable(); // turn off
        //mBluetoothStatus.setText("Bluetooth đã tắt");
        Toast.makeText(getApplicationContext(),"Bluetooth đã tắt", Toast.LENGTH_SHORT).show();
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(CheckStudent(device.getAddress())!=-1) {
                    int check = CheckStudent(device.getAddress());
                    mDevicesListView.setItemChecked(check, true);
                    listStd.get(check+1).setActive(true);
                    numStudent++;
                    status.setText(getString(R.string.numStudent, numStudent, listStd.size()-1));
                }
            }
        }
    };

    private void Reset()
    {
        numStudent = 0;
        listStd.clear();
        mBTArrayAdapter.clear(); // clear items

        List<Students> list = mDbHelper.getAllNotes();
        listStd.addAll(list);
        status.setText(getString(R.string.numStudent, numStudent, listStd.size()-1));

        for(int i=1;i<listStd.size();i++) {
            listStd.get(i).setActive(false);
            mDevicesListView.setItemChecked(i-1, false);
        }
    }

    private void listStudent(View view){
        Reset();

        for(int i=1;i<listStd.size();i++) {
            mBTArrayAdapter.add(listStd.get(i).getMssv() + "     " + listStd.get(i).getName());
        }
        mBTArrayAdapter.notifyDataSetChanged();

        if(mBTAdapter.isDiscovering()){
			unregisterReceiver(blReceiver);
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Dừng quét",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Bắt đầu quét", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth chưa được bật", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dssv(View view){
        listStd.clear();
        mBTArrayAdapter.clear();

        Intent i = new Intent(MainActivity.this, Dssv.class);
        MainActivity.this.startActivity(i);
    }

    private int CheckStudent(String str)
    {
        int ck = -1;
        for(int i=1;i<listStd.size();i++) {
            if(str.equals(listStd.get(i).getMac1()) || str.equals(listStd.get(i).getMac2()))
            {
                ck = i;
            }
        }
        return ck-1;
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        buffer = new byte[1024];
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }














}
