package com.mcuhq.simplebluetooth;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;



/**
 * Created by chuong on 10/17/2017.
 */

public class Dssv extends AppCompatActivity {
    private Button btnAdd;
    private Button btnAddExcel;
    private Button btncapNhatDiaChiMac;
    private ListView listStudent;

    private ArrayAdapter<String> arrStudent;
    private String excelFile;
    List<Students> listStd = new ArrayList<Students>();
    List<Students> listAllStd = new ArrayList<Students>();
    FeedReaderDbHelper mDbHelper;


    //open file dialog

    Button buttonUp;
    TextView textFolder;

    String KEY_TEXTPSS = "TEXTPSS";
    static final int CUSTOM_DIALOG_ID = 0;
    ListView dialog_ListView;
    private ArrayAdapter<String> arrLisFolder;
    File root;
    File curFolder;

    private List<String> fileList = new ArrayList<String>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dssv);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDbHelper = new FeedReaderDbHelper(this);

        btnAdd = (Button)findViewById(R.id.btnAdd);

        btncapNhatDiaChiMac=(Button)findViewById(R.id.capNhatDiaChiMac);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowCustomDialog(0, 0);
            }
        });

        arrStudent = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        listStudent = (ListView)findViewById(R.id.listStudent);
        listStudent.setAdapter(arrStudent);
        registerForContextMenu(listStudent);

        LoadData();

        //open file dialog
        btnAddExcel=(Button)findViewById(R.id.btnAddExcel);
        btnAddExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowOpenFileDialog();
            }
        });

        //Toast.makeText(getApplicationContext(),root.toString(),Toast.LENGTH_SHORT).show();
    }

    //open file dialog
    private void ShowOpenFileDialog()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        else {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(Dssv.this);
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
                        readExcel(excelFile);
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


    private void readExcel(String fileExcel)
    {

        final List<Students> listSV=new ArrayList<Students>();

        try {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(Dssv.this);
            final View mView = getLayoutInflater().inflate(R.layout.dialog_dssv_import, null);
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();

             ListView listSVimprot=(ListView)mView.findViewById(R.id.list_dssv_import);
             ArrayAdapter<String> arrSVImport=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
            listSVimprot.setAdapter(arrSVImport);
             TextView countSVImprot=(TextView)mView.findViewById(R.id.tvCountSVImport);
             Button btnHuySVImport=(Button)mView.findViewById(R.id.btn_huy);
             Button btnLuuSVImport=(Button)mView.findViewById(R.id.btn_luu);

            //dọc file
            FileInputStream file = new FileInputStream(new File(fileExcel));
            XSSFWorkbook wb = new XSSFWorkbook(file);

            XSSFWorkbook test = new XSSFWorkbook();

            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;

            Iterator rows = sheet.rowIterator();
            rows.next();
            rows.next();
            rows.next();
            rows.next();
            rows.next();
            rows.next();
            rows.next();
            rows.next();
            rows.next();
            rows.next();

            while (rows.hasNext())
            {
                row=(XSSFRow) rows.next();

                Iterator cells = row.cellIterator();
                String name="";
                String mssv="";
                cells.next();//số thứ tự
                cell=(XSSFCell) cells.next();//mã số sinh viên


                if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
                {
                    mssv=cell.getStringCellValue();
                }
                else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
                {
                    mssv=String.valueOf(cell.getNumericCellValue());
                }
                else
                {
                    //U Can Handel Boolean, Formula, Errors
                }

                cell=(XSSFCell) cells.next();//tên

                if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
                {
                    name=cell.getStringCellValue();
                }
                else if(cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC)
                {
                    name=String.valueOf(cell.getNumericCellValue());
                }
                else
                {
                    //U Can Handel Boolean, Formula, Errors
                }

                if(mssv.equals("") || name.equals(""))
                    break;
                arrSVImport.add(mssv+" - "+name);
                listAllStd.clear();
                List<Students> list = mDbHelper.getAllStudents();
                listAllStd.addAll(list);

                Students std = new Students(listAllStd.get(listAllStd.size() - 1).getId() +listSV.size()+ 1, mssv, name, MainActivity.currentClass);
                listSV.add(std);
                countSVImprot.setText("Số Sinh Viên: "+listSV.size());
            }
            arrSVImport.notifyDataSetChanged();
            btnHuySVImport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoadData();
                    dialog.cancel();



            }});

            btnLuuSVImport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(Students item : listSV)
                    {
                        mDbHelper.addStudent(item);
                    }
                    LoadData();
                    dialog.cancel();

                }});




        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Không đọc được file "+excelFile,
                    Toast.LENGTH_LONG).show();
        }
    }


/////////////////////////////////////////////////////

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.update:
                ShowCustomDialog(1, info.position);
                break;
            case R.id.del:
                mDbHelper.deleteStudent(listStd.get(info.position).getId());
                LoadData();
                Toast.makeText(getApplicationContext(),"Xóa thành công" ,Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void LoadData()
    {
        arrStudent.clear();
        listStd.clear();

        List<Students> list = mDbHelper.getListStudents(MainActivity.currentClass);
        listStd.addAll(list);

        for(int i=0;i<listStd.size();i++)
        {
            arrStudent.add(listStd.get(i).getMssv() + "    "+ listStd.get(i).getName());
        }

        arrStudent.notifyDataSetChanged();
    }

    private void ShowCustomDialog(final int type, final int pos)
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Dssv.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        final EditText name = (EditText)mView.findViewById(R.id.name);
        final EditText mssv = (EditText)mView.findViewById(R.id.mssv);
        final EditText mac1 = (EditText)mView.findViewById(R.id.mac1);
        final EditText mac2 = (EditText)mView.findViewById(R.id.mac2);

        if(type==1)
        {
            name.setText(listStd.get(pos).getName());
            mssv.setText(listStd.get(pos).getMssv());
            mac1.setText(listStd.get(pos).getMac1());
            mac2.setText(listStd.get(pos).getMac2());
        }

        Button btnOk = (Button)mView.findViewById(R.id.ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mssv.getText().toString().isEmpty() && !name.getText().toString().isEmpty()) {
                    if (type == 0) {
                        listAllStd.clear();
                        List<Students> list = mDbHelper.getAllStudents();
                        listAllStd.addAll(list);

                        Students std = new Students(listAllStd.get(listAllStd.size() - 1).getId() + 1, mssv.getText().toString(), name.getText().toString(),
                                MainActivity.currentClass, mac1.getText().toString(), mac2.getText().toString());
                        mDbHelper.addStudent(std);
                        Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Students std = new Students(listStd.get(pos).getId(), mssv.getText().toString(), name.getText().toString()
                                , mac1.getText().toString(), mac2.getText().toString());
                        mDbHelper.updateStudent(std);
                        Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    }

                    LoadData();
                    dialog.cancel();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
