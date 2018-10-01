/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources;

import java.util.List;

import org.eclipse.core.expressions.PropertyTester;

import com.elektrobit.ebrace.core.interactor.api.datainput.DataInputResourceModel;

public class SelectedDataInputReading extends PropertyTester
{

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
    {
        if (receiver instanceof List<?>)
        {
            return ((List<?>)receiver).stream().filter( entry -> entry instanceof DataInputResourceModel )
                    .anyMatch( entry -> ((DataInputResourceModel)entry).isConnected() );
        }
        return false;
    }

}
