package com.ps.kidsworld.core;

import com.google.ar.sceneform.rendering.Renderable;
import com.ps.kidsworld.utils.ArSupportUtils;
import com.ps.kidsworld.utils.CommonService;

import org.json.JSONObject;

public class SceneFrame {

    private Float minSize, maxSize;
    private String modelFilePath = "", audioFilePath = "";

    private ArSupportUtils.Animation arrivalAnimation, exitAnimation, animation;

    private boolean isLoadLocal;
    private Renderable renderable;


    public SceneFrame(JSONObject curFrame, boolean loadLocal) {
        String modelFileName, audioFileName;
        try {
            minSize = (float) curFrame.getDouble("minSize");
            maxSize = (float) curFrame.getDouble("maxSize");

            isLoadLocal = loadLocal;
            if (loadLocal) {
                modelFileName = curFrame.getString("mFName");
                modelFilePath = "scenes/" + CommonService.curSceneName + "/models/" + modelFileName;

                audioFileName = curFrame.getString("aFName");
                audioFilePath = "scenes/" + CommonService.curSceneName + "/audio/" + audioFileName;
            } else {
                modelFileName = curFrame.getString("mUri");
                modelFilePath = CommonService.baseUrl + CommonService.curSceneName + "/models/" + modelFileName;

                audioFileName = curFrame.getString("aUri");
                audioFilePath = CommonService.baseUrl + CommonService.curSceneName + "/audio/" + audioFileName;
            }

            try {
                arrivalAnimation = ArSupportUtils.Animation.valueOf(curFrame.getString("arrAnim"));
            } catch (Exception e) {
                arrivalAnimation = ArSupportUtils.Animation.valueOf("ARRIVE_FROM_LEFT");
            }
            try {
                exitAnimation = ArSupportUtils.Animation.valueOf(curFrame.getString("exitAnim"));
            } catch (Exception e) {
                exitAnimation = ArSupportUtils.Animation.valueOf("EXIT_TO_BACK");
            }

            try {
                animation = ArSupportUtils.Animation.valueOf(curFrame.getString("anim"));
            } catch (Exception e) {
                animation = ArSupportUtils.Animation.valueOf("ROTATE");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SceneFrame(SceneFrame frame) {
        this.audioFilePath = frame.audioFilePath;
        this.modelFilePath = frame.modelFilePath;
        this.renderable = frame.renderable;
        this.maxSize = frame.maxSize;
        this.minSize = frame.minSize;
        this.isLoadLocal = frame.isLoadLocal;
        this.arrivalAnimation = frame.arrivalAnimation;
        this.exitAnimation = frame.exitAnimation;
        this.animation = frame.animation;
    }

    public Renderable getRenderable() {
        return renderable;
    }

    public void setRenderable(Renderable renderable) {
        this.renderable = renderable;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public Float getMaxSize() {
        return maxSize;
    }

    public Float getMinSize() {
        return minSize;
    }

    public String getModelFilePath() {
        return modelFilePath;
    }

    public ArSupportUtils.Animation getArrivalAnimation() {
        return arrivalAnimation;
    }

    public ArSupportUtils.Animation getExitAnimation() {
        return exitAnimation;
    }

    public ArSupportUtils.Animation getAnimation() {
        return animation;
    }
}
