package com.ps.kidsworld.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.google.ar.sceneform.rendering.Renderable;
import com.ps.kidsworld.R;
import com.ps.kidsworld.core.SceneFrame;
import com.ps.kidsworld.interfaces.MyOnTapModelListener;
import com.ps.kidsworld.utils.CommonService;
import com.ps.kidsworld.utils.lrucache.LRUCache;

import org.json.JSONArray;
import org.json.JSONObject;

public class SceneFragment extends Fragment {
    private static final String TAG = SceneFragment.class.getSimpleName();
    ImageButton moveBtn, prevBtn, nextBtn, playBtn, backBtn;
    ImageView bottomBar;
    MyARFragment myARFragment;
    JSONArray curSceneArr;
    JSONObject curSceneConfig;
    SceneFrame curFrame;
    int curFrameNo = 0;
    LottieAnimationView loader;
    boolean isLoadingFrame = false;
    boolean bNodeAdded = false;
    boolean isAnimating = false;
    LRUCache<Integer, SceneFrame> lruCache;
    //    MediaPlayer mp = new MediaPlayer();
    MediaPlayer mediaPlayer;
    private AnimatedVectorDrawable animation;
    private boolean mUserRequestedInstall = true;
    private boolean isSoundPlaying = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scene, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkARInstalled();
        if (CommonService.bARSupported && CommonService.bARInstalled) {
            myARFragment = new MyARFragment();
            getChildFragmentManager().beginTransaction().add(R.id.arFragmentHolder, myARFragment).commit();
        }
        initialise(view);


    }

    void checkARInstalled() {
        try {
            if (CommonService.bARSupported) {
                switch (ArCoreApk.getInstance().requestInstall(getActivity(), mUserRequestedInstall)) {
                    case INSTALLED:
                        // Success: Safe to create the AR session.
                        CommonService.bARInstalled = true;
                        break;
                    case INSTALL_REQUESTED:
                        // When this method returns `INSTALL_REQUESTED`:
                        // 1. ARCore pauses this activity.
                        // 2. ARCore prompts the user to install or update Google Play
                        //    Services for AR (market://details?id=com.google.ar.core).
                        // 3. ARCore downloads the latest device profile data.
                        // 4. ARCore resumes this activity. The next invocation of
                        //    requestInstall() will either return `INSTALLED` or throw an
                        //    exception if the installation or update did not succeed.
                        mUserRequestedInstall = false;
                        break;
                }
            }
        } catch (UnavailableUserDeclinedInstallationException e) {
            // Display an appropriate message to the user and return gracefully.
        } catch (Exception e) {
            // mSession remains null, since session creation has failed.
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void initialise(View view) {
        curSceneArr = CommonService.getSceneArr(CommonService.curSceneName);
        curSceneConfig = CommonService.getSceneConfig(CommonService.curSceneName);
        try {
            lruCache = new LRUCache<>(curSceneConfig.getInt("cPreload"));
        } catch (Exception e) {
            e.printStackTrace();
            lruCache = new LRUCache<>(2);
        }
        if (curSceneArr == null) {
            return;
        }

        initialiseViewsItems(view);
        setNextPrevBtnVisibility();
        addOnClickListeners();
    }

    // Must be called only once
    void initSceneModel() {
        try {
            if (curSceneArr != null) {
                isLoadingFrame = true;
                curFrame = new SceneFrame(curSceneArr.getJSONObject(curFrameNo),
                        curSceneConfig.getBoolean("loadLocal"));
                myARFragment.addFirstModel(curFrame, new MyOnTapModelListener() {
                    @Override
                    public void onModelDownloaded(Renderable renderable) {
                        curFrame.setRenderable(renderable);
                        lruCache.put(curFrameNo, new SceneFrame(curFrame));  // TODO check reference bug here
                        isLoadingFrame = false;
                        myARFragment.startModelAnimation(false);
                        playSound(curFrame);
                        preloadFrame(curFrameNo + 1);
                    }

                    @Override
                    public void onModelError(Throwable exception) {
                        isLoadingFrame = false;
                        preloadFrame(curFrameNo + 1);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed in loading Scene Model", e);
        }
    }

    void addOnClickListeners() {
//        this.moveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CommonService.bMoveEnabled = !CommonService.bMoveEnabled;
//                setMoveBtnImage();
//                MyDragTransformableNode curSelectedModel = (MyDragTransformableNode) myARFragment.getTransformationSystem().getSelectedNode();
//                if (curSelectedModel != null) {
//                    curSelectedModel.triggerTranslationControllerChange(CommonService.bMoveEnabled);
//                }
//
//            }
//        });

        bottomBar.setOnClickListener(view -> {
            // do nothing. This is to prevent walking when clicked on bottom bar
        });
        backBtn.setOnClickListener(view -> getActivity().onBackPressed());

        playBtn.setOnClickListener(view -> {
            if (!buttonClickAllowed()) return;
            myARFragment.startModelAnimation(false);
            playSound(curFrame);
            myARFragment.startAnim(curFrame.getAnimation(), false, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    isAnimating = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isAnimating = false;
                }
            });
        });
        nextBtn.setOnClickListener(view -> {
            if (!buttonClickAllowed()) return;
            releaseMediaPlayerResources();
            curFrameNo++;
            callChangeFrame(curFrameNo, 1);
        });

        prevBtn.setOnClickListener(view -> {
            if (!buttonClickAllowed()) return;
            releaseMediaPlayerResources();
            curFrameNo--;
            callChangeFrame(curFrameNo, -1);
        });
    }

    void callChangeFrame(int frameNo, int relativePreLoadFrameNo) {
        if (isLoadingFrame) return;

        try {
            setNextPrevBtnVisibility();
            if (curSceneArr != null) {
                isLoadingFrame = true;


                if (lruCache.get(frameNo) != null) {
                    curFrame = lruCache.get(frameNo);
                    myARFragment.replaceModelWithCustomModel(curFrame);
                    isLoadingFrame = false;
                    myARFragment.startModelAnimation(false);
                    playSound(curFrame);
                    preloadFrame(frameNo + relativePreLoadFrameNo);
                } else {

                    curFrame = new SceneFrame(curSceneArr.getJSONObject(frameNo),
                            curSceneConfig.getBoolean("loadLocal"));
                    myARFragment.changeModel(curFrame, new MyOnTapModelListener() {
                        @Override
                        public void onModelDownloaded(Renderable renderable) {
                            curFrame.setRenderable(renderable);
                            lruCache.put(frameNo, new SceneFrame(curFrame)); // TODO check ref bug here
                            isLoadingFrame = false;
                            myARFragment.startModelAnimation(false);
                            playSound(curFrame);
                            preloadFrame(frameNo + relativePreLoadFrameNo);
                        }

                        @Override
                        public void onModelError(Throwable exception) {
                            isLoadingFrame = false;
                            preloadFrame(frameNo + relativePreLoadFrameNo);
                        }
                    });
                }


            }

        } catch (Exception e) {
            isLoadingFrame = false;
            Log.e(TAG, "Failed in changing Scene Model Prev Btn", e);
        }
    }

    void preloadFrame(int frameNo) {
        if (lruCache.get(frameNo) != null) {
            return;
        }
        if (frameNo >= 0 && frameNo <= curSceneArr.length() - 1) {
            try {
                SceneFrame tempFrame = new SceneFrame(curSceneArr.getJSONObject(frameNo), curSceneConfig.getBoolean("loadLocal"));
                myARFragment.downloadModel(tempFrame, false, new MyOnTapModelListener() {
                    @Override
                    public void onModelDownloaded(Renderable renderable) {
                        tempFrame.setRenderable(renderable);
                        lruCache.put(frameNo, new SceneFrame(tempFrame));
                    }

                    @Override
                    public void onModelError(Throwable exception) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    void setNextPrevBtnVisibility() {
        if (curFrameNo >= (curSceneArr.length() - 1)) {
            nextBtn.setBackgroundResource(R.drawable.next_btn_disabled);
            nextBtn.setEnabled(false);
        } else {
            nextBtn.setBackgroundResource(R.drawable.next_btn);
            nextBtn.setEnabled(true);
        }

        if (curFrameNo <= 0) {
            prevBtn.setBackgroundResource(R.drawable.prev_btn_disabled);
            prevBtn.setEnabled(false);
        } else {
            prevBtn.setBackgroundResource(R.drawable.prev_btn);
            prevBtn.setEnabled(true);
        }
    }

    void setMoveBtnImage() {
        if (CommonService.bMoveEnabled) {
            // set icon to rotate
            moveBtn.setBackgroundResource(R.drawable.baseline_360_24);

        } else {
            // set icon to move
            moveBtn.setBackgroundResource(R.drawable.outline_open_with_24);
        }
    }

    void initialiseViewsItems(View view) {
        prevBtn = (ImageButton) view.findViewById(R.id.Ib_FSceneLeft);
        nextBtn = (ImageButton) view.findViewById(R.id.Ib_FSceneRight);

        backBtn = (ImageButton) view.findViewById(R.id.Ib_FSceneBack);

        playBtn = (ImageButton) view.findViewById(R.id.Ib_FScenePlay);
//        moveBtn = (ImageButton) view.findViewById(R.id.btnMove);
//        setMoveBtnImage();


        bottomBar = (ImageView) view.findViewById(R.id.IV_FScene_1);

        loader = (LottieAnimationView) view.findViewById(R.id.loader);

    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void playSound(SceneFrame frame) {
        if (isSoundPlaying) {
            return;
        }

        isSoundPlaying = true;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();

        try {
            boolean loadLocal = curSceneConfig.getBoolean("loadLocal");
            if (loadLocal) {
                AssetFileDescriptor afd = getActivity().getAssets().openFd(frame.getAudioFilePath());
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            } else {
                mediaPlayer.setDataSource(getContext(), Uri.parse(frame.getAudioFilePath()));
            }
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            releaseMediaPlayerResources();
            e.printStackTrace();
            Log.e(TAG, "prepare() failed");
        }

        mediaPlayer.setOnCompletionListener(mediaPlayer -> releaseMediaPlayerResources());
        mediaPlayer.setOnPreparedListener(mediaPlayer -> mediaPlayer.start());

    }

    private void releaseMediaPlayerResources() {
        if (mediaPlayer == null) {
            return;
        }
        mediaPlayer.release();
        mediaPlayer = null;
        isSoundPlaying = false;
    }

    private boolean buttonClickAllowed() {
        return !(isLoadingFrame || !bNodeAdded || isAnimating);
    }


}
