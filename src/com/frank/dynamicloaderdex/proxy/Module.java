package com.frank.dynamicloaderdex.proxy;

/**
 * 需要动态加载的组件信息
 */
public enum Module {
	/** 需要第一时间初始化的第三方模块 */
	EXLIB_SYNC("exlib", "v1", true, false),

	/** 可以异步加载的第三方模块 */
	EXLIB_ASYNC("async_ext", "v4", true, true);

	/** 组件名（文件名） */
	private final String mModuleName;
	/** 组件版本 */
	private final String mVersion;
	/** 组件动态加载开关 */
	private final boolean mIsDynamic;
	/** 组件加载方式：true:异步 false:同步 */
	private final boolean mIsAsync;

	private Module(String moduleName, String version, boolean isDynamic,
			boolean isAsync) {
		mModuleName = moduleName;
		mVersion = version;
		mIsDynamic = isDynamic;
		mIsAsync = isAsync;
	}

	public String getModuleName() {
		return mModuleName;
	}

	public String getVersion() {
		return mVersion;
	}

	public boolean isDynamic() {
		return mIsDynamic;
	}

	public boolean isAsync() {
		return mIsAsync;
	}
}
