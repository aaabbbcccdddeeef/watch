package com.ctop.studentcard.util;

import android.content.Context;

import java.io.FileOutputStream;
import java.util.Properties;


/**
 * 读取 assets 中的信息
 */
public class PropertiesUtil {

    private static PropertiesUtil mPropUtil = null;
    private static Properties property = null;//声明变量

    public static PropertiesUtil getInstance() {
        if (mPropUtil == null) {
            mPropUtil = new PropertiesUtil();
        }
        if (property == null) {
            property = new Properties();
        }
        return mPropUtil;
    }

    /**
     * 获取
     */
    public String getHost(Context context) {
        String url = null;
        try {
            property.load(context.openFileInput("zhihui.properties"));
            url = property.getProperty("host");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     *
     */
    public int getTcp_port(Context context) {
        int url = 0;
        try {
            property.load(context.openFileInput("zhihui.properties"));
            url = Integer.valueOf(property.getProperty("tcp_port"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     *
     */
    public String getAes_key(Context context) {
        String url = null;
        try {
            property.load(context.openFileInput("zhihui.properties"));
            url = property.getProperty("aes_key");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     *
     */
    public String getAes_vector(Context context) {
        String url = null;
        try {
            property.load(context.openFileInput("zhihui.properties"));
            url = property.getProperty("aes_vector");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     *
     */
    public String getFactory(Context context) {
        String url = null;
        try {
            property.load(context.openFileInput("zhihui.properties"));
            url = property.getProperty("factory");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     *
     */
    public String getUrlTest(Context context) {
        String url = null;
        try {
            property.load(context.openFileInput("zhihui.properties"));
            url = property.getProperty("urltest");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    //修改配置文件
    public String setProperties(Context context, String keyName, String keyValue) {
        try {
            property.load(context.openFileInput("zhihui.properties"));
            property.setProperty(keyName, keyValue);
            FileOutputStream out = context.openFileOutput("zhihui.properties", Context.MODE_PRIVATE);
            property.store(out, null);
        } catch (Exception e) {
            e.printStackTrace();
            return "修改配置文件失败!";
        }
        return "设置成功";
    }

    //初始化配置文件
    public static String initProperties(Context context) {
        Properties props = new Properties();
        try {
            props.load(context.getAssets().open("zhihui.properties"));
            FileOutputStream out = context.openFileOutput("zhihui.properties", Context.MODE_PRIVATE);
            props.store(out, null);
        } catch (Exception e) {
            e.printStackTrace();
            return "修改配置文件失败!";
        }
        return "设置成功";
    }


    /**
     * number_of_ordinary_keys = 3
     * #终端有没有SOS键:(0=没有, 1=有
     * has_sos = 1
     * #终端类型:1=GPS, 2=CellID, 3=AGPS
     * devices_type =2
     * #终端是否具备区域报警功能:(0=#不具备, 1=具备
     * area_alarm = 1
     * #终端是否具备设置呼入号码功能:(0=不具备, 1=具备)
     * set_incoming = 1
     * #终端软件协议版本:终端软件协议版本号,一共2位,第一位为大版本号,第二位为小版本号,目前该字段值为21，小版本号为0为每一版本的基础版本
     * protocol_version = 2312
     */
    public String getNumber_of_ordinary_keys(Context context) {
        String url = "";
        try {
            property.load(context.openFileInput("zhihui.properties"));
            url = property.getProperty("number_of_ordinary_keys");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public String getHas_sos(Context context) {
        String url = "";
        try {
            property.load(context.openFileInput("zhihui.properties"));
            url = property.getProperty("has_sos");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public String getDevices_type(Context context) {
        String url = "";
        try {
            property.load(context.openFileInput("zhihui.properties"));
            url = property.getProperty("devices_type");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public String getArea_alarm(Context context) {
        String url = "";
        try {
            property.load(context.openFileInput("zhihui.properties"));
            url = property.getProperty("area_alarm");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public String getSet_incoming(Context context) {
        String url = "";
        try {
            property.load(context.openFileInput("zhihui.properties"));
            url = property.getProperty("set_incoming");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public String getProtocol_version(Context context) {
        String url = "";
        try {
            property.load(context.openFileInput("zhihui.properties"));
            url = property.getProperty("protocol_version");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public String getLoginString(Context context) {
        String url = getNumber_of_ordinary_keys(context) + "@" + getHas_sos(context) + "@"
                + getDevices_type(context) + "@" + getArea_alarm(context) + "@"
                + getSet_incoming(context) + "@" + getProtocol_version(context);
        return url;
    }


    public boolean isWeixiao(Context context) {
        boolean isWeixiao = false;
        try {
            property.load(context.openFileInput("zhihui.properties"));
            isWeixiao = Boolean.valueOf(property.getProperty("weixiao"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isWeixiao;
    }
}
