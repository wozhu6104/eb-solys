/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.filter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class RuntimeEventChannelTypeFilter extends ViewerFilter
{
    Class<?> assignableType;

    public RuntimeEventChannelTypeFilter(Class<?> assignableType)
    {
        this.assignableType = assignableType;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {
        if (element instanceof RuntimeEventChannel)
        {
            RuntimeEventChannel<?> channel = (RuntimeEventChannel<?>)element;
            return this.assignableType.isAssignableFrom( channel.getUnit().getDataType() );
        }
        return false;
    }

}
