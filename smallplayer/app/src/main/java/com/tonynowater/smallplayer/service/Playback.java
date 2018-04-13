/*
 * Copyright (C) 2014 The Android Open Source Project
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
package com.tonynowater.smallplayer.service;

/**
 * Interface representing either Local or Remote Playback. The {@link PlayMusicService} works
 * directly with an instance of the Playback object to make the various calls such as
 * play, pause etc.
 */
public interface Playback {

    /**
     * Stop the playback. All resources can be de-allocated by implementations here.
     * @param notifyListeners if true and a callback has been set by setCallback,
     *                        callback.onPlaybackStatusChanged will be called after changing
     *                        the state.
     */
    void stop(boolean notifyListeners);

    /**
     * Get the current {@link android.media.session.PlaybackState#getState()}
     */
    int getState();
    
    /**
     * @return boolean indicating whether the player is playing or is supposed to be
     * playing when we gain audio focus.
     */
    boolean isPlaying();

    /**
     * @return pos if currently playing an item
     */
    long getCurrentStreamPosition();

    /**
     * @return duration if currently playing an item
     */
    long getCurrentDuration();

    /**
     * Set the current position. Typically used when switching players that are in
     * paused state.
     *
     * @param pos position in the stream
     */
    void setCurrentStreamPosition(int pos);

    /**
     * Query the underlying stream and update the internal last known stream position.
     */
    void updateLastKnownStreamPosition();

    /**
     * play music
     */
    void play();

    /**
     * Pause the current playing item
     */
    void pause();

    /**
     * Seek to the given position
     */
    void seekTo(int position);

    void releaseResource();

    void setEqualizer(EqualizerType equalizerType);

    EqualizerType getEqualizerType();

    interface Callback {

        void onCompletion();

        void onPlaybackStateChanged();

        void onError(String error);
    }
}
