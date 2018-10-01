/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.console.application.impl;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator
{

    public static BundleContext bundleContext;
    public static IEclipseContext SERVICE_CONTEXT;

    @Override
    public void start(BundleContext bundleContext) throws Exception
    {
        Activator.bundleContext = bundleContext;
        Activator.SERVICE_CONTEXT = EclipseContextFactory.getServiceContext( bundleContext );

    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception
    {
    }

}
