package com.example.camera;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import com.example.deinvirtuellerfreund.MainActivity;
import com.example.voice.TFLiteClassifier;
import com.google.android.gms.vision.CameraSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GraphicOverlay extends View implements SurfaceHolder.Callback {
    CameraSource mCameraSource;
    Context mContext;
    private final Object mLock = new Object();
    private int mPreviewWidth;
    private float mWidthScaleFactor = 1.0F;
    private int mPreviewHeight;
    private float mHeightScaleFactor = 1.0F;
    private int mFacing = 0;
    private int rotation;
    private Set<Graphic> mGraphics = new HashSet();



    public static boolean camera_active = true;
    public static boolean delay_active = true;

    private double time=System.currentTimeMillis();
    private double dTime;

    int left;
    int top;
    int right;
    int bottom;
    int x;
    int y;
    int x_face;
    int y_face;
    int width;
    int height;

    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void clear() {
        Object var1 = this.mLock;
        synchronized(this.mLock) {
            this.mGraphics.clear();
        }

        this.postInvalidate();
    }

    public void add(GraphicOverlay.Graphic graphic) {
        Object var2 = this.mLock;
        synchronized(this.mLock) {
            this.mGraphics.add(graphic);
        }

        this.postInvalidate();
    }

    public void remove(GraphicOverlay.Graphic graphic) {
        Object var2 = this.mLock;
        synchronized(this.mLock) {
            this.mGraphics.remove(graphic);
        }

        this.postInvalidate();
    }

    public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
        Object var4 = this.mLock;
        synchronized(this.mLock) {
            this.mPreviewWidth = previewWidth;
            this.mPreviewHeight = previewHeight;
            this.mFacing = facing;
        }

        this.postInvalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(camera_active==false){

        }else {
            if(delay_active==false){

                Object var2 = this.mLock;
                synchronized (this.mLock) {
                    if (this.mPreviewWidth != 0 && this.mPreviewHeight != 0) {
                        this.mWidthScaleFactor = (float) canvas.getWidth() / (float) this.mPreviewWidth;
                        this.mHeightScaleFactor = (float) canvas.getHeight() / (float) this.mPreviewHeight;
                    }

                    Iterator var3 = this.mGraphics.iterator();

                    while (var3.hasNext()) {
                        GraphicOverlay.Graphic graphic = (GraphicOverlay.Graphic) var3.next();
                        graphic.draw(canvas);
                        left = Math.round(graphic.getLeft());
                        right = Math.round(graphic.getRight());
                        top = Math.round(graphic.getTop());
                        bottom = Math.round(graphic.getBottom());
                        x = Math.round(graphic.getX());
                        y = Math.round(graphic.getY());
                        width = Math.round(graphic.getWidth());
                        height = Math.round(graphic.getHeight());
                        x_face = Math.round(graphic.getX_face());
                        y_face = Math.round(graphic.getY_face());
                        //add delay mit if
                        takephoto();
                    }

                }

            }else {

                //TODO ADD DELAY
                double curTime=System.currentTimeMillis();
                dTime+=curTime-time;
                time=curTime;
                if(dTime>3000f){
                    dTime=0;
                    Object var2 = this.mLock;
                    synchronized (this.mLock) {
                        if (this.mPreviewWidth != 0 && this.mPreviewHeight != 0) {
                            this.mWidthScaleFactor = (float) canvas.getWidth() / (float) this.mPreviewWidth;
                            this.mHeightScaleFactor = (float) canvas.getHeight() / (float) this.mPreviewHeight;
                        }

                        Iterator var3 = this.mGraphics.iterator();

                        while (var3.hasNext()) {
                            GraphicOverlay.Graphic graphic = (GraphicOverlay.Graphic) var3.next();
                            graphic.draw(canvas);
                            left = Math.round(graphic.getLeft());
                            right = Math.round(graphic.getRight());
                            top = Math.round(graphic.getTop());
                            bottom = Math.round(graphic.getBottom());
                            x = Math.round(graphic.getX());
                            y = Math.round(graphic.getY());
                            width = Math.round(graphic.getWidth());
                            height = Math.round(graphic.getHeight());
                            x_face = Math.round(graphic.getX_face());
                            y_face = Math.round(graphic.getY_face());
                            //add delay mit if
                            takephoto();
                        }

                    }
                }


            }
        }
    }

    public void setSourceAndContext(CameraSource mCameraSource, final Context mContext){
        this.mCameraSource = mCameraSource;
        this.mContext = mContext;
    }

    public void takephoto(){
        System.out.println("METHODE: TAKEPHOTO wird ausgefuehrt!!!!!!!!!!!!!!");
        try{
            //openCamera(CameraInfo.CAMERA_FACING_BACK);
            //releaseCameraSource();
            //releaseCamera();
            //openCamera(CameraInfo.CAMERA_FACING_BACK);
            //setUpCamera(camera);
            //Thread.sleep(1000);
            mCameraSource.takePicture(null, new CameraSource.PictureCallback() {

                private File imageFile;
                @Override
                public void onPictureTaken(byte[] bytes) {
                    try {
                        // convert byte array into bitmap
                        Bitmap loadedImage = null;
                        Bitmap rotatedBitmap = null;
                        loadedImage = BitmapFactory.decodeByteArray(bytes, 0,
                                bytes.length);

                        // rotate Image
                        Matrix rotateMatrix = new Matrix();
                        rotateMatrix.postRotate(rotation);
                        rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                                loadedImage.getWidth(), loadedImage.getHeight(),
                                rotateMatrix, false);
                        String state = Environment.getExternalStorageState();
                        File folder = null;
                        if (state.contains(Environment.MEDIA_MOUNTED)) {
                            folder = new File(Environment
                                    .getExternalStorageDirectory() + "/Demo");
                        } else {
                            folder = new File(Environment
                                    .getExternalStorageDirectory() + "/Demo");
                        }

                        boolean success = true;
                        if (!folder.exists()) {
                            success = folder.mkdirs();
                        }
                        if (success) {
                            java.util.Date date = new java.util.Date();
                            imageFile = new File(folder.getAbsolutePath()
                                    + File.separator
                                    //+ new Timestamp(date.getTime()).toString()
                                    + "Image.jpg");

                            imageFile.createNewFile();
                        } else {
                            Toast.makeText(mContext, "Image Not saved",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ByteArrayOutputStream ostream = new ByteArrayOutputStream();


                        rotatedBitmap = resize(rotatedBitmap, 800, 600);
                        //rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);

                        Bitmap croppedBmp = Bitmap.createBitmap(rotatedBitmap, x_face, y_face, width, height);
                        Bitmap scaledBmp = Bitmap.createScaledBitmap(croppedBmp, 48, 48, true);

                        scaledBmp.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                        System.out.println("Bild ist: " + scaledBmp.toString());

                        //TO-DO: scaledBmp is the image you have to compare with the database.
                        if(delay_active==true) {
                            TFLiteClassifier tflite = new TFLiteClassifier(MainActivity.activity);
                            tflite.recognizeImage(scaledBmp);
                        } else {
                            TFLiteClassifier tflite = new TFLiteClassifier(MainActivity.activity);
                            tflite.checkIfLaughing(scaledBmp);
                        }

                        FileOutputStream fout = new FileOutputStream(imageFile);
                        fout.write(ostream.toByteArray());
                        fout.close();
                        ContentValues values = new ContentValues();

                        values.put(MediaStore.Images.Media.DATE_TAKEN,
                                System.currentTimeMillis());
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                        values.put(MediaStore.MediaColumns.DATA,
                                imageFile.getAbsolutePath());

                        mContext.getContentResolver().insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                        //setResult(Activity.RESULT_OK); //add this
                        //finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception ex){
            //txTextoCapturado.setText("Error al capturar fotografia!");
            System.out.println("Error beim Fotografieren!");
        }
    }


    /**
     * Metodo para cambiar el tamaño de la fotografia una resolucion predeterminada.
     *
     * @param image
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    public static boolean isCamera_active() {
        return camera_active;
    }

    public static void setCamera_active(boolean camera_active) {
        GraphicOverlay.camera_active = camera_active;
    }

    public static boolean isDelay_active() {
        return delay_active;
    }

    public static void setDelay_active(boolean delay_active) {
        GraphicOverlay.delay_active = delay_active;
    }

    public abstract static class Graphic {
        private GraphicOverlay mOverlay;

        public Graphic(GraphicOverlay overlay) {
            this.mOverlay = overlay;
        }

        public abstract void draw(Canvas var1);

        public abstract float getLeft();
        public abstract float getRight();
        public abstract float getTop();
        public abstract float getBottom();
        public abstract float getX();
        public abstract float getY();
        public abstract float getHeight();
        public abstract float getWidth();
        public abstract float getX_face();
        public abstract float getY_face();


        public float scaleX(float horizontal) {
            return horizontal * this.mOverlay.mWidthScaleFactor;
        }

        public float scaleY(float vertical) {
            return vertical * this.mOverlay.mHeightScaleFactor;
        }

        public float translateX(float x) {
            return this.mOverlay.mFacing == 1 ? (float)this.mOverlay.getWidth() - this.scaleX(x) : this.scaleX(x);
        }

        public float translateY(float y) {
            return this.scaleY(y);
        }

        public void postInvalidate() {
            this.mOverlay.postInvalidate();
        }
    }
}