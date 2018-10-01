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

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class RuntimeEventChannelModelElementFilter extends ViewerFilter
{
    ModelElement modelElement;
    RuntimeEventAcceptor runtimeEventAcceptor = new GenericOSGIServiceTracker<RuntimeEventAcceptor>( RuntimeEventAcceptor.class )
            .getService();

    public RuntimeEventChannelModelElementFilter()
    {
        // TODO Auto-generated constructor stub
    }

    public RuntimeEventChannelModelElementFilter(ModelElement modelElement)
    {
        this.modelElement = modelElement;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {
        if (this.modelElement != null && element instanceof RuntimeEventChannel)
        {
            List<RuntimeEventChannel<?>> runtimeEventChannels = runtimeEventAcceptor
                    .getRuntimeEventChannelsForModelElement( this.modelElement );
            RuntimeEventChannel<?> channel = (RuntimeEventChannel<?>)element;
            return runtimeEventChannels.contains( channel );
        }
        return false;
    }

    public void setModelElement(ModelElement modelElement)
    {
        this.modelElement = modelElement;
    }

}
