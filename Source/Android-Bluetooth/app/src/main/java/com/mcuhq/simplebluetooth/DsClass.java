package com.mcuhq.simplebluetooth;

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ds_lop);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDbHelper = new FeedReaderDbHelper(this);

        btnAddClass = (Button)findViewById(R.id.btnAddClass);
        tvLopChon=(TextView)findViewById(R.id.tbTenLopDangChon);
        btnAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowCustomDialog(0, 0);
            }
        });

        listTenClass = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        listClass = (ListView)findViewById(R.id.listClass);
        listClass.setAdapter(listTenClass);
        registerForContextMenu(listClass);
        listTenClass.add("Tên Lớp Học");
        listTenClass.notifyDataSetChanged();
    }
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
        inflater.inflate(R.menu.menu_lop, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.updateLop:
                //ShowCustomDialog(1, info.position+1);
                Toast.makeText(getApplicationContext(),"Cập nhật thành công" ,Toast.LENGTH_SHORT).show();
                break;
            case R.id.delLop:
                Toast.makeText(getApplicationContext(),"Xóa thành công" ,Toast.LENGTH_SHORT).show();
                break;
            case R.id.selectLop:
                Toast.makeText(getApplicationContext(),"Chọn lớp thành công" ,Toast.LENGTH_SHORT).show();
                //View mView = getLayoutInflater().inflate(R.layout.activity_main, null);
                //TextView tv=(TextView) mView.findViewById(R.id.tbTenLop);
                tvLopChon.setText("Lớp: CNPMCS");
                //this.finish();
                break;
        }

        return super.onContextItemSelected(item);
    }


    private void ShowCustomDialog(final int type, final int pos)
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
        }

        Button btnOk = (Button)mView.findViewById(R.id.okLop);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TenLop.getText().toString().isEmpty() ) {
                    if (type == 0) {
                       //thêm vào csdl
                        Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        //cập nhật vào csdl
                        Toast.makeText(getApplicationContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
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


}
