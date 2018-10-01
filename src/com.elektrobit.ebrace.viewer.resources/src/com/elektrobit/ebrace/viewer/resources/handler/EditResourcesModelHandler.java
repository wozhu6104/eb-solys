/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.viewer.resources.dialog.EditResourceModelNameDialog;

public abstract class EditResourcesModelHandler extends BaseResourcesModelHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        Object o = getSelection( event );
        ResourceModel selectedModel = o instanceof ResourceModel ? (ResourceModel)o : null;
        EditResourceModelNameDialog dialog = createDialog( HandlerUtil.getActiveShell( event ), selectedModel );
        dialog.open();

        return null;
    }

    abstract protected EditResourceModelNameDialog createDialog(Shell activeShell, ResourceModel selectedModel);
}
