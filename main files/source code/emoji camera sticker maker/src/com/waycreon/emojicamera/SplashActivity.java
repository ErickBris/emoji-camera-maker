package com.waycreon.emojicamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import com.waycreon.emojicamera.R;

public class SplashActivity extends Activity {
	/** Called when the activity is first created. */

	protected boolean _active = true;
	protected int _splashTime = 3000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_activity);

		/*if (isOnline() == true) {*/
			Thread splashTread = new Thread() {
				@Override
				public void run() {
					try {
						int waited = 0;
						while (_active && (waited < _splashTime)) {
							sleep(100);
							if (_active) {
								waited += 100;
							}
						}
					} catch (InterruptedException e) {
						e.toString();
					} finally {

						finish();
						Intent intent = new Intent(getApplicationContext(),MainActivity.class);
						startActivity(intent);
//						overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
					}
				}
			};
			splashTread.start();
		/*} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					Activity_Splash.this);
			builder.setMessage("Check Your Internet Connection ?")
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Activity_Splash.this.finish();

								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}*/

	}

/*	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;
		}
		return false;
	}*/

	@Override
	protected void onPause() {
		super.onPause();

	}
}