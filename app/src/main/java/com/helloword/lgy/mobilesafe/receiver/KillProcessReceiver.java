package com.helloword.lgy.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.helloword.lgy.mobilesafe.engine.ProcessInfoProvider;

public class KillProcessReceiver extends BroadcastReceiver {
    public KillProcessReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ProcessInfoProvider.killAllProcess(context);
    }
}
