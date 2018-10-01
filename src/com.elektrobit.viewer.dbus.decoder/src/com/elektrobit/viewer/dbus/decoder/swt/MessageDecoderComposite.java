/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.viewer.dbus.decoder.swt;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PatternFilter;

import com.elektrobit.ebrace.core.interactor.api.runtimeeventdecoder.RuntimeEventDecoderNotifyCallback;
import com.elektrobit.ebrace.viewer.common.provider.ChannelValueProvider;
import com.elektrobit.ebrace.viewer.common.swt.CommonFilteredTree;
import com.elektrobit.ebrace.viewer.common.swt.SearchInAllColumnsPatternFilter;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.decoder.common.services.DecoderService;
import com.elektrobit.ebsolys.decoder.common.services.DecoderServiceManagerImpl;
import com.elektrobit.viewer.dbus.decoder.providers.DecodedModelContentProvider;
import com.elektrobit.viewer.dbus.decoder.providers.DecodedModelLabelProvider;
import com.elektrobit.viewer.dbus.decoder.providers.DecodedValueLabelProvider;

public class MessageDecoderComposite extends Composite implements RuntimeEventDecoderNotifyCallback
{
    private static final String FILTERED_TREE_COLUMN_NAME = "Name";
    private static final String FILTERED_TREE_COLUMN_VALUE = "Value";
    private static final String SEARCHBAR_DEFAULT_TEXT = "Search..";

    protected CommonFilteredTree filteredTree;
    protected Text headerTextField;
    private Text summaryTextField;
    private final GridData layoutData;
    private SashForm sash;
    private boolean showSearchAndDecoder = true;

    public MessageDecoderComposite(Composite parent, int style)
    {
        this( parent, style, true );
    }

    public MessageDecoderComposite(Composite parent, int style, boolean showSearchAndDecoder)
    {
        super( parent, style );
        this.showSearchAndDecoder = showSearchAndDecoder;

        setLayout( new GridLayout() );
        layoutData = new GridData( SWT.FILL, SWT.FILL, true, true );
        setLayoutData( layoutData );
        if (showSearchAndDecoder)
        {
            headerTextField = new Text( this, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP );
            headerTextField.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
            sash = new SashForm( this, SWT.VERTICAL );
            sash.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
            sash.setLayout( new GridLayout() );
        }
        createFilteredTree( parent );
        registerListeners();
    }

    private void createFilteredTree(Composite parent)
    {
        int styleForTree = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION;
        PatternFilter filter = new SearchInAllColumnsPatternFilter();
        if (showSearchAndDecoder)
        {
            filteredTree = new CommonFilteredTree( sash, styleForTree, filter );
            summaryTextField = new Text( sash, SWT.BORDER | SWT.READ_ONLY | SWT.CANCEL | SWT.MULTI | SWT.V_SCROLL );
            summaryTextField.setLayoutData( new GridData( GridData.FILL_HORIZONTAL ) );
        }
        else
        {
            filteredTree = new CommonFilteredTree( this, styleForTree, filter );
        }

        filteredTree.setInitialText( SEARCHBAR_DEFAULT_TEXT );
        filteredTree.getViewer().getTree().setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        filteredTree.getViewer().setContentProvider( new DecodedModelContentProvider() );
        createColumns();
    }

    protected void createColumns()
    {
        filteredTree.createColumn( FILTERED_TREE_COLUMN_NAME, new DecodedModelLabelProvider() );
        filteredTree.createColumn( FILTERED_TREE_COLUMN_VALUE, new DecodedValueLabelProvider() );
    }

    private void updateDecoderView(DecodedRuntimeEvent event, String summaryText)
    {
        filteredTree.setInput( event );
        headerTextField.setText( event.getSummary() );
        summaryTextField.setText( summaryText );
        layout();
    }

    public void clearDecoderView()
    {
        filteredTree.setInput( null );
        headerTextField.setText( "" );
        summaryTextField.setText( "" );
        layout();
    }

    private void setSummaryTextFieldValueForNode(Object first)
    {
        if (first != null)
        {
            if (first instanceof DecodedNode)
            {
                summaryTextField.setText( ((DecodedNode)first).getSummaryValue() );
            }
            else
            {
                if (first instanceof ChannelValueProvider)
                {
                    if (!((ChannelValueProvider)first).getNodes().isEmpty())
                    {
                        summaryTextField.setText( ((ChannelValueProvider)first).getNodes().get( 0 ).getSummaryValue() );
                    }
                    else
                    {
                        summaryTextField.setText( "" );
                    }
                }
            }
        }
        else
        {
            summaryTextField.setText( "" );
        }
        layout();
    }

    private void registerListeners()
    {
        filteredTree.getViewer().addSelectionChangedListener( new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                Object first = selection.getFirstElement();
                if (showSearchAndDecoder)
                {
                    setSummaryTextFieldValueForNode( first );
                }
            }
        } );
    }

    public boolean toggleAndReturnExclude()
    {
        layoutData.exclude = !layoutData.exclude;
        setVisible( !layoutData.exclude );
        return layoutData.exclude;
    }

    public CommonFilteredTree getFilteredTree()
    {
        return filteredTree;
    }

    @Override
    public void onRuntimeEventDecoded(DecodedRuntimeEvent event)
    {
        String summaryText = "";
        DecodedTree decodedTree = event.getDecodedTree();
        if (decodedTree != null)
        {
            DecodedNode rootNode = decodedTree.getRootNode();
            if (rootNode != null)
            {
                List<DecodedNode> decodedNodes = rootNode.getChildren();
                if (!decodedNodes.isEmpty())
                {
                    summaryText = decodedNodes.get( 0 ).getSummaryValue();
                }
            }
        }
        updateDecoderView( event, summaryText );
    }

    public void decodeRuntimeEvent(RuntimeEvent<?> selectedEvent)
    {
        DecoderService decoderService = DecoderServiceManagerImpl.getInstance()
                .getDecoderServiceForEvent( selectedEvent );
        if (decoderService != null)
        {
            DecodedRuntimeEvent decodedRuntimeEvent = decoderService.decode( selectedEvent );

            String summaryText = "";
            List<DecodedNode> decodedNodes = decodedRuntimeEvent.getDecodedTree().getRootNode().getChildren();
            if (!decodedNodes.isEmpty())
            {
                summaryText = decodedNodes.get( 0 ).getSummaryValue();
            }
            updateDecoderView( decodedRuntimeEvent, summaryText );
        }
        else
        {
            clearDecoderView();
        }
    }

}
