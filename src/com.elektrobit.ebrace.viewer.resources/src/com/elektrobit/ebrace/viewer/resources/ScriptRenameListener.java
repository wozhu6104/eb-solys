/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.xtext.xbase.ui.editor.XbaseEditor;

import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;

@SuppressWarnings("restriction")
public class ScriptRenameListener implements IPropertyListener, CreateResourceInteractionCallback
{
    private final Map<XbaseEditor, RaceScriptResourceModel> openEditors = new HashMap<>();

    public ScriptRenameListener()
    {
    }

    public void editorOpened(XbaseEditor editor, RaceScriptResourceModel model)
    {
        openEditors.put( editor, model );
        editor.addPropertyListener( this );
    }

    @Override
    public void propertyChanged(Object source, int propId)
    {
        if (propId == IEditorPart.PROP_INPUT)
        {
            if (source instanceof XbaseEditor)
            {
                XbaseEditor xbaseEditor = (XbaseEditor)source;
                if (xbaseEditor.getEditorInput() instanceof IFileEditorInput)
                {
                    // FIXME rage2903 : Should rebuild be triggered this way?
                    // Rebuild must be triggered, otherwise Jar are not generated after rename of Xtend file
                    try
                    {
                        ResourcesPlugin.getWorkspace().build( IncrementalProjectBuilder.INCREMENTAL_BUILD, null );
                    }
                    catch (CoreException e)
                    {
                        e.printStackTrace();
                    }
                }

            }
        }

    }

    public void editorClosed(XbaseEditor editor)
    {
        editor.removePropertyListener( this );
        openEditors.remove( editor );
    }

    @Override
    public void onChartChannelsTypeMismatch()
    {
    }

    @Override
    public void onDerivedResourceAlreadyExists()
    {
    }

    @Override
    public void onProVersionNotAvailable()
    {
    }

}
