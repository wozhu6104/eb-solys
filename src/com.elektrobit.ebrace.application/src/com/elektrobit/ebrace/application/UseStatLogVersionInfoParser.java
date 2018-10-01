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

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.activation.api.Version;
import com.elektrobit.ebrace.core.interactor.api.common.ProVersion;
import com.elektrobit.ebrace.core.interactor.api.common.ProVersionProvider;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogParamParser;

public class UseStatLogVersionInfoParser implements UseStatLogParamParser
{
    private final Version version = new GenericOSGIServiceTracker<Version>( Version.class ).getService();

    @Override
    public String parse(Object[] args)
    {
        return "versionName:" + version.getName() + " | versionVariant:" + version.getVariant() + " | proVersion:"
                + getProVersionResult();
    }

    private String getProVersionResult()
    {
        ProVersionProvider proVersionProvider = ProVersion.getInstance();
        String proVersionResult = "NO";
        if (proVersionProvider.isActive())
        {
            proVersionResult = "YES";
        }
        return proVersionResult;
    }

}
