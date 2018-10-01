/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.snapshot.handler;

import org.eclipse.swt.widgets.Shell;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.viewer.resources.dialog.EditResourceModelNameDialog;
import com.elektrobit.ebrace.viewer.resources.handler.EditResourcesModelHandler;
import com.elektrobit.ebrace.viewer.snapshot.dialog.SnapshotTableDialog;

public class EditSnapshotTableHandler extends EditResourcesModelHandler
{
    @Override
    protected EditResourceModelNameDialog createDialog(Shell activeShell, ResourceModel selectedModel)
    {
        return new SnapshotTableDialog( activeShell, selectedModel );
    }
}
