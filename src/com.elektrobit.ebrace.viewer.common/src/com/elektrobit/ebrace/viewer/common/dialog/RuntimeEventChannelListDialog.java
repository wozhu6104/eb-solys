/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.elektrobit.ebrace.viewer.common.swt.FilteredRuntimeEventChannelListComposite;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class RuntimeEventChannelListDialog extends BaseSaveSettingsDialog
{
    private static final String DEFAULT_SHELL_TEXT = "Runtime event channel list";
    private static final String DEFAULT_TEXT = "Please select a runtime event channel.";
    private static final String TITLE = "Runtime event channel list";

    TableViewer channels;
    List<RuntimeEventChannel<?>> selectedChannels = new ArrayList<RuntimeEventChannel<?>>();

    public RuntimeEventChannelListDialog(Shell parentShell)
    {
        super( parentShell );
    }

    @Override
    protected Control createContents(Composite parent)
    {
        Composite container = new Composite( parent, SWT.BORDER );
        container.setLayout( new GridLayout() );
        container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        super.createContents( container );
        Composite content = new Composite( (Composite)getDialogArea(), SWT.NONE );
        content.setLayout( new GridLayout() );
        content.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        FilteredRuntimeEventChannelListComposite composite = new FilteredRuntimeEventChannelListComposite( content,
                                                                                                           SWT.CHECK );
        composite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        this.channels = composite.getViewer();
        getButton( IDialogConstants.OK_ID ).setEnabled( false );
        setTitle( TITLE );
        setMessage( DEFAULT_TEXT );
        getShell().setText( DEFAULT_SHELL_TEXT );
        registerListener();
        return container;
    }

    private void registerListener()
    {
        this.channels.addSelectionChangedListener( new ISelectionChangedListener()
        {

            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                getButton( IDialogConstants.OK_ID ).setEnabled( !channels.getSelection().isEmpty() );
                selectedChannels.clear();
                StructuredSelection sel = (StructuredSelection)event.getSelection();
                for (Object o : sel.toList())
                {
                    if (o instanceof RuntimeEventChannel<?>)
                    {
                        selectedChannels.add( (RuntimeEventChannel<?>)o );
                    }
                }
            }
        } );
        this.channels.addDoubleClickListener( new IDoubleClickListener()
        {

            @Override
            public void doubleClick(DoubleClickEvent event)
            {
                close();
            }
        } );
    }

    public List<RuntimeEventChannel<?>> getSelectedChannels()
    {
        return selectedChannels;
    }
}
