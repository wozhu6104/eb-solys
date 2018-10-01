/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.racescriptexecutor.api.Constants;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.viewer.script.util.GlobalScriptExecutionHelper;

public class DeleteScriptHandler extends AbstractHandler
{
    private final GenericOSGIServiceTracker<ResourcesModelManager> resourceManagerTracker = new GenericOSGIServiceTracker<ResourcesModelManager>( ResourcesModelManager.class );

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        StructuredSelection selection = (StructuredSelection)HandlerUtil.getCurrentSelection( event );
        List<?> selectedobjects = selection.toList();
        boolean confirmDelete = confirmDelete( HandlerUtil.getActiveShell( event ) );
        for (Object selectedObject : selectedobjects)
        {
            if (selectedObject instanceof RaceScriptResourceModel && confirmDelete)
            {
                RaceScriptResourceModel selectedScript = (RaceScriptResourceModel)selectedObject;
                deleteScript( selectedScript );
            }
        }
        return null;
    }

    public void deleteScript(RaceScriptResourceModel script)
    {
        try
        {
            if (script.getScriptInfo().isRunning())
            {
                GlobalScriptExecutionHelper.stopScript( script.getScriptInfo() );
            }
            deleteResourcesModelFromResourcesManager( script );
            deleteProjectFromWorkspace( script );
            deleteJarFromScriptsFolder( script );
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
    }

    private void deleteResourcesModelFromResourcesManager(RaceScriptResourceModel selectedScript)
    {
        List<ResourceModel> toBeDeleted = new ArrayList<ResourceModel>();
        toBeDeleted.add( selectedScript );
        ResourcesModelManager rsManager = resourceManagerTracker.getService();
        rsManager.deleteResourcesModels( toBeDeleted );
    }

    private void deleteProjectFromWorkspace(RaceScriptResourceModel selectedScript) throws CoreException
    {
        IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
        selectedScript.getSourceFile().delete();
        workspace.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );
    }

    private void deleteJarFromScriptsFolder(RaceScriptResourceModel selectedScript)
    {
        File scriptFile = new File( Constants.DEFAULT_PATH_TO_SCRIPT_FOLDER + File.separator + selectedScript.getName()
                + Constants.RACE_SCRIPT_EXTENTION );
        if (scriptFile.exists() && scriptFile.isFile())
        {
            scriptFile.delete();
        }
    }

    private boolean confirmDelete(Shell shell)
    {
        return MessageDialog
                .openQuestion( shell,
                               "Delete Script",
                               "Are you sure you want to delete this script? The delete operation can not be undone." );
    }
}
