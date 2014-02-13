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

import android.os.SystemClock;

/**
 * Model holding the Timer state.
 */
public class Pomo {

    /**
     * Interface to listen for changes on the {@link Pomo}.
     */
    public interface TimerListener {
        /** Timer has started. */
        public void onStart();
        /** Timer has been paused. */
        public void onPause();
        /** Timer has been reset */
        public void onReset();
        /** Pomodoro is resting */
        public void onBeginRest();
        /** Pomodoro begins */
        public void onBeginPomo();
    }

    private long mDurationMillis; 
    private long mStartTimeMillis;
    private long mPauseTimeMillis;
    private int mPomosCompleted = 0;
    private boolean mIsResting;

    private TimerListener mListener;

    public Pomo() {
        this(25 * 60 * 1000); // defaults to 25 minutes
    }

    public Pomo(long durationMillis) {
        setDurationMillis(durationMillis);
    }

    /**
     * Sets the timer's duration in milliseconds.
     */
    public void setDurationMillis(long durationMillis) {
        mDurationMillis = durationMillis;
        if (mListener != null) {
            mListener.onReset();
        }
    }

    /**
     * Gets the timer's duration in milliseconds.
     */
    public long getDurationMillis() {
        return mDurationMillis;
    }

    public int getPomosCompleted() {
		return mPomosCompleted;
	}
    

	public void setPomosCompleted(int mPomosCompleted) {
		this.mPomosCompleted = mPomosCompleted;
	}

	/**
     * Returns whether or not the timer is running.
     */
    public boolean isRunning() {
        return mStartTimeMillis > 0 && mPauseTimeMillis == 0;
    }

    /**
     * Returns whether or not the timer has been started.
     */
    public boolean isStarted() {
        return mStartTimeMillis > 0;
    }

    /**
     * Gets the remaining time in milliseconds.
     */
    public long getRemainingTimeMillis() {
        long remainingTime = mDurationMillis;

        if (mPauseTimeMillis != 0) {
            remainingTime -= mPauseTimeMillis - mStartTimeMillis;
        } else if (mStartTimeMillis != 0) {
            remainingTime -= SystemClock.elapsedRealtime() - mStartTimeMillis;
        }
        
        if(remainingTime < 0){
        	if(mIsResting){
        		mPomosCompleted++;
            	startPomo();
        	}
        	else {
        		startRest();
        	}
        	remainingTime = mDurationMillis;
        }

        return remainingTime;
    }

    /**
     * Starts the timer.
     */
    public void start() {
        long elapsedTime = mPauseTimeMillis - mStartTimeMillis;

        mStartTimeMillis = SystemClock.elapsedRealtime() - elapsedTime;
        mPauseTimeMillis = 0;
        if (mListener != null) {
            mListener.onStart();
        }
    }

    /**
     * Pauses the timer.
     */
    public void pause() {
        if (isStarted()) {
            mPauseTimeMillis = SystemClock.elapsedRealtime();
            if (mListener != null) {
                mListener.onPause();
            }
        }
    }
    
    /**
     * Starts the Rest
     */
    public void startRest(){
    	mIsResting = true;
    	mDurationMillis = 5 * 60 * 1000;
    	reset();
    	if(mListener != null){
    		mListener.onBeginRest();
    	}
    }
    
    /**
     * Starts the next Pomodoro
     */
    public void startPomo(){
    	mIsResting = false;
    	mDurationMillis = 25 * 60 * 1000;
    	reset();
    	if(mListener != null){
    		mListener.onBeginPomo();
    	}
    	if(!isStarted()){
    		start();
    	}
    }

    /**
     * Resets the timer.
     */
    public void reset() {
        mStartTimeMillis = SystemClock.elapsedRealtime();
        mPauseTimeMillis = 0;
    }
    
    public void totalReset(){
        mStartTimeMillis = 0;
        mPauseTimeMillis = 0;
        mPomosCompleted = 0;
        if(mListener != null) {
        	mListener.onReset();
        }
    }
    

    /**
     * Sets a {@link TimerListener}.
     */
    public void setListener(TimerListener listener) {
        mListener = listener;
    }
}
