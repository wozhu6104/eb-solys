/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.views;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;

class TableTreeToolTipSupport extends ColumnViewerToolTipSupport
{
    public final static void enableFor(ColumnViewer viewer, int style)
    {
        if (viewer == null)
        {
            throw new IllegalArgumentException( "TableViewer can't be null." );
        }
        new TableTreeToolTipSupport( viewer, style, false );
    }

    private TableTreeToolTipSupport(ColumnViewer viewer, int style, boolean manualActivation)
    {
        super( viewer, style, manualActivation );
    }

    @Override
    public boolean isHideOnMouseDown()
    {
        return true;
    }
}
