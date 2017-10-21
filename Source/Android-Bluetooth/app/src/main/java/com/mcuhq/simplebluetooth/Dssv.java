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
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chuon on 10/17/2017.
 */

public class Dssv extends AppCompatActivity {
    private Button btnAdd;
    private ListView listStudent;
    private ArrayAdapter<String> arrStudent;
    List<Students> listStd = new ArrayList<Students>();
    FeedReaderDbHelper mDbHelper;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dssv);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDbHelper = new FeedReaderDbHelper(this);

        btnAdd = (Button)findViewById(R.id.btnAdd);
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
        /*listStudent.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.showContextMenu();
            }
        });*/

        LoadData();
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

        List<Students> list = mDbHelper.getAllNotes();
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
        final EditText mac = (EditText)mView.findViewById(R.id.mac);

        if(type==1)
        {
            name.setText(listStd.get(pos).getName());
            mssv.setText(listStd.get(pos).getMssv());
            mac.setText(listStd.get(pos).getMac());
        }

        Button btnOk = (Button)mView.findViewById(R.id.ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mssv.getText().toString().isEmpty() && !name.getText().toString().isEmpty()) {
                    if (type == 0) {
                        Students std = new Students(listStd.get(listStd.size() - 1).getId() + 1, mssv.getText().toString(), name.getText().toString(), mac.getText().toString());
                        mDbHelper.addStudent(std);
                        Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Students std = new Students(listStd.get(pos).getId(), mssv.getText().toString(), name.getText().toString(), mac.getText().toString());
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
