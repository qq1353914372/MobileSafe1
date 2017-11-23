package com.helloword.lgy.mobilesafe;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.helloword.lgy.mobilesafe.db.dao.BlackNumberDAO;
import com.helloword.lgy.mobilesafe.db.db.domain.BlackNumberInfo;

import java.util.List;

import static com.helloword.lgy.mobilesafe.R.id.iv_delete;
import static com.helloword.lgy.mobilesafe.R.id.tv_mode;
import static com.helloword.lgy.mobilesafe.R.id.tv_phone;


// TODO: 2017/10/25 ListView优化之数据分页显示已完成  下一步，开启黑名单服务
public class BlackNumberMainActivity extends AppCompatActivity {
    private TextView tv_addblacknumber;
    private ListView lv_blacknumber;
    private BlackNumberDAO mDAO;
    MyAdapter myAdapter;
    private List<BlackNumberInfo> mBlackNumberInfoList;
    private int mode = 1;
    private int count = 0;
    private boolean isLoad=false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (myAdapter == null) {
                myAdapter = new MyAdapter();
                lv_blacknumber.setAdapter(myAdapter);
            } else {
                myAdapter.notifyDataSetChanged();
            }


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number_main);
        initUI();
        initData();

    }

    private void initData() {
        //查询黑名单数据库是耗时操作，开启子线程
        new Thread() {
            @Override
            public void run() {
                super.run();
                mDAO = BlackNumberDAO.getInstance(getApplicationContext());
                mBlackNumberInfoList = mDAO.find(0);
                count = mDAO.getCount();
                //发送消息告诉主线程已拿到数据，可以展示出来
                mHandler.sendEmptyMessage(0);
            }
        }.start();


    }

    private void initUI() {
        tv_addblacknumber = (TextView) findViewById(R.id.tv_addblacknumber);
        lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
        tv_addblacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddBlackNumberDialog();
            }
        });

        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        &&lv_blacknumber.getLastVisiblePosition()>=mBlackNumberInfoList.size()-1&&!isLoad){
                    if (count>mBlackNumberInfoList.size()){
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                mDAO = BlackNumberDAO.getInstance(getApplicationContext());
                                List<BlackNumberInfo> moreDate=   mBlackNumberInfoList = mDAO.find(mBlackNumberInfoList.size());

                                mBlackNumberInfoList.addAll(moreDate);
                                //发送消息告诉主线程已拿到数据，可以展示出来
                                mHandler.sendEmptyMessage(0);
                            }
                        }.start();
                    }


                }

            }


            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


    }

    public void showAddBlackNumberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.item_addblacknumber_dialog, null);
        dialog.setView(view);

        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
        RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
        Button btn_addblacknumber = (Button) view.findViewById(R.id.btn_addblacknumber);
        Button btn_cancaladdblacknumber = (Button) view.findViewById(R.id.btn_cancaladdblacknumber);

        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sms:
                        mode = 1;
                        break;
                    case R.id.rb_phone:
                        mode = 2;
                        break;
                    case R.id.rb_all:
                        mode = 3;
                        break;
                }
            }
        });

        btn_addblacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1拿到输入框数据
                String phone = et_phone.getText().toString().trim();
                //2插入数据
                    BlackNumberDAO.getInstance(getApplicationContext()).insert(phone, mode + "");
                //3更新List泛型的数据
                BlackNumberInfo info = new BlackNumberInfo();
                info.phone = phone;
                info.mode = mode + "";
                mBlackNumberInfoList.add(0, info);
                //通知数据适配器更新数据
                myAdapter.notifyDataSetChanged();
                dialog.dismiss();


            }
        });
        btn_cancaladdblacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    class ViewHolder {
        TextView tv_phone;
        TextView tv_mode;
        ImageView iv_delete;
    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mBlackNumberInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumberInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(BlackNumberMainActivity.this, R.layout.blacknumber_item, null);
                holder = new ViewHolder();
                holder.tv_phone = (TextView) convertView.findViewById(tv_phone);
                holder.tv_mode = (TextView) convertView.findViewById(tv_mode);
                holder.iv_delete = (ImageView) convertView.findViewById(iv_delete);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_phone.setText(mBlackNumberInfoList.get(position).getPhone());
            int mode = Integer.parseInt(mBlackNumberInfoList.get(position).getMode());
            switch (mode) {
                case 1:
                    holder.tv_mode.setText("拦截短信");
                    break;
                case 2:
                    holder.tv_mode.setText("拦截电话");
                    break;
                case 3:
                    holder.tv_mode.setText("拦截所有");
                    break;
            }

            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //删除数据库中相应的数据
                    mDAO.delete(mBlackNumberInfoList.get(position).phone);
                    //删除集合中的数据
                    mBlackNumberInfoList.remove(position);
                    //通知数据适配器更新数据
                    myAdapter.notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }

}
