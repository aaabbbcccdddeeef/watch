package com.wisdomin.studentcard.feature;

import android.content.Context;
import android.util.Log;

import com.wisdomin.studentcard.util.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

public class NoteUtil {


    public static final String V8_TEMPERATURE_NODE =    "/sys/class/bd1568/device/bd1568_enable";
    public static final String V8_TEMPERATURE_VALUE =    "/sys/class/bd1568/device/bd1568_value";
    public static final String V8_STEP_NODE =           "/sys/class/input/input2/enable";
    public static final String V8_STEP_DATA =           "/sys/class/input/input2/step";


    public static NoteUtil mDevUtilInstance;

    public static Context mContext;

    public static NoteUtil getInstance(Context context) {
        mContext = context;
        if (mDevUtilInstance == null) {
            mDevUtilInstance = new NoteUtil();
        }
        return mDevUtilInstance;
    }


    private static boolean writeFile(String filePath, String str) {
        boolean flag = true;
        FileOutputStream out = null;
        PrintStream p = null;
        File file = new File(filePath);

        if (file.exists()) {
            try {
                out = new FileOutputStream(filePath);
                p = new PrintStream(out);
                p.print(str);
            } catch (Exception e) {
                flag = false;
                LogUtil.e( "WindowManager Write file error!!!" + e);
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                if (p != null) {
                    try {
                        p.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        } else {
            LogUtil.e( "WindowManager File is not exist");
        }
        LogUtil.e( "WindowManager value: " + str);
        return flag;
    }

    public static String readFile(String filePath)
    {
        ArrayList newList=new ArrayList<String>();
        File file = new File(filePath);

        if(file.exists()){
            try {
                InputStream instream = new FileInputStream(filePath);
                if (instream != null)
                {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while (( line = buffreader.readLine()) != null) {
                        newList.add(line+"\n");
                    }
                    instream.close();
                }
            }catch (Exception e)
            {
                LogUtil.e( "readFile e = " + e);
            }

        }
        LogUtil.e( "newList.toString() = " + newList.toString());
        return newList.get(0).toString();
    }

    public static void sendCommand(String path , String command)
    {
        LogUtil.e( "sendCommand path: " + path + " : command = " + command );
        writeFile(path,command);
    }

    public static void getInfo(String command) {
        BufferedReader reader = null;
        String content = "";
        try {
            Process process = Runtime.getRuntime().exec(command);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer output = new StringBuffer();
            int read;
            char[] buffer = new char[1024];
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();
            content = output.toString();
            LogUtil.e( "getInfo content= " + content);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogUtil.e( "getInfo content e= " + e);
        }
    }
}

