/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.headlessexecutor.impl;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileProgressListener;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;

public class HeadlessFileLoader implements LoadFileProgressListener
{
    private final LoadFileService loadFileService;

    public HeadlessFileLoader(LoadFileService loadFileService)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "LoadFileService", loadFileService );
        this.loadFileService = loadFileService;
        loadFileService.registerFileProgressListener( this );
    }

    public void loadFile(String pathToFile)
    {
        if (loadFileService.isFileNotTooBig( pathToFile ))
        {
            loadFileService.loadFile( pathToFile );
        }
        else
        {
            System.out.println( "Error: File is too big and needs to be splitted " + pathToFile );
        }
    }

    @Override
    public void onLoadFileProgressChanged(int percentDone)
    {
        System.out.println( "Loading file: " + percentDone + "%" );
    }

    @Override
    public synchronized void onLoadFileDone(long fileStartTime, long fileEndTime, long chunkStartTime,
            long chunkEndTime)
    {
        System.out.println( "Loading file: done" );
    }

    @Override
    public void onLoadFileCanceled()
    {
    }

    @Override
    public void onLoadFileStarted(String pathToFile)
    {
        System.out.println( "Loading traces " + pathToFile );
    }
}
