package com.ps.kidsworld.interfaces;

import com.google.ar.sceneform.rendering.Renderable;

public interface MyOnTapModelListener {
    void onModelDownloaded(Renderable renderable);

    void onModelError(Throwable exception);
}
