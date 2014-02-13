/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 *  This file has been modified.  For all modifications:
 *  
 *  Copyright (C) 2014 Jonathan Freeman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.freethejazz.pomodoro;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.View;

/**
 * SurfaceHolder.Callback used to draw the timer on the timeline {@link LiveCard}.
 */
public class PomoDrawer implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private PomoView mView;

    private PomoView.ChangeListener mListener = new PomoView.ChangeListener() {
        @Override
        public void onChange() {
            if (mHolder != null) {
                draw();
            }
        }
    };

    public PomoDrawer(Context context) {
        mView = new PomoView(context);
        mView.setListener(mListener);
    }

    public Pomo getTimer() {
        return mView.getTimer();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Measure and layout the view with the canvas dimensions.
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        mView.measure(measuredWidth, measuredHeight);
        mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());
        draw();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        draw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder = null;
    }

    public void draw() {
        Canvas canvas;
        try {
            canvas = mHolder.lockCanvas();
        } catch (Exception e) {
            return;
        }
        if (canvas != null) {
            mView.draw(canvas);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }
}
