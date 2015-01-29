package com.frank.dynamicloaderdex.proxy.gson;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import android.util.Log;

import com.frank.dynamicloaderdex.proxy.ModuleLoader;

public class TypeToken<T> {
	
	private static final String GSON_TYPE_CLASS = "com.google.gson.internal.$Gson$Types";
	
	private Class<?> mClass;

	private Method mCanonicalizeMethod;
	
	protected TypeToken() {
		
	}
	
	public Type getType() {
		Type superclass = getClass().getGenericSuperclass();
	    if ((superclass instanceof Class)) {
	      throw new RuntimeException("Missing type parameter.");
	    }
	    ParameterizedType parameterized = (ParameterizedType)superclass;
		return canonicalize(parameterized.getActualTypeArguments()[0]);
	}
	
	private Type canonicalize(Type type) {
		try {
			if (mClass == null) {
				mClass = ModuleLoader.getClass(GSON_TYPE_CLASS);
			}
			if (mCanonicalizeMethod == null) {
				mCanonicalizeMethod = mClass.getMethod("canonicalize", Type.class);
			}
			return (Type) mCanonicalizeMethod.invoke(null, type);
		} catch (Exception e) {
			Log.e("DEX_TypeToken", "canonicalize：" + e.getMessage());
			e.printStackTrace();
		}
		return type;
	}
}
