package choongyul.android.com.study0317;

/**
 * Created by DoDo on 2017-03-17.
 */

public interface CacheHelper<V> {
    V get(String key);
    void put(String key, V what);
}
