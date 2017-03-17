
package choongyul.android.com.study0317;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.View;

import java.io.InputStream;

/**
 * Created by DoDo on 2017-03-17.
 */
// 이미지뷰 객체.
public class AutoCachingImageView extends View {

    private LruCache<String, Bitmap> cache;

    private Bitmap mBitmap;
    private Paint paint;
    private boolean doAutoCaching = false;
    private float bitmapWidth, bitmapHeight;
    private String cacheTag = null;
    CacheHelper<Bitmap> cacheHelper;

    private AutoCachingImageView(Context context) {
        super(context);
    }

    public AutoCachingImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public AutoCachingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AutoCachingImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        terminate();
    }

    // R.styleable.AutoCachingImageView 에
//    <decl 디클레어
//        <attr name = bitmapId format="integer"
//        <attr name = cache format=""
//  이런식으로 한다.

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.AutoCachingImageView);
        cacheTag = ta.getString(R.styleable.AutoCachingImageView_cacheTag);
        if(cacheTag != null)
            doAutoCaching = true;
        int bitmapId = ta.getInteger(R.styleable.AutoCachingImageView_cacheTag,-1);
        if(bitmapId == -1)
            return;
        setBitmap(bitmapId);

        cacheHelper = new DiskCacher(getContext());
        paint = new Paint();

    }

    private void terminate(){

    }

    private BitmapFactory.Options getPreBitmapOptions(int resId){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(),resId,options);
        return options;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, getX(), getY(), paint);
    }

    private BitmapFactory.Options getPreBitmapOptions(InputStream stream){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream,new Rect(-1,-1,-1,-1),options);
        return options;
    }

    private int getSampleSize(int height, int width){
        int rate = 1;
        if(height>getHeight()||width>getWidth()){
            rate = 2;
            height /= rate;
            width /= rate;
            while(height>getHeight()||width>getWidth()){
                rate++;
                height /= rate;
                width /= rate;
            }
        }
        return rate;
    }
    private int getSampleSize(BitmapFactory.Options options){
        return getSampleSize(options.outHeight,options.outWidth);
    }


    public void setBitmap(int resId){
        Bitmap temp = null;
        if(doAutoCaching)
            temp = getCachedBitmap(resId);
        if(temp == null)
            temp = buildBitmap(resId);
        setBitmap(temp, resId+"");
    }

    public void setmBitmap(String cacheTag){
        this.cacheTag = cacheTag;
        setBitmap(getCachedBitmap(cacheTag),cacheTag);
    }
    public void setBitmap(Bitmap bitmap, String cacheTag){
        mBitmap = buildBitmap(bitmap);
        cacheBitmap(mBitmap,cacheTag);
        invalidate();
    }
    public void setmBitmap(InputStream stream, String cacheTag){
        mBitmap = buildBitmap(stream);
        cacheBitmap(mBitmap,cacheTag);
        invalidate();
    }

    private void cacheBitmap(Bitmap bitmap, String cacheTag){
        //cache
        cacheHelper.put(cacheTag,bitmap);
        this.cacheTag = cacheTag;
        Log.i("AutoCachingImageView","Image "+cacheTag+" is cached");
    }


    private Bitmap getCachedBitmap(int resId){
        return getCachedBitmap(resId+"");
    }
    private Bitmap getCachedBitmap(String cacheTag){
        return cacheHelper.get(cacheTag);
    }

    private Bitmap buildBitmap(int resId){
        Bitmap temp = null;
        BitmapFactory.Options options = getPreBitmapOptions(resId);
        options.inSampleSize = getSampleSize(options);
        options.inJustDecodeBounds = false;
        temp = BitmapFactory.decodeResource(getResources(),resId,options);
        return temp;
    }

    private Bitmap buildBitmap(InputStream stream){
        Bitmap temp = null;
        BitmapFactory.Options options = getPreBitmapOptions(stream);
        options.inSampleSize = getSampleSize(options);
        options.inJustDecodeBounds = false;
        temp = BitmapFactory.decodeStream(stream, new Rect(-1,-1,-1,-1),options);
        return temp;
    }

    private Bitmap buildBitmap(Bitmap bitmap){
        Matrix matrix = new Matrix();
        float rate = 1/getSampleSize(bitmap.getHeight(),bitmap.getWidth());
        matrix.postScale(rate,rate);
        Bitmap temp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return temp;
    }

}
