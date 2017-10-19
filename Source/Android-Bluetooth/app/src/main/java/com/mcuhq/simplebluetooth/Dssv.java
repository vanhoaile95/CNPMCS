package com.mcuhq.simplebluetooth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;

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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dssv);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnAdd = (Button)findViewById(R.id.btnAdd);
        arrStudent = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        listStudent = (ListView)findViewById(R.id.listStudent);
        listStudent.setAdapter(arrStudent);

        listStudent.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.showContextMenu();
            }
        });

        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this);
        mDbHelper.createDefaultStudents();
        List<Students> list = mDbHelper.getAllNotes();
        listStd.addAll(list);

        for(int i=0;i<listStd.size();i++)
        {
            arrStudent.add(listStd.get(i).getMssv() + "    "+listStd.get(i).getName());
        }
        arrStudent.notifyDataSetChanged();
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
}
