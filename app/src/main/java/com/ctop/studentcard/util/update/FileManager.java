package com.ctop.studentcard.util.update;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * 文件管理类
 * 
 * @author liufuning
 * 
 */
public final class FileManager {

	public static final String CACHE_ROOT_DIRECTORY = "dadaCache"; // 缓存根目录
	public static final String CACHE_IMAGE_DIRECTORY = "imageCache"; // 缓存图片目录
	public static final String CACHE_AUDIO_DIRECTORY = "audioCache"; // 缓存语音目录
	public static final String CACHE_FILE_DIRECTORY = "fileCache"; // 缓存其他文件目录
	public static final String CACHE_CRASH_DIRECTORY = "crash"; // 错误日志目录
	public static final String CACHE_VIDEO_DIRECTORY = "videoCache"; // 缓存视频目录

	public static final String CACHE_TEMP_DIRECTORY = "temp"; // 临时缓存目录，需要及时清掉
	public static final String CACHE_IMAGE_LOADER_DIRECTORY = "imageLoader";//ImageLoader缓存目录

	/**
	 * 判断是否存在SD卡
	 * 
	 * @return
	 */
	public static final boolean hasSDCard() {

		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public static final String getCachePath(Context context) {
		if (hasSDCard()) {
			return Environment.getExternalStorageDirectory().toString() + File.separator + CACHE_ROOT_DIRECTORY;
		} else {
			// return context.getFilesDir()+File.separator+CACHE_ROOT_DIRECTORY;
			return context.getCacheDir() + File.separator + CACHE_ROOT_DIRECTORY;
		}

	}

	/**
	 * 获取图片缓存路径
	 *
	 * @param context
	 * @return
	 */
	public static final String getImageCachePath(Context context) {

		String path = getCachePath(context) + File.separator + CACHE_IMAGE_DIRECTORY;

		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return path;
	}

	/**
	 * 获取语音缓存路径（网络下载的语音）
	 *
	 * @param context
	 * @return
	 */
	public static final String getAudioCachePath(Context context) {
		String path = getCachePath(context) + File.separator + CACHE_AUDIO_DIRECTORY;
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return path;
	}

	/**
	 * 获取缓存文件路径
	 *
	 * @param context
	 * @return
	 */
	public static final String getFileCachePath(Context context) {
		String path = getCachePath(context) + File.separator + CACHE_FILE_DIRECTORY;

		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return path;
	}

	public static final String getFileCrashPath(Context context) {
		String path = getCachePath(context) + File.separator + CACHE_CRASH_DIRECTORY;

		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return path;
	}

	/**
	 * 获取视频缓存路径
	 *
	 * @param context
	 * @return
	 */
	public static final String getVideoCachePath(Context context) {
		String path = getCachePath(context) + File.separator + CACHE_VIDEO_DIRECTORY;
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return path;
	}

	/**
	 * 获取临时缓存文件，由于存放录制的语音，临时图片，文件等
	 *
	 * @param context
	 * @return
	 */
	public static final String getTempPath(Context context) {

		String path = getCachePath(context) + File.separator + CACHE_TEMP_DIRECTORY;

		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return path;
	}

	/**
	 * 清除临时图片缓存
	 *
	 * @param context
	 */
	public static final boolean clearTempCache(Context context) {

		try {
			return FileUtils.clearDir(getTempPath(context));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static final boolean clearImageCache(Context context) {

		try {
			return FileUtils.clearDir(getImageCachePath(context));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 清除语音缓存
	 *
	 * @param context
	 */
	public static final boolean clearAudioCache(Context context) {

		try {
			return FileUtils.clearDir(getAudioCachePath(context));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 清除语音缓存
	 *
	 * @param context
	 */
	public static final boolean clearVideoCache(Context context) {

		try {
			return FileUtils.clearDir(getVideoCachePath(context));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 清除文件缓存
	 *
	 * @param context
	 */
	public static final boolean clearFileCache(Context context) {

		try {
			return FileUtils.clearDir(getFileCachePath(context));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 清除所有缓存
	 *
	 * @param context
	 */
	public static final boolean clearAllCache(Context context) {

		try {
			return FileUtils.clearDir(getCachePath(context));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 计算SD卡的剩余空间
	 *
	 * @return 返回-1，说明没有安装sd卡
	 */
	public static long getFreeDiskSpace() {
		String status = Environment.getExternalStorageState();
		long freeSpace = 0;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				freeSpace = availableBlocks * blockSize / 1024;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return -1;
		}
		return (freeSpace);
	}

	/**
	 * 获取ImageLoader缓存路径
	 *
	 * @param context
	 * @return
	 */
	public static final String getImageLoaderCachePath(Context context) {

		String path = getCachePath(context) + File.separator + CACHE_IMAGE_LOADER_DIRECTORY;

		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return path;
	}

	/**
	 * 清除ImageLoader缓存路径
	 *
	 * @param context
	 */
	public static final boolean clearImageLoaderCache(Context context) {

		try {
			return FileUtils.clearDir(getImageLoaderCachePath(context));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
