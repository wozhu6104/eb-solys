/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

/**
 * Content Provider which returns the list with registered runtime event channels. If we have a treeview or a table
 * viewer which has to show the runtime event channels this is the content provider to use.
 */

public class RuntimeEventChannelContentProvider implements ITreeContentProvider
{

    @Override
    public void dispose()
    {
        // nothing to dispose.

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        // nothing to do.

    }

    @Override
    public Object[] getElements(Object inputElement)
    {
        return getChildren( inputElement );
    }

    @Override
    public Object[] getChildren(Object parentElement)
    {
        if (parentElement instanceof String || parentElement instanceof ArrayList<?>)
        {
            RuntimeEventAcceptor runtimeEventAcceptor = new GenericOSGIServiceTracker<RuntimeEventAcceptor>( RuntimeEventAcceptor.class )
                    .getService();
            if (runtimeEventAcceptor == null)
            {
                return Collections.emptyList().toArray();
            }
            return ((Collection<RuntimeEventChannel<?>>)runtimeEventAcceptor.getRuntimeEventChannels()).toArray();
        }
        return Collections.emptyList().toArray();
    }

    @Override
    public Object getParent(Object element)
    {
        return null;
    }

    @Override
    public boolean hasChildren(Object element)
    {
        return false;
    }

}
