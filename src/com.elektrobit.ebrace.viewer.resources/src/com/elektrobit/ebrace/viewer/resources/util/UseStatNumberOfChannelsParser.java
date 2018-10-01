/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.util;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogParamParser;

public class UseStatNumberOfChannelsParser implements UseStatLogParamParser
{

    @Override
    public String parse(Object[] args)
    {
        if (args.length == 2 && args[1] instanceof ResourceModel)
        {
            ResourceModel model = (ResourceModel)args[1];
            return "" + model.getChannels().size();
        }
        return null;
    }

}
