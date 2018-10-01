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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class RuntimeEventChannelLabelProvider extends LabelProvider
{

    @Override
    public String getText(Object element)
    {
        if (element instanceof RuntimeEventChannel)
        {
            RuntimeEventChannel<?> runtimeEventChannel = (RuntimeEventChannel<?>)element;
            return runtimeEventChannel.getName();
        }
        return null;
    }

    @Override
    public Image getImage(Object element)
    {
        return null;
    }
}
