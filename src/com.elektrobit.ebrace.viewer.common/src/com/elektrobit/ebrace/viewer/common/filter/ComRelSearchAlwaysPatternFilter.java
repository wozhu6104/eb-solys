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

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;

import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;

public class ComRelSearchAlwaysPatternFilter extends SearchAlwaysPatternFilter
{
    @Override
    protected boolean isLeafMatch(Viewer viewer, Object element)
    {
        if (element instanceof ComRelation)
        {
            ComRelation cr = ((ComRelation)element);
            String labelText = ((ILabelProvider)((StructuredViewer)viewer).getLabelProvider()).getText( element )
                    + cr.getReceiver().getName() + cr.getSender().getName();
            return wordMatches( labelText );
        }
        return super.isLeafMatch( viewer, element );
    }

}
