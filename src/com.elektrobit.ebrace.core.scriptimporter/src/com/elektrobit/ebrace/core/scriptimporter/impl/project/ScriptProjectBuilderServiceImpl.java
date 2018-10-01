/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.scriptimporter.impl.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.common.utils.GenericListenerCaller;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptConstants;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptData;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptProjectBuildListener;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptProjectBuilderService;
import com.elektrobit.ebrace.core.scriptimporter.impl.jarexporter.JarExporter;
import com.elektrobit.ebrace.core.scriptimporter.impl.jarexporter.JarExporterFactory;
import com.elektrobit.ebrace.dev.debug.annotations.api.EnterExitPrinter;
import com.elektrobit.ebrace.dev.debug.annotations.api.InterceptMethod;
import com.elektrobit.ebsolys.core.targetdata.api.reset.StartupDoneListener;

@Component(service = {ScriptProjectBuilderService.class, StartupDoneListener.class})
public class ScriptProjectBuilderServiceImpl
        implements
            IResourceChangeListener,
            ScriptProjectBuilderService,
            StartupDoneListener
{

    private JarExporter jarExporter;

    private final GenericListenerCaller<ScriptProjectBuildListener> listenerCaller = new GenericListenerCaller<>();

    private ScriptProjectBuilderImpl scriptProjectBuilderImpl;

    private PreferencesService preferencesService;

    private ScriptProjectImporter scriptProjectImporter;

    @Reference
    public void bindPreferencesService(PreferencesService preferencesService)
    {
        this.preferencesService = preferencesService;
    }

    @InterceptMethod(interceptor = EnterExitPrinter.class)
    @Activate
    public void start()
    {
    }

    @InterceptMethod(interceptor = EnterExitPrinter.class)
    @Override
    public void onApplicationStarted()
    {
        buildProject();
    }

    @Override
    public void buildProject()
    {
        scriptProjectImporter = new ScriptProjectImporter( preferencesService.getScriptFolderPath() );
        scriptProjectBuilderImpl = ScriptProjectBuilderImplFactory.create( scriptProjectImporter );

        jarExporter = JarExporterFactory.create( preferencesService.getScriptFolderPath() );

        scriptProjectImporter.importProject();
        activateScriptExportingAfterBuild();
    }

    public void activateScriptExportingAfterBuild()
    {
        ResourcesPlugin.getWorkspace().addResourceChangeListener( this, IResourceChangeEvent.POST_BUILD );
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event)
    {
        List<File> changedClassFiles = new ArrayList<>();
        try
        {
            event.getDelta().accept( new IResourceDeltaVisitor()
            {

                @Override
                public boolean visit(IResourceDelta delta) throws CoreException
                {
                    File changedFile = delta.getResource().getFullPath().toFile();
                    // System.out.println( "Kind: " + delta.getKind() );
                    // System.out.println( "Flag: " + delta.getFlags() );
                    if (changedFile.getAbsolutePath().endsWith( "class" ))
                    {
                        changedClassFiles.add( changedFile );
                    }
                    return true;
                }
            } );
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }

        if (!changedClassFiles.isEmpty() && !compiledClassFiles().isEmpty())
        {
            List<ScriptData> scriptNames = jarExporter.exportAllScriptsToJar();
            notifyListener( scriptNames );
        }
    }

    private List<Path> compiledClassFiles()
    {
        IFolder binFolder = scriptProjectImporter.getScriptsProject().getFolder( ScriptConstants.BIN_FOLDER );
        if (binFolder.exists())
        {
            try
            {
                List<Path> classFiles = Files.find( Paths.get( binFolder.getLocation().toFile().getAbsolutePath() ),
                                                    Integer.MAX_VALUE,
                                                    (filePath, fileAttr) -> fileAttr.isRegularFile()
                                                            && filePath.toString().toLowerCase().endsWith( ".class" ) )
                        .collect( Collectors.toList() );
                return classFiles;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return Collections.emptyList();
    }

    @InterceptMethod(interceptor = EnterExitPrinter.class)
    private void notifyListener(List<ScriptData> scriptNames)
    {
        listenerCaller.notifyListeners( (listener) -> {
            listener.onScriptsBuildAndExported( scriptNames );
        } );
    }

    @Deactivate
    public void stop()
    {
        deactivateScriptExportingAfterBuild();
    }

    public void deactivateScriptExportingAfterBuild()
    {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener( this );
    }

    @Override
    public void addScriptBuildListener(ScriptProjectBuildListener listener)
    {
        listenerCaller.add( listener );
    }

    @Override
    public void removeScriptBuildListener(ScriptProjectBuildListener listener)
    {
        listenerCaller.remove( listener );
    }

    @Override
    public void addUserScripts(List<File> userScripts)
    {
        scriptProjectBuilderImpl.addUserScripts( userScripts );
    }

    public void unbindPreferencesService(PreferencesService preferencesService)
    {
        this.preferencesService = null;
    }

    @Override
    public void addPreinstalledScripts(List<File> preinstalledScripts)
    {
        scriptProjectBuilderImpl.addPreinstalledScripts( preinstalledScripts );
    }

}
