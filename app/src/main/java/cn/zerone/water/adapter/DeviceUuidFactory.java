package cn.zerone.water.adapter;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by Administrator on 2018/8/30.
 */

public class DeviceUuidFactory {
//    protected static final String PREFS_FILE = "doctor";
//    protected static final String PREFS_DEVICE_ID = "uuid";
    protected static UUID uuid;

    public static UUID GetDeviceUuidFactory(Context context) {
        if (uuid == null) {
            synchronized (DeviceUuidFactory.class) {
                if (uuid == null) {
                //    final SharedPreferences prefs = context.getSharedPreferences( "uuid", 0);
                   // final String id = SPUtils.getString(context,"uuid", null);
//                    if (id != null) {
//                        // Use the ids previously computed and stored in the prefs file
//                        uuid = UUID.fromString(id);
//                    } else {
                        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                            } else {
                                final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                                uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();

                            }
                            return uuid;
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);

                        }
                        // Write the value out to the prefs file
                       // prefs.edit().putString("uuid", uuid.toString()).commit();
                        //SPUtils.putString(context,"uuid", uuid.toString());
                    }else {
                    return null;
                }
                }
            }else {
            return uuid;
        }

       // }
    }

}