package com.frank.dynamicloaderdex.proxy;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.frank.dynamicloaderdex.BaseApplication;

import dalvik.system.DexClassLoader;

/**
 * 动态加载的管理类
 * 
 */
public class DynamicClassLoaderManager {

	private static final String TAG = "DEX_DynamicClassLoaderManager";
	public static ClassLoader sOriginalClassLoader = null;
	public static ClassLoader sCustomedClassLoader = null;
	private static ArrayList<DemoDexClassLoader> loaderList = new ArrayList<DemoDexClassLoader>();
	private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	private static Object getFieldValue(Object obj, String fieldName) {
		if (obj == null || fieldName == null) {
			return null;
		}
		Class<?> c = obj.getClass();
		Object value = null;
		while (c != null) {
			try {
				Field field = c.getDeclaredField(fieldName);
				field.setAccessible(true);
				value = field.get(obj);
				if (value != null) {
					return value;
				}
			} catch (Exception e) {
				Log.e(TAG, "getFieldValue：" + e.getMessage());
			} finally {
				c = c.getSuperclass();
			}
		}
		return null;
	}

	private static void setFieldValue(Object obj, String fieldName,
			Object fieldValue) {
		if (obj == null || fieldName == null) {
			return;
		}
		Class<?> clazz = obj.getClass();
		while (clazz != null) {
			try {
				Field field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(obj, fieldValue);
				return;
			} catch (Exception e) {
				Log.e(TAG, "setFieldValue：" + e.getMessage());
			} finally {
				clazz = clazz.getSuperclass();
			}
		}
	}

	/**
	 * 初始化关联自定义ClassLoader
	 * 
	 * @param context
	 */
	public static void initClassLoader() {
		Application application = BaseApplication.getInstance();
		if (application == null) {
			return;
		}
		Context mBase = (Context) getFieldValue(application, "mBase");
		Object mPackageInfo = getFieldValue(mBase, "mPackageInfo");
		sOriginalClassLoader = (ClassLoader) getFieldValue(mPackageInfo,
				"mClassLoader");
		sCustomedClassLoader = new TCClassLoader(sOriginalClassLoader);
		setFieldValue(mPackageInfo, "mClassLoader", sCustomedClassLoader);
	}

	public static void setClassLoader(DemoDexClassLoader classLoader) {
		readWriteLock.writeLock().lock();
		try {
			loaderList.remove(classLoader);
			loaderList.add(classLoader);
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	/**
	 * 自定义ClassLoader，先尝试使用自定义DexClassLoader加载类，再使用原有双亲委托逻辑加载类
	 */
	public static class TCClassLoader extends ClassLoader {

		private ClassLoader mOriginalClassLoader;

		public TCClassLoader(ClassLoader parent) {
			super(parent);
			mOriginalClassLoader = parent;
		}

		@Override
		public Class<?> loadClass(String className)
				throws ClassNotFoundException {
			readWriteLock.readLock().lock();
			try {
				for (DemoDexClassLoader loader : loaderList) {
					if (loader != null) {
						Class<?> c = null;
						try {
							c = loader.findClass(className);
						} catch (ClassNotFoundException e) {
							// do nothing
						}
						if (c != null) {
							return c;
						}
					}
				}
			} finally {
				readWriteLock.readLock().unlock();
			}

			return mOriginalClassLoader.loadClass(className);
		}
	}

	/**
	 * 自定义DexClassLoader，用于动态加载
	 */
	public static class DemoDexClassLoader extends DexClassLoader {

		public DemoDexClassLoader(String dexPath, String dexOutputDir,
				String libPath, ClassLoader parent) {
			super(dexPath, dexOutputDir, libPath, parent);
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			return super.findClass(name);
		}

		@Override
		public Class<?> loadClass(String className)
				throws ClassNotFoundException {
			Class<?> clazz = findLoadedClass(className);
			if (clazz == null) {
				try {
					clazz = findClass(className);
				} catch (ClassNotFoundException e) {
					// do nothing
				}
				if (clazz != null) {
					return clazz;
				}
				return super.loadClass(className);
			}
			return clazz;
		}

	}
}
