package com.ctop.studentcard.util.update;

import android.os.Environment;
import android.text.TextUtils;

import com.ctop.studentcard.util.LogUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件工具类
 * 
 * @author liufuning
 * 
 */
public class FileUtils {
	
	private static final String TAG = FileUtils.class.getSimpleName();

	public static String readFile(File f) throws IOException {
		
		InputStream is = new FileInputStream(f);
		byte[] bs = new byte[is.available()];
		try {
			is.read(bs);
		} finally{
			try {
				is.close();
				is = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new String(bs);
	}
	
	/**
	 * 根据文件绝对路径获取文件名
	 * @param filePath
	 * @return
	 */
	public static String getFileName( String filePath )
	{
		if( TextUtils.isEmpty(filePath) )	return "";
		return filePath.substring( filePath.lastIndexOf( File.separator )+1 );
	}
	/**
	 * 根据文件的绝对路径获取文件名但不包含扩展名
	 * @param filePath
	 * @return
	 */
	public static String getFileNameNoFormat( String filePath){
		if(TextUtils.isEmpty(filePath)){
			return "";
		}
		int end = filePath.lastIndexOf('.');
		int start = filePath.lastIndexOf(File.separator)+1;
		if(end < 0 || start < 1) return "";
		return filePath.substring(start,end);
	}
	
	/**
	 * 获取文件扩展名
	 * @param fileName
	 * @return
	 */
	public static String getFileFormat( String fileName )
	{
		if( TextUtils.isEmpty(fileName) )	return "";
		
		int point = fileName.lastIndexOf( '.' );
		return fileName.substring( point+1 );
	}
	
	/**
	 * 获取文件大小
	 * @param filePath
	 * @return
	 */
	public static long getFileSize( String filePath )
	{
		long size = 0;
		
		File file = new File( filePath );
		if(file!=null && file.exists())
		{
			size = file.length();
		} 
		return size;
	}
	
	/**
	 * 获取文件大小
	 * @param size 字节
	 * @return
	 */
	public static String getFileSize(long size) 
	{
		if (size <= 0)	return "0";
		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		float temp = (float)size / 1024;
		if (temp >= 1024) 
		{
			return df.format(temp / 1024) + "M";
		}
		else 
		{
			return df.format(temp) + "K";
		}
	}

	/**
	 * 转换文件大小
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize(long fileS) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

	/**
	 * 获取目录文件大小
	 * @param dir
	 * @return
	 */
	public static long getDirSize(File dir) {
		if (dir == null) {
			return 0;
		}
	    if (!dir.isDirectory()) {
	    	return 0;
	    }
	    long dirSize = 0;
	    File[] files = dir.listFiles();
	    for (File file : files) {
	    	if (file.isFile()) {
	    		dirSize += file.length();
	    	} else if (file.isDirectory()) {
	    		dirSize += file.length();
	    		dirSize += getDirSize(file); //递归调用继续统计
	    	}
	    }
	    return dirSize;
	}
	
	/**
	 * 获取目录文件个数
	 * @return
	 */
	public long getFileList(File dir){
        long count = 0;
        File[] files = dir.listFiles();
        count = files.length;
        for (File file : files) {
            if (file.isDirectory()) {
            	count = count + getFileList(file);//递归
            	count--;
            }
        }
        return count;  
    }
	
	public static byte[] toBytes(InputStream in) throws IOException 
	{
		ByteArrayOutputStream out = null;
		byte buffer[] = null;
		try {
			
			out = new ByteArrayOutputStream();
		    int ch;
		    while ((ch = in.read()) != -1)
		    {
		    	out.write(ch);
		    }
		    buffer=out.toByteArray();
		    out.close();
		} finally{
			if(out != null){
				try {
					out.close();
					out = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	    return buffer;
	}
	
	/**
	 * 检查文件是否存在
	 * @param name
	 * @return
	 */
	public static boolean checkFileExists(String name) {
		boolean status;
		if (!name.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + name);
			status = newPath.exists();
		} else {
			status = false;
		}
		return status;

	}

	/**
	 * 删除指定的文件夹
	 * 
	 * @param filepath
	 * @throws IOException
	 */
	public static boolean deleteDir(String filepath) throws IOException {
		
		File f = new File(filepath);// 定义文件路径
		
		if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
			
			if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
				
				f.delete();
				
			} else { // 若有则把文件放进数组，并判断是否有下级目录
				
				File delFile[] = f.listFiles();
				
				final int i = f.listFiles().length;
				
				for (int j = 0; j < i; j++) {
					
					if (delFile[j].isDirectory()) {
						deleteDir(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
					}
					delFile[j].delete();
				}
				
				f.delete();
			}
		}
		return true;
	}

	/**
	 * 清空文件夹，但不删除该文件夹
	 * @param dirPath
	 */
	public static boolean clearDir(String dirPath){
		
		if(dirPath == null){
			return false;
		}
		
		File f = new File(dirPath);// 定义文件路径
		
		File delFile[] = f.listFiles();
		
		final int i = f.listFiles().length;
		
		for (int j = 0; j < i; j++) {
			
			if (delFile[j].isDirectory()) {
				try {
					deleteDir(delFile[j].getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			delFile[j].delete();
		}
		return true;
	}
	
	
	/**
	 * 删除指定的文件
	 * 
	 * @param filePath
	 */
	public static void deleteFile(String filePath) throws IOException{

		if (filePath == null || "".equals(filePath)) {
			return;
		}

		File file = new File(filePath);

		if (file.exists() && file.isFile()) {
			file.delete();
		}
	}

	/**
	 * 根据后缀名删除指定文件夹下特定后缀名的文件
	 * 
	 * @param dirPath
	 * @param suffix
	 */
	public static void deleteFileBySuffix(String dirPath, String suffix)throws IOException {

		File f = new File(dirPath);// 定义文件路径

		if (f.exists() && f.isDirectory()) {// 判断是文件还是目录

			if (f.listFiles().length != 0) {
				File delFile[] = f.listFiles();
				final int size = f.listFiles().length;
				for (int j = 0; j < size; j++) {

					if (delFile[j].getName().endsWith(suffix)) {
						delFile[j].delete();
					}

				}

			}
		}
	}

	/**
	 * 根据文件名删除指定文件夹下的文件
	 * 
	 * @param dirPath
	 * @param fileName
	 */
	public static void deleteFileByName(String dirPath, String fileName) throws Exception{

		File f = new File(dirPath);// 定义文件路径

		if (f.exists() && f.isDirectory()) {// 判断是文件还是目录

			if (f.listFiles().length != 0) {

				File delFile[] = f.listFiles();

				final int size = f.listFiles().length;
				for (int j = 0; j < size; j++) {

					if (delFile[j].getName().equals(fileName)) {
						delFile[j].delete();
					}

				}

			}
		}
	}

	/**
	 * 复制文件
	 * 
	 * @param sourcePath
	 * @param savePath
	 * @throws IOException
	 */
	public static void copyFile(String sourcePath, String savePath) throws IOException {

		if (sourcePath == null || "".equals(sourcePath)) {
			throw new FileNotFoundException("原来目录不存在");
		}

		FileInputStream fis = new FileInputStream(new File(sourcePath));
		File saveFile = new File(savePath);
		File dir = saveFile.getParentFile();

		if (dir != null && !dir.exists()) {
			dir.mkdirs();
		}
		FileOutputStream fos = null;
		try {
			
			fos = new FileOutputStream(saveFile);
			byte[] buffer = new byte[1024];
			int length = 0;
			
			while ((length = fis.read(buffer)) != -1) {
				fos.write(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(fos != null){
				try {
					fos.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	/**
	 * 文件转字符串
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception 
	 */
	public static final String writeFile(String filePath) throws Exception{
		
		InputStream inputStream = new FileInputStream(filePath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		
		final int bufferSize = 2048;
        byte[] buffer = new byte[bufferSize];
        
        int lenght = -1;
        while((lenght = inputStream.read(buffer))!= -1){
        	baos.write(buffer, 0, lenght);
        }
		return baos.toString();
	}
	
	public static final String getFilePath( String fileName) throws IOException{
		
		
		InputStream abpath = FileUtils.class.getResourceAsStream("/assets/"+fileName);
		
		String path = new String(FileUtils.InputStreamToByte(abpath));

		LogUtil.showLog(TAG, path);
		
		return path;
	}
	
	private static final  byte[] InputStreamToByte(InputStream is) throws IOException {
	    ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
	    byte imgdata[] = null;
	    try {
		
		    int ch;
		    while ((ch = is.read()) != -1) {
		        bytestream.write(ch);
		    }
		    imgdata = bytestream.toByteArray();
			
		}finally{
			if(bytestream != null){
				try {
					bytestream.close();
					bytestream = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	    return imgdata;
	}
	/**
	 * 把字节数组保存为一个文件
	 *
	 * @param b
	 * @param outputFile
	 * @return
	 */
	public static File getFileFromBytes(byte[] b, String outputFile) {
		File ret = null;
		BufferedOutputStream stream = null;
		try {
			ret = new File(outputFile);
			FileOutputStream fstream = new FileOutputStream(ret);
			stream = new BufferedOutputStream(fstream);
			stream.write(b);
		} catch (Exception e) {
			// log.error("helper:get file from byte process error!");
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// log.error("helper:get file from byte process error!");
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	/**
	 * 创建目录
	 *
	 * @param path
	 */
	public static void setMkdir(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
			LogUtil.showLog("file", "目录不存在  创建目录    ");
		} else {
			LogUtil.showLog("file", "目录存在");
		}
	}

	/**
	 * 获取目录名称
	 *
	 * @param url
	 * @return FileName
	 */
	public static String getFilesName(String url) {
		int lastIndexStart = url.lastIndexOf("/");
		if (lastIndexStart != -1) {
			return url.substring(lastIndexStart + 1, url.length());
		} else {
			return null;
		}
	}

	/**
	 * 删除该目录下的文件
	 *
	 * @param path
	 */
	public static void delFile(String path) {
		if (!TextUtils.isEmpty(path)) {
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
		}
	}
	
}
