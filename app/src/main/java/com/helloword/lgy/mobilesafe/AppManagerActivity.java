package com.helloword.lgy.mobilesafe;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.helloword.lgy.mobilesafe.db.db.domain.AppInfo;
import com.helloword.lgy.mobilesafe.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

import static com.helloword.lgy.mobilesafe.R.id.tv_apppath;


public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_memory, tv_sdmemory, tv_appinfodesc;
    private ListView lv_app_list;
    List<AppInfo> mAppInfoList;
    List<AppInfo> mSystemList;
    List<AppInfo> mCostemorList;
    private MyAdapter myAdapter;
    private AppInfo mAppInfo;
    private PopupWindow mPopupWindow;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myAdapter = new MyAdapter();
            lv_app_list.setAdapter(myAdapter);
            if (tv_appinfodesc != null && mCostemorList != null) {
                tv_appinfodesc.setText("用户应用（" + mCostemorList.size() + ")");
            }

        }
    };


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
            return mSystemList.size() + mCostemorList.size();
        }

        @Override
        public AppInfo getItem(int position) {
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
                    convertView = View.inflate(getApplicationContext(), R.layout.item_appinfo_title, null);
                    viewTitleHolder = new ViewTitleHolder();
                    viewTitleHolder.tv_appinfotitle = (TextView) convertView.findViewById(R.id.tv_appinfotitle);
                    convertView.setTag(viewTitleHolder);
                } else {
                    viewTitleHolder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    viewTitleHolder.tv_appinfotitle.setText("用户应用(" + mCostemorList.size() + ")");

                } else {
                    viewTitleHolder.tv_appinfotitle.setText("系统应用(" + mSystemList.size() + ")");
                }
                return convertView;

            } else {//显示文本+图片条目
                ViewHolder viewHolder;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.item_appinfo, null);
                    viewHolder = new ViewHolder();
                    viewHolder.iv_appicon = (ImageView) convertView.findViewById(R.id.iv_appicon);
                    viewHolder.tv_apppath = (TextView) convertView.findViewById(tv_apppath);
                    viewHolder.tv_appname = (TextView) convertView.findViewById(R.id.tv_appname);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.iv_appicon.setBackground(getItem(position).getIcon());
                viewHolder.tv_appname.setText(getItem(position).getName());
                if (getItem(position).isCdCard) {
                    viewHolder.tv_apppath.setText("sd卡应用");
                } else {
                    viewHolder.tv_apppath.setText("手机应用");

                }
                return convertView;
            }
        }
    }

    class ViewHolder {
        ImageView iv_appicon;
        TextView tv_appname;
        TextView tv_apppath;
    }

    class ViewTitleHolder {
        TextView tv_appinfotitle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initGetSpace();
        initList();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();

    }
    public void getData(){
        //拿到应用信息
        new Thread() {
            @Override
            public void run() {
                super.run();
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mSystemList = new ArrayList<AppInfo>();
                mCostemorList = new ArrayList<AppInfo>();
                for (AppInfo appinfo : mAppInfoList) {
                    if (appinfo.isSystem) {//系统应用
                        mSystemList.add(appinfo);
                    } else {//非系统应用
                        mCostemorList.add(appinfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initList() {
        //找到控件
        lv_app_list = (ListView) findViewById(R.id.lv_app_list);
        tv_appinfodesc = (TextView) findViewById(R.id.tv_appinfodesc);

        //listview注册滚动事件
        lv_app_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            //滚动过程中调用的方法
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //第一个参数是listview
                //第二个参数是第一个可见条目
                //第三个参数是当前屏幕可见条目
                //条目总数
                if (mCostemorList != null && mSystemList != null) {
                    if (firstVisibleItem >= mCostemorList.size() + 1) {
                        //滚动到了系统应用条目
                        tv_appinfodesc.setText("系统应用（" + mSystemList.size() + ")");
                    } else {
                        //滚动到了用户应用条目
                        tv_appinfodesc.setText("用户应用（" + mCostemorList.size() + ")");
                    }
                }
            }
        });
        //listviewItem点击事件
        lv_app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mCostemorList.size() + 1) {
                    return;
                } else {
                    if (position < mCostemorList.size() + 1) {
                        mAppInfo = mCostemorList.get(position - 1);
                    } else {
                        mAppInfo = mSystemList.get(position - mCostemorList.size() - 2);
                    }
                }
                showPopupWindow(view);
            }
        });

    }

    private void showPopupWindow(View view) {
        //准备挂载到popupwindow的view
        View popupwindowview = View.inflate(this, R.layout.popupwindow_layout, null);
        TextView tv_uninstall = (TextView) popupwindowview.findViewById(R.id.tv_uninstall);
        TextView tv_start = (TextView) popupwindowview.findViewById(R.id.tv_start);
        TextView tv_share = (TextView) popupwindowview.findViewById(R.id.tv_share);
        tv_uninstall.setOnClickListener(this);
        tv_start.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        //透明动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(300);
        alphaAnimation.setFillAfter(true);
        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0, 1,
                0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setDuration(300);
        scaleAnimation.setFillAfter(true);
        //动画集合
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(alphaAnimation);
        set.addAnimation(scaleAnimation);
        //
        mPopupWindow = new PopupWindow(popupwindowview,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //设置透明背景
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        //
        mPopupWindow.showAsDropDown(view, 250, -view.getHeight());
        popupwindowview.startAnimation(set);
    }

    //显示可用内存与总内存
    private void initGetSpace() {
        String path = Environment.getDataDirectory().getAbsolutePath();
        String sdcardpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String memory = Formatter.formatFileSize(this, getAbleSpace(path));
        String sdmemory = Formatter.formatFileSize(this, getCountSpace(path));
        tv_memory = (TextView) findViewById(R.id.tv_memory);
        tv_sdmemory = (TextView) findViewById(R.id.tv_sdmemory);
        tv_sdmemory.setText("总内存：" + sdmemory);
        tv_memory.setText("可用内存：" + memory);
    }

    //可用内存
    private long getAbleSpace(String path) {//用long而不用int原因：int最大值表示内存只能表示2G，
        StatFs statFs = new StatFs(path);
        long blockssize = statFs.getBlockSize();//获取区间大小
        long blocks = statFs.getAvailableBlocks();//获取可用区间
        return blockssize * blocks;//可用内存等于每个区间大小乘以可用区间
    }

    //总内存
    private long getCountSpace(String path) {//用long而不用int原因：int最大值表示内存只能表示2G，
        StatFs statFs = new StatFs(path);
        long blockssize = statFs.getBlockSize();//获取区间大小
        long blocks = statFs.getAvailableBlocks();//获取可用区间
        long count = statFs.getBlockCount();
        return blockssize * count;//可用内存等于每个区间大小乘以可用区间
    }

    //popupwindow里面的view的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_uninstall:
                if (mAppInfo.isSystem) {
                    Toast.makeText(this, "此应用不能卸载", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + mAppInfo.getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.tv_start:
                //开启应用
                PackageManager pm=getPackageManager();
               Intent launchIntentForPackage= pm.getLaunchIntentForPackage(mAppInfo.getPackageName());
                if (launchIntentForPackage!=null) {
                    startActivity(launchIntentForPackage);
                }else {
                    Toast.makeText(this, "此应用不能启动", Toast.LENGTH_SHORT).show();
                }
                break;
            //分享：暂只有短信分享
            case R.id.tv_share:
                Intent smsIntent=new Intent(Intent.ACTION_SEND);
                smsIntent.putExtra(Intent.EXTRA_TEXT,
                        "分享一个应用给你，应用名称为"+mAppInfo.getName());
                smsIntent.setType("text/plain");
                startActivity(smsIntent);
                break;
        }
        if (mPopupWindow!=null){
            mPopupWindow.dismiss();
        }


    }
}
