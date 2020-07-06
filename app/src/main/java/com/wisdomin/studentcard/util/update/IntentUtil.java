package com.wisdomin.studentcard.util.update;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.wisdomin.studentcard.base.BaseApplication;

import java.io.File;


/**
 * 
 * @author liufuning
 * 
 *         create time:2013-11-14上午9:24:12
 */
public final class IntentUtil {

	/**
	 * 获取安装APK的Intent
	 *
	 * @param uri
	 * @return
	 */
	public static Intent getInstallAPKIntent(Uri uri) {
		Intent installIntent = new Intent(Intent.ACTION_VIEW);
		installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 新开辟一个栈
		installIntent.setDataAndType(uri,
				"application/vnd.android.package-archive");
		return installIntent;
	}

	/**
	 * 获取安装APK的Intent
	 *
	 * @param apkFile
	 * @return
	 */
//	public static Intent getInstallAPKIntent(Context context, File apkFile) {
//		Intent installIntent = new Intent(Intent.ACTION_VIEW);
////        修复报错:: Permission Denial: opening provider android.support.v4.content.FileProvider from ProcessRecord{743a61f
////        30446:com.android.packageinstaller/u0a23} (pid=30446, uid=10023) that is not exported from UID 10209
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
////            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////            installIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
////        }
//		FileProviderHelper.setIntentDataAndType(context, installIntent
//				, "application/vnd.android.package-archive", apkFile, true);
//
//		return installIntent;
//	}

	/**
	 * Android获取一个用于打开AUDIO文件的intent
	 *
	 * @param param
	 * @return
	 */
	public static Intent getAudioFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "audio/*");
		return intent;
	}

	/**
	 * Android获取一个用于打开Html文件的intent
	 *
	 * @param param
	 * @return
	 */
	public static Intent getHtmlFileIntent(String param) {

		Uri uri = Uri.parse(param).buildUpon()
				.encodedAuthority("com.android.htmlfileprovider")
				.scheme("content").encodedPath(param).build();
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setDataAndType(uri, "text/html");
		return intent;
	}

	/**
	 * Android获取一个用于打开图片文件的intent
	 *
	 * @param param
	 * @return
	 */
	public static Intent getImageFileIntent(String param) {

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	/**
	 * Android获取一个用于打开PPT文件的intent
	 *
	 * @param param
	 * @return
	 */
	public static Intent getPptFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		return intent;
	}

	/**
	 * Android获取一个用于打开Excel文件的intent
	 *
	 * @param param
	 * @return
	 */
	public static Intent getExcelFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		return intent;
	}

	/**
	 * Android获取一个用于打开Word文件的intent
	 *
	 * @param param
	 * @return
	 */
	public static Intent getWordFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/msword");
		return intent;
	}

	/**
	 * Android获取一个用于打开CHM文件的intent
	 *
	 * @param param
	 * @return
	 */
	public static Intent getChmFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/x-chm");
		return intent;
	}

	/**
	 * Android获取一个用于打开文本文件的intent
	 *
	 * @param param
	 * @param paramBoolean
	 * @return
	 */
	public static Intent getTextFileIntent(String param, boolean paramBoolean) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (paramBoolean) {
			Uri uri1 = Uri.parse(param);
			intent.setDataAndType(uri1, "text/plain");
		} else {
			Uri uri2 = Uri.fromFile(new File(param));
			intent.setDataAndType(uri2, "text/plain");
		}
		return intent;
	}

	/**
	 * Android获取一个用于打开PDF文件的intent
	 *
	 * @param param
	 * @return
	 */
	public static Intent getPdfFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}


	/**
	 * 打开文件通用方法
	 *
	 * @param filePath
	 * @return
	 */
	public static Intent openFile(String filePath) {

		File file = new File(filePath);
		if (!file.exists())
			return null;
		/* 取得扩展名 */
		String end = file
				.getName()
				.substring(file.getName().lastIndexOf(".") + 1,
						file.getName().length()).toLowerCase();
		/* 依扩展名的类型决定MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			return getAudioFileIntent(filePath);
		} else if (end.equals("3gp") || end.equals("mp4")) {
			return getAudioFileIntent(filePath);
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			return getImageFileIntent(filePath);
		} else if (end.equals("apk")) {
			return getApkFileIntent(filePath);
		} else if (end.equals("ppt")) {
			return getPptFileIntent(filePath);
		} else if (end.equals("xls")) {
			return getExcelFileIntent(filePath);
		} else if (end.equals("doc")) {
			return getWordFileIntent(filePath);
		} else if (end.equals("pdf")) {
			return getPdfFileIntent(filePath);
		} else if (end.equals("chm")) {
			return getChmFileIntent(filePath);
		} else if (end.equals("txt")) {
			return getTextFileIntent(filePath, false);
		} else {
			return getAllIntent(filePath);
		}
	}
	/**
	 * Android获取一个用于打开APK文件的intent
	 */
	public static Intent getAllIntent(String param) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		Uri uri ;//= Uri.fromFile(new File(param));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			uri = FileProvider.getUriForFile(BaseApplication.getAppContext(),BaseApplication.getAppContext().getPackageName() + ".FileProvider",
					new File(param));
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
		} else {
			uri = Uri.fromFile(new File(param));
		}
		intent.setDataAndType(uri, "*/*");
		return intent;
	}
	/**
	 * Android获取一个用于打开APK文件的intent
	 *
	 * @param param
	 * @return
	 */
	public static Intent getApkFileIntent(String param) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		Uri uri ;//= Uri.fromFile(new File(param));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			uri = FileProvider.getUriForFile(BaseApplication.getAppContext(),BaseApplication.getAppContext().getPackageName() + ".FileProvider",
					new File(param));
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
		} else {
			uri = Uri.fromFile(new File(param));
		}
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		return intent;
	}

}
