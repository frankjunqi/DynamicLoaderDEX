package com.frank.dynamicloaderdex.proxy.gson;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import android.util.Log;

import com.frank.dynamicloaderdex.proxy.ModuleLoader;

public class Gson {
	private static final String CLASS_NAME = "com.google.gson.Gson";
	private static final String TAG = "DEX_Gson";
	private Class<?> mClass;
	private Object mInstance;
	private Method mFromJsonWithClassMethod;
	private Method mToJsonMethod;
	private Method mFromJsonWithTypeMethod;
	private Method mToJsonWithTypeMethod;

	public Gson() {
		mClass = ModuleLoader.getClass(CLASS_NAME);
		try {
			mInstance = mClass.newInstance();
		} catch (Exception e) {
			Log.e(TAG, "Constructor" + e.getMessage());
			e.printStackTrace();
		}
	}

	public Gson(Object instance) {
		mClass = ModuleLoader.getClass(CLASS_NAME);
		mInstance = instance;
	}

	public <T> T fromJson(String json, Class<T> classOfT) {
		try {
			if (mFromJsonWithClassMethod == null) {
				mFromJsonWithClassMethod = mClass.getMethod("fromJson",
						String.class, Class.class);
			}
			return (T) mFromJsonWithClassMethod.invoke(mInstance, json,
					classOfT);
		} catch (Exception e) {
			Log.e(TAG, "fromJson" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public Object fromJson(String json, Type type) {
		try {
			if (mFromJsonWithTypeMethod == null) {
				mFromJsonWithTypeMethod = mClass.getMethod("fromJson",
						String.class, Type.class);
			}
			return mFromJsonWithTypeMethod.invoke(mInstance, json, type);
		} catch (Exception e) {
			Log.e(TAG, "fromJson" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public String toJson(Object src) {
		try {
			if (mToJsonMethod == null) {
				mToJsonMethod = mClass.getMethod("toJson", Object.class);
			}
			return (String) mToJsonMethod.invoke(mInstance, src);
		} catch (Exception e) {
			Log.e(TAG, "toJson" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public String toJson(Object src, Type typeOfSrc) {
		try {
			if (mToJsonWithTypeMethod == null) {
				mToJsonWithTypeMethod = mClass.getMethod("toJson",
						Object.class, Type.class);
			}
			return (String) mToJsonWithTypeMethod.invoke(mInstance, src,
					typeOfSrc);
		} catch (Exception e) {
			Log.e(TAG, "toJson" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}
