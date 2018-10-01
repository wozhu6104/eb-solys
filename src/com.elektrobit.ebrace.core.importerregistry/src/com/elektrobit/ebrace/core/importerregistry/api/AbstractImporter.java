/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.importerregistry.api;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;

public abstract class AbstractImporter implements Importer
{
    private final static Logger LOG = Logger.getLogger( AbstractImporter.class );
    public static final int BYTES_TO_MB = 1024 * 1024;
    private String lastPercentDone;
    protected ImporterProgressListener progressListener = null;
    private final DecimalFormat percentFormat = new DecimalFormat( "#%" );
    private volatile boolean importCanceled = false;

    @Override
    public final void importFile(File file) throws IOException
    {
        importCanceled = false;
        processFileContent( file );
    }

    @Override
    public void importFrom(long startTimestamp, Long desiredChunkLengthTime, File file) throws IOException
    {
        importCanceled = false;
        processFileContent( startTimestamp, desiredChunkLengthTime, file );
    }

    public void processFileContent(long startTimestamp, Long desiredChunkLengthTime, File file) throws IOException
    {
        throw new UnsupportedOperationException( "Load data chunk loading not supported for this importer." );
    }

    abstract public void processFileContent(File file) throws IOException;

    abstract protected long getMaximumTraceFileSizeInMB();

    protected void postProgress(double actual, double total)
    {
        if (progressListener != null)
        {
            String percentDone = percentFormat.format( actual / total );
            if (!percentDone.equals( lastPercentDone ))
            {
                LOG.info( "AbstractImporter - postProgress() " + percentDone );
                progressListener.onLoadFileProgressChanged( percentDone );
                lastPercentDone = percentDone;
            }
        }
    }

    @Override
    public void setLoadFileProgressListener(ImporterProgressListener _usecaseImporterFeedbackListener)
    {
        progressListener = _usecaseImporterFeedbackListener;
    }

    @Override
    public boolean isFileTooBig(File file)
    {
        long fileSizeB = file.length();
        long fileSizeMB = fileSizeB / BYTES_TO_MB;
        long maximumTraceFileSizeInMB = getMaximumTraceFileSizeInMB();
        return fileSizeMB > maximumTraceFileSizeInMB;
    }

    @Override
    public void cancelImport()
    {
        importCanceled = true;
    }

    public boolean isImportCanceled()
    {
        return importCanceled;
    }

    @Override
    public Long getFileStartTime()
    {
        return new Long( -1 );
    }

    @Override
    public Long getFileEndTime()
    {
        return new Long( -1 );
    }

    @Override
    public Long getChunkStartTime()
    {
        return new Long( -1 );
    }

    @Override
    public Long getChunkEndTime()
    {
        return new Long( -1 );
    }

    @Override
    public boolean isChunkLoadingSupported()
    {
        return false;
    }

    @Override
    public boolean isLoadingAtLeastPartiallySuccessful()
    {
        return true;
    }
}
