package com.frank.dynamicloaderdex;

import com.frank.dynamicloaderdex.proxy.ModuleLoader;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application {

	private static BaseApplication mBaseApplication;

	public static BaseApplication getInstance() {
		return mBaseApplication;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mBaseApplication = this;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		ModuleLoader.load(this);
	}
}
