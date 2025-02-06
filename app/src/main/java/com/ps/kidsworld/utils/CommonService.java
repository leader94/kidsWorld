package com.ps.kidsworld.utils;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ps.kidsworld.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class CommonService {
    private static final String TAG = CommonService.class.getSimpleName();

    public static boolean bARSupported = false;
    public static boolean bARInstalled = false;
    public static boolean bMoveEnabled = false;
    public static String curSceneName = "";
    public static JSONObject sceneConfig;

    public static String baseUrl = "http://192.168.1.38:8080/";


//    static {
//        try {
//            sceneConfig = new JSONObject(CommonService.getJsonStrFromAsset());
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public static void replaceFragment(AppCompatActivity activity, int layoutId, Fragment fragment) {
        FrameLayout frmLayout = (FrameLayout) activity.findViewById(layoutId);
        frmLayout.setVisibility(View.VISIBLE);
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(layoutId, fragment, "YOUR_FRAGMENT_STRING_TAG");
        transaction.addToBackStack(null);  // persists the fragment
        transaction.commit();
    }

    public static void addFragment(AppCompatActivity activity, int layoutId, Fragment fragment) {
        FrameLayout frmLayout = (FrameLayout) activity.findViewById(layoutId);
        frmLayout.setVisibility(View.VISIBLE);
        FragmentManager manager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(layoutId, fragment, "YOUR_FRAGMENT_STRING_TAG");
//        transaction.addToBackStack(null);   // DO NOT UNCOMMENT
        transaction.commit();
    }


    public static JSONArray getSceneArr(String sceneName) {
        try {
            JSONObject scenes = CommonService.sceneConfig.getJSONObject("scenes");
            JSONArray sceneArr = scenes.getJSONArray(sceneName);
            return sceneArr;
        } catch (Exception e) {
            Log.e(TAG, "Failed in getting scene" + e.toString());
            return null;
        }
    }

    public static JSONObject getSceneConfig(String sceneName) {
        try {
            JSONObject scenes = CommonService.sceneConfig.getJSONObject("config");
            JSONObject sceneConfig = scenes.getJSONObject(sceneName);
            return sceneConfig;
        } catch (Exception e) {
            Log.e(TAG, "Failed in getting scene config" + e.toString());
            return null;
        }
    }

    public static String getJsonStrFromAsset() {
        String json = null;
        try {
            InputStream is = MainActivity.getContext().getAssets().open("scene_config.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
