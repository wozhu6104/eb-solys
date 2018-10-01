/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.browser;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.elektrobit.ebrace.core.interactor.api.browsercontent.HtmlViewChangedCallback;
import com.elektrobit.ebrace.core.interactor.api.browsercontent.HtmlViewNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlViewModel;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ModelNameNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ModelNameNotifyUseCase;
import com.elektrobit.ebrace.viewer.resources.editor.ResourcesModelEditorInput;

import lombok.extern.log4j.Log4j;

@Log4j
public class BrowserEditor extends EditorPart implements HtmlViewChangedCallback, ModelNameNotifyCallback
{

    public static final String PLUGIN_ID = "com.elektrobit.ebrace.ui.ecl.browser";

    private ResourcesModelEditorInput editorInput;

    private HtmlViewModel model;

    private Browser browser;

    private HtmlViewNotifyUseCase htmlNotifyUseCase;
    private ModelNameNotifyUseCase modelNameNotifyUseCase;

    @Override
    public void doSave(IProgressMonitor monitor)
    {
    }

    @Override
    public void doSaveAs()
    {
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        setSite( site );

        editorInput = (ResourcesModelEditorInput)input;

        setInput( editorInput );

        model = (HtmlViewModel)editorInput.getModel();
    }

    @Override
    public boolean isDirty()
    {
        return false;
    }

    @Override
    public boolean isSaveAsAllowed()
    {
        return false;
    }

    @Override
    public String getTitle()
    {
        if (editorInput != null)
        {
            return editorInput.getModel().getName();
        }
        return super.getTitle();
    }

    @Override
    public void createPartControl(Composite parent)
    {
        htmlNotifyUseCase = UseCaseFactoryInstance.get().makeHtmlViewNotifyUseCase( this );
        htmlNotifyUseCase.register( model );
        modelNameNotifyUseCase = UseCaseFactoryInstance.get().makeModelNameNotifyUseCase( this );
        modelNameNotifyUseCase.register( editorInput.getModel() );

        browser = new Browser( parent, SWT.NONE );

        new JumpToTimeMarkerFunction( browser, "jumpToTimeMarker" );
        new CallScriptFromJS( browser, "callSolysScript" );
        updateURI( model.getURL() );
    }

    private void updateURI(String htmlPageUrl)
    {
        try
        {
            URL url = new URL( htmlPageUrl );
            browser.setUrl( url.toURI().toString() );
        }
        catch (URISyntaxException | MalformedURLException e)
        {
            log.warn( "Couldn't find browser url!", e );
        }
    }

    @Override
    public void setFocus()
    {
    }

    @Override
    public void dispose()
    {
        htmlNotifyUseCase.unregister();
        modelNameNotifyUseCase.unregister();
        super.dispose();
    }

    @Override
    public void onContentChanged()
    {
        updateURI( model.getURL() );
    }

    @Override
    public void onNewResourceName(String newName)
    {
        firePropertyChange( PROP_TITLE );
    }

    @Override
    public void onResourceDeleted()
    {
        getSite().getPage().closeEditor( this, false );
    }

    @Override
    public void onJavaScriptFunctionRequested(String function, String arg)
    {
        String call = function + "(" + arg + ");";
        browser.execute( call );
    }

}
