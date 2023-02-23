package com.ps.kidsworld.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3Evaluator;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Light;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.RenderableInstance;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.ps.kidsworld.core.MyDragTransformableNode;
import com.ps.kidsworld.core.SceneFrame;
import com.ps.kidsworld.interfaces.MyOnTapModelListener;
import com.ps.kidsworld.utils.ArSupportUtils;
import com.ps.kidsworld.utils.Callback;

public class MyARFragment extends ArFragment implements BaseArFragment.OnSessionConfigurationListener {
    private static final String TAG = MyARFragment.class.getSimpleName();
    SceneFragment sceneFragment;
    MyDragTransformableNode node;
    LottieAnimationView parentLoader;
    ArSupportUtils arSupportUtils = new ArSupportUtils();
    private Renderable onTapRenderable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        setOnSessionConfigurationListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sceneFragment = (SceneFragment) getParentFragment();
        sceneFragment.initSceneModel();
        parentLoader = sceneFragment.loader;
    }


    public void addFirstModel(SceneFrame frame, @Nullable MyOnTapModelListener listener) {
        firstTimeDownloadAndSetModel(frame.getModelFilePath(), false);

        setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    Log.i(TAG, "inside setOnTapArPlaneListener");
                    if (onTapRenderable == null) {
                        return;
                    }
                    if (node != null) {
                        Anchor n = hitResult.createAnchor();
                        AnchorNode an = new AnchorNode(n);
                        an.setParent(getArSceneView().getScene());
                        startWalking(an);
                        node.select();
                        return;
                    }

                    Light light = Light.builder(Light.Type.DIRECTIONAL)
                            .setShadowCastingEnabled(false)
                            .setIntensity(12000)
                            .setColor(new Color(android.graphics.Color.WHITE))
                            .build();

                    Node cameraNode = getArSceneView().getScene().getCamera();
                    cameraNode.setLight(light);

                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(getArSceneView().getScene());


                    // Create the transformable node and add it to the anchor.
                    node = new MyDragTransformableNode(getTransformationSystem());

                    node.setParent(anchorNode);


                    _doAddModelProcedure(frame, onTapRenderable);

                    sceneFragment.bNodeAdded = true;

                    if (listener != null) {
                        listener.onModelDownloaded(onTapRenderable);
                    }
                });

    }


    public void removeModel(SceneFrame frame, Callback callback) {
        if (node != null) {
            startAnim(frame.getExitAnimation(), false, new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    node.setRenderable(null);
                    sceneFragment.isAnimating = false;
                    callback.OnSuccess();
                }
            });

        }
    }

    void startModelAnimation(boolean repeat) {
        // Animate if has animation
        RenderableInstance renderableInstance = node.getRenderableInstance();
        if (renderableInstance != null && renderableInstance.hasAnimations()) {
            renderableInstance.animate(repeat).start();
        }
    }

    void firstTimeDownloadAndSetModel(String glbSource, boolean bAddLoader) {
        if (bAddLoader) {
            parentLoader.setVisibility(View.VISIBLE);
        }

        ModelRenderable.builder()
                .setSource(
                        getContext(),
                        Uri.parse(glbSource))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(
                        modelRenderable -> {
                            onTapRenderable = modelRenderable;

                        })
                .exceptionally(
                        throwable -> {
                            Log.e(TAG, "Failed to create ModelRenderable downloadAndSetModel function", throwable);
                            return null;
                        });
    }


    void downloadModel(SceneFrame frame, boolean bAddLoader, @Nullable MyOnTapModelListener listener) {
        if (bAddLoader) {
            parentLoader.setVisibility(View.VISIBLE);
        }

        ModelRenderable.builder()
                .setSource(
                        getContext(),
                        Uri.parse(frame.getModelFilePath()))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(
                        modelRenderable -> {
                            if (listener != null) {
                                listener.onModelDownloaded(modelRenderable);
                            }
                        })
                .exceptionally(
                        throwable -> {
                            Log.e(TAG, "Failed to create ModelRenderable Download Model function", throwable);
                            if (listener != null) {
                                listener.onModelError(throwable);
                            }
                            return null;
                        });
    }


    public void changeModel(SceneFrame frame, @Nullable MyOnTapModelListener listener) {
        this.removeModel(frame, new Callback() {
            @Override
            public void OnSuccess() {
                _continueChangeModel(frame, listener);
            }

            @Override
            public void OnError(Exception e) {
                _continueChangeModel(frame, listener);
            }
        });

    }

    private void _continueChangeModel(SceneFrame frame, @Nullable MyOnTapModelListener listener) {
        parentLoader.setVisibility(View.VISIBLE);
        ModelRenderable.builder()
                .setSource(
                        getContext(),
                        Uri.parse(frame.getModelFilePath()))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(
                        modelRenderable -> {
                            _doAddModelProcedure(frame, modelRenderable);
                            if (listener != null) {
                                listener.onModelDownloaded(modelRenderable);
                            }

                        })
                .exceptionally(
                        throwable -> {
                            parentLoader.setVisibility(View.GONE);
                            Log.e(TAG, "Failed to create ModelRenderable ChangeModel function", throwable);
                            if (listener != null) {
                                listener.onModelError(throwable);
                            }
                            return null;
                        });
    }

    void replaceModelWithCustomModel(SceneFrame frame) {
        if (frame.getRenderable() == null) return;
        this.removeModel(frame, new Callback() {
            @Override
            public void OnSuccess() {
                _continueReplaceModelWithCustomModel(frame);
            }

            @Override
            public void OnError(Exception e) {
                _continueReplaceModelWithCustomModel(frame);
            }
        });

    }

    private void _continueReplaceModelWithCustomModel(SceneFrame frame) {
        // Add loader animation start
        parentLoader.setVisibility(View.VISIBLE);
        _doAddModelProcedure(frame, frame.getRenderable());
    }

    private void _doAddModelProcedure(SceneFrame frame, Renderable renderable) {
        onTapRenderable = renderable;

        node.getScaleController().setMaxScale(frame.getMaxSize());
        node.getScaleController().setMinScale(frame.getMinSize());
        parentLoader.setVisibility(View.GONE);
        node.setRenderable(onTapRenderable);
        startAnim(frame.getArrivalAnimation(), false, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                sceneFragment.isAnimating = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                sceneFragment.isAnimating = false;
            }
        });

        node.select();
    }


    @Override
    public void onSessionConfiguration(Session session, Config config) {
        // Comment this in to feed the DepthTexture with Raw Depth Data.
        /*if (session.isDepthModeSupported(Config.DepthMode.RAW_DEPTH_ONLY))
            config.setDepthMode(Config.DepthMode.RAW_DEPTH_ONLY);*/

        Log.i(TAG, "Depth mode checking");
        if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
            Log.i(TAG, "Depth mode supported");
            config.setDepthMode(Config.DepthMode.AUTOMATIC);
        }


//        config.setLightEstimationMode(Config.LightEstimationMode.AMBIENT_INTENSITY);
        config.setPlaneFindingMode(Config.PlaneFindingMode.HORIZONTAL);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
    }


    private void startWalking(Node endNode) {
        ObjectAnimator objectAnimation = new ObjectAnimator();
        objectAnimation.setAutoCancel(true);
        objectAnimation.setTarget(node);

        // All the positions should be world positions
        // The first position is the start, and the second is the end.
        objectAnimation.setObjectValues(node.getWorldPosition(), endNode.getWorldPosition());

        // Use setWorldPosition to position andy.
        objectAnimation.setPropertyName("worldPosition");

        // The Vector3Evaluator is used to evaluator 2 vector3 and return the next
        // vector3.  The default is to use lerp.
        objectAnimation.setEvaluator(new Vector3Evaluator());
        // This makes the animation linear (smooth and uniform).
        objectAnimation.setInterpolator(new LinearInterpolator());
        // Duration in ms of the animation.
        objectAnimation.setDuration(500);
        objectAnimation.start();
    }

    void startAnim(ArSupportUtils.Animation animName, boolean repeat, Animator.AnimatorListener callback) {
        sceneFragment.isAnimating = true;
        switch (animName) {
            case BOUNCE:
                arSupportUtils.bounceAnim(node, repeat, callback);
                break;
            case ARRIVE_FROM_BACK:
                arSupportUtils.arriveFromBackAnim(node, repeat, callback);
                break;
            case ARRIVE_FROM_LEFT:
                arSupportUtils.arriveFromLeftAnim(node, repeat, callback);
                break;
            case EXIT_TO_RIGHT:
                arSupportUtils.exitToRightAnim(node, repeat, callback);
                break;
            case EXIT_TO_BACK:
                arSupportUtils.exitToBackAnim(node, repeat, callback);
                break;
            case FALL_FROM_TOP:
                arSupportUtils.fallFromTopAnim(node, repeat, callback);
                break;
//            case BOUNCE_FROM_LEFT:
//                arSupportUtils.bounceFromLeftAnim(node, repeat, callback);
//                break;
            case ROTATE:
                arSupportUtils.rotateAnim(node, repeat, callback);
                break;
            default:
        }
    }
}
