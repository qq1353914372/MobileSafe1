package com.helloword.lgy.mobilesafe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.helloword.lgy.mobilesafe.engine.CommonnumDao;

import java.util.List;

public class CommonNumberActivity extends AppCompatActivity {
    private ExpandableListView elv_commonnumber;
    List<CommonnumDao.Group> mGroupList;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);
        initData();
        initUI();
    }

    //初始化数据
    private void initData() {
        CommonnumDao dao = new CommonnumDao();
        mGroupList = dao.getGoup();
    }

    private void initUI() {
        elv_commonnumber = (ExpandableListView) findViewById(R.id.elv_commonnumber);
        myAdapter = new MyAdapter();
        elv_commonnumber.setAdapter(myAdapter);
        elv_commonnumber.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                Toast.makeText(CommonNumberActivity.this, , Toast.LENGTH_SHORT).show();
                callPhone(myAdapter.getChild(groupPosition, childPosition).number);
                return false;

            }
        });
    }

    private void callPhone(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    class MyAdapter extends BaseExpandableListAdapter{
        //组的大小
        @Override
        public int getGroupCount() {
            return mGroupList.size();
        }
        //组中孩子的大小
        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroupList.get(groupPosition).childList.size();
        }
        //组的对象
        @Override
        public CommonnumDao.Group getGroup(int groupPosition) {
            return mGroupList.get(groupPosition);
        }

        //孩子的对象
        @Override
        public CommonnumDao.Child getChild(int groupPosition, int childPosition) {
            return mGroupList.get(groupPosition).childList.get(childPosition);
        }
        //组的id
        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView=new TextView(getApplicationContext());
            textView.setText("      "+mGroupList.get(groupPosition).name);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view=View.inflate(getApplicationContext(),R.layout.item_elv_child,null);
        TextView tv_childname= (TextView) view.findViewById(R.id.tv_childname);
        TextView tv_childnumber= (TextView) view.findViewById(R.id.tv_childnumber);
            tv_childname.setText(mGroupList.get(groupPosition).childList.get(childPosition).name);
            tv_childnumber.setText(mGroupList.get(groupPosition).childList.get(childPosition).number);
            return view;
        }
    //孩子是否可以被点击
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
