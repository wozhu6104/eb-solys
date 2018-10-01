/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.importer.internal.racefile;

import java.io.File;
import java.io.IOException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.chronograph.api.TimestampProvider;
import com.elektrobit.ebrace.core.importerregistry.api.Importer;
import com.elektrobit.ebrace.core.importerregistry.api.ImporterProgressListener;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.BetaFeatureConfigurator;
import com.elektrobit.ebrace.targetadapter.communicator.services.ProtocolMessageDispatcher;
import com.elektrobit.ebsolys.core.targetdata.api.importer.FileSizeLimitService;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TargetHeaderMetaDataService;

@Component(service = {Importer.class, ResetListener.class})
public class RaceFileImporterProxy implements Importer, ResetListener
{
    private Importer importer;
    private ResetListener resetListener;

    private ProtocolMessageDispatcher protocolMessageDispatcher = null;
    private TimestampProvider timestampProvider = null;
    private UserInteractionPreferences userInteractionPreferences = null;

    private UserMessageLogger userMessageLogger;

    private RuntimeEventAcceptor runtimeEventAcceptor;
    private TargetHeaderMetaDataService targetHeaderMetaDataService;
    private FileSizeLimitService fileSizeLimitService;

    public RaceFileImporterProxy()
    {
    }

    @Override
    public String getSupportedFileExtension()
    {
        return importer.getSupportedFileExtension();
    }

    @Override
    public String getSupportedFileTypeName()
    {
        return importer.getSupportedFileTypeName();
    }

    @Override
    public void onReset()
    {
        if (resetListener != null)
        {
            resetListener.onReset();
        }
    }

    @Override
    public void importFile(File file) throws IOException
    {
        importer.importFile( file );
    }

    @Override
    public void importFrom(long startTimestamp, Long desiredChunkLengthTime, File file) throws IOException
    {
        importer.importFrom( startTimestamp, desiredChunkLengthTime, file );
    }

    @Override
    public void setLoadFileProgressListener(ImporterProgressListener loadFileProgressListener)
    {
        importer.setLoadFileProgressListener( loadFileProgressListener );
    }

    @Override
    public boolean isFileTooBig(File file)
    {
        return importer.isFileTooBig( file );
    }

    @Override
    public void cancelImport()
    {
        importer.cancelImport();
    }

    @Override
    public Long getFileStartTime()
    {
        return importer.getFileStartTime();
    }

    @Override
    public Long getFileEndTime()
    {
        return importer.getFileEndTime();
    }

    @Override
    public Long getChunkStartTime()
    {
        return importer.getChunkStartTime();
    }

    @Override
    public Long getChunkEndTime()
    {
        return importer.getChunkEndTime();
    }

    @Override
    public boolean isChunkLoadingSupported()
    {
        return importer.isChunkLoadingSupported();
    }

    @Activate
    public void activate()
    {
        if (BetaFeatureConfigurator.Features.DATA_CHUNK.isActive())
        {
            RaceDataChunkImporter raceDataChunkImporter = new RaceDataChunkImporter();
            raceDataChunkImporter.bind( protocolMessageDispatcher );
            raceDataChunkImporter.bind( timestampProvider );
            raceDataChunkImporter.bind( userInteractionPreferences );
            raceDataChunkImporter.bind( userMessageLogger );
            raceDataChunkImporter.bind( runtimeEventAcceptor );
            raceDataChunkImporter.bind( targetHeaderMetaDataService );
            importer = raceDataChunkImporter;
            resetListener = raceDataChunkImporter;
            raceDataChunkImporter.activate();
        }
        else
        {
            RaceFileImporter raceFileImporter = new RaceFileImporter();
            raceFileImporter.bind( protocolMessageDispatcher );
            raceFileImporter.bind( timestampProvider );
            raceFileImporter.bind( userMessageLogger );
            raceFileImporter.bind( fileSizeLimitService );
            importer = raceFileImporter;
            resetListener = raceFileImporter;
        }
    }

    @Reference
    public void bindProtocolMessageDispatcher(ProtocolMessageDispatcher protocolMessageDispatcher)
    {
        this.protocolMessageDispatcher = protocolMessageDispatcher;
    }

    public void unbindProtocolMessageDispatcher(ProtocolMessageDispatcher protocolMessageDispatcher)
    {
        this.protocolMessageDispatcher = null;
    }

    @Reference
    public void bindTimestampProvider(TimestampProvider timestampProvider)
    {
        this.timestampProvider = timestampProvider;
    }

    public void unbindTimestampProvider(TimestampProvider timestampProvider)
    {
        this.timestampProvider = null;
    }

    @Reference
    public void bindUserInteractionPreferences(UserInteractionPreferences userInteractionPreferences)
    {
        this.userInteractionPreferences = userInteractionPreferences;
    }

    public void unbindUserInteractionPreferences(UserInteractionPreferences userInteractionPreferences)
    {
        this.userInteractionPreferences = null;
    }

    @Reference
    public void bindUserMessageLogger(UserMessageLogger userMessageLogger)
    {
        this.userMessageLogger = userMessageLogger;
    }

    public void unbindUserMessageLogger(UserMessageLogger userMessageLogger)
    {
        this.userMessageLogger = null;
    }

    @Reference
    public void bindTargetHeaderMetaDataService(TargetHeaderMetaDataService targetHeaderMetaDataService)
    {
        this.targetHeaderMetaDataService = targetHeaderMetaDataService;
    }

    public void unbindTargetHeaderMetaDataService(TargetHeaderMetaDataService targetHeaderMetaDataService)
    {
        this.targetHeaderMetaDataService = null;
    }

    @Reference
    public void bindFileSizeLimitService(FileSizeLimitService fileSizeLimitService)
    {
        this.fileSizeLimitService = fileSizeLimitService;
    }

    public void unbindFileSizeLimitService(FileSizeLimitService fileSizeLimitService)
    {
        this.fileSizeLimitService = null;
    }

    @Reference
    public void bindRuntimeEventAcceptor(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
    }

    public void unbindRuntimeEventAcceptor(RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = null;
    }

    @Override
    public boolean isLoadingAtLeastPartiallySuccessful()
    {
        return importer.isLoadingAtLeastPartiallySuccessful();
    }
}
