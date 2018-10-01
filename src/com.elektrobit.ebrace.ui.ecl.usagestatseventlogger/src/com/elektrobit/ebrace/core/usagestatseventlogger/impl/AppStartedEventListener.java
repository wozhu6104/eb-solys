/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.usagestatseventlogger.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLog;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogTypes;

@Component(immediate = true, enabled = true, property = "event.topics=org/eclipse/e4/ui/LifeCycle/appShutdownStarted")
public class AppStartedEventListener implements EventHandler
{
    @UseStatLog(UseStatLogTypes.APP_SHUTDOWN_STARTED)
    @Override
    public void handleEvent(Event event)
    {
    }

}
