package com.uit.diemdanhbluetooth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 17/11/2017.
 */

public class DsClass extends AppCompatActivity {
    private Button btnAddClass;
    private TextView tvLopChon;
    private ListView listClass;
    private ArrayAdapter<String> listTenClass;
    private FeedReaderDbHelper mDbHelper;
    private List<ClassRooms> listclassRooms;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ds_lop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent returnIntent_select = new Intent();
        returnIntent_select.putExtra("result","Success");
        setResult(Activity.RESULT_OK,returnIntent_select);

        mDbHelper = new FeedReaderDbHelper(this);

        btnAddClass = (Button)findViewById(R.id.btnAddClass);
        tvLopChon=(TextView)findViewById(R.id.tbTenLopDangChon);
        btnAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowCustomDialog(0, 0);
            }
        });

        listclassRooms=new ArrayList<ClassRooms>();
        listTenClass = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        listClass = (ListView)findViewById(R.id.listClass);
        listClass.setAdapter(listTenClass);
        registerForContextMenu(listClass);
        //
        if(mDbHelper.getNotesClassRoomCount()!=0) {
            tvLopChon.setText("Lớp điểm danh: " + mDbHelper.getClassRoomON().getName());
        }
        //load danh sách lớp
        LoadListClass();

        //listTenClass.add("Tên Lớp Học");
        //listTenClass.notifyDataSetChanged();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                MainActivity.lop.setText("Lớp điểm danh: " + MainActivity.currentClass);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.lop.setText("Lớp điểm danh: " + MainActivity.currentClass);
        this.finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lop, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.updateLop:
                ShowCustomDialog(1, info.position);
                break;
            case R.id.delLop:
                if(MainActivity.currentClass.equals(listclassRooms.get(info.position).getName()))
                {
                    Toast.makeText(getApplicationContext(), "Lớp đang được chọn không thể xóa!", Toast.LENGTH_SHORT).show();
                }
                else {
                    //delete all student of class
                    List<Students> listStd = new ArrayList<Students>();
                    List<Students> list = mDbHelper.getListStudents(listclassRooms.get(info.position).getName());
                    listStd.addAll(list);

                    for (int i = 0; i < listStd.size(); i++) {
                        mDbHelper.deleteStudent(listStd.get(i).getId());
                    }

                    deleteClassRoom(listclassRooms.get(info.position).getId());
                }
                break;
            case R.id.selectLop:
                MainActivity.currentClass = listclassRooms.get(info.position).getName();
                updateClassRoomON(listclassRooms.get(info.position).getId());
                break;
        }

        return super.onContextItemSelected(item);
    }


    private void ShowCustomDialog(final int type, final int pos)//type=0 thêm, 1 update
    {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(DsClass.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_lop, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        final EditText TenLop = (EditText)mView.findViewById(R.id.tblop);


        if(type==1)
        {
            //get tên lớp
            TenLop.setText(listclassRooms.get(pos).getName());
        }

        Button btnOk = (Button)mView.findViewById(R.id.okLop);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TenLop.getText().toString().isEmpty() ) {
                    if (type == 0) {
                       //thêm vào csdl
                        addClassRoom(TenLop.getText().toString());
                    } else {
                        //cập nhật vào csdl
                        updateClassRoom(pos, TenLop.getText().toString());
                    }

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

    private void LoadListClass()
    {
        //load danh sách
        try {
            listTenClass.clear();
            listclassRooms= mDbHelper.getAllClassRoom();
            for(int i=0; i<listclassRooms.size();i++)
            {
                if(!listclassRooms.get(i).getStatus().equals("2"))
                    listTenClass.add(listclassRooms.get(i).getName());

                if(listclassRooms.get(i).getStatus().equals("1")) {
                    tvLopChon.setText("Lớp điểm danh: " + listclassRooms.get(i).getName());
                    MainActivity.currentClass = listclassRooms.get(i).getName();
                }
            }
            listTenClass.notifyDataSetChanged();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Load danh sách lớp thất bại" ,Toast.LENGTH_SHORT).show();
        }


    }

    private void addClassRoom(String className)
    {
        try{
            List<ClassRooms> listAllStd = new ArrayList<ClassRooms>();
            listAllStd.clear();
            List<ClassRooms> list = mDbHelper.getAllClassRoom();
            listAllStd.addAll(list);
            int id;
            if(mDbHelper.getNotesClassRoomCount()==0)
            {
                id = 1;
            }
            else
            {
                id = listAllStd.get(listAllStd.size() - 1).getId() + 1;
            }

            ClassRooms clr=new ClassRooms(id, className,"0");
            mDbHelper.addClassRooms(clr);
            if(mDbHelper.getNotesClassRoomCount()==1)
            {
                updateClassRoomON(clr.getId());
                MainActivity.currentClass = clr.getName();
                tvLopChon.setText("Lớp điểm danh: " + mDbHelper.getClassRoomON().getName());
            }
            LoadListClass();
            Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Thêm thất bại" ,Toast.LENGTH_SHORT).show();
        }


    }

    private void updateClassRoom(int id, String className)
    {
        try{
            int clrid=listclassRooms.get(id).getId();
            String clrName=className;
            ClassRooms clr=new ClassRooms(clrid,className,"0");
            mDbHelper.updateClassRoom(clr);
            LoadListClass();
            Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Cập nhật thất bại" ,Toast.LENGTH_SHORT).show();
        }


    }

    private void deleteClassRoom(int id)
    {
        try{

            mDbHelper.deleteClassroom(id);
            LoadListClass();
            Toast.makeText(getApplicationContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Xóa thất bại" ,Toast.LENGTH_SHORT).show();
        }


    }

    private void updateClassRoomON(int id)
    {
        try{
            mDbHelper.updateClassRoomON(id);
            LoadListClass();
            Toast.makeText(getApplicationContext(), "Chon thành công", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Chọn thất bại" ,Toast.LENGTH_SHORT).show();
        }


    }

}
