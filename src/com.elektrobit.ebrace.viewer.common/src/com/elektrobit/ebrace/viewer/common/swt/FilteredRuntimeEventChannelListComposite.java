/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.swt;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.elektrobit.ebrace.viewer.common.provider.RuntimeEventChannelColumnLabelProvider;
import com.elektrobit.ebrace.viewer.common.provider.RuntimeEventChannelTypeColumnLabelProvider;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class FilteredRuntimeEventChannelListComposite extends Composite
{
    private static final String CHANNEL_TYPE = "Type";
    private static final String CHANNEL_NAME = "Name";
    public static final String[] PROPS = {CHANNEL_NAME, CHANNEL_TYPE};

    private Composite container;
    private CommonFilteredTable filteredTable;
    private ResourceManager resourceManager = null;

    public FilteredRuntimeEventChannelListComposite(Composite parent, int style)
    {
        super( parent, style );
        resourceManager = new LocalResourceManager( JFaceResources.getResources() );
        setLayout( new GridLayout() );
        createPartControl( this );
    }

    private void createPartControl(Composite parent)
    {
        createFilteredTable( parent );
        createColumns();
    }

    private void createFilteredTable(Composite parent)
    {
        container = new Composite( parent, SWT.BORDER );
        container.setLayout( new GridLayout() );
        container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        filteredTable = new CommonFilteredTable( container,
                                                 SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER
                                                         | SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.CHECK,
                                                 "" );

        filteredTable.getViewer().setSorter( new ViewerSorter()
        {
            @Override
            public int compare(org.eclipse.jface.viewers.Viewer viewer, Object e1, Object e2)
            {
                if (e1 instanceof RuntimeEventChannel && e2 instanceof RuntimeEventChannel)
                {
                    RuntimeEventChannel<?> channel1 = (RuntimeEventChannel<?>)e1;
                    RuntimeEventChannel<?> channel2 = (RuntimeEventChannel<?>)e2;
                    return channel1.getUnit().getDataType().getSimpleName()
                            .compareToIgnoreCase( channel2.getUnit().getDataType().getSimpleName() );
                }
                return super.compare( viewer, e1, e2 );
            };
        } );
    }

    private void createColumns()
    {
        filteredTable.createColumn( CHANNEL_NAME, new RuntimeEventChannelColumnLabelProvider() );
        filteredTable.createColumn( CHANNEL_TYPE, new RuntimeEventChannelTypeColumnLabelProvider() );
    }

    public CommonFilteredTable getFilteredTable()
    {
        return filteredTable;
    }

    public TableViewer getViewer()
    {
        return this.filteredTable.getViewer();
    }

    @Override
    public void dispose()
    {
        if (resourceManager != null)
        {
            resourceManager.dispose();
        }
        super.dispose();
    }
}
