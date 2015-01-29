package com.frank.dynamicloaderdex.proxy.gson;

import java.lang.reflect.Method;

import android.util.Log;

import com.frank.dynamicloaderdex.proxy.ModuleLoader;

public class GsonBuilder {
	private static final String CLASS_NAME = "com.google.gson.GsonBuilder";
	private static final String TAG = "DEX_GsonBuilder";
	private Class<?> mClass;
	private Method mCreateMethod;
	private Object mInstance;

	public GsonBuilder() {
		mClass = ModuleLoader.getClass(CLASS_NAME);
		try {
			mInstance = mClass.newInstance();
		} catch (Exception e) {
			Log.e(TAG, "Constructor：" + e.getMessage());
			e.printStackTrace();
		}
	}

	public Gson create() {
		try {
			if (mCreateMethod == null) {
				mCreateMethod = mClass.getMethod("create");
			}
			return new Gson(mCreateMethod.invoke(mInstance));
		} catch (Exception e) {
			Log.e(TAG, "create：" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
