package com.frank.dynamicloaderdex.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileLock;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

/**
 * 管理组件的加载
 * 
 */
public final class ModuleLoader {
	private static final String TAG = "ModuleLoader";
	private static final String LIB_SUFFIX = ".zip";

	private ModuleLoader() {
	}

	/**
	 * 加载{@link com.frank.dynamicloaderdex.proxy.Module}中定义的组件
	 */
	public static void load(Context context) {
		startAsyncLoad(context);
		startSyncLoad(context);
	}

	/**
	 * 异步加载组件
	 */
	private static void startAsyncLoad(final Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (final Module module : Module.values()) {
					if (!module.isAsync() || !module.isDynamic()) {
						continue;
					}
					new Thread(new Runnable() {
						@Override
						public void run() {
							if (Module.EXLIB_ASYNC.equals(module)) {
								ExlibAsyncLoader.load(context);
								return;
							}
							try {
								ModuleLoader.loadModule(context, module);
							} catch (Exception e) {
								Log.e(TAG, module.getModuleName()
										+ " : Module init error");
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		}).start();
	}

	/**
	 * 主线程阻塞加载组件
	 */
	private static void startSyncLoad(Context context) {
		if (Module.EXLIB_SYNC.isDynamic()) {
			try {
				ModuleLoader.loadModule(context, Module.EXLIB_SYNC);
			} catch (Exception e) {
				Log.e(TAG, "exlib sync : Module init error");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 加载组件，失败时会重试一次
	 * 
	 * @param module
	 *            要加载的组件{@link com.frank.dynamicloaderdex.proxy.Module}
	 */
	static void loadModule(Context context, Module module) throws IOException,
			IllegalArgumentException, IllegalAccessException,
			NoSuchFieldException, InvocationTargetException,
			NoSuchMethodException {
		try {
			initModule(context, module);
		} catch (Exception e) {
			Log.w(TAG, module.getModuleName()
					+ " : Module init error, try to init again");
			initModule(context, module);
		}
	}

	/**
	 * 初始化组件： 从assets目录中拷贝文件到data/data/pkgname/app-dex中 将文件路径添加到ClassLoader的加载路径
	 * 
	 * @param module
	 *            要加载的组件{@link com.frank.dynamicloaderdex.proxy.Module}
	 */
	private static void initModule(Context context, Module module)
			throws IOException, IllegalArgumentException,
			IllegalAccessException, NoSuchFieldException,
			InvocationTargetException, NoSuchMethodException {
		// 存放在data/data/pkgname/app-dex目录下
		File dexDir = context.getDir("dex", Context.MODE_PRIVATE);
		dexDir.mkdir();
		// 文件名格式 “模块名_版本.后缀”
		String moduleName = module.getModuleName();
		String destFileName = new StringBuilder().append(moduleName)
				.append("_").append(module.getVersion()).append(LIB_SUFFIX)
				.toString();

		File destFile = new File(dexDir, destFileName);

		if (!destFile.exists()) {
			// 删除老版本文件
			String[] files = dexDir.list();
			for (String fileName : files) {
				if (fileName.contains(moduleName)) {
					new File(dexDir, fileName).delete();
				}
			}
			// 从assets目录中拷贝文件
			InputStream assetInput = context.getAssets().open(
					new StringBuilder().append(moduleName).append(LIB_SUFFIX)
							.toString());
			FileOutputStream output = null;
			try {
				output = new FileOutputStream(destFile);
				FileLock fileLock = output.getChannel().lock();
				copyWithoutOutputClosed(assetInput, output);
				fileLock.release();
			} finally {
				if (output != null) {
					try {
						output.close();
					} catch (IOException e) {
					}
				}
			}
		}
		// 添加文件路径到ClassLoader加载路径
		ArrayList<File> dexFiles = new ArrayList<File>();
		dexFiles.add(destFile);
		MultiDex.installSecondaryDexes(context.getClassLoader(), dexDir,
				dexFiles);
	}

	/**
	 * 不关输出流的文件copy
	 * 
	 * @param inputStream
	 *            输入流
	 * @param outputStream
	 *            输出流
	 * @throws java.io.IOException
	 */
	private static void copyWithoutOutputClosed(InputStream inputStream,
			OutputStream outputStream) throws IOException {
		int iBufferSize = 8 * 1024;
		try {
			byte[] buffer = new byte[iBufferSize];
			int temp = -1;
			if (!(inputStream instanceof BufferedInputStream)) {
				inputStream = new BufferedInputStream(inputStream, iBufferSize);
			}
			if (!(outputStream instanceof BufferedOutputStream)) {
				outputStream = new BufferedOutputStream(outputStream,
						iBufferSize);
			}
			while ((temp = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, temp);
				outputStream.flush();
			}
			outputStream.flush();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 兼容老的反射加载方式，用于同步组件中通过getClass主动加载类
	 */
	public static Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
