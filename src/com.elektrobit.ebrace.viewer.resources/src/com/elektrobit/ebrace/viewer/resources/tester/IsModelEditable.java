/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.tester;

import java.util.List;

import org.eclipse.core.expressions.PropertyTester;

import com.elektrobit.ebrace.core.interactor.api.resources.model.EditRight;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;

public class IsModelEditable extends PropertyTester
{
    private final String PROPERTY_NAME = "isEditable";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        Boolean allEditable = null;
        if (PROPERTY_NAME.equals( property ))
        {
            if (receiver instanceof List<?>)
            {
                List<?> selected = (List<?>)receiver;
                for (Object selectedObject : selected)
                {
                    if (selectedObject instanceof ResourceModel)
                    {
                        ResourceModel model = (ResourceModel)selectedObject;
                        boolean editable = model.getEditRight() == EditRight.EDITABLE;
                        if (allEditable == null)
                            allEditable = editable;
                        else
                            allEditable = allEditable && editable;
                    }
                }
            }
        }
        return allEditable == null ? false : allEditable;
    }

}
