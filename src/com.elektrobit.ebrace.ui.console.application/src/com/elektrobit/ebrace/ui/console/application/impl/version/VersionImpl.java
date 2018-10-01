/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.console.application.impl.version;

import org.osgi.service.component.annotations.Component;

import com.elektrobit.ebrace.common.utils.VersionHelper;
import com.elektrobit.ebrace.core.activation.api.Version;

@Component
public class VersionImpl implements Version
{
    private static final String EBRACE_CONSOLE_APPLICATION_PLUGIN_ID = "com.elektrobit.ebrace.ui.console.application";
    private final VersionHelper versionHelper;

    public VersionImpl()
    {
        versionHelper = new VersionHelper( EBRACE_CONSOLE_APPLICATION_PLUGIN_ID );
    }

    @Override
    public String getVariant()
    {
        return versionHelper.getVariant();
    }

    @Override
    public String getName()
    {
        return versionHelper.getName();
    }

    @Override
    public String toString()
    {
        return getName() + "/" + getVariant();
    }
}
