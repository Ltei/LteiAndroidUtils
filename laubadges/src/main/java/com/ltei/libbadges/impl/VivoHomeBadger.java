package com.ltei.laubadges.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.Collections;
import java.util.List;

import com.ltei.laubadges.Badger;
import com.ltei.laubadges.ShortcutBadgeException;

/**
 * @author leolin
 */
public class VivoHomeBadger implements Badger {

    @Override
    public void executeBadge(Context context, ComponentName componentName, int badgeCount) throws ShortcutBadgeException {
        Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
        intent.putExtra("packageName", context.getPackageName());
        intent.putExtra("className", componentName.getClassName());
        intent.putExtra("notificationNum", badgeCount);
        context.sendBroadcast(intent);
    }

    @Override
    public List<String> getSupportLaunchers() {
        return Collections.singletonList("com.vivo.launcher");
    }
}
