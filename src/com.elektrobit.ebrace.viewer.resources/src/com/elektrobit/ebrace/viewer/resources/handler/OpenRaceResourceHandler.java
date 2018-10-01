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

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.xtext.xbase.ui.editor.XbaseEditor;

import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionStateNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.viewer.resources.ScriptRenameListener;
import com.elektrobit.ebrace.viewer.resources.connection.ConnectHandler;
import com.elektrobit.ebrace.viewer.resources.util.OpenEditorUtil;

@SuppressWarnings("restriction")
public class OpenRaceResourceHandler implements ConnectionStateNotifyCallback
{
    private final String viewIdToFocus;
    private final ScriptRenameListener scriptRenameListener;
    private boolean connected;

    public OpenRaceResourceHandler(String viewIdToFocus)
    {
        this.viewIdToFocus = viewIdToFocus;
        this.scriptRenameListener = new ScriptRenameListener();
    }

    public void openResource(ResourceModel model)
    {
        if (model instanceof RaceScriptResourceModel)
        {
            openScript( (RaceScriptResourceModel)model );
        }
        else if (model instanceof ConnectionModel)
        {
            handleConnect( model );
        }
        else if (model instanceof ResourceModel)
        {
            ResourceModel rModel = model;
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            OpenEditorUtil.openResourcesEditor( page, rModel );
            setFocus();
        }
    }

    private void handleConnect(ResourceModel model)
    {
        ConnectionModel connectionModel = (ConnectionModel)model;
        if (!connected)
        {
            new ConnectHandler().connect( connectionModel );
        }
        else
        {
            if (connectionModel.isConnected())
            {
                new ConnectHandler().disconnect( connectionModel );
            }
        }
    }

    private void openScript(final RaceScriptResourceModel model)
    {
        try
        {
            IEditorPart editor = IDE.openEditor( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
                                                 model.getSourceFile().toURI(),
                                                 "org.eclipse.xtend.core.Xtend",
                                                 true );
            if (editor instanceof XbaseEditor)
            {
                scriptRenameListener.editorOpened( (XbaseEditor)editor, model );
            }
        }
        catch (PartInitException e)
        {
            e.printStackTrace();
        }
    }

    private void setFocus()
    {
        try
        {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView( viewIdToFocus );
        }
        catch (PartInitException e)
        {
            e.printStackTrace();
        }
    }

    public void onCloseEditor(IWorkbenchPart part)
    {
        if (part instanceof XbaseEditor)
        {
            XbaseEditor editor = (XbaseEditor)part;
            scriptRenameListener.editorClosed( editor );
        }
    }

    @Override
    public void onTargetDisconnected()
    {
        connected = false;
    }

    @Override
    public void onTargetConnected()
    {
        connected = true;
    }

    @Override
    public void onTargetConnecting()
    {
        connected = true;
    }

}
