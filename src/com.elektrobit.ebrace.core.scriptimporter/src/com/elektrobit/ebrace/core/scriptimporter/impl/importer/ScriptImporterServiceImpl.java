/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.scriptimporter.impl.importer;

import java.io.File;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptLoader;
import com.elektrobit.ebrace.core.scriptimporter.api.ImportScriptStatusListener;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptImporterService;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptProjectBuilderService;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;

@Component
public class ScriptImporterServiceImpl implements ScriptImporterService
{
    private volatile boolean active = false;
    private ResourcesModelManager resourcesModelManager;
    private ScriptImporterService scriptImporterImpl;
    private RaceScriptLoader raceScriptLoader;
    private ScriptProjectBuilderService scriptProjectBuilder;

    @Reference
    public void bindScriptProjectBuilder(ScriptProjectBuilderService scriptProjectBuilder)
    {
        this.scriptProjectBuilder = scriptProjectBuilder;
    }

    @Reference
    public void bindResourcesModelManager(ResourcesModelManager resourcesModelManager)
    {
        this.resourcesModelManager = resourcesModelManager;
    }

    @Reference
    public void bindRaceScriptLoader(RaceScriptLoader raceScriptLoader)
    {
        this.raceScriptLoader = raceScriptLoader;
    }

    @Activate
    public void start()
    {
        scriptImporterImpl = new ScriptImporterImpl( resourcesModelManager, raceScriptLoader, scriptProjectBuilder );
        active = true;
    }

    @Override
    public void importUserScript(File sourceXtendScript)
    {
        if (active)
        {
            scriptImporterImpl.importUserScript( sourceXtendScript );
        }
    }

    @Override
    public void importPreinstalledScript(File sourceXtendScript)
    {
        if (active)
        {
            scriptImporterImpl.importPreinstalledScript( sourceXtendScript );
        }
    }

    @Override
    public void addListener(ImportScriptStatusListener scriptImportStatusListener)
    {
        if (active)
        {
            scriptImporterImpl.addListener( scriptImportStatusListener );
        }
    }

    @Override
    public void removeListener(ImportScriptStatusListener scriptImportStatusListener)
    {
        if (active)
        {
            scriptImporterImpl.removeListener( scriptImportStatusListener );
        }
    }

    @Deactivate
    public void stop()
    {
        active = false;
    }

    public void unbindResourcesModelManager(ResourcesModelManager resourcesModelManager)
    {
        this.resourcesModelManager = null;
    }

    public void unbindRaceScriptLoader(RaceScriptLoader raceScriptLoader)
    {
        this.raceScriptLoader = null;
    }

    public void unbindScriptProjectBuilder(ScriptProjectBuilderService scriptProjectBuilder)
    {
        this.scriptProjectBuilder = null;
    }
}
