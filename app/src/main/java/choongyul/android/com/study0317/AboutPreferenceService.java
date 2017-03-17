package choongyul.android.com.study0317;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.IntDef;

public class AboutPreferenceService extends Service {
    SharedPreferences sp;

    public AboutPreferenceService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences(SharedPreferenceActivity.PREFERENCE_NAME, MODE_PRIVATE);
        // 프리퍼런스 값의 변화가 생기면 호출되는 리스너.
        SharedPreferences.OnSharedPreferenceChangeListener spListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override                               // 변화가생긴 프리퍼런스 , 변화가생긴프리퍼런스의 키값
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            }
        };
        sp.registerOnSharedPreferenceChangeListener(spListener);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
