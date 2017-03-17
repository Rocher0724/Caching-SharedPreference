package choongyul.android.com.study0317;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SharedPreferenceActivity extends AppCompatActivity {

    // (러시앤)캐싱
    // 인메모리 캐싱과 디스크 캐싱?

    // 하기전에 쉐어드 프리퍼런스
    SharedPreferences sp;
    static final String PREFERENCE_NAME = "MY_REFERENCE";
    boolean isFirst = true;
    boolean accesable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //첫 방문시 체크를 위해 주었다.                    첫방문시 FIRST_VISIT이 put된적이 없으므로 true를 반환하게 한다.
        isFirst = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).getBoolean("FIRST_VISIT",true);

        // 로그인시 처리를 위한 불린값 가져오기
        accesable = (getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).getString("USER_INFO",null).equals(null))? false : true;
        if( !isFirst )
            return;

        sp = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("firstStringVlaue","안녕하세요");

        // 첫방문시 펄스만들어 다음번에 안내화면이 안뜨게한다.다.
        editor.putBoolean("FIRST_VISIT",false);

        // 자동로그인때문에 하는것
        String cookie = "기기에서 받아오는값";
        editor.putString("USER_INFO",cookie);
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isFirst) {
            // 안내화면 표시
        }
    }
}
