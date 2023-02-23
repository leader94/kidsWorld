package com.ps.kidsworld.core;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.DragGesture;
import com.google.ar.sceneform.ux.DragGestureRecognizer;
import com.google.ar.sceneform.ux.GesturePointersUtility;
import com.google.ar.sceneform.ux.TransformableNode;


import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.ux.BaseGesture;
import com.google.ar.sceneform.ux.BaseGestureRecognizer;
import com.google.ar.sceneform.ux.BaseTransformableNode;
import com.google.ar.sceneform.ux.BaseTransformationController;
import com.google.ar.sceneform.ux.DragGesture;
import com.google.ar.sceneform.ux.DragGestureRecognizer;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;


public final class MyDragRotationController extends BaseTransformationController {
    // Rate that the node rotates in degrees per degree of twisting.
    private float rotationRateDegrees =  0.5f;


    TransformableNode transformableNode;
    DragGestureRecognizer gestureRecognizer;
    public MyDragRotationController(TransformableNode transformableNode, DragGestureRecognizer gestureRecognizer) {
        super(transformableNode, (BaseGestureRecognizer)gestureRecognizer);
        this.transformableNode = transformableNode;
        this.gestureRecognizer = gestureRecognizer;
    }

    @Override
    protected boolean canStartTransformation(BaseGesture gesture) {
        return this.transformableNode.isSelected();
    }

    @Override
    protected void onContinueTransformation(BaseGesture gesture) {
        DragGesture gesture1 = (DragGesture) gesture;
        Quaternion localRotation = transformableNode.getLocalRotation();
        float rotationAmountX = gesture1.getDelta().x * this.rotationRateDegrees;
        Quaternion rotationDeltaX = new Quaternion(Vector3.up(), rotationAmountX);
        localRotation = Quaternion.multiply(localRotation, rotationDeltaX);
        transformableNode.setLocalRotation(localRotation);
    }

    @Override
    protected void onEndTransformation(BaseGesture gesture) {

    }
}
