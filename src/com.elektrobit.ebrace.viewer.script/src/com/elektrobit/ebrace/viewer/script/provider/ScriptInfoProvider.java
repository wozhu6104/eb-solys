/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.provider;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ScriptInfoProvider implements ITreeContentProvider
{
    @Override
    public void dispose()
    {
        // nothing to do.
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        // nothing to do.
    }

    @Override
    public Object[] getElements(Object inputElement)
    {
        return ((List<?>)inputElement).toArray();
    }

    @Override
    public Object[] getChildren(Object parentElement)
    {
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
