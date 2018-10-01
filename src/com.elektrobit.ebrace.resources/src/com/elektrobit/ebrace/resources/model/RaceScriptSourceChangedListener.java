/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.resources.model;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public abstract class RaceScriptSourceChangedListener
{
    private static final Logger LOG = Logger.getLogger( RaceScriptSourceChangedListener.class );

    abstract public void onResourceChanged();

    private volatile boolean runnablePlanned = false;

    ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor( 1 );

    public RaceScriptSourceChangedListener()
    {
        addResourceChangedListener();
    }

    private void addResourceChangedListener()
    {
        ResourcesPlugin.getWorkspace().addResourceChangeListener( new IResourceChangeListener()
        {

            @Override
            public void resourceChanged(IResourceChangeEvent event)
            {
                try
                {
                    IResourceDelta delta = event.getDelta();
                    if (delta != null)
                        delta.accept( new IResourceDeltaVisitor()
                        {

                            @Override
                            public boolean visit(IResourceDelta delta) throws CoreException
                            {
                                String name = delta.getResource().getName();

                                if (name.endsWith( ".class" ))
                                {
                                    LOG.info( "Resource changed " + name );
                                    scheduleNotificationRunnable();
                                    return false;
                                }
                                return true;
                            }
                        } );
                }
                catch (CoreException e)
                {
                    e.printStackTrace();
                }
            }
        } );
    }

    private void scheduleNotificationRunnable()
    {
        if (runnablePlanned)
            return;
        runnablePlanned = true;
        threadPoolExecutor.schedule( new Runnable()
        {
            @Override
            public void run()
            {
                waitForTreeUnlock();
                runnablePlanned = false;
                onResourceChanged();
            }
        }, 0, TimeUnit.MILLISECONDS );
    }

    private void waitForTreeUnlock()
    {
        while (ResourcesPlugin.getWorkspace().isTreeLocked())
            LOG.warn( "waiting for resource tree unlock" );
    }
}
