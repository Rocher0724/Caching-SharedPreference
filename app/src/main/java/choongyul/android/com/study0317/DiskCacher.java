package choongyul.android.com.study0317;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.os.Environment.isExternalStorageRemovable;

/**
 * Created by DoDo on 2017-03-17.
 */

public class  DiskCacher implements CacheHelper<Bitmap> {

    private Context context;
    private static final String DISK_CAHCHE_NAME = "DISK_CACHE";
    private static final int MB = 1024*1024;
    private static final long DISK_CACHE_SIZE = 15*MB;
    private static final int APP_VERSION = 1;
    private static final int VAL_ENTRY = 1;
    private static final int BUFFER_SIZE =1024;
    private File diskCacheDir;
    private DiskLruCache mDiskLruCache;
    private boolean mDiskCacheStarting = false;
    private Object mDiskCacheLock = new Object();
    public DiskCacher(Context context) {
        this.context = context;
        diskCacheDir = getDiskCacheDir(context,DISK_CAHCHE_NAME);
    }


    private File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !isExternalStorageRemovable() ?
                        context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor ) throws IOException, FileNotFoundException {
            OutputStream out = null;
        try {
            out = new BufferedOutputStream( editor.newOutputStream( 0 ), BUFFER_SIZE);
            return bitmap.compress(Bitmap.CompressFormat.PNG, 100, out );
        } finally {
            if ( out != null ) {
                out.close();
            }
        }
    }

    public void put( String key, Bitmap data ) {

        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskLruCache.edit( key );
            if ( editor == null ) {
                return;
            }

            if( writeBitmapToFile( data, editor ) ) {
                mDiskLruCache.flush();
                editor.commit ();
                if ( BuildConfig.DEBUG ) {
                    Log.d( "cacheTest_", "cached " + key );
                }
            } else {
                editor.abort();
                if ( BuildConfig.DEBUG ) {
                    Log.d( "cache_test_DISK_", "ERROR on: image put on disk cache " + key );
                }
            }
        } catch (IOException e) {
            if ( BuildConfig.DEBUG ) {
                Log.d( "cache_test_DISK_", "ERROR on: image put on disk cache " + key );
            }
            try {
                if ( editor != null ) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }

    }

    public Bitmap get( String key ) {

        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {

            snapshot = mDiskLruCache.get( key );
            if ( snapshot == null ) {
                return null;
            }
            final InputStream in = snapshot.getInputStream( 0 );
            if ( in != null ) {
                final BufferedInputStream buffIn =
                        new BufferedInputStream( in,BUFFER_SIZE );
                bitmap = BitmapFactory.decodeStream( buffIn );
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( snapshot != null ) {
                snapshot.close();
            }
        }

        if ( BuildConfig.DEBUG ) {
            Log.d( "cache_test_DISK_", bitmap == null ? "" : "image read from disk " + key);
        }

        return bitmap;

    }


    public boolean containsKey( String key ) {

        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskLruCache.get( key );
            contained = snapshot != null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ( snapshot != null ) {
                snapshot.close();
            }
        }

        return contained;

    }

    public void clearCache() {
        if ( BuildConfig.DEBUG ) {
            Log.d( "cache_test_DISK_", "disk cache CLEARED");
        }
        try {
            mDiskLruCache.delete();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public File getCacheFolder() {
        return mDiskLruCache.getDirectory();
    }

}
