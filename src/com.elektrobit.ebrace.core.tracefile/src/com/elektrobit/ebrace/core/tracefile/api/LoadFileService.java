/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.tracefile.api;

public interface LoadFileService
{
    /**
     * Loads a file and returns only after loading is done or has failed.
     * 
     * @param path
     *            Path to trace file that should be loaded
     * @throws IllegalArgumentException
     *             if file is too big. You should check the file with {@link #isFileNotTooBig(String)} first.
     */
    public boolean loadFile(String path);

    /**
     * Load file from given timestamp until desired length of chunk is reached, or importer decides to stop loading.
     * 
     * @param startTimestamp
     *            Start loading of file from the given timestamp.
     * @param desiredChuckLengthTime
     *            Time length of the chunk that should be loaded. If null, importer will decide when to stop loading.
     * @return
     */
    public boolean loadFileFrom(long startTimestamp, Long desiredChuckLengthTime);

    public void cancelFileLoading();

    /**
     * Checks if the file is not too big to be loaded at once
     * 
     * @param path
     *            Path to trace file
     * @return True if file is not too big and can be loaded at once, otherwise false
     */
    public boolean isFileNotTooBig(String path);

    /**
     * Checks if the file is empty
     * 
     * @param path
     *            Path to trace file
     * @return True if file is empty, otherwise false
     */
    public boolean isFileEmpty(String path);

    boolean isFileNotLoaded(String path);

    boolean isFileLoadingFailed(String path);

    boolean isFileLoading();

    long getStartTimestamp();

    long getEndTimestamp();

    long getChunkStartTimestamp();

    long getChunkEndTimestamp();

    public void registerFileProgressListener(LoadFileProgressListener listener);

    public void unregisterFileProgressListener(LoadFileProgressListener listener);
}
