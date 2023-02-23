package com.ps.kidsworld.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.collision.Box;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.QuaternionEvaluator;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.math.Vector3Evaluator;

public class ArSupportUtils {
    private Vector3 getSizeOfNode(Node node) {
        Box box = (Box) node.getRenderable().getCollisionShape();
        return box.getSize();

    }

    public void bounceAnim(Node node, boolean repeat, Animator.AnimatorListener callback) {
        Vector3 sizeOfNode = getSizeOfNode(node);
        Float jumpHeight = sizeOfNode.y * .2f;  //TODO not working
        Vector3 orgPos = node.getWorldPosition();

        ObjectAnimator objectAnimation1 = ObjectAnimator.ofObject(node, "worldPosition", new Vector3Evaluator(),
                node.getWorldPosition(),
                new Vector3(node.getWorldPosition().x, node.getWorldPosition().y + jumpHeight, node.getWorldPosition().z));
        objectAnimation1.setAutoCancel(true);
        objectAnimation1.setTarget(node);
        objectAnimation1.setInterpolator(new LinearInterpolator());

        ObjectAnimator objectAnimation2 = ObjectAnimator.ofObject(node, "worldPosition", new Vector3Evaluator(),
                new Vector3(node.getWorldPosition().x, node.getWorldPosition().y + jumpHeight, node.getWorldPosition().z),
                orgPos);
        objectAnimation2.setAutoCancel(true);
        objectAnimation2.setTarget(node);
        objectAnimation2.setInterpolator(new BounceInterpolator());
        objectAnimation2.setDuration(1000);


        AnimatorSet set = new AnimatorSet();
        set.play(objectAnimation1)
                .before(objectAnimation2);
        _addAnimatorSetListener(set, callback);
        set.start();

    }

    public void arriveFromBackAnim(Node node, boolean repeat, Animator.AnimatorListener callback) {
        ObjectAnimator objectAnimation1 = ObjectAnimator.ofObject(node, "worldPosition", new Vector3Evaluator(),
                new Vector3(node.getWorldPosition().x, node.getWorldPosition().y, node.getWorldPosition().z - 0.8f)
                , node.getWorldPosition());
        _setObjectAnimatorProperties(objectAnimation1, node, new OvershootInterpolator(), 600, callback, null);

//        objectAnimation1.setAutoCancel(true);
//        objectAnimation1.setTarget(node);
//        objectAnimation1.setInterpolator(new OvershootInterpolator());
//        objectAnimation1.setDuration(600);
//        _addObjectAnimatorListener(objectAnimation1, callback);
//        objectAnimation1.start();
    }

    public void arriveFromLeftAnim(Node node, boolean repeat, Animator.AnimatorListener callback) {
        ObjectAnimator objectAnimation1 = ObjectAnimator.ofObject(node, "worldPosition", new Vector3Evaluator(),
                new Vector3(node.getWorldPosition().x - 2.0f, node.getWorldPosition().y, node.getWorldPosition().z)
                , node.getWorldPosition());

        _setObjectAnimatorProperties(objectAnimation1, node, new OvershootInterpolator(), 600, callback, null);


//        objectAnimation1.setAutoCancel(true);
//        objectAnimation1.setTarget(node);
//        objectAnimation1.setInterpolator(new OvershootInterpolator());
//        objectAnimation1.setDuration(600);
//        _addObjectAnimatorListener(objectAnimation1, callback);
//        objectAnimation1.start();
    }

    public void bounceFromLeftAnim(Node node, boolean repeat, Animator.AnimatorListener callback) {
        ObjectAnimator objectAnimation1 = ObjectAnimator.ofObject(node, "worldPosition", new Vector3Evaluator(),
                new Vector3(node.getWorldPosition().x - 1.0f, node.getWorldPosition().y, node.getWorldPosition().z)
                , node.getWorldPosition());
        objectAnimation1.setAutoCancel(true);
        objectAnimation1.setTarget(node);
        objectAnimation1.setInterpolator(new LinearInterpolator());
        objectAnimation1.setDuration(1300);

        ObjectAnimator objectAnimation2 = ObjectAnimator.ofObject(node, "worldPosition", new Vector3Evaluator(),
                new Vector3(node.getWorldPosition().x, node.getWorldPosition().y + 1f, node.getWorldPosition().z)
                , node.getWorldPosition());
        objectAnimation2.setAutoCancel(true);
        objectAnimation2.setTarget(node);
        objectAnimation2.setInterpolator(new BounceInterpolator());
        objectAnimation2.setDuration(1300);
        AnimatorSet set = new AnimatorSet();
        set.play(objectAnimation2).with(objectAnimation1);
        set.setDuration(1300);
        set.start();
    }

    public void exitToRightAnim(Node node, boolean repeat, Animator.AnimatorListener callback) {
        Vector3 orgPos = node.getWorldPosition();
        ObjectAnimator objectAnimation1 = ObjectAnimator.ofObject(node, "worldPosition", new Vector3Evaluator(),
                node.getWorldPosition(),
                new Vector3(node.getWorldPosition().x + 2.0f, node.getWorldPosition().y, node.getWorldPosition().z)
        );
        _setObjectAnimatorProperties(objectAnimation1, node, new AnticipateInterpolator(), 600, callback, orgPos);

//        objectAnimation1.setAutoCancel(true);
//        objectAnimation1.setTarget(node);
//        objectAnimation1.setInterpolator(new AnticipateInterpolator());
//        objectAnimation1.setDuration(600);
//        _addObjectAnimatorListener(objectAnimation1, callback, node, orgPos);
//        objectAnimation1.start();
    }

    public void exitToBackAnim(Node node, boolean repeat, Animator.AnimatorListener callback) {
        Vector3 orgPos = node.getWorldPosition();
        ObjectAnimator objectAnimation1 = ObjectAnimator.ofObject(node, "worldPosition", new Vector3Evaluator(),
                node.getWorldPosition(),
                new Vector3(node.getWorldPosition().x, node.getWorldPosition().y, node.getWorldPosition().z - 0.8f)
        );

        _setObjectAnimatorProperties(objectAnimation1, node, new AnticipateInterpolator(), 600, callback, orgPos);
//        objectAnimation1.setAutoCancel(true);
//        objectAnimation1.setTarget(node);
//        objectAnimation1.setInterpolator(new AnticipateInterpolator());
//        objectAnimation1.setDuration(600);
//
//        _addObjectAnimatorListener(objectAnimation1, callback, node, orgPos);
//        objectAnimation1.start();

    }

    public void fallFromTopAnim(Node node, boolean repeat, Animator.AnimatorListener callback) {
        ObjectAnimator objectAnimation1 = ObjectAnimator.ofObject(node, "worldPosition", new Vector3Evaluator(),
                new Vector3(node.getWorldPosition().x, node.getWorldPosition().y + 1f, node.getWorldPosition().z)
                , node.getWorldPosition());
        _setObjectAnimatorProperties(objectAnimation1, node, new BounceInterpolator(), 1400, callback, null);
//        objectAnimation1.setAutoCancel(true);
//        objectAnimation1.setTarget(node);
//        objectAnimation1.setInterpolator(new BounceInterpolator());
//        objectAnimation1.setDuration(1400);
//        _addObjectAnimatorListener(objectAnimation1, callback);
//        objectAnimation1.start();
    }

    public void rotateAnim(Node node, boolean repeat, Animator.AnimatorListener callback) {
        Quaternion orientation1 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 0);
        Quaternion orientation2 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 180);
        ObjectAnimator orbitAnimation = new ObjectAnimator();
        orbitAnimation.setObjectValues(orientation1, orientation2);
        orbitAnimation.setPropertyName("localRotation");
        orbitAnimation.setEvaluator(new QuaternionEvaluator());
        orbitAnimation.setInterpolator(new AnticipateOvershootInterpolator());
        orbitAnimation.setAutoCancel(true);
        orbitAnimation.setTarget(node);
        orbitAnimation.setDuration(400);

        ObjectAnimator orbitAnimation2 = new ObjectAnimator();
        orbitAnimation2.setObjectValues(orientation2, orientation1);
        orbitAnimation2.setPropertyName("localRotation");
        orbitAnimation2.setEvaluator(new QuaternionEvaluator());
        orbitAnimation2.setInterpolator(new OvershootInterpolator());
        orbitAnimation2.setAutoCancel(true);
        orbitAnimation2.setTarget(node);
        orbitAnimation2.setDuration(300);

        AnimatorSet set = new AnimatorSet();
        set.play(orbitAnimation).before(orbitAnimation2);
        _addAnimatorSetListener(set, callback);
        set.start();
    }

    private void _setObjectAnimatorProperties(ObjectAnimator objAnimator, Node node,
                                              Interpolator interpolator, int duration,
                                              Animator.AnimatorListener callback, Vector3 orgPos) {
        objAnimator.setAutoCancel(true);
        objAnimator.setTarget(node);
        objAnimator.setInterpolator(interpolator);
        objAnimator.setDuration(duration);
        _addObjectAnimatorListener(objAnimator, callback, node, orgPos);
        objAnimator.start();
    }

    private void _addObjectAnimatorListener(ObjectAnimator objectAnimator, Animator.AnimatorListener callback) {
        _addObjectAnimatorListener(objectAnimator, callback, null, null);
    }

    private void _addObjectAnimatorListener(ObjectAnimator objectAnimator, Animator.AnimatorListener callback, Node node, Vector3 orgPos) {
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                if (node != null && orgPos != null) node.setWorldPosition(orgPos);
                if (callback != null) callback.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (node != null && orgPos != null) node.setWorldPosition(orgPos);
                if (callback != null) callback.onAnimationEnd(animation);
            }
        });
    }

    private void _addAnimatorSetListener(AnimatorSet set, Animator.AnimatorListener callback) {
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                callback.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                callback.onAnimationEnd(animation);
            }
        });
    }

    public static enum Animation {
        BOUNCE, ARRIVE_FROM_BACK, ARRIVE_FROM_LEFT, EXIT_TO_RIGHT, EXIT_TO_BACK, FALL_FROM_TOP, ROTATE
    }
}
