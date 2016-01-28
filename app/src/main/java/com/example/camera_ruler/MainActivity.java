package com.example.camera_ruler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import com.example.camera_ruler.R;
import android.opengl.Visibility;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {

    private SurfaceView cvCamera = null;
    private MySurfaceView cvDraw = null;

    private Camera camera;
    private boolean preview = false;

    private boolean showEditRoundBtn = false; // show dragable circle button

    private final int btnCnt = 8;
    private ImageButton[] btns = new ImageButton[btnCnt];

    private ImageButton btnSave, btnEdit;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEdit = (ImageButton) this.findViewById(R.id.ImageButtonEdit);
        btnSave = (ImageButton) this.findViewById(R.id.ImageButtonSave);

        btns[0] = (ImageButton) this.findViewById(R.id.ImageButton01);
        btns[1] = (ImageButton) this.findViewById(R.id.ImageButton02);
        btns[2] = (ImageButton) this.findViewById(R.id.ImageButton03);
        btns[3] = (ImageButton) this.findViewById(R.id.ImageButton04);

        btns[4] = (ImageButton) this.findViewById(R.id.ImageButton05);
        btns[5] = (ImageButton) this.findViewById(R.id.ImageButton06);
        btns[6] = (ImageButton) this.findViewById(R.id.ImageButton07);
        btns[7] = (ImageButton) this.findViewById(R.id.ImageButton08);

        cvCamera = (SurfaceView) this.findViewById(R.id.surfaceViewCamera);

        cvCamera.getHolder().setFixedSize(200, 200);
        cvCamera.getHolder().addCallback(new SurfaceViewCallback());

        cvDraw = (MySurfaceView) this.findViewById(R.id.surfaceViewDraw);

        btnEdit.setVisibility(cvDraw.isShowRuler() ? View.VISIBLE : View.INVISIBLE);

        ShowButtons(false);
        btnSave.setVisibility(View.INVISIBLE);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        final int screenWidth = dm.widthPixels;
        final int screenHeight = dm.heightPixels - 50;

        for (ImageButton btn : btns) {
            btn.setImageAlpha(cvDraw.getDrawAlpha());

            btn.setOnTouchListener(new OnTouchListener() {

                int lastX = 0;
                int lastY = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int ea = event.getAction();
                    // Log.i("TAG", "Touch:" + ea);
                    // for (int i=0; i<8; ++i){
                    // if (v.getId() == btns[i].getId()){
                    // Log.i("Button", "Button: " +(i+1));
                    // }
                    // }
                    switch (ea) {
                        case MotionEvent.ACTION_DOWN:
                            lastX = (int) event.getRawX();// get original touch location x point
                            lastY = (int) event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            int dx = (int) event.getRawX() - lastX;
                            int dy = (int) event.getRawY() - lastY;
                            int l = (int) v.getX() + dx;
                            int t = (int) v.getY() + dy;

                            // whether outside screen
                            if (l < 0) {
                                l = 0;
                            }
                            if (t < 0) {
                                t = 0;
                            }
                            if (l + v.getWidth() / 2 > screenWidth) {
                                l = screenWidth - v.getWidth() / 2;
                            }
                            if (t + v.getHeight() / 2 > screenHeight) {
                                t = screenHeight - v.getHeight() / 2;
                            }

                            lastX = (int) event.getRawX();
                            lastY = (int) event.getRawY();

                            OnMoveButton(v, l, t);

                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                    }
                    return false;
                }
            });
        }

        LoadOptions();
        if (LoadConfig()) {
            Log.i("daoche", "read success");
        }

        getCameraCount();
        RefreshDraw();

    }

    protected void OnMoveButton(View v, int l, int t) {
        // TODO Auto-generated method stub
        Point p = new Point(l + v.getWidth() / 2, t + v.getHeight() / 2);
        int id = v.getId();
        int side = 0;
        if (id == btns[0].getId()) {
            cvDraw.getSideLeft().CalcP0(p);
            side = SideInfo.LEFT_SIDE;
        } else if (id == btns[1].getId()) {
            side = SideInfo.LEFT_SIDE;
            // cvDraw.getSideLeft().setP05(p);
            cvDraw.getSideLeft().CalcP05(p);
        } else if (id == btns[2].getId()) {
            side = SideInfo.LEFT_SIDE;
            cvDraw.getSideLeft().CalcP15(p);
        } else if (id == btns[3].getId()) {
            side = SideInfo.LEFT_SIDE;
            cvDraw.getSideLeft().CalcP1(p);
        } else if (id == btns[4].getId()) {
            side = SideInfo.RIGHT_SIDE;
            cvDraw.getSideRight().CalcP0(p);
        } else if (id == btns[5].getId()) {
            side = SideInfo.RIGHT_SIDE;
            cvDraw.getSideRight().CalcP05(p);
        } else if (id == btns[6].getId()) {
            side = SideInfo.RIGHT_SIDE;
            cvDraw.getSideRight().CalcP15(p);
        } else if (id == btns[7].getId()) {
            side = SideInfo.RIGHT_SIDE;
            cvDraw.getSideRight().CalcP1(p);
        }

        Log.i("Position", "button Position:" + p.toString());

        cvDraw.draw();
        RepostionButton(side, -1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void setButtonPos(ImageButton btn, Point p) {
        btn.setX(p.x - btn.getWidth() / 2);
        btn.setY(p.y - btn.getHeight() / 2);
        btn.postInvalidate();

    }

    private void ShowButtons(boolean bVisible) {
        for (ImageButton btn : btns) {
            btn.setVisibility(bVisible ? View.VISIBLE : View.INVISIBLE);
            if (bVisible) {
                btn.setLayoutParams(btn.getLayoutParams());
            }
        }
    }

    private void RepostionButton(int side, int id) {
        if (side == SideInfo.LEFT_SIDE || side == SideInfo.ALL) {
            SideInfo sl = cvDraw.getSideLeft();
            if (id != btns[0].getId())
                setButtonPos(btns[0], sl.getP0());
            if (id != btns[1].getId())
                setButtonPos(btns[1], sl.getP05());
            if (id != btns[2].getId())
                setButtonPos(btns[2], sl.getP15());
            if (id != btns[3].getId())
                setButtonPos(btns[3], sl.getP1());
        }

        if (side == SideInfo.RIGHT_SIDE || side == SideInfo.ALL) {
            SideInfo sr = cvDraw.getSideRight();
            if (id != btns[4].getId())
                setButtonPos(btns[4], sr.getP0());
            if (id != btns[5].getId())
                setButtonPos(btns[5], sr.getP05());
            if (id != btns[6].getId())
                setButtonPos(btns[6], sr.getP15());
            if (id != btns[7].getId())
                setButtonPos(btns[7], sr.getP1());
        }
    }

    public void OnButtonShowRulerClick(View v) {
        cvDraw.setShowRuler(!cvDraw.isShowRuler());

        cvDraw.draw();
        RepostionButton(0, -1);
        btnEdit.setVisibility(cvDraw.isShowRuler() ? View.VISIBLE : View.INVISIBLE);

        showEditRoundBtn = false;
        ShowButtons(false);

        btnSave.setVisibility(View.INVISIBLE);
    }

    public void OnButtonEditClick(View v) {
        if (cvDraw.isShowRuler()) {
            showEditRoundBtn = !showEditRoundBtn;
            if (showEditRoundBtn)
                RepostionButton(SideInfo.ALL, -1);

            ShowButtons(showEditRoundBtn);

            btnSave.setVisibility(showEditRoundBtn ? View.VISIBLE : View.INVISIBLE);
        }

    }

    public void OnButtonSaveClick(View v) {
        if (showEditRoundBtn) {
            SaveConfig();
            Toast.makeText(getApplicationContext(), "save success", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * get data
     *
     * @return
     */
    private boolean LoadConfig() {
        // read file
        Properties properties = new Properties();
        try {
            String strFile = getApplicationContext().getFilesDir() + "/settings.dat";
            File fil = new File(strFile);
            if (!fil.exists()) {
                fil.createNewFile();
                SaveConfig();
                return false;
            } else {
                FileInputStream s = new FileInputStream(strFile);
                properties.load(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        cvDraw.getSideLeft().fromString(properties.getProperty("left", ""));
        cvDraw.getSideRight().fromString(properties.getProperty("right", ""));

        return true;
    }

    /**
     * save data
     */
    private void SaveConfig() {
        String strFile = getApplicationContext().getFilesDir() + "/settings.dat";
        try {
            FileOutputStream s = new FileOutputStream(strFile, false);
            Properties properties = new Properties();

            File fil = new File(strFile);
            if (!fil.exists()) {
                fil.createNewFile();

            }
            properties.setProperty("left", cvDraw.getSideLeft().toString());
            properties.setProperty("right", cvDraw.getSideRight().toString());

            properties.store(s, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadOptions() {
        String strFile = getApplicationContext().getFilesDir() + "/options.dat";
        try {
            File fil = new File(strFile);
            if (!fil.exists()) {
                fil.createNewFile();
                SaveOptions();

                return;

            }

            FileInputStream s = new FileInputStream(strFile);
            Properties properties = new Properties();

            properties.load(s);

            cvDraw.op.showBaseline = Boolean.valueOf(properties.getProperty("showgrid", "true"));
            cvDraw.op.draw_alpha = Integer.valueOf(properties.getProperty("alpha", "128"));
            cvDraw.op.shadowDeep = Integer.valueOf(properties.getProperty("shadow", "0"));
            cvDraw.op.scaleWidth = Integer.valueOf(properties.getProperty("scalewidth", "60"));
            cvDraw.op.lineWidth = Integer.valueOf(properties.getProperty("linewidth", "20"));
            cvDraw.op.gridLineCnt = Integer.valueOf(properties.getProperty("gridcnt", "5"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void SaveOptions() {
        String strFile = getApplicationContext().getFilesDir() + "/options.dat";
        try {
            FileOutputStream s = new FileOutputStream(strFile, false);
            Properties properties = new Properties();

            File fil = new File(strFile);
            if (!fil.exists()) {
                fil.createNewFile();
            }

            properties.setProperty("alpha", String.valueOf(cvDraw.op.draw_alpha));
            properties.setProperty("shadow", String.valueOf(cvDraw.op.shadowDeep));
            properties.setProperty("showgrid", String.valueOf(cvDraw.op.showBaseline));
            properties.setProperty("gridcnt", String.valueOf(cvDraw.op.gridLineCnt));

            properties.setProperty("scalewidth", String.valueOf(cvDraw.op.scaleWidth));
            properties.setProperty("linewidth", String.valueOf(cvDraw.op.lineWidth));

            properties.store(s, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private int mNumberOfCameras = 0;
    private int mCurrCameraID = -1;

    // private Camera.Parameters cameraParms=null;
    // List<Size> prevSize;
    private int getCameraCount() {
        mNumberOfCameras = Camera.getNumberOfCameras();
        // cameraParms = camera.getParameters();
        // prevSize = cameraParms.getSupportedPreviewSizes();

        mCurrCameraID = Math.min(0, mNumberOfCameras - 1);
        return mCurrCameraID;


    }

    public class SurfaceViewCallback implements Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // TODO Auto-generated method stub
            //previewCamera(holder);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub

            previewCamera();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            closeCamera();
        }

    }


    private int request_code = 1;

    // open option dialog
    public void OnButtonOptionClick(View v) {
        Intent intent = new Intent();
        intent.setClass(this, OptionActivity.class);// TestActivity
        Bundle bundle = new Bundle();
        bundle.putSerializable("option", cvDraw.op);
        intent.putExtras(bundle);
        startActivityForResult(intent, request_code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (request_code == requestCode) {
            OptionParam op = (OptionParam) data.getSerializableExtra("option");
            cvDraw.op = op;
            SaveOptions();
            RefreshDraw();

        }
    }

    protected void RefreshDraw() {
        for (ImageButton btn : btns) {
            btn.setImageAlpha(cvDraw.getDrawAlpha());
        }
        cvDraw.draw();
        ShowButtons(showEditRoundBtn);

    }

    public void OnButtonSwitchCameraClick(View v) {

        closeCamera();
        if (mCurrCameraID < mNumberOfCameras - 1)
            mCurrCameraID++;
        else
            mCurrCameraID = 0;
        previewCamera();

    }

    private void previewCamera() {
        try {
            if (mCurrCameraID < 0)
                return;
            if (preview) return;
            camera = Camera.open(mCurrCameraID);
            if (camera == null) {
                Toast.makeText(getApplicationContext(), "can't open camera", Toast.LENGTH_LONG).show();
                return;
            }
            initPreview(cvCamera.getWidth(), cvCamera.getHeight());

            camera.setPreviewDisplay(cvCamera.getHolder());
            camera.startPreview();
            preview = true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (camera != null) {
            /* if camera is occupied stop */
            if (preview) {
                camera.stopPreview();
                preview = false;
            }
            // if register callback call it before release or crash
            camera.setPreviewCallback(null);
            camera.release();
        }
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return result;
    }

    private void initPreview(int width, int height) {
        if (camera != null && cvCamera != null) {
            try {
                camera.setPreviewDisplay(cvCamera.getHolder());
            } catch (Throwable t) {
                //Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
                Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = getBestPreviewSize(width, height, parameters);

            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                camera.setParameters(parameters);
            }
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

}
