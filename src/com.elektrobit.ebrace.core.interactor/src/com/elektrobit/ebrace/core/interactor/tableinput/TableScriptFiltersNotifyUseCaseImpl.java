/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.tableinput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elektrobit.ebrace.core.interactor.api.common.UIExecutor;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScript;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.table.TableScriptFiltersNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.table.TableScriptFiltersNotifyUseCase;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptInfoChangedListener;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptLoader;
import com.elektrobit.ebrace.resources.api.manager.ResourceTreeChangedListener;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;

public class TableScriptFiltersNotifyUseCaseImpl
        implements
            TableScriptFiltersNotifyUseCase,
            RaceScriptInfoChangedListener,
            ResourceTreeChangedListener
{
    private final Map<String, List<RaceScriptMethod>> scriptNamesToMethodsMap = new HashMap<String, List<RaceScriptMethod>>();
    private TableScriptFiltersNotifyCallback callback;
    private final ResourcesModelManager modelManager;
    private final RaceScriptLoader raceScriptLoader;

    public TableScriptFiltersNotifyUseCaseImpl(TableScriptFiltersNotifyCallback callback,
            ResourcesModelManager modelManager, RaceScriptLoader raceScriptLoader)
    {
        this.callback = callback;
        this.modelManager = modelManager;
        this.raceScriptLoader = raceScriptLoader;
        modelManager.registerTreeListener( this );
        raceScriptLoader.registerRaceScriptChangedListener( this );

        collectAllCurrentFilterMethods();
        postCurrentValuesToCallback();
    }

    private void collectAllCurrentFilterMethods()
    {
        List<RaceScriptResourceModel> allScripts = modelManager.getAllScripts();
        for (RaceScriptResourceModel script : allScripts)
        {
            List<RaceScriptMethod> filterMethods = script.getScriptInfo().getFilterMethods();
            List<RaceScriptMethod> methodsList = new ArrayList<RaceScriptMethod>( filterMethods );
            scriptNamesToMethodsMap.put( script.getName(), methodsList );
        }
    }

    private void postCurrentValuesToCallback()
    {
        final List<RaceScriptMethod> allFilterMethods = new ArrayList<RaceScriptMethod>();

        for (List<RaceScriptMethod> methodsInMap : scriptNamesToMethodsMap.values())
        {
            allFilterMethods.addAll( methodsInMap );
        }

        UIExecutor.post( new Runnable()
        {
            @Override
            public void run()
            {
                if (callback != null)
                {
                    callback.onScriptFilterMethodsChanged( allFilterMethods );
                }
            }
        } );
    }

    @Override
    public void filterMethodsChanged(RaceScript script, List<RaceScriptMethod> filterMethods)
    {
        ArrayList<RaceScriptMethod> newFilterMethodsCopy = new ArrayList<RaceScriptMethod>( filterMethods );
        scriptNamesToMethodsMap.put( script.getName(), newFilterMethodsCopy );

        postCurrentValuesToCallback();
    }

    @Override
    public void unregister()
    {
        raceScriptLoader.unregisterRaceScriptChangedListener( this );
        callback = null;
    }

    @Override
    public void onResourceDeleted(ResourceModel resourceModel)
    {
        if (resourceModel instanceof RaceScriptResourceModel)
        {
            removeScriptMethodAndPostNewData( (RaceScriptResourceModel)resourceModel );
        }
    }

    private void removeScriptMethodAndPostNewData(RaceScriptResourceModel script)
    {
        String scriptName = script.getName();
        if (scriptNamesToMethodsMap.containsKey( scriptName ))
        {
            scriptNamesToMethodsMap.remove( scriptName );
            postCurrentValuesToCallback();
        }
    }

    @Override
    public void onResourceRenamed(ResourceModel resourceModel)
    {
    }

    @Override
    public void scriptInfoChanged(RaceScript script)
    {
    }

    @Override
    public void onResourceTreeChanged()
    {
    }

    @Override
    public void onResourceAdded(ResourceModel resourceModel)
    {
    }

    @Override
    public void onOpenResourceModel(ResourceModel resourceModel)
    {
    }
}
