package com.waycreon.emojicamera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.myandroid.views.MultiTouchListener;
import com.myandroid.views.myView;
import com.waycreon.emojicamera.R;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements Callback, OnClickListener {

	Camera camera;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	ImageView mImageViewCamera;
	ImageView mImageViewGallery;
	ImageView mImageViewCapture;
	ImageView mImageViewFrontCamera;
	ImageView mImageViewSave;
	ImageView mImageViewGalleryImage;
	ImageView mImageViewOpenGallery;
	ImageView mImageViewOpenCamera;
	ImageView mImageViewShare;
	ImageView delete_btn;

	ArrayList<myView> imageViewArray = new ArrayList<myView>();

	PictureCallback jpegCallback;
	public Bitmap bitmaptemp = null;
	Bitmap mBitmap;
	LinearLayout mLinearLayoutButtons;
	RelativeLayout mRelativeLayoutCapture;
	RelativeLayout mRelativeLayoutMain;
	Boolean isClicked = false;
	int camId;
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	private static final int PICTURE_TAKEN_FROM_GALLERY = 1;
	int mode = NONE;
	ProgressDialog progress;
	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	String savedItemClicked;
	Integer[] frameId = new Integer[] { 
			 R.drawable.es1, R.drawable.es2, R.drawable.es3, R.drawable.es4, R.drawable.es5, R.drawable.es6, R.drawable.es7, R.drawable.es8,
			 R.drawable.es8, R.drawable.es9, R.drawable.es10, R.drawable.es11, R.drawable.es12, R.drawable.es13, R.drawable.es14,
			 R.drawable.es15, R.drawable.es16, R.drawable.es17, R.drawable.es18, R.drawable.es19, R.drawable.es20,
			R.drawable.icon_1, R.drawable.icon_2,
			R.drawable.icon_3, R.drawable.icon_4, R.drawable.icon_5,
			R.drawable.icon_6, R.drawable.icon_7, R.drawable.icon_8,R.drawable.icon_9, R.drawable.icon_10, R.drawable.icon_11,R.drawable.icon_12,R.drawable.icon_13, 
			R.drawable.icon_14,R.drawable.icon_15,R.drawable.icon_16,R.drawable.icon_17,R.drawable.icon_18,R.drawable.icon_19,
			R.drawable.icon_20,R.drawable.icon_21,R.drawable.icon_22,R.drawable.icon_23,R.drawable.icon_24,R.drawable.icon_25,
			R.drawable.icon_26,R.drawable.icon_27,R.drawable.icon_28,R.drawable.icon_29,R.drawable.icon_30,R.drawable.icon_31,
			R.drawable.icon_31,R.drawable.icon_32,R.drawable.icon_33,R.drawable.icon_34,R.drawable.icon_35,R.drawable.icon_36,
			R.drawable.icon_37,R.drawable.icon_38,R.drawable.icon_39,R.drawable.icon_40,R.drawable.icon_120,R.drawable.icon_123,
			R.drawable.icon_126,R.drawable.icon_127,};
	ArrayList<Mylist> mlist = new ArrayList<Mylist>();
	ListView mListView;
	Boolean moving;
	float currX, currY = 0.0f;
	int id = 0;
	ArrayList<ImageView> mArrayList = new ArrayList<ImageView>();
	ImageView[] mImageView;
	int i;
	ScaleGestureDetector scaleGestureDetector;
	LayoutParams params;
	private float scale = 1f;
	private ScaleGestureDetector SGD;

	Bitmap mBitmapCamera;
	Bitmap mBitmapDrawing;
	Bitmap mBitmapMain = null;
	Bitmap takenPictureData = null;
	boolean imageSaved = false;
	InterstitialAd interstitialAds;
	AdRequest adRequest;
	boolean isDelete = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		AdView adView = (AdView)this.findViewById(R.id.adView);
		AdRequest adRequest1 = new AdRequest.Builder()
		    .build();
		adView.loadAd(adRequest1);
		
		interstitialAds = new InterstitialAd(this);
		interstitialAds.setAdUnitId(getResources().getString(R.string.admob_intersitials));
	     adRequest = new AdRequest.Builder().build();
	     interstitialAds.loadAd(adRequest);
	     interstitialAds.show();
		
		imageSaved = false;
		mListView = (ListView) findViewById(R.id.list_tatoo);
		getList();
		SGD = new ScaleGestureDetector(this, new ScaleListener());
		camId = Camera.CameraInfo.CAMERA_FACING_BACK;
		setCamFocusMode();

		mListView.setAdapter(new TatooImagesAdapter(this, mlist));
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		mImageViewCamera = (ImageView) findViewById(R.id.imageview_camera);
		mImageViewGallery = (ImageView) findViewById(R.id.imageview_gallery);
		mImageViewFrontCamera = (ImageView) findViewById(R.id.imageview_front_camera);
		mImageViewCapture = (ImageView) findViewById(R.id.imageview_capture);
		// mImageViewTatoo = (ImageView) findViewById(R.id.imageview_tatoo);
		mImageViewSave = (ImageView) findViewById(R.id.imageview_save);
		mImageViewGalleryImage = (ImageView) findViewById(R.id.imageview_from_gallery);
		mImageViewShare = (ImageView) findViewById(R.id.imageview_share);
		mImageViewOpenCamera = (ImageView) findViewById(R.id.image_camera);
		mImageViewOpenGallery = (ImageView) findViewById(R.id.image_gallery);

		delete_btn = (ImageView) findViewById(R.id.delete_btn);
		mRelativeLayoutCapture = (RelativeLayout) findViewById(R.id.layout_capture_image);
		mLinearLayoutButtons = (LinearLayout) findViewById(R.id.layout_buttons);
		mRelativeLayoutMain = (RelativeLayout) findViewById(R.id.capture_id_rl);

		mImageViewCamera.setOnClickListener(this);
		mImageViewGallery.setOnClickListener(this);
		mImageViewCapture.setOnClickListener(this);
		mImageViewFrontCamera.setOnClickListener(this);
		mImageViewSave.setOnClickListener(this);
		mImageViewOpenCamera.setOnClickListener(this);
		mImageViewShare.setOnClickListener(this);
		mImageViewOpenGallery.setOnClickListener(this);

		delete_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ckhDelete();
				interstitialAds.show();
				isDelete = !isDelete;

			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				interstitialAds.show();
				myView myViewTemp = new myView(getApplicationContext());
				myViewTemp.setImageResource(mlist.get(arg2).getId());

				myViewTemp.setOnTouchListener(new MultiTouchListener());
				addView(myViewTemp);
				isDelete = false;
				ckhDelete();

			}
		});

		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mImageViewCapture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isClicked) {
					isClicked = true;

					MainActivity.this.camera.takePicture(null, null,
							jpegCallback);

				}
			}
		});
		jpegCallback = new PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {

				Log.i("inside jpegcallback", "" + data);

				bitmaptemp = BitmapFactory
						.decodeByteArray(data, 0, data.length);
				Log.i("bitmap created ", "" + bitmaptemp);

				Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmaptemp,
						1280, 960, true);

				/*
				 * Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0,
				 * 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
				 * true);
				 */
				mBitmapCamera = scaledBitmap;

				mRelativeLayoutCapture.setVisibility(View.GONE);
				mImageViewGalleryImage.setVisibility(View.VISIBLE);

				Log.i("back width", "" + bitmaptemp.getWidth());
				Log.i("back height", "" + bitmaptemp.getHeight());

				mImageViewGalleryImage.setImageBitmap(mBitmapCamera);
				isClicked = false;

				/*
				 * try { mBitmapMain =
				 * Bitmap.createBitmap(mBitmapCamera.getWidth(),
				 * mBitmapCamera.getHeight(), Config.ARGB_8888); Canvas c = new
				 * Canvas(mBitmapMain);
				 * 
				 * Drawable drawable1 = new BitmapDrawable(mBitmapCamera);
				 * Drawable drawable2 = new BitmapDrawable(mBitmapDrawing);
				 * 
				 * int i = (int) (mBitmapMain.getWidth() - mBitmapDrawing
				 * .getWidth()) / 2;
				 * 
				 * int j = (int) (mBitmapMain.getHeight() - mBitmapDrawing
				 * .getHeight()) / 2; drawable1.setBounds(0, 0,
				 * mBitmapCamera.getWidth(), mBitmapCamera.getHeight());
				 * drawable2.setBounds(0, 0, mBitmapDrawing.getWidth() + i, j +
				 * mBitmapDrawing.getHeight()); drawable1.draw(c);
				 * drawable2.draw(c);
				 * 
				 * } catch (Exception e) {
				 * 
				 * Log.i("asynk call ", "in catch" + e); }
				 */
			}
		};
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		interstitialAds.show();
		refreshCamera();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageview_camera:
			mLinearLayoutButtons.setVisibility(View.GONE);
			mRelativeLayoutCapture.setVisibility(View.VISIBLE);
			// mImageViewTatoo.setVisibility(View.VISIBLE);
			mImageViewGalleryImage.setVisibility(View.GONE);
			openCamera();
			break;
		case R.id.image_camera:
			mImageViewGalleryImage.setVisibility(View.GONE);
			// mImageViewTatoo.setVisibility(View.VISIBLE);
			mLinearLayoutButtons.setVisibility(View.GONE);
			mRelativeLayoutCapture.setVisibility(View.VISIBLE);
			openCamera();
			refreshCamera();
			isClicked = false;
			break;
		case R.id.imageview_front_camera:
			mLinearLayoutButtons.setVisibility(View.GONE);
			mRelativeLayoutCapture.setVisibility(View.VISIBLE);
			openFrontFacingCamera();

			refreshCamera();
			isClicked = false;
			break;
		case R.id.imageview_gallery:
			// mImageViewTatoo.setVisibility(View.VISIBLE);
			mLinearLayoutButtons.setVisibility(View.GONE);
			mRelativeLayoutCapture.setVisibility(View.GONE);
			Intent gallerypickerIntent = new Intent(Intent.ACTION_PICK);
			gallerypickerIntent.setType("image/*");
			startActivityForResult(gallerypickerIntent,
					PICTURE_TAKEN_FROM_GALLERY);
			break;

		case R.id.image_gallery:
			// mImageViewTatoo.setVisibility(View.VISIBLE);
			mLinearLayoutButtons.setVisibility(View.GONE);
			mRelativeLayoutCapture.setVisibility(View.GONE);
			Intent gallerypickerIntent1 = new Intent(Intent.ACTION_PICK);
			gallerypickerIntent1.setType("image/*");
			startActivityForResult(gallerypickerIntent1,
					PICTURE_TAKEN_FROM_GALLERY);
			break;
		case R.id.imageview_save:
			
			if (mImageViewGalleryImage.getDrawable() == null) {
				
				Toast.makeText(this, "please select image first", 0).show();
			}else {
				saveImage();
			}
			
			break;
		case R.id.imageview_share:
			
			if (mImageViewGalleryImage.getDrawable() == null) {
				
				Toast.makeText(this, "please select image first", 0).show();
			}else {
				saveImageshare();
			}
			

		default:
			break;
		}
	}

	public void saveImage() {
		
		
		

		mBitmapDrawing = Bitmap.createBitmap(mRelativeLayoutMain.getWidth(),
				mRelativeLayoutMain.getHeight(), Config.ARGB_8888);

		mBitmapDrawing = ThumbnailUtils.extractThumbnail(mBitmapDrawing,
				mBitmapDrawing.getWidth(), mBitmapDrawing.getHeight());

		Canvas b = new Canvas(mBitmapDrawing);
		mRelativeLayoutMain.draw(b);

		int x = (mRelativeLayoutMain.getWidth() - mImageViewGalleryImage
				.getWidth()) / 2;
		int y = (mRelativeLayoutMain.getHeight() - mImageViewGalleryImage
				.getHeight()) / 2;

		mBitmapMain = Bitmap.createBitmap(mBitmapDrawing, x, y,
				mImageViewGalleryImage.getWidth(),
				mImageViewGalleryImage.getHeight());

		new SaveFile(mBitmapMain)
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	public void saveImageshare() {

		mBitmapDrawing = Bitmap.createBitmap(mRelativeLayoutMain.getWidth(),
				mRelativeLayoutMain.getHeight(), Config.ARGB_8888);

		mBitmapDrawing = ThumbnailUtils.extractThumbnail(mBitmapDrawing,
				mBitmapDrawing.getWidth(), mBitmapDrawing.getHeight());

		Canvas b = new Canvas(mBitmapDrawing);
		mRelativeLayoutMain.draw(b);

		int x = (mRelativeLayoutMain.getWidth() - mImageViewGalleryImage
				.getWidth()) / 2;
		int y = (mRelativeLayoutMain.getHeight() - mImageViewGalleryImage
				.getHeight()) / 2;

		mBitmapMain = Bitmap.createBitmap(mBitmapDrawing, x, y,
				mImageViewGalleryImage.getWidth(),
				mImageViewGalleryImage.getHeight());
		 
		new SaveFileshare(mBitmapMain)
		.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("requestcode", "" + requestCode);
		Log.i("resultcode", "" + resultCode);
		Log.i("data", "" + data);

		switch (requestCode) {

		case PICTURE_TAKEN_FROM_GALLERY:
			if (resultCode == Activity.RESULT_OK) {
				takenPictureData = handleResultFromChooser(data);
			}
			break;
		}

		if (takenPictureData != null) {
			
			mImageViewGalleryImage.setVisibility(View.VISIBLE);
			mImageViewGalleryImage.setImageBitmap(takenPictureData);
		} else {
			mLinearLayoutButtons.setVisibility(View.VISIBLE);
		}

	}

	private Bitmap handleResultFromChooser(Intent data) {
		Bitmap takenPictureData = null;

		Uri selectedImage = data.getData();
		if (selectedImage != null) {
			try {
				String picturePath = getRealPathFromURI(selectedImage);
				System.out.println("picture path " + picturePath);

				takenPictureData = Media.getBitmap(getContentResolver(),
						selectedImage);

			} catch (FileNotFoundException e1) {

				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			takenPictureData.compress(Bitmap.CompressFormat.PNG, 100, baos);
		}

		return takenPictureData;
	}

	public String getRealPathFromURI(Uri contentUri) {

		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		SGD.onTouchEvent(ev);
		return true;
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			scale *= detector.getScaleFactor();
			scale = Math.max(0.1f, Math.min(scale, 5.0f));
			matrix.setScale(scale, scale);
			for (i = 0; i < mArrayList.size(); i++) {
				mArrayList.get(i).setImageMatrix(matrix);
			}

			return true;
		}
	}

	public void getList() {
		for (int i = 0; i < frameId.length; i++) {
			Mylist ld = new Mylist();
			ld.setId(((frameId[i])));
			mlist.add(ld);
		}
	}

	@Override
	protected void onResume() {
		camId = Camera.CameraInfo.CAMERA_FACING_BACK;
		refreshCamera();
		isClicked = false;
		super.onResume();
	}

	public void refreshCamera() {
		if (surfaceHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			camera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		try {
			camera.setPreviewDisplay(surfaceHolder);
			camera.startPreview();
		} catch (Exception e) {

		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

		refreshCamera();
	}

	public void openCamera() {
		try {
			// open the camera

			camera = Camera.open();
			// camera.setDisplayOrientation(180);
		} catch (RuntimeException e) {
			// check for exceptions
			System.err.println(e);
			return;
		}
		Camera.Parameters param;
		param = camera.getParameters();

		// modify parameter
		param.setPreviewSize(352, 288);
		camera.setParameters(param);
		try {
			// The Surface has been created, now tell the camera where to draw
			// the preview.
			camera.setPreviewDisplay(surfaceHolder);
			camera.startPreview();
		} catch (Exception e) {
			// check for exceptions
			System.err.println(e);
			return;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {

		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
	}

	class SaveFile extends AsyncTask<String, String, String> {

		Bitmap bmp;

		public SaveFile(Bitmap bmp) {

			this.bmp = bmp;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			MainActivity.this.progress = ProgressDialog.show(MainActivity.this,
					"", "Please wait...");
		}

		@Override
		protected String doInBackground(String... f_url) {
			Log.i("asynk call ", "from");

			OutputStream output;

			Calendar cal = Calendar.getInstance();

			// Find the SD Card path
			File filepath = Environment.getExternalStorageDirectory();

			// Create a new folder in SD Card
			File dir = new File(filepath.getAbsolutePath() + "/Emoji Camera Sticker Maker/");
			dir.mkdirs();
			
			ContentValues values = new ContentValues();
		    values.put(MediaStore.Images.Media.DATA, dir.getAbsolutePath());
		    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg"); // setar isso
		    getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		    
			String mImagename = "image" + cal.getTimeInMillis() + ".png";

			// Create a name for the saved image
			File file = new File(dir, mImagename);

			try {

				output = new FileOutputStream(file);
				// Compress into png format image from 0% - 100%
				bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
				output.flush();
				output.close();
			}

			catch (Exception e) {
				e.printStackTrace();

				progress.dismiss();
				Log.i("captureImage call + in catch   ", "" + e);

			}

			Log.i("asynk call end  ", "from");

			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			// btnSave.setVisibility(Button.GONE);
			progress.dismiss();
			Toast.makeText(MainActivity.this, "Image Saved...",
					Toast.LENGTH_SHORT).show();

		}

	}

	class SaveFileshare extends AsyncTask<String, String, String> {

		Bitmap bmp;

		public SaveFileshare(Bitmap bmp) {

			this.bmp = bmp;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			MainActivity.this.progress = ProgressDialog.show(MainActivity.this,
					"", "Please wait...");
		}

		@Override
		protected String doInBackground(String... f_url) {
			Log.i("asynk call ", "from");

			OutputStream output;

			Calendar cal = Calendar.getInstance();

			// Find the SD Card path
			File filepath = Environment.getExternalStorageDirectory();

			// Create a new folder in SD Card
			File dir = new File(filepath.getAbsolutePath() + "/Emoji Camera Sticker Maker/");
			dir.mkdirs();

			String mImagename = "image" + cal.getTimeInMillis() + ".png";

			// Create a name for the saved image
			File file = new File(dir, mImagename);
			
			ContentValues values = new ContentValues();
		    values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
		    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg"); // setar isso
		    getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

			try {

				output = new FileOutputStream(file);
				// Compress into png format image from 0% - 100%
				bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
				output.flush();
				output.close();
			}

			catch (Exception e) {
				e.printStackTrace();

				progress.dismiss();
				Log.i("captureImage call + in catch   ", "" + e);

			}

			Log.i("asynk call end  ", "from");

			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			// btnSave.setVisibility(Button.GONE);
			progress.dismiss();
			Toast.makeText(MainActivity.this, "Image Saved...",
					Toast.LENGTH_SHORT).show();

			if (mBitmapMain != null) {
				String pathofBmp = Images.Media.insertImage(
						getContentResolver(), mBitmapMain, "title", null);
				Uri bmpUri = Uri.parse(pathofBmp);
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				sharingIntent.setType("image/*");
				sharingIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
				startActivity(Intent.createChooser(sharingIntent,
						"Share image using"));
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "No image to share",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	public void openFrontFacingCamera() {
		int numberOfCamera = Camera.getNumberOfCameras();

		if (numberOfCamera > 0) {
			if (camId == Camera.CameraInfo.CAMERA_FACING_BACK) {
				camId = Camera.CameraInfo.CAMERA_FACING_FRONT;
				Toast.makeText(getApplicationContext(), "BACK TO FRONT", 1000)
						.show();
				try {
					camera.release();
					camera = Camera.open(camId);
					camera.setPreviewDisplay(surfaceHolder);
					camera.startPreview();
				} catch (RuntimeException e) {

				} catch (IOException e) {
				}
			} else if (camId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				camId = Camera.CameraInfo.CAMERA_FACING_BACK;
				Toast.makeText(getApplicationContext(), "FRONT TO BACK",

				1000).show();
				try {
					camera.release();
					camera = Camera.open(camId);
					camera.setPreviewDisplay(surfaceHolder);
					camera.startPreview();
				} catch (RuntimeException e) {
				} catch (IOException e) {
				}
			}
		}
	}

	public void addView(myView myView_) {

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		imageViewArray.add(myView_);
		mRelativeLayoutMain.addView(myView_, params);

	}

	public void addOnclick() {
		for (myView view : imageViewArray) {

			view.setOnTouchListener(null);

			view.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					v.setVisibility(View.GONE);
				}
			});

		}
	}

	public void addmultitouch() {

		for (myView view : imageViewArray) {

			view.setOnTouchListener(new MultiTouchListener());

		}

	}

	public void ckhDelete() {

		if (isDelete) {
			addOnclick();
			delete_btn.setBackgroundColor(Color.RED);

		} else {
			addmultitouch();
			delete_btn.setBackgroundColor(Color.TRANSPARENT);
		}

	}

	private void setCamFocusMode() {

		if (null == camera) {
			return;
		}

		/* Set Auto focus */
		Parameters parameters = camera.getParameters();
		List<String> focusModes = parameters.getSupportedFocusModes();
		if (focusModes
				.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
			parameters
					.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		} else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}

		camera.setParameters(parameters);
	}

}
