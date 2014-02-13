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
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * View used to draw a running timer.
 */
public class PomoView extends FrameLayout {

    /**
     * Interface to listen for changes on the view layout.
     */
    public interface ChangeListener {
        /** Notified of a change in the view. */
        public void onChange();
    }

    private static final long DELAY_MILLIS = 1000;
    private static final int SOUND_PRIORITY = 1;
    private static final int MAX_STREAMS = 1;

    private final SoundPool mSoundPool;
    private final int mTimerFinishedSoundId;

    private final FrameLayout mMainView;
    private final TextView mPomosCompletedTextView;
    private final TextView mPomosCompletedNumberView;
    private final TextView mMinutesView;
    private final TextView mSecondsView;
    private final TextView mTipView;

    private final int mWhiteColor;
    private final int mRedColor;
    private final int mGreenColor;

    private final Handler mHandler = new Handler();
    private final Runnable mUpdateTextRunnable = new Runnable() {
        @Override
        public void run() {
            if (mRunning) {
                mHandler.postDelayed(mUpdateTextRunnable, DELAY_MILLIS);
                updateText();
            }
        }
    };

    private final Pomo mTimer;
    private final Pomo.TimerListener mTimerListener = new Pomo.TimerListener() {
        @Override
        public void onStart() {
            mRunning = true;
            long delayMillis = Math.abs(mTimer.getRemainingTimeMillis()) % DELAY_MILLIS;
            if (delayMillis == 0) {
                delayMillis = DELAY_MILLIS;
            }
            mHandler.postDelayed(mUpdateTextRunnable, delayMillis);
        }

        @Override
        public void onPause() {
            mRunning = false;
            mHandler.removeCallbacks(mUpdateTextRunnable);
        }

        @Override
        public void onReset() {
            long delayMillis = Math.abs(mTimer.getRemainingTimeMillis()) % DELAY_MILLIS;
            if (delayMillis == 0) {
                delayMillis = DELAY_MILLIS;
            }
            
        	mMainView.setBackgroundColor(mGreenColor);
            mTipView.setVisibility(View.INVISIBLE);
            if(mRunning == false){
                mHandler.postDelayed(mUpdateTextRunnable, delayMillis);
            }
            updateText(mTimer.getRemainingTimeMillis(), mTimer.getPomosCompleted(),  mWhiteColor);
        }
        
        @Override
        public void onBeginRest(){
            playSound();
        	mMainView.setBackgroundColor(mRedColor);
        	mTipView.setVisibility(View.VISIBLE);
        }
        
        @Override
        public void onBeginPomo(){
            playSound();
        	mMainView.setBackgroundColor(mGreenColor);
        	mTipView.setVisibility(View.INVISIBLE);
        }
    };

    private boolean mRunning;
    private boolean mRedText;

    private ChangeListener mChangeListener;

    public PomoView(Context context) {
        this(context, null, 0);
    }

    public PomoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PomoView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);

        mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        mTimerFinishedSoundId = mSoundPool.load(context, R.raw.timer_finished, SOUND_PRIORITY);

        LayoutInflater.from(context).inflate(R.layout.card_timer, this);
        
        mMainView = (FrameLayout) findViewById(R.id.mainView);
        mMinutesView = (TextView) findViewById(R.id.minutes);
        mSecondsView = (TextView) findViewById(R.id.seconds);
        mPomosCompletedTextView = (TextView) findViewById(R.id.pomosCompletedText);
        mPomosCompletedNumberView = (TextView) findViewById(R.id.pomos);
        mTipView = (TextView) findViewById(R.id.tip);
        mTipView.setText(context.getResources().getString(R.string.timer_finished));
        mTipView.setVisibility(View.INVISIBLE);
        mPomosCompletedTextView.setText(context.getResources().getString(R.string.pomos_completed));

        mWhiteColor = context.getResources().getColor(R.color.white);
        mRedColor = context.getResources().getColor(R.color.red);
        mGreenColor = context.getResources().getColor(R.color.green);

        mTimer = new Pomo();
        mTimer.setListener(mTimerListener);
        
        mPomosCompletedNumberView.setText(Integer.toString(mTimer.getPomosCompleted()));
    }

    public Pomo getTimer() {
        return mTimer;
    }

    /**
     * Set a {@link ChangeListener}.
     */
    public void setListener(ChangeListener listener) {
        mChangeListener = listener;
    }

    /**
     * Updates the text from the Timer's value.
     */
    private void updateText() {
        long remainingTimeMillis = mTimer.getRemainingTimeMillis();

        mRedText = false;
        // Round up: x001 to (x + 1)000 milliseconds should resolve to x seconds.
        remainingTimeMillis -= 1;
        remainingTimeMillis += TimeUnit.SECONDS.toMillis(1);

        updateText(remainingTimeMillis, mTimer.getPomosCompleted(), mRedText ? mRedColor : mWhiteColor);
    }

    /**
     * Updates the displayed text with the provided values.
     */
    private void updateText(long timeMillis, int pomosCompleted, int textColor) {
    	mPomosCompletedNumberView.setText(Integer.toString(pomosCompleted));
        timeMillis %= TimeUnit.HOURS.toMillis(1);
        mMinutesView.setText(String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(timeMillis)));
        mMinutesView.setTextColor(textColor);
        timeMillis %= TimeUnit.MINUTES.toMillis(1);
        mSecondsView.setText(String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(timeMillis)));
        mSecondsView.setTextColor(textColor);
        if (mChangeListener != null) {
            mChangeListener.onChange();
        }
    }

    /**
     * Plays the "timer finishd" sound once.
     */
    private void playSound() {
         mSoundPool.play(mTimerFinishedSoundId,
                        1 /* leftVolume */,
                        1 /* rightVolume */,
                        SOUND_PRIORITY,
                        0 /* loop */,
                        1 /* rate */);
    }
}
