/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.application;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCaller;
import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCommand;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLog;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogTypes;
import com.elektrobit.ebsolys.core.targetdata.api.reset.StartupDoneListener;

@Component(immediate = true, enabled = true, property = "event.topics=org/eclipse/e4/ui/LifeCycle/appStartupComplete")
public class ApplicationStartedNotifier implements EventHandler
{

    @UseStatLog(UseStatLogTypes.APP_STARTUP_FINISHED)
    @Override
    public void handleEvent(Event event)
    {
        notifyWorkbenchStarted();
    }

    private void notifyWorkbenchStarted()
    {
        new OSGIWhiteBoardPatternCaller<StartupDoneListener>( StartupDoneListener.class )
                .callOSGIService( new OSGIWhiteBoardPatternCommand<StartupDoneListener>()
                {
                    @Override
                    public void callOSGIService(StartupDoneListener service)
                    {
                        service.onApplicationStarted();
                    }
                } );
    }

}
