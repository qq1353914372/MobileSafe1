package com.helloword.lgy.mobilesafe;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.helloword.lgy.mobilesafe.db.dao.AppLockDAO;
import com.helloword.lgy.mobilesafe.db.db.domain.AppInfo;
import com.helloword.lgy.mobilesafe.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

public class AppLockActivity extends AppCompatActivity {
    private Button btn_unlock, btn_lock;
    private LinearLayout ll_unlock, ll_lock;
    private TextView tv_unlock, tv_lock;
    private ListView lv_unlock, lv_lock;
    private List<AppInfo> mAppInfoList;
    private List<AppInfo> mUnlockAppList;
    private List<AppInfo> mLockAppList;
    private AppLockDAO mDAO;
    private TranslateAnimation mTranslateAnimation;
    private MyAdapter mLockAdapter;
    private MyAdapter mUnlockAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mLockAdapter= new MyAdapter(false);
            lv_unlock.setAdapter(mLockAdapter);
            mUnlockAdapter = new MyAdapter(true);
            lv_lock.setAdapter(mUnlockAdapter);


        }
    };

    class MyAdapter extends BaseAdapter {
        private boolean isLock;

        public MyAdapter(boolean isLock) {
            this.isLock = isLock;
        }

        @Override
        public int getCount() {
            if (isLock) {
                tv_lock.setText("已加锁应用："+mLockAppList.size());
                return mLockAppList.size();
            } else {
                tv_unlock.setText("未加锁应用"+mUnlockAppList.size());
                return mUnlockAppList.size();
            }
        }

        @Override
        public AppInfo getItem(int position) {
            if (isLock) {
                return mLockAppList.get(position);
            } else {
                return mUnlockAppList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(getApplicationContext(), R.layout.item_applock_list, null);
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.iv_lock = (ImageView) convertView.findViewById(R.id.iv_lock);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final AppInfo appInfo=getItem(position);
            viewHolder.iv_icon.setBackground(appInfo.icon);
            viewHolder.tv_name.setText(appInfo.name);
            if (isLock) {
                viewHolder.iv_lock.setImageResource(R.mipmap.lock);
            } else {
                viewHolder.iv_lock.setImageResource(R.mipmap.unlock);
            }
            //设置为final可在内部类调用
            final View view=convertView;

            viewHolder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //设置动画
                    view.startAnimation(mTranslateAnimation);
                    //动画监听
                    mTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (isLock){//加锁>未加锁
                                //加锁应用集合+1，未加锁集合-1  加减的对象：getItem
                                //数据库数据减一
                                mLockAppList.remove(appInfo);
                                mUnlockAppList.add(appInfo);
                                mDAO.delete(appInfo.packageName);
                                //加锁数据适配器刷新
//                        mLockAdapter.notifyDataSetChanged();
                                mUnlockAdapter.notifyDataSetChanged();

                            }else {
                                //加锁应用集合-1，未加锁集合+1  加减的对象：getItem
                                //数据库数据加一
                                mLockAppList.add(appInfo);
                                mUnlockAppList.remove(appInfo);
                                mDAO.insert(appInfo.packageName);
                                //加锁数据适配器刷新
//                        mUnlockAdapter.notifyDataSetChanged();
                                mLockAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });


                }
            });
            return convertView;
        }
    }
    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_lock;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initUI();
        initData();
        initAnimation();
    }
//设置item的平移动画
    private void initAnimation() {
        mTranslateAnimation =new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,1,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0

        );
        mTranslateAnimation.setDuration(500);
    }

    private void initData() {
        //可能是耗时操作
        new Thread() {
            @Override
            public void run() {
                super.run();
                //拿到手机应用集合
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                //创建两个集合区分加锁与未加锁
                mUnlockAppList = new ArrayList<AppInfo>();
                mLockAppList = new ArrayList<AppInfo>();
                //拿到已加锁数据库中app的包名
                mDAO = AppLockDAO.getInstance(getApplicationContext());
                List<String> mLockPackageNameLsit = mDAO.queryAll();
                for (AppInfo info : mAppInfoList) {
                    if (mLockPackageNameLsit.contains(info.packageName)) {//已加锁
                        mLockAppList.add(info);
                    } else {//未加锁
                        mUnlockAppList.add(info);
                    }
                }
                //告知主线程更新数据
                mHandler.sendEmptyMessage(0);

            }
        }.start();

    }

    private void initUI() {
        btn_unlock = (Button) findViewById(R.id.btn_unlock);
        btn_lock = (Button) findViewById(R.id.btn_lock);

        ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
        ll_lock = (LinearLayout) findViewById(R.id.ll_lock);

        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        tv_lock = (TextView) findViewById(R.id.tv_lock);

        lv_unlock = (ListView) findViewById(R.id.lv_unlock);
        lv_lock = (ListView) findViewById(R.id.lv_lock);

        btn_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示加锁应用，切换btn背景
                ll_lock.setVisibility(View.VISIBLE);
                ll_unlock.setVisibility(View.GONE);

                btn_lock.setBackgroundResource(R.mipmap.tab_right_pressed);
                btn_unlock.setBackgroundResource(R.mipmap.tab_left_default);

            }
        });
        btn_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示未加锁应用。切换btn背景
                ll_lock.setVisibility(View.GONE);
                ll_unlock.setVisibility(View.VISIBLE);

                btn_lock.setBackgroundResource(R.mipmap.tab_right_default);
                btn_unlock.setBackgroundResource(R.mipmap.tab_left_pressed);
            }
        });


    }
}
