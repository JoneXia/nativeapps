package org.bcsphere.camera;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.bcsphere.findme.R;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

@SuppressLint({ "NewApi", "SdCardPath" })
public class TakePictureAty extends Activity implements OnTouchListener,OnClickListener,SurfaceHolder.Callback{
	private Button btnFlashLightSwitch,btnLensSwitch,btnBrowsePhoto,btnTakePicture,btnClose;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera;
	private boolean isFront = false;
	private boolean isOk = true;
	private int flashLightState = 0;
	private int mode = 0;
	private Bitmap mBitmap;
	private View mainView;
	private String flashLightMode[] = {Camera.Parameters.FLASH_MODE_AUTO,Camera.Parameters.FLASH_MODE_ON,
			Camera.Parameters.FLASH_MODE_OFF};
	private int flashBackground[] = {R.drawable.flash_auto,R.drawable.flash_turn_on,R.drawable.flash_turn_off};
	private PopupWindow mPopupWindow;
	private ImageView imgPhoto;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.take_picture_activity);
		Intent intent = getIntent();
		mode = intent.getIntExtra("MODE", 0);
		LayoutInflater inflater = LayoutInflater.from(TakePictureAty.this);
		mainView  = inflater.inflate(R.layout.take_picture_activity, null);
		View view = inflater.inflate(R.layout.popupwindow_view, null);
		mPopupWindow = new PopupWindow(view,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		imgPhoto = (ImageView) view.findViewById(R.id.imgPhoto);
		imgPhoto.setOnClickListener(this);
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		mSurfaceView.setOnTouchListener(this);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSurfaceHolder.addCallback(this);
		btnFlashLightSwitch = (Button) findViewById(R.id.btnFlashLightSwitch);
		btnLensSwitch = (Button) findViewById(R.id.btnLensSwitch);
		btnBrowsePhoto = (Button) findViewById(R.id.btnBrowsePhoto);
		btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
		btnClose = (Button) findViewById(R.id.btnClose);
		btnFlashLightSwitch.setOnClickListener(this);
		btnLensSwitch.setOnClickListener(this);
		btnBrowsePhoto.setOnClickListener(this);
		btnTakePicture.setOnClickListener(this);
		btnClose.setOnClickListener(this);
		btnFlashLightSwitch.setBackgroundDrawable(getResources().getDrawable(flashBackground[flashLightState%3]));
		isFront = false;
	}

	@Override 
	protected void onResume() {
		super.onResume();
		org.bcsphere.camera.Camera.setCameraState(true);
		if (mCamera == null) {
			if (isFront) {
				mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
			}else {
				mCamera = Camera.open();
			}
			initCamera(isFront);
		}
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("TAKE_PICTURE");
		registerReceiver(receiver, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		org.bcsphere.camera.Camera.setCameraState(false);
		unregisterReceiver(receiver);
		cleanCamera();
	}
	
	public void takePicture(){
		if (examineSDCard()) {
			if (examineStorageSpace((getScreenHeight(TakePictureAty.this)+1) * (getScreenWidth(TakePictureAty.this)+1))) {
				if (mCamera != null) {
					if (isOk) {
						isOk = false;
						mCamera.takePicture(shutterCallback, null, pictureCallback);
					}
				}
			}else{
				Toast.makeText(TakePictureAty.this, "Insufficient storage space in!", Toast.LENGTH_SHORT).show();
			}
		}else {
			Toast.makeText(TakePictureAty.this, "Did not mount the sd card ! ", Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressWarnings("deprecation")
	private void initCamera(boolean isFrontLens){
		mCamera.setDisplayOrientation(90);
		btnFlashLightSwitch.setBackgroundDrawable(getResources().getDrawable(flashBackground[flashLightState%3]));
		if (!isFrontLens) {
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setFlashMode(flashLightMode[flashLightState%3]);
			mCamera.setParameters(parameters);
		}
		Parameters parameters = mCamera.getParameters();
		Camera.Size pictureSize = getMostFitSize(parameters.getSupportedPictureSizes());
		parameters.setPictureSize(pictureSize.width, pictureSize.height);
		Camera.Size previewSize = getMostFitSize(parameters.getSupportedPreviewSizes());
		parameters.setPreviewSize(previewSize.width,previewSize.height);
		mCamera.setParameters(parameters);

		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mCamera.startPreview();
		if (mode== 0) {
			Handler handler = new Handler();
			handler.postDelayed(runnable, 1000);
		}
	}

	@SuppressWarnings("deprecation")
	private void setCameraLens(boolean isFrontLens){
		int lensCount = 0;
		if (isFrontLens) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				lensCount = Camera.getNumberOfCameras();
			}
			if (lensCount == 2) {
				cleanCamera();
				flashLightState = 0;
				btnFlashLightSwitch.setBackgroundDrawable(getResources().getDrawable(flashBackground[flashLightState%3]));
				mCamera  = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
			}else {
				isFront = false;
			}
		}else {
			cleanCamera();
			mCamera = Camera.open();
			btnFlashLightSwitch.setBackgroundDrawable(getResources().getDrawable(flashBackground[flashLightState%3]));
		}
		initCamera(isFrontLens);
	}

	private void cleanCamera(){
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	public static boolean examineSDCard(){
		return  Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	@SuppressWarnings("deprecation")
	public static boolean examineStorageSpace(long fileSize){
		File path =new File("/mnt/sdcard/");
		StatFs statFs = new StatFs(path.getPath());
		long blockSize = statFs.getBlockSize();
		long availableBlocks = statFs.getAvailableBlocks();
		long spaceSize = availableBlocks * blockSize;
		if (spaceSize > fileSize) {
			return true;
		}else {
			return false;
		}
	}

	public static byte[] getBytesFromBitmapUri(Context context, Uri uri) {
		byte[] data = null;
		ContentResolver resolver = context.getContentResolver();
		try {
			data = getBytesFromInputStream(
					resolver.openInputStream(Uri.parse(uri.toString())),
					3500000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public static byte[] getBytesFromInputStream(InputStream is, int bufsiz)
			throws IOException {
		int total = 0;
		byte[] bytes = new byte[4096];
		ByteBuffer bb = ByteBuffer.allocate(bufsiz);
		while (true) {
			int read = is.read(bytes);
			if (read == -1)
				break;
			bb.put(bytes, 0, read);
			total += read;
		}
		byte[] content = new byte[total];
		bb.flip();
		bb.get(content, 0, total);
		return content;
	}

	public static int getScreenHeight(Activity activity){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}

	public static int getScreenWidth(Activity activity){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.widthPixels;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btnFlashLightSwitch:
			if (isFront) {
				Toast.makeText(TakePictureAty.this, "Device does not support flash light !", Toast.LENGTH_SHORT).show();
			}else {
				flashLightState++;
				btnFlashLightSwitch.setBackgroundDrawable(getResources().getDrawable(flashBackground[flashLightState%3]));
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setFlashMode(flashLightMode[flashLightState%3]);
				mCamera.setParameters(parameters);
			}
			break;
		case R.id.btnLensSwitch:
			isFront = (!isFront);
			setCameraLens(isFront);
			break;
		case R.id.btnBrowsePhoto:
			if (mBitmap != null) {
				imgPhoto.setBackgroundDrawable(new BitmapDrawable(mBitmap));
				mPopupWindow.showAsDropDown(mainView, 0, 0);
			}
			break;
		case R.id.btnTakePicture:
			if (examineSDCard()) {
				if (examineStorageSpace((getScreenHeight(TakePictureAty.this)+1) * (getScreenWidth(TakePictureAty.this)+1))) {
					if (mCamera != null) {
						if (isOk) {
							isOk = false;
							mCamera.takePicture(shutterCallback, null, pictureCallback);
						}
					}
				}else{
					Toast.makeText(TakePictureAty.this, "Insufficient storage space in!", Toast.LENGTH_SHORT).show();
				}
			}else {
				Toast.makeText(TakePictureAty.this, "Did not mount the sd card ! ", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btnClose:
			close();
			break;
		case R.id.imgPhoto:
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
			}
			break;
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		if (mCamera != null) {
			mCamera.autoFocus(new AutoFocusCallback() {

				@Override
				public void onAutoFocus(boolean arg0, Camera arg1) {

				}
			});
		}
		return false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		mSurfaceHolder = arg0;
		if (mCamera != null) {
			try {
				mCamera.setPreviewDisplay(mSurfaceHolder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		cleanCamera();
	}

	private ShutterCallback shutterCallback = new ShutterCallback() {

		@Override
		public void onShutter() {

		}
	};

	private PictureCallback pictureCallback = new PictureCallback() {

		@SuppressWarnings("deprecation")
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			isOk = true;
			if (mode == 0) {
				close();
			}else if (mode == 1) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				arg1.startPreview();
			}
			Bitmap bitmap = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
			Matrix matrix = new Matrix();
			if (isFront) {
				matrix.setRotate(270);  
			}else {
				matrix.setRotate(90);  
			}
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			ContentResolver localContentResolver = getContentResolver();
			try {
				String url = MediaStore.Images.Media.insertImage(localContentResolver, bitmap, "", "");
				refresh(url, TakePictureAty.this.getApplicationContext());
			} catch (Exception error) {
				error.printStackTrace();
				cleanCamera();
			}
			mBitmap = bitmap;
			btnBrowsePhoto.setBackgroundDrawable(new BitmapDrawable(mBitmap));
		}
	};
	private void refresh(String url, Context context) {
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri uri = Uri.fromFile(new File(url));
		intent.setData(uri);
		context.sendBroadcast(intent);
	}
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
				return false;
			}
			TakePictureAty.this.finish();
			return false;
		}else {
			return super.onKeyDown(keyCode, event);
		}
	};

	public Camera.Size getMostFitSize(List<Camera.Size> list){
		int screenHeight = getScreenHeight(TakePictureAty.this);
		int screenWidth = getScreenWidth(TakePictureAty.this);
		List<Integer> indexList = new ArrayList<Integer>();
		int minHeight = -1;
		int minWidth = -1;
		int mostFitSize = -1;
		for (int i = 0; i < list.size(); i++) {
			Camera.Size size = list.get(i);
			if (i == 0) {
				minHeight = Math.abs(size.height - screenWidth);
				indexList.add(i);
			}else {
				if (Math.abs(size.height - screenWidth) < minHeight) {
					minHeight = Math.abs(size.height - screenWidth);
					indexList.clear();
					indexList.add(i);
				}else if (Math.abs(size.height - screenWidth) == minHeight) {
					indexList.add(i);
				}
			}
		}
		for (int i = 0; i < indexList.size(); i++) {
			if ( i == 0) {
				minWidth = Math.abs(list.get(indexList.get(i)).width - screenHeight);
				mostFitSize = indexList.get(i);
			}else {
				if (Math.abs(list.get(indexList.get(i)).width - screenHeight) < minWidth) {
					minWidth = Math.abs(list.get(indexList.get(i)).width - screenHeight);
					mostFitSize = indexList.get(i);
				}
			}
		}
		return list.get(mostFitSize);
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			takePicture();
		}
	};
	
	Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			takePicture();
		}
	};
	
	private void close(){
		TakePictureAty.this.finish();
		Intent intent = new Intent();
		intent.setAction("ON_CAMERA_CLOSE");
		sendBroadcast(intent);
	}
		
}
