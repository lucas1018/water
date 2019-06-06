package cn.zerone.water.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class SPUtils {
    private static SharedPreferences sharedPreferences = null;

    public static final String KEY_CACHE_GPS = "user_cache_gps";
    public static final String KEY_USER = "user";
    public static void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
    }

    public static void putDeviceId(String deviceId) {
        sharedPreferences.edit().putString("deviceId", deviceId).commit();
    }

    public static String getDeviceId() {
        return sharedPreferences.getString("deviceId", "");
    }

    /**
     * @param ctx
     * @param key   存储boolean变量方法
     * @param value
     */
    public static void putBoolean(Context ctx, String key, boolean value) {

        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    /**
     * @param ctx
     * @param key      读取boolean变量方法
     * @param defValue
     * @return
     */
    public static boolean getBoolean(Context ctx, String key, boolean defValue) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sharedPreferences.getBoolean(key, defValue);
    }

    /**
     * @param ctx
     * @param key   存String变量
     * @param value
     */

    public static void putString(Context ctx, String key, String value) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putString(key, value).commit();
    }

    /**
     * @param ctx
     * @param key      取String变量
     * @param defValue
     * @return
     */
    public static String getString(Context ctx, String key, String defValue) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(key, defValue);
    }

    /**
     * @param ctx
     * @param key   存String变量
     * @param value
     */

    public static void putInt(Context ctx, String key, int value) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putInt(key, value).commit();
    }

    /**
     * @param ctx
     * @param key      取String变量
     * @param defValue
     * @return
     */
    public static int getInt(Context ctx, String key, int defValue) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sharedPreferences.getInt(key, defValue);

    }

    /**
     * 存放token的方法
     *
     * @param token
     * @param context
     */
    public static void setToken(String token, Context context) {
        sharedPreferences = context.getSharedPreferences("tokenflag", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("token", token).commit();

    }

    public static void setState(String state, Context context) {
        sharedPreferences = context.getSharedPreferences("stateflag", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("state", state).commit();

    }


    /**
     * 获取token的方法
     *
     * @param context
     * @return
     */
    public static String getToken(Context context) {
        sharedPreferences = context.getSharedPreferences("tokenflag", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "0");
    }

    public static String getState(Context context) {
        sharedPreferences = context.getSharedPreferences("stateflag", Context.MODE_PRIVATE);
        return sharedPreferences.getString("state", "0");
    }

    /**
     * 存放token的方法
     *
     * @param uid
     * @param context
     */
    public static void setUid(String uid, Context context) {
        sharedPreferences = context.getSharedPreferences("uidflag", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("uid", uid).commit();

    }

    public static void setLat(String lat, Context context) {
        sharedPreferences = context.getSharedPreferences("lat", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("lat", lat).commit();

    }

    public static void setLng(String lng, Context context) {
        sharedPreferences = context.getSharedPreferences("lng", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("lng", lng).commit();

    }

    public static String getLat(Context context) {
        sharedPreferences = context.getSharedPreferences("lat", Context.MODE_PRIVATE);
        return sharedPreferences.getString("lat", "0");
    }

    public static void setAddress(String address, Context context) {
        sharedPreferences = context.getSharedPreferences("address", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("address", address).commit();

    }

    public static String getAddress(Context context) {
        sharedPreferences = context.getSharedPreferences("address", Context.MODE_PRIVATE);
        return sharedPreferences.getString("address", "");
    }

    public static String getLng(Context context) {
        sharedPreferences = context.getSharedPreferences("lng", Context.MODE_PRIVATE);
        return sharedPreferences.getString("lng", "0");
    }

    /**
     * 获取token的方法
     *
     * @param context
     * @return
     */
    public static String getUid(Context context) {
        sharedPreferences = context.getSharedPreferences("uidflag", Context.MODE_PRIVATE);
        return sharedPreferences.getString("uid", "0");
    }

    /**
     * 获取token的方法
     *
     * @param
     * @return
     */
//    public static String getToken() {
//        sharedPreferences = PrimaryApplication.getApplication().getSharedPreferences("tokenflag", Context.MODE_PRIVATE);
//        return sharedPreferences.getString("token", "0");
//    }

    /**
     * 获取密码的方法
     */
    public static String getPassword(Context context) {
        sharedPreferences = context.getSharedPreferences("tokenflag", Context.MODE_PRIVATE);
        return sharedPreferences.getString("password", "0");
    }

    /**
     * 存放密码的方法
     */
    public static void setPassword(String token, Context context) {
        sharedPreferences = context.getSharedPreferences("tokenflag", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("password", token).commit();

    }


    /**
     * 存储用户设置
     *
     * @param context 上下文
     * @return 返回创建好文件的sp对象
     */
    public static SharedPreferences userSetting(Context context) {
        return context.getSharedPreferences("user_setting", Context.MODE_PRIVATE);
    }

    /**
     * 存储常用设置
     *
     * @param context 上下文
     * @return 返回创建好文件的sp对象
     */
    public static SharedPreferences commonSetting(Context context) {
        return context.getSharedPreferences("common_setting", Context.MODE_PRIVATE);
    }

    /**
     * put long preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putLong(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    /**
     * get long preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     * name that is not a long
     * @see #getLong(Context, String, long)
     */
    public static long getLong(Context context, String key) {
        return getLong(context, key, -1);
    }

    /**
     * get long preferences
     *
     * @param context
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a long
     */
    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences settings = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return settings.getLong(key, defaultValue);
    }

    public static void deleteData(Context context, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences("config", Context.MODE_PRIVATE).edit();
        editor.remove(key);
        editor.commit();
    }
    public static void putBean(Context context, String key, Object obj) {
        if (obj instanceof Serializable) {// obj必须实现Serializable接口，否则会出问题
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(obj);
                String string64 = new String(Base64.encode(baos.toByteArray(),0));
                SharedPreferences.Editor editor = context.getSharedPreferences("user", Context.MODE_PRIVATE).edit();
                editor.putString(key, string64).commit();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            throw new IllegalArgumentException("the obj must implement Serializble");
        }

    }
    public static void deleteBean(Context context, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(key, Context.MODE_PRIVATE).edit();
        editor.remove(key);
        editor.commit();
    }

    public static Object getBean(Context context, String key) {
        Object obj = null;
        try {
            String base64 = context.getSharedPreferences("user", Context.MODE_PRIVATE).getString(key, "");
            if (base64.equals("")) {
                return null;
            }
            byte[] base64Bytes = Base64.decode(base64.getBytes(), 1);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;}
}
