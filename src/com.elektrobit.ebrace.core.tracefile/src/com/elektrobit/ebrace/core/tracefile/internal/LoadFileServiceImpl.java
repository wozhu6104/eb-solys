/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.tracefile.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.importerregistry.api.Importer;
import com.elektrobit.ebrace.core.importerregistry.api.ImporterProgressListener;
import com.elektrobit.ebrace.core.importerregistry.api.ImporterRegistry;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.file.FileModel;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileProgressListener;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLog;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogTypes;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ClearChunkDataListener;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;

import lombok.extern.log4j.Log4j;

@Log4j
@Component(service = {LoadFileService.class, ClearChunkDataListener.class})
public class LoadFileServiceImpl implements LoadFileService, ImporterProgressListener, ClearChunkDataListener
{
    private final List<LoadFileProgressListener> listeners = new CopyOnWriteArrayList<LoadFileProgressListener>();
    private final List<String> alreadyLoadedFiles = new ArrayList<String>();
    private final List<String> failedLoadingFiles = new ArrayList<String>();

    private UserInteractionPreferences userInteractionPreferences = null;
    private ImporterRegistry importerRegistry = null;
    private ResourcesModelManager resourcesModelManager = null;
    private ResetNotifier resetNotifier = null;

    private volatile boolean isFileLoading = false;
    private volatile boolean isLoadingCanceled = false;

    private volatile Importer currentImporter;
    private UserMessageLogger userMessageLogger;

    private String path;

    public LoadFileServiceImpl()
    {
    }

    public void openFile(String path)
    {
        this.path = path;
        loadFile( path );
    }

    @UseStatLog(value = UseStatLogTypes.FILE_LOAD_STARTED, parser = UseStatLogLoadFileParser.class)
    @Override
    public boolean loadFile(String path)
    {
        this.path = path;
        return loadFileFrom( 0, null );
    }

    @Override
    public boolean loadFileFrom(long startTimestamp, Long desiredChunkLengthTime)
    {
        if (path != null)
        {
            isFileLoading = true;
            boolean resultOK = true;
            File file = new File( path );
            try
            {
                currentImporter = importerRegistry.getImporterForFile( file );
                currentImporter.setLoadFileProgressListener( this );

                userInteractionPreferences.setIsLiveMode( false );
                if (currentImporter.isChunkLoadingSupported())
                {
                    notifyFileLoadingStarted( path );

                    currentImporter.importFrom( startTimestamp, desiredChunkLengthTime, file );
                }
                else
                {
                    if (!isFileNotTooBig( path ))
                    {
                        throw new IllegalArgumentException( "File is too big, you should check the size with isFileNotTooBig(String path) first" );
                    }
                    if (!isFileNotLoaded( path ))
                    {
                        throw new IllegalArgumentException( "This file has been already loaded, you should check it with isFileNotLoaded(String path) first" );
                    }

                    notifyFileLoadingStarted( path );
                    currentImporter.importFile( file );
                }

            }
            catch (Exception e)
            {
                log.error( "Exception while file loading", e );
                userMessageLogger.logUserMessage( UserMessageLoggerTypes.ERROR,
                                                  "An error has occured while loading the file" );
                resultOK = false;
            }

            resultOK = resultOK && currentImporter.isLoadingAtLeastPartiallySuccessful();
            if (!isLoadingCanceled && resultOK)
            {
                Long fileStartTime = currentImporter.getFileStartTime();
                Long fileEndTime = currentImporter.getFileEndTime();
                Long chunkStartTime = currentImporter.getChunkStartTime();
                Long chunkEndTime = currentImporter.getChunkEndTime();

                alreadyLoadedFiles.add( path );
                createFileModelIfNotExist( file.getName(), path );

                userInteractionPreferences.setIsLiveMode( true );
                userInteractionPreferences.setIsLiveMode( false );

                notifyFileLoadingDone( fileStartTime, fileEndTime, chunkStartTime, chunkEndTime );
            }
            else
            {
                failedLoadingFiles.add( path );
                resultOK = false;
                notifyFileLoadingCanceled();
            }

            isFileLoading = false;
            isLoadingCanceled = false;
            return resultOK;
        }
        return false;
    }

    @Override
    public void cancelFileLoading()
    {
        isLoadingCanceled = true;
        currentImporter.cancelImport();
        resetNotifier.performReset();
    }

    @Override
    public boolean isFileNotTooBig(String path)
    {
        if (anySizeOfFileCanBeLoaded())
        {
            return true;
        }
        File file = new File( path );
        Importer importer = importerRegistry.getImporterForFile( file );
        return !importer.isFileTooBig( file );
    }

    private boolean anySizeOfFileCanBeLoaded()
    {
        return resourcesModelManager.isCallbackScriptRunning();
    }

    @Override
    public boolean isFileNotLoaded(String path)
    {
        return !alreadyLoadedFiles.contains( path );
    }

    @Override
    public boolean isFileLoadingFailed(String path)
    {
        return failedLoadingFiles.contains( path );
    }

    @Override
    public void registerFileProgressListener(LoadFileProgressListener listener)
    {
        listeners.add( listener );
    }

    @Override
    public void unregisterFileProgressListener(LoadFileProgressListener listener)
    {
        listeners.remove( listener );
    }

    @Override
    public void onLoadFileProgressChanged(String percentDone)
    {
        List<LoadFileProgressListener> listenersCopy = new ArrayList<LoadFileProgressListener>( listeners );
        int integerWithoutPercent = Integer.parseInt( percentDone.substring( 0, percentDone.length() - 1 ) );
        for (LoadFileProgressListener loadFileProgressListener : listenersCopy)
        {
            loadFileProgressListener.onLoadFileProgressChanged( integerWithoutPercent );
        }
    }

    private void notifyFileLoadingStarted(String path)
    {
        List<LoadFileProgressListener> listenersCopy = new ArrayList<LoadFileProgressListener>( listeners );
        for (LoadFileProgressListener loadFileProgressListener : listenersCopy)
        {
            loadFileProgressListener.onLoadFileStarted( path );
        }
    }

    private void notifyFileLoadingDone(long fileStartTime, long fileEndTime, long chunkStartTime, long chunkEndTime)
    {
        List<LoadFileProgressListener> listenersCopy = new ArrayList<LoadFileProgressListener>( listeners );
        for (LoadFileProgressListener loadFileProgressListener : listenersCopy)
        {
            loadFileProgressListener.onLoadFileDone( fileStartTime, fileEndTime, chunkStartTime, chunkEndTime );
        }
    }

    private void notifyFileLoadingCanceled()
    {
        List<LoadFileProgressListener> listenersCopy = new ArrayList<LoadFileProgressListener>( listeners );
        for (LoadFileProgressListener loadFileProgressListener : listenersCopy)
        {
            loadFileProgressListener.onLoadFileCanceled();
        }
    }

    private void createFileModelIfNotExist(String fileName, String path)
    {
        if (!fileModelExists( path ))
        {
            resourcesModelManager.createFileModel( fileName, path );
        }
    }

    private boolean fileModelExists(String path)
    {
        List<ResourceModel> allFileModels = resourcesModelManager.getFiles();
        for (ResourceModel resourceModel : allFileModels)
        {
            FileModel fileModel = (FileModel)resourceModel;
            if (fileModel.getPath().equals( path ))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isFileEmpty(String path)
    {
        File file = new File( path );
        if (file.length() == 0)
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean isFileLoading()
    {
        return isFileLoading;
    }

    @Override
    public void onClearChunkData()
    {
        alreadyLoadedFiles.clear();
        failedLoadingFiles.clear();
    }

    @Override
    public long getStartTimestamp()
    {
        if (currentImporter != null)
        {
            return currentImporter.getFileStartTime();
        }
        else
        {
            return 0;
        }
    }

    @Override
    public long getEndTimestamp()
    {
        if (currentImporter != null)
        {
            return currentImporter.getFileEndTime();
        }
        else
        {
            return 0;
        }
    }

    @Override
    public long getChunkStartTimestamp()
    {
        if (currentImporter != null)
        {
            return currentImporter.getChunkStartTime();
        }
        else
        {
            return 0;
        }

    }

    @Override
    public long getChunkEndTimestamp()
    {
        if (currentImporter != null)
        {
            return currentImporter.getChunkEndTime();
        }
        else
        {
            return 0;
        }
    }

    @Reference
    public void bindResetNotifier(ResetNotifier resetNotifier)
    {
        this.resetNotifier = resetNotifier;
    }

    public void unbindResetNotifier(ResetNotifier resetNotifier)
    {
        this.resetNotifier = null;
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
    public void bindImporterRegistry(ImporterRegistry importerRegistry)
    {
        this.importerRegistry = importerRegistry;
    }

    public void unbindImporterRegistry(ImporterRegistry importerRegistry)
    {
        this.importerRegistry = null;
    }

    @Reference
    public void bindResourcesModelManager(ResourcesModelManager resourcesModelManager)
    {
        this.resourcesModelManager = resourcesModelManager;
    }

    public void unbindResourcesModelManager(ResourcesModelManager resourcesModelManager)
    {
        this.resourcesModelManager = null;
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
}
