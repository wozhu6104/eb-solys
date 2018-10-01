/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.swt.dialog;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;

import com.elektrobit.ebrace.viewer.common.filter.RuntimeEventChannelTypeFilter;

public class ColorListSelectionDialog extends ListSelectionDialog
{
    Class<?> classType;

    public ColorListSelectionDialog(Shell parentShell, Object input, IStructuredContentProvider contentProvider,
            LabelProvider labelProvider, String message)
    {
        super( parentShell, input, contentProvider, labelProvider, message );

    }

    @Override
    protected Control createDialogArea(final Composite parent)
    {
        Control dialogControl = super.createDialogArea( parent );
        getViewer().addFilter( new RuntimeEventChannelTypeFilter( classType ) );
        return dialogControl;
    }

    public void setFilterType(Class<?> classType)
    {
        this.classType = classType;
    }
}
