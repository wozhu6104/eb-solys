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

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class RuntimeEventChannelTypeColumnLabelProvider extends ColumnLabelProvider implements RowFormatter
{
    @Override
    public String getText(Object element)
    {
        if (element instanceof RuntimeEventChannel<?>)
        {
            return ((RuntimeEventChannel<?>)element).getUnit().getDataType().getSimpleName();
        }
        return null;
    };
}
