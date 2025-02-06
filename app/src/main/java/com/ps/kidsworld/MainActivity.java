package com.ps.kidsworld;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.ArCoreApk;
import com.ps.kidsworld.Fragments.MainListFragment;
import com.ps.kidsworld.services.BackgroundSoundService;
import com.ps.kidsworld.utils.CommonService;
import com.smartlook.android.core.api.Smartlook;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private static Context mContext;
    Intent backgroundSoundServiceIntent;
    ProgressDialog pd;
    private AppCompatActivity self;
//    BackgroundSound mBackgroundSound = new BackgroundSound();

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    public static Context getContext() {
        return mContext;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) return;

        mContext = getApplicationContext();
        self = this;

        setContentView(R.layout.activity_main);

        Smartlook smartlook = Smartlook.getInstance();
        smartlook.getPreferences().setProjectKey("25c0d1699268f9823445ec5ce37d328ad9e58aad");
        smartlook.start();
        checkARSupport();

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        addLayoutItems();
        new JsonTask().execute("https://drive.google.com/uc?export=download&id=1yYxR0W8g3Jscjto3QknNqHQI6SUDd6aM");
        backgroundSoundServiceIntent = new Intent(this, BackgroundSoundService.class);

    }


    private void addLayoutItems() {
        CommonService.addFragment(self, R.id.frame_layout, new MainListFragment());

    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(backgroundSoundServiceIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(backgroundSoundServiceIntent);
    }

    void checkARSupport() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isTransient()) {
            // Continue to query availability at 5Hz while compatibility is checked in the background.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkARSupport();
                }
            }, 200);
        }
        if (availability.isSupported()) {
            CommonService.bARSupported = true;
        } else { // The device is unsupported or unknown.
            CommonService.bARSupported = false;
        }
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {

            if (true) {
                return CommonService.getJsonStrFromAsset();
            }

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            try {
                CommonService.sceneConfig = new JSONObject(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class BackgroundSound extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            MediaPlayer player = MediaPlayer.create(MainActivity.this, R.raw.background_music);
            player.setLooping(true); // Set looping
            player.setVolume(0.4f, 0.4f);
            player.start();
            return null;
        }

    }
//    @Override
//    public void onBackPressed() {
//        Log.i(TAG, "inside Onbackpresss");
//        FragmentManager manager = getSupportFragmentManager();
//
//        if (manager.getBackStackEntryCount() > 1) {
//            // If there are back-stack entries, leave the FragmentActivity
//            // implementation take care of them.
//            manager.popBackStack();
//
//        } else {
//            // Otherwise, ask user if he wants to leave :)
//            new AlertDialog.Builder(this)
//                    .setTitle("Really Exit?")
//                    .setMessage("Are you sure you want to exit?")
//                    .setNegativeButton(android.R.string.no, null)
//                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface arg0, int arg1) {
//                            MainActivity.super.onBackPressed();
//                            MainActivity.super.onBackPressed();
//                        }
//                    }).create().show();
//        }
//    }


//    @Override
//    public void onConfigurationChanged(@NotNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
//        }
//    }
}