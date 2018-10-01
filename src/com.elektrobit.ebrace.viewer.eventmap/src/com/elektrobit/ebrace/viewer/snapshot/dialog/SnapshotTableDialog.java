/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.snapshot.dialog;

import org.eclipse.swt.widgets.Shell;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.viewer.resources.dialog.EditResourceModelChannelDialog;

public class SnapshotTableDialog extends EditResourceModelChannelDialog
{

    public SnapshotTableDialog(Shell parentShell, ResourceModel modelToEdit)
    {
        super( parentShell, modelToEdit );
    }

    @Override
    protected String getResourceModelSimpleName()
    {
        return "Snapshot";
    }

    @Override
    public void onProVersionNotAvailable()
    {
    }
}
