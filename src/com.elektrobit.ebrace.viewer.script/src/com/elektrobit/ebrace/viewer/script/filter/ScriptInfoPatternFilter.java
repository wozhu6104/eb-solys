/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.filter;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Text;

import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScript;

public class ScriptInfoPatternFilter extends ViewerFilter
{
    Text textFiled;

    public ScriptInfoPatternFilter(Text textFiled)
    {
        this.textFiled = textFiled;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element)
    {

        if (element instanceof RaceScript)
        {
            RaceScript s = (RaceScript)element;
            if (textFiled.getText().isEmpty())
            {
                return true;
            }
            return s.getName().toLowerCase().contains( textFiled.getText().toLowerCase() );
        }

        return false;
    }
}
