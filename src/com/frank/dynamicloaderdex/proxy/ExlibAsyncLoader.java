package com.frank.dynamicloaderdex.proxy;

import android.content.Context;
import android.util.Log;

/**
 * 第三方jar异步加载（assets/async_ext.zip）
 */
public final class ExlibAsyncLoader {
	private static final String TAG = "ExlibAsyncLoader";
	private static boolean isLoaded = false;

	public static void load(final Context context) {
		synchronized (ExlibAsyncLoader.class) {
			try {
				ModuleLoader.loadModule(context, Module.EXLIB_ASYNC);
			} catch (Exception e) {
				Log.e(TAG, "exlib async module init error");
				e.printStackTrace();
			} finally {
				isLoaded = true;
				ExlibAsyncLoader.class.notifyAll();
			}
		}
	}

	public static Class<?> getClass(String className) {
		synchronized (ExlibAsyncLoader.class) {
			if (!isLoaded) {
				try {
					ExlibAsyncLoader.class.wait();
				} catch (InterruptedException unused) {
				}
			}

			if (!isLoaded) {
				return null;
			}

			try {
				return Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
