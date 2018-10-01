/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.test.util.memory;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

import javax.management.MBeanServer;

import org.apache.log4j.Logger;

public class HeapDumper
{
    private final static Logger LOG = Logger.getLogger( HeapDumper.class );

    private static final String HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic";

    private static volatile Object hotspotMBean;

    public final static void dumpLiveHeapObjectToFile(final String filePath)
    {
        dumpHeap( filePath, true );
    }

    private static void dumpHeap(String fileName, boolean live)
    {
        initIfNot();

        deleteFileIfExists( fileName );

        try
        {
            Class<?> clazz = Class.forName( "com.sun.management.HotSpotDiagnosticMXBean" );
            Method m = clazz.getMethod( "dumpHeap", String.class, boolean.class );
            m.invoke( hotspotMBean, fileName, live );
        }
        catch (RuntimeException re)
        {
            throw re;
        }
        catch (Exception exp)
        {
            throw new RuntimeException( exp );
        }
    }

    private static void deleteFileIfExists(String fileName)
    {
        File file = new File( fileName );

        if (file.exists())
        {
            boolean wasDeleted = file.delete();
            if (!wasDeleted)
            {
                LOG.warn( "Couldn't delete file " + fileName );
            }
        }
    }

    private static void initIfNot()
    {
        if (hotspotMBean == null)
        {
            synchronized (HeapDumper.class)
            {
                if (hotspotMBean == null)
                {
                    hotspotMBean = getHotspotMBean();
                }
            }
        }
    }

    private static Object getHotspotMBean()
    {
        try
        {
            Class<?> clazz = Class.forName( "com.sun.management.HotSpotDiagnosticMXBean" );
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            Object bean = ManagementFactory.newPlatformMXBeanProxy( server, HOTSPOT_BEAN_NAME, clazz );
            return bean;
        }
        catch (RuntimeException runtimeException)
        {
            throw runtimeException;
        }
        catch (Exception otherExceptions)
        {
            throw new RuntimeException( otherExceptions );
        }
    }
}
