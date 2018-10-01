/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.impl.uimodels;

import java.util.List;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.resources.model.timelineview.TimelineViewModel;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;
import com.elektrobit.ebsolys.script.external.Console;
import com.elektrobit.ebsolys.script.external.STimelineView;

public class STimelineViewImpl extends SBaseResourceImpl<TimelineViewModel, STimelineView> implements STimelineView
{
    private static final String ADD_CHANNELS_MISMATCH_MESSAGE = "ERROR: All channels in Timeline View must have a unit Unit.TIMESEGMENT";;
    private final Console scriptConsole;

    public STimelineViewImpl(TimelineViewModel model, Console scriptConsole,
            ResourcesModelManager resourcesModelManager)
    {
        super( model, resourcesModelManager );
        RangeCheckUtils.assertReferenceParameterNotNull( "scriptConsole", scriptConsole );
        this.scriptConsole = scriptConsole;
    }

    @Override
    protected STimelineView getThis()
    {
        return this;
    }

    @Override
    protected boolean canChannelsBeAddedToView(List<RuntimeEventChannel<?>> channels)
    {
        boolean channelsMatchType = channelsMatchType( channels );
        if (!channelsMatchType)
        {
            scriptConsole.println( ADD_CHANNELS_MISMATCH_MESSAGE );
        }
        return channelsMatchType;
    }

    private boolean channelsMatchType(List<RuntimeEventChannel<?>> channels)
    {
        for (RuntimeEventChannel<?> channel : channels)
        {
            if (!channel.getUnit().equals( Unit.TIMESEGMENT ))
            {
                return false;
            }
        }
        return true;
    }
}
