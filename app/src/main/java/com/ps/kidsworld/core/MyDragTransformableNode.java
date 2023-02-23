package com.ps.kidsworld.core;

import android.view.MotionEvent;

import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.ux.BaseTransformationController;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;
import com.ps.kidsworld.utils.CommonService;

import org.jetbrains.annotations.NotNull;


public class MyDragTransformableNode extends TransformableNode {
    private MyDragRotationController dragRotationController;

    public MyDragTransformableNode(@NotNull TransformationSystem transformationSystem) {
        super(transformationSystem);
        this.dragRotationController = new MyDragRotationController(this, transformationSystem.getDragRecognizer());

        // Remove rotation controller
        this.removeTransformationController((BaseTransformationController) this.getRotationController());
        this.triggerTranslationControllerChange(false);
    }

    public void triggerTranslationControllerChange(boolean bEnableTranslationChange) {
        if (bEnableTranslationChange) {
            // remove custom rotation controller
            this.dragRotationController.setEnabled(false);
            this.removeTransformationController((BaseTransformationController) this.dragRotationController);

            // Add location change of model
            this.addTransformationController((BaseTransformationController) this.getTranslationController());
            this.getTranslationController().setEnabled(true);
//
//            // Remove rotation controller
//            this.removeTransformationController((BaseTransformationController) this.getRotationController());
        } else {
            // Prevent location change of model
            this.getTranslationController().setEnabled(false);
            this.removeTransformationController((BaseTransformationController) this.getTranslationController());

            // Remove rotation controller
            this.removeTransformationController((BaseTransformationController) this.getRotationController());
            // add custom rotation controller
            this.addTransformationController((BaseTransformationController) this.dragRotationController);
            this.dragRotationController.setEnabled(true);
        }
    }

    @Override
    public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
        super.onTap(hitTestResult, motionEvent);
        this.select();
        triggerTranslationControllerChange(CommonService.bMoveEnabled);
    }

    public MyDragRotationController getDragRotationController() {
        return dragRotationController;
    }
}