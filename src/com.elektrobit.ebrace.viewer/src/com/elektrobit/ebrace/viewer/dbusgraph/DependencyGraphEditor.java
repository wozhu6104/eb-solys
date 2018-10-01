/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.dbusgraph;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.elektrobit.ebrace.viewer.common.view.ITableViewerView;
import com.elektrobit.ebrace.viewer.common.view.ToggleDecoderComposite;
import com.elektrobit.ebrace.viewer.graph.MessagesView;
import com.elektrobit.ebrace.viewer.views.StructureExplorerView;

public class DependencyGraphEditor extends EditorPart implements ToggleDecoderComposite, ITableViewerView
{
    private GraphView graphView;
    private MessagesView messagesView;
    private SashForm sashTopBottom;
    private SashForm sashLeftRight;

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
        setInput( input );
    }

    @Override
    public String getTitle()
    {
        return "Communication";
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
    public void createPartControl(Composite parent)
    {
        createSashForm( parent );
        createViews();
    }

    private void createViews()
    {
        graphView = new GraphView( sashLeftRight );
        new StructureExplorerView( sashLeftRight, getSite() );
        messagesView = new MessagesView( sashTopBottom, getSite() );

        sashLeftRight.setWeights( new int[]{70, 30} );
        sashTopBottom.setWeights( new int[]{65, 35} );
    }

    private void createSashForm(Composite parent)
    {
        sashTopBottom = new SashForm( parent, SWT.NONE | SWT.VERTICAL );
        sashTopBottom.setSashWidth( 5 );

        sashLeftRight = new SashForm( sashTopBottom, SWT.NONE );
        sashLeftRight.setSashWidth( 5 );
    }

    @Override
    public void setFocus()
    {
        graphView.setFocus();
    }

    @Override
    public void toggleDecoderComposite()
    {
        messagesView.toggleDecoderComposite();
    }

    @Override
    public ColumnViewer getTreeViewer()
    {
        return messagesView.getTreeViewer();
    }

    @Override
    public List<?> getContent()
    {
        return messagesView.getContent();
    }

}
