/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.handler;

import java.util.List;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.viewer.common.view.ITableViewerView;

public class IsAnyTableContent extends PropertyTester
{
    final String PROPERTY_NAME = "isAnyTableContent";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        if (PROPERTY_NAME.equals( property ))
        {
            if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null
                    && PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() != null)
            {
                IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .getActivePart();
                if (part instanceof ITableViewerView)
                {
                    List<?> content = ((ITableViewerView)part).getContent();
                    return (content != null) && !content.isEmpty();
                }
            }
            return false;
        }
        return false;
    }
}
