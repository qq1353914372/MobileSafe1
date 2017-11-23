package com.helloword.lgy.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.helloword.lgy.mobilesafe.db.db.domain.ProcessInfo;
import com.helloword.lgy.mobilesafe.engine.ProcessInfoProvider;

import java.util.ArrayList;
import java.util.List;

import static android.text.format.Formatter.formatFileSize;



public class ProcessActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_processactivity_process, tv_tv_processactivity_memory, tv_desc;
    private Button  btn_select_all,btn_select_reverse,btn_clearprocess,btn_settingprocess;
    private ListView lv_list_process;
    private List<ProcessInfo> mSystemList;
    private List<ProcessInfo> mProcessInfoList;
    private List<ProcessInfo> mCostemorList;
    private MyAdapter myAdapter;
    private ProcessInfo mProcessInfo;
    private int mProcessCount;
    private long mAvailableSpace;
    private String mCountSpace_G;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myAdapter = new MyAdapter();
            lv_list_process.setAdapter(myAdapter);
            if (tv_desc != null && mCostemorList != null) {
                tv_desc.setText("用户应用（" + mCostemorList.size() + ")");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        initProcessCount();
        initSpace();
        initProcessList();
        initBtn();
    }

    private void initBtn() {
        btn_select_all= (Button) findViewById(R.id.btn_select_all);
        btn_select_reverse= (Button) findViewById(R.id.btn_select_reverse);
        btn_clearprocess= (Button) findViewById(R.id.btn_clearprocess);
        btn_settingprocess= (Button) findViewById(R.id.btn_settingprocess);

        btn_select_all.setOnClickListener(this);
        btn_select_reverse.setOnClickListener(this);
        btn_clearprocess.setOnClickListener(this);
        btn_settingprocess.setOnClickListener(this);
    }

    private void initProcessList() {
        lv_list_process = (ListView) findViewById(R.id.lv_list_process);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        getProcessListData();
        lv_list_process.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mCostemorList != null && mSystemList != null) {
                    if (firstVisibleItem >= mCostemorList.size() + 1) {
                        //滚动到了系统应用条目
                        tv_desc.setText("系统应用（" + mSystemList.size() + ")");
                    } else {
                        //滚动到了用户应用条目
                        tv_desc.setText("用户应用（" + mCostemorList.size() + ")");
                    }
                }
            }
        });

        lv_list_process.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //条目判断
                if (position == 0 || position == mCostemorList.size() + 1) {
                    return;
                } else {
                    if (position < mCostemorList.size() + 1) {
                        mProcessInfo = mCostemorList.get(position - 1);
                    } else {
                        mProcessInfo = mSystemList.get(position - mCostemorList.size() - 2);
                    }
                }
                //非空判断后，再作非本程序判断
                if (mProcessInfo != null) {
                    if (!mProcessInfo.getPackageName().equals(getPackageName())) {
                        //状态反选，设置单选框状态
                        mProcessInfo.isCheck = !mProcessInfo.isCheck;
                        CheckBox cb_select_process = (CheckBox) view.findViewById(R.id.cb_select_process);
                        cb_select_process.setChecked(mProcessInfo.isCheck);
                    }
                }

            }
        });

    }

    private void getProcessListData() {
        //拿到进程信息
        new Thread() {
            @Override
            public void run() {
                super.run();
                mProcessInfoList = ProcessInfoProvider.getProcessInfo(getApplicationContext());
                mSystemList = new ArrayList<ProcessInfo>();
                mCostemorList = new ArrayList<ProcessInfo>();
                for (ProcessInfo info : mProcessInfoList) {
                    if (info.isSystem) {//系统进程
                        mSystemList.add(info);
                    } else {//非系统进程
                        mCostemorList.add(info);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_select_all:
                selectAll();
                break;
            case R.id.btn_select_reverse:
                selectReverse();
                break;
            case R.id.btn_clearprocess:
                clearProcess();

                break;
            case R.id.btn_settingprocess:
                settingProcess();

                break;
        }
    }

    private void settingProcess() {
        Intent settingProcessIntent=new Intent(this,ProcessSettingActivity.class);
        startActivityForResult(settingProcessIntent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (myAdapter!=null){
            myAdapter.notifyDataSetChanged();
        }
    }

    private void clearProcess() {
        List<ProcessInfo> killProcess =new ArrayList<ProcessInfo>();
        //遍历用户进程集合
        for (ProcessInfo pr:mCostemorList) {
            //判断是否为本程序
            if (pr.getPackageName().equals(getPackageName())){
                continue;
            }
            if (pr.isCheck){
                //不能在遍历集合时删除集合里的内容
                killProcess.add(pr);
            }

        }
        //遍历系统进程集合
        for (ProcessInfo pr:mSystemList) {
            if (pr.isCheck){
                killProcess.add(pr);
            }
        }
        long releaseSpace=0;
        //遍历被选中的进程集合
        for (ProcessInfo pr:killProcess) {
            if (mCostemorList.contains(pr)){
                mCostemorList.remove(pr);
            } else  if (mSystemList.contains(pr)){
                mSystemList.remove(pr);
            }
            //杀死进程
            ProcessInfoProvider.killProcess(this,pr);

            releaseSpace+=pr.processmemory;
        }
        //通知数据适配器更新数据
        if (myAdapter!=null){
            myAdapter.notifyDataSetChanged();
        }
        //更新进程数
        mProcessCount-=killProcess.size();
        tv_processactivity_process.setText("进程数：" + mProcessCount);

        //更新可用空间
        mAvailableSpace+=releaseSpace;
      String updateSize=  Formatter.formatFileSize(this,mAvailableSpace);
        tv_tv_processactivity_memory.setText("剩余/总共:" + updateSize + "/" + mCountSpace_G);
        //吐司提示
        Toast.makeText(this, String.format("杀死了%d个进程,清理了%s内存",
                killProcess.size(),Formatter.formatFileSize(this,releaseSpace)), Toast.LENGTH_SHORT).show();


    }

    private void selectReverse() {
        //将进程集合中的ischeck字段取反
        for (ProcessInfo pr:mCostemorList) {
            //判断是否为本程序
            if (pr.getPackageName().equals(getPackageName())){
                continue;
            }
            pr.isCheck=!pr.isCheck;
        }
        for (ProcessInfo pr:mSystemList) {
            pr.isCheck=!pr.isCheck;
        }
        //通知适配器更新数据
        if (myAdapter!=null){
            myAdapter.notifyDataSetChanged();
        }
    }

    private void selectAll() {//全选
        //将进程集合中的ischeck字段设置为true
        for (ProcessInfo pr:mCostemorList) {
            //判断是否为本程序
            if (pr.getPackageName().equals(getPackageName())){
                continue;
            }
            pr.isCheck=true;
        }
        for (ProcessInfo pr:mSystemList) {
            pr.isCheck=true;
        }
        //通知适配器更新数据
        if (myAdapter!=null){
            myAdapter.notifyDataSetChanged();
        }
    }

    //数据适配器
    class MyAdapter extends BaseAdapter {
        //指定索引指向的条目类型。条目类型状态码指定（0（复用系统）,1）
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCostemorList.size() + 1) {
                return 0;//返回0代表纯文本条目的状态码
            } else {
                return 1;//返回1代表图片+文本条目的状态码
            }
        }

        //获取数据适配器中条目类型的总数，并修改成两种（纯文本，文本+图片）
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public int getCount() {
            SharedPreferences sp=getSharedPreferences("config",MODE_PRIVATE);
            if (sp.getBoolean("showsystem",false)){

                return mSystemList.size() + mCostemorList.size()+2;
            }else {
                return mCostemorList.size()+1;
            }
        }

        @Override
        public ProcessInfo getItem(int position) {
            //position==0||position==mCostemorList.size()+1灰色条目
            if (position == 0 || position == mCostemorList.size() + 1) {
                return null;
            } else {
                if (position < mCostemorList.size() + 1) {
                    return mCostemorList.get(position - 1);
                } else {
                    return mSystemList.get(position - mCostemorList.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {//显示灰色纯文本条目
                ViewTitleHolder viewTitleHolder;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(),
                            R.layout.item_appinfo_title, null);
                    viewTitleHolder = new ViewTitleHolder();
                    viewTitleHolder.tv_appinfotitle = (TextView) convertView.findViewById(R.id.tv_appinfotitle);
                    convertView.setTag(viewTitleHolder);
                } else {
                    viewTitleHolder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    viewTitleHolder.tv_appinfotitle.setText("用户进程(" + mCostemorList.size() + ")");

                } else {
                    viewTitleHolder.tv_appinfotitle.setText("系统进程(" + mSystemList.size() + ")");
                }
                return convertView;

            } else {//显示文本+图片条目
                ViewHolder viewHolder;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.item_processinfo, null);
                    viewHolder = new ViewHolder();
                    viewHolder.iv_appicon = (ImageView) convertView.findViewById(R.id.iv_appicon);
                    viewHolder.tv_memory = (TextView) convertView.findViewById(R.id.tv_memory);
                    viewHolder.tv_appname = (TextView) convertView.findViewById(R.id.tv_appname);
                    viewHolder.cb_select_process = (CheckBox) convertView.findViewById(R.id.cb_select_process);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.iv_appicon.setBackground(getItem(position).getIcon());
                viewHolder.tv_appname.setText(getItem(position).getName());

                String strMemory = formatFileSize(getApplicationContext(), getItem(position).getProcessmemory());
                viewHolder.tv_memory.setText("占用内存" + strMemory);
                if (getItem(position).getPackageName().equals(getPackageName())) {
                    viewHolder.cb_select_process.setVisibility(View.GONE);
                } else {
                    viewHolder.cb_select_process.setVisibility(View.VISIBLE);
                }
                viewHolder.cb_select_process.setChecked(getItem(position).isCheck);


                return convertView;
            }
        }
    }

    class ViewHolder {
        ImageView iv_appicon;
        TextView tv_appname;
        TextView tv_memory;
        CheckBox cb_select_process;
    }

    class ViewTitleHolder {
        TextView tv_appinfotitle;
    }


    private void initSpace() {
        tv_tv_processactivity_memory = (TextView) findViewById(R.id.tv_tv_processactivity_memory);
        mAvailableSpace  = ProcessInfoProvider.getAvailableSpace(this);
        String availableSpace_G = formatFileSize(this, mAvailableSpace);
        long countSpace = ProcessInfoProvider.getCountSpace(this);
        mCountSpace_G= formatFileSize(this, countSpace);
        tv_tv_processactivity_memory.setText("剩余/总共:" + availableSpace_G + "/" + mCountSpace_G);
    }

    private void initProcessCount() {
        tv_processactivity_process = (TextView) findViewById(R.id.tv_processactivity_process);
        //拿到进程数
        mProcessCount = ProcessInfoProvider.getProcessCount(this);
        tv_processactivity_process.setText("进程数：" + mProcessCount);
    }

}
