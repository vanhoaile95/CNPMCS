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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Text;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static jxl.Workbook.*;

public class MainActivity extends AppCompatActivity {

    public static String currentClass = "CNPMCS";

    // GUI Components
    private TextView today;
    private TextView status;
    private TextView count;
    public static TextView lop;
    private Switch diemDanhThuCong;
    private Button dsLop;
    private Button dssv;
    private Button listStudent;
    private Button btnSave;
    private  BluetoothAdapter mBTAdapter;
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
    List<GhiChu> ghiChu = new ArrayList<GhiChu>();
    int pos;

    //
    Button buttonUp;
    TextView textFolder;

    String KEY_TEXTPSS = "TEXTPSS";
    static final int CUSTOM_DIALOG_ID = 0;
    ListView dialog_ListView;
    private ArrayAdapter<String> arrLisFolder;
    File root;
    File curFolder;
    private List<String> fileList = new ArrayList<String>();
    String excelFile;


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
        mDbHelper.createDefault();

        //////////////////////////////////////////////////////////
        List<Students> list = mDbHelper.getListStudents(currentClass);
        listStd.addAll(list);

        count = (TextView)findViewById(R.id.count);
        today = (TextView)findViewById(R.id.today);
        status = (TextView)findViewById(R.id.status);
        diemDanhThuCong = (Switch) findViewById(R.id.manual);
        dssv = (Button)findViewById(R.id.dssv);
        dsLop=(Button)findViewById(R.id.btndsLop);
        lop=(TextView)findViewById(R.id.tbTenLop);
        listStudent = (Button)findViewById(R.id.listStudent);
        btnSave = (Button)findViewById(R.id.save);

        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio


        mDevicesListView = (ListView)findViewById(R.id.devicesListView);
        mDevicesListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        registerForContextMenu(mDevicesListView);

        //lớp điểm danh
        MainActivity.currentClass=mDbHelper.getClassRoomON().getName();
        lop.setText("Lớp điểm danh: "+ MainActivity.currentClass);

        //Diem danh thu cong
        mDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!manual) {
                    if (!listStd.get(i).isActive())
                        mDevicesListView.setItemChecked(i, false);
                    else
                        mDevicesListView.setItemChecked(i, true);
                }
                else
                {
                    if (!listStd.get(i).isActive()) {
                        numStudent++;
                        status.setText(getString(R.string.numStudent, numStudent, listStd.size()));

                        mDevicesListView.setItemChecked(i, true);
                        listStd.get(i).setActive(true);
                    }
                    else {
                        numStudent--;
                        status.setText(getString(R.string.numStudent, numStudent, listStd.size()));

                        mDevicesListView.setItemChecked(i, false);
                        listStd.get(i).setActive(false);
                    }
                }
            }
        });


        // Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        //////////////////////////////////////////////////////////////////////

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
                    //Save day
                    List<NgayDiemDanh> arrDays = new ArrayList<NgayDiemDanh>();
                    List<NgayDiemDanh> listDays = mDbHelper.getAllDays(currentClass);
                    arrDays.addAll(listDays);
                    int num = 0;

                    for(int i=0;i<arrDays.size();i++)
                    {
                        if(arrDays.get(i).getDay().equals(strToday))
                        {
                            pos = i;
                            num++;
                        }
                    }

                    if(num==0) {
                        NgayDiemDanh day = new NgayDiemDanh(mDbHelper.getDayCount()+1, strToday, 1, currentClass);
                        mDbHelper.addDay(day);
                    }
                    else
                    {
                        NgayDiemDanh day = new NgayDiemDanh(arrDays.get(pos).getId(), strToday, arrDays.get(pos).getLan()+1, currentClass);
                        mDbHelper.updateDay(day);
                    }
                    ///////////////////////////////////////////////////////////////
                    //Save data
                    for(int i=0;i<listStd.size();i++)
                    {
                        if(listStd.get(i).isActive()==true)
                        {
                            DiemDanh dd = new DiemDanh(mDbHelper.getDiemDanhCount()+1, listStd.get(i).getId(), strToday, countValue, "x");
                            mDbHelper.addDiemDanh(dd);
                        }
                        else
                        {
                            String noiDung = "";
                            for(int k=0;k<ghiChu.size();k++)
                            {
                                if(ghiChu.get(k).getId()==i)
                                {
                                    noiDung = ghiChu.get(k).getNoiDung();
                                }
                            }
                            DiemDanh dd = new DiemDanh(mDbHelper.getDiemDanhCount()+1, listStd.get(i).getId(), strToday, countValue, noiDung);
                            mDbHelper.addDiemDanh(dd);
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

        dsLop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsLop(v);
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

        status.setText("");
        count.setText(getString(R.string.count,countValue));

        numStudent = 0;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ghichu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.ghichu:
                ShowCustomDialog(info.position);
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void ShowCustomDialog(final int pos)
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_ghichu, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        final EditText TenLop = (EditText)mView.findViewById(R.id.tbghiChu);

        Button btnOk = (Button)mView.findViewById(R.id.okLop);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TenLop.getText().toString().isEmpty() ) {
                    GhiChu gc = new GhiChu(pos, TenLop.getText().toString());
                    ghiChu.add(gc);
                    Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();

                    //cập nhật
                    dialog.cancel();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ExportExcel()
    {
        //Request permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        listStd.clear();
        List<Students> list = mDbHelper.getListStudents(currentClass);
        listStd.addAll(list);

        //Get all day of class
        List<NgayDiemDanh> arrDays = new ArrayList<NgayDiemDanh>();
        List<NgayDiemDanh> listDays = mDbHelper.getAllDays(currentClass);
        arrDays.addAll(listDays);

        File sd = Environment.getExternalStorageDirectory();

        String csvFile = currentClass + ".xls";

        File directory = new File(sd.getAbsolutePath() + "/Điểm danh/");
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
            WritableSheet sheet = workbook.createSheet(currentClass, 0);
            sheet.addCell(new Label(0, 0, "STT"));
            sheet.addCell(new Label(1, 0, "Mã số sinh viên"));
            sheet.addCell(new Label(2, 0, "Họ tên"));
            for(int i=0;i<arrDays.size();i++)
            {
                int column = 0;
                if(i==0) {
                    column = i + 3;
                    sheet.addCell(new Label(column, 0, arrDays.get(i).getDay()));
                }
                else
                {
                    column = i + 3 + arrDays.get(i-1).getLan() - 1;
                    sheet.addCell(new Label(column, 0, arrDays.get(i).getDay()));
                }

                for(int j=0;j<arrDays.get(i).getLan();j++)
                {
                    sheet.addCell(new Label(j + column, 1, "Lần " + String.valueOf(j + 1)));
                }
            }

            for(int i=0;i<listStd.size();i++)
            {
                String id = String.valueOf(i+1);
                String mssv = listStd.get(i).getMssv();
                String name = listStd.get(i).getName();
                List<DiemDanh> arrDD = new ArrayList<DiemDanh>();
                List<DiemDanh> listDD = mDbHelper.getAllDiemDanh(listStd.get(i).getId());
                arrDD.addAll(listDD);

                sheet.addCell(new Label(0, i+2, id));
                sheet.addCell(new Label(1, i+2, mssv));
                sheet.addCell(new Label(2, i+2, name));
                for(int j=0;j<arrDD.size();j++)
                {
                    sheet.addCell(new Label(j+3, i+2, arrDD.get(j).getGhichu()));
                }
            }

            workbook.write();
            workbook.close();
            Toast.makeText(getApplicationContext(), getString(R.string.exportSuccess), Toast.LENGTH_SHORT).show();

            Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/Điểm danh/");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(selectedUri, "resource/folder");
            startActivity(Intent.createChooser(intent, "Open folder"));

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void ExportExcel(String excelpath, String folder)
    {
        try
        {
            //Toast.makeText(getApplicationContext(),"Chọn file danh sách lớp cần xuất vào" , Toast.LENGTH_LONG).show();
            listStd.clear();
            List<Students> list = mDbHelper.getListStudents(currentClass);
            listStd.addAll(list);

            //Get all day of class
            List<NgayDiemDanh> arrDays = new ArrayList<NgayDiemDanh>();
            List<NgayDiemDanh> listDays = mDbHelper.getAllDays(currentClass);
            arrDays.addAll(listDays);


            FileInputStream file = new FileInputStream(new File(excelpath));
            XSSFWorkbook wb = new XSSFWorkbook(file);

            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;

            XSSFCell cellNew;

            Iterator rows = sheet.rowIterator();
            //bỏ 9 dòng đầu
            rows.next();
            rows.next();
            rows.next();
            rows.next();
            rows.next();
            rows.next();
            rows.next();
            rows.next();
            row=(XSSFRow) rows.next();//dòng head



            int cellnum=4;
            for(int i=0;i<arrDays.size();i++)
            {

                for(int j=0;j<arrDays.get(i).getLan();j++)
                {
                    cellNew = row.createCell(cellnum++, XSSFCell.CELL_TYPE_STRING);
                    cellNew.setCellValue(arrDays.get(i).getDay()+"\nLần " + String.valueOf(j + 1));
                    sheet.setColumnWidth(cellnum-1,4500);
                }
            }


            cellnum=4;
            for(int i=0;i<listStd.size();i++) {
                if(rows.hasNext()) {
                    row = (XSSFRow) rows.next();

                    Iterator cells = row.cellIterator();

                    cells.next();//số thứ tự


                    String mssv = listStd.get(i).getMssv();
                    String name = listStd.get(i).getName();
                    List<DiemDanh> arrDD = new ArrayList<DiemDanh>();
                    List<DiemDanh> listDD = mDbHelper.getAllDiemDanh(listStd.get(i).getId());
                    arrDD.addAll(listDD);

                    cell = (XSSFCell) cells.next();//mã số sinh viên


                    cell.setCellValue(mssv);

                    cell = (XSSFCell) cells.next();//tên

                    cell.setCellValue(name);

                    for (int j = 0; j < arrDD.size(); j++) {

                        cellNew = row.createCell(cellnum++, XSSFCell.CELL_TYPE_STRING);
                        cellNew.setCellValue(arrDD.get(j).getGhichu());

                    }
                    cellnum = 4;
                }
            }
            while(rows.hasNext()) {
                row = (XSSFRow) rows.next();

                Iterator cells = row.cellIterator();

                cells.next();//số thứ tự
                cell = (XSSFCell) cells.next();//mã số sinh viên


                cell.setCellValue("");

                cell = (XSSFCell) cells.next();//tên

                cell.setCellValue("");
            }
            File fileĐiemanh = new File(folder+"/"+currentClass+"DiemDanh.xlsx");
            fileĐiemanh.getParentFile().mkdirs();

            FileOutputStream outFile = new FileOutputStream(fileĐiemanh);
            wb.write(outFile);

            Uri selectedUri = Uri.parse(folder+"/"+currentClass+"DiemDanh.xlsx");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(selectedUri, "resource/folder");
            startActivity(Intent.createChooser(intent, "Open file"));
        }
        catch (Exception e)
        {

        }



    }

    private void ShowOpenFileDialog()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        else {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.dialog_openfile, null);
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();

            root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            curFolder = root;
            textFolder = (TextView) mView.findViewById(R.id.folder);
            buttonUp = (Button) mView.findViewById(R.id.up);
            buttonUp.setEnabled(false);
            dialog_ListView = (ListView) mView.findViewById(R.id.dialoglist);

            ListDir(curFolder);

            buttonUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListDir(curFolder.getParentFile());
                }
            });

            dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    File selected = new File(fileList.get(position));
                    if (selected.isDirectory()) {
                        ListDir(selected);
                    } else {
                        //Toast.makeText(getApplicationContext(), selected.toString() + " selected",
                        //Toast.LENGTH_LONG).show();
                        excelFile = selected.toString();

                        dialog.cancel();
                        ExportExcel(excelFile,curFolder.getPath());
                    }
                }
            });
        }
    }

    void ListDir(File f) {
        if(f.equals(root)) {
            buttonUp.setEnabled(false);
        } else {
            buttonUp.setEnabled(true);
        }

        curFolder = f;
        textFolder.setText(f.getPath());

        File[] files = f.listFiles();
        fileList.clear();
        List<String> fileListNode= new ArrayList<String>();
        for(File file : files) {
            fileList.add(file.getPath());
            fileListNode.add(file.getName());
        }

        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, fileListNode);
        dialog_ListView.setAdapter(directoryList);
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
            case R.id.exportExcel:
                //ExportExcel();
                ShowOpenFileDialog();
                return true;
            case R.id.exportExcelNew:
                ExportExcel();
                return true;
            case R.id.huongdan:
                Intent i = new Intent(MainActivity.this, HuongDan.class);
                MainActivity.this.startActivity(i);
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
                    listStd.get(check).setActive(true);
                    numStudent++;
                    status.setText(getString(R.string.numStudent, numStudent, listStd.size()));
                }
            }
            //Nếu bluetooth tắt thì bật lại
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                mBTAdapter.startDiscovery();
            }
        }
    };

    private void Reset()
    {
        ghiChu.clear();
        numStudent = 0;
        listStd.clear();
        mBTArrayAdapter.clear(); // clear items

        List<Students> list = mDbHelper.getListStudents(currentClass);
        listStd.addAll(list);
        status.setText(getString(R.string.numStudent, numStudent, listStd.size()));

        for(int i=0;i<listStd.size();i++) {
            listStd.get(i).setActive(false);
            mDevicesListView.setItemChecked(i, false);
        }
    }

    private void listStudent(View view){
        Reset();

        for(int i=0;i<listStd.size();i++) {
            mBTArrayAdapter.add(listStd.get(i).getMssv() + "     " + listStd.get(i).getName());
        }
        mBTArrayAdapter.notifyDataSetChanged();

        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
            unregisterReceiver(blReceiver);
            Toast.makeText(getApplicationContext(),"Đã dừng quét",Toast.LENGTH_SHORT).show();
        }
        else
        {

            if(mBTAdapter.isEnabled()) {
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Bắt đầu quét", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                registerReceiver(blReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth chưa được bật", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void dssv(View view){
        Reset();
        status.setText("");

        try{
            if (mBTAdapter.isDiscovering())
                mBTAdapter.cancelDiscovery();
            unregisterReceiver(blReceiver);
        }
        catch (Exception e)
        {}



        Intent i = new Intent(MainActivity.this, Dssv.class);
        MainActivity.this.startActivity(i);
    }

    private void dsLop(View view)
    {
        Reset();
        status.setText("");

        Intent i = new Intent(MainActivity.this, DsClass.class);
        MainActivity.this.startActivity(i);
    }

    private int CheckStudent(String str)
    {
        int ck = -1;
        for(int i=0;i<listStd.size();i++) {
            if(str.equals(listStd.get(i).getMac1()) || str.equals(listStd.get(i).getMac2()))
            {
                ck = i;
            }
        }
        return ck;
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
