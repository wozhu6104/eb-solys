/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.timemarker.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.IEvaluationService;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.selectelement.SelectElementsInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.selectelement.SelectElementsInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.tableinput.DataCollector;
import com.elektrobit.ebrace.core.interactor.api.tableinput.FilteredTableInputNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.tableinput.FilteredTableNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermNotifyUseCase;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLog;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogTypes;
import com.elektrobit.ebrace.viewer.common.provider.TimeMarkerLabelProvider;
import com.elektrobit.ebrace.viewer.common.provider.TimestampColumnLabelProvider;
import com.elektrobit.ebrace.viewer.common.swt.CommonFilteredTable;
import com.elektrobit.ebrace.viewer.common.swt.SearchComboListener;
import com.elektrobit.ebrace.viewer.common.timemarker.dnd.RuntimeeventTimestampDropTargetAdapter;
import com.elektrobit.ebrace.viewer.common.timemarker.edit.NameEditingSupport;
import com.elektrobit.ebrace.viewer.common.timemarker.listener.DeleteTimeMarkerDoubleClickListener;
import com.elektrobit.ebrace.viewer.common.transfer.RuntimeEventChannelTransfer;
import com.elektrobit.ebrace.viewer.common.transfer.RuntimeEventTimstampTransfer;
import com.elektrobit.ebrace.viewer.common.view.ITableViewerView;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkersChangedListener;

public class TimeMarkersView extends ViewPart
        implements
            ITableViewerView,
            TimeMarkersChangedListener,
            FilteredTableNotifyCallback,
            SearchComboListener,
            TableSearchTermNotifyCallback,
            SelectElementsInteractionCallback
{
    private final TimeMarkerManager timeMarkerManager = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class )
            .getService();

    private final String TOOLBAR_ID = "com.elektrobit.ebrace.viewer.timemarkermanager.toolbar";
    private final String CONTEXT_MENU_ID = "com.elektrobit.ebrace.viewer.timemarkermanager.contextmenu";

    private CommonFilteredTable filteredTable;
    private Composite container;
    private DeleteTimeMarkerDoubleClickListener deleteTimeMarkerDoubleClickListener;

    private FilteredTableInputNotifyUseCase filteredTableInputNotifyUseCase;
    private final List<RowFormatter> columnsToApplyFilter = new ArrayList<RowFormatter>();

    private TimestampColumnLabelProvider timestampColumnLabelProvider;
    private TableViewerColumn timestampColumn;
    private TableSearchTermInteractionUseCase searchTermInteractionUseCase;
    private TableSearchTermNotifyUseCase searchTermNotifyUseCase;
    private List<String> searchTerms = Collections.emptyList();
    private SelectElementsInteractionUseCase selectElementsInteractionUseCase;

    private void addMouseListener()
    {
        filteredTable.getViewer().getTable().addMouseListener( new MouseListener()
        {

            @Override
            public void mouseUp(MouseEvent e)
            {
            }

            @Override
            public void mouseDown(MouseEvent e)
            {
                toggleSelectedResources();
            }

            @Override
            public void mouseDoubleClick(MouseEvent e)
            {

            }
        } );
    }

    private void toggleSelectedResources()
    {

        IStructuredSelection selection = (IStructuredSelection)filteredTable.getViewer().getSelection();
        List<TimebasedObject> rEvent = new ArrayList<TimebasedObject>();

        for (Object obj : selection.toList())
        {
            if (obj instanceof TimeMarker)
            {
                rEvent.add( (TimebasedObject)obj );
            }
        }

        if (!rEvent.isEmpty())
        {
            selectElementsInteractionUseCase.selectedResource( rEvent );
        }
    }

    @Override
    public void createPartControl(Composite parent)
    {
        createTableViewerBuilder( parent );
        createTableColumns();

        createViewContextMenu( filteredTable.getViewer().getTable(), CONTEXT_MENU_ID );
        registerListeners();
        getSite().setSelectionProvider( filteredTable.getViewer() );
        updateTableInput();
        addMouseListener();
    }

    private void registerListeners()
    {
        deleteTimeMarkerDoubleClickListener = new DeleteTimeMarkerDoubleClickListener();
        filteredTable.getViewer().addDoubleClickListener( deleteTimeMarkerDoubleClickListener );
        timeMarkerManager.registerListener( this );
        filteredTable.registerComboListener( this );

        String tableSearchTermsID = this.getClass().getCanonicalName();
        searchTermInteractionUseCase = UseCaseFactoryInstance.get()
                .makeTableSearchTermInteractionUseCase( tableSearchTermsID );
        searchTermNotifyUseCase = UseCaseFactoryInstance.get().makeTableSearchTermNotifyUseCase( this,
                                                                                                 tableSearchTermsID );
        selectElementsInteractionUseCase = UseCaseFactoryInstance.get().makeSelectElementsInteractionUseCase( this );
    }

    private void createTableColumns()
    {
        TimeMarkerLabelProvider nameColomnLabelProvider = new TimeMarkerLabelProvider();
        columnsToApplyFilter.add( nameColomnLabelProvider );
        TableViewerColumn nameColumn = filteredTable.createColumn( "Name", nameColomnLabelProvider );
        addNameColumnEditingSupport( nameColumn );

        timestampColumnLabelProvider = new TimestampColumnLabelProvider( filteredTable.getViewer() );
        timestampColumn = filteredTable.createColumn( "Timestamp", timestampColumnLabelProvider );
        timestampColumn.setEditingSupport( new NameEditingSupport( filteredTable.getViewer() ) );
    }

    private void addNameColumnEditingSupport(TableViewerColumn nameColumn)
    {
        nameColumn.setEditingSupport( new NameEditingSupport( filteredTable.getViewer() ) );
        ColumnViewerEditorActivationStrategy activationSupport = new ColumnViewerEditorActivationStrategy( filteredTable
                .getViewer() )
        {
            @Override
            protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event)
            {
                // Enable editor only with mouse double click
                if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
                        || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION)
                {
                    return false;
                }

                if (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED)
                {
                    return false;
                }
                return true;
            }
        };
        TableViewerEditor.create( filteredTable.getViewer(),
                                  activationSupport,
                                  ColumnViewerEditor.TABBING_HORIZONTAL
                                          | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                                          | ColumnViewerEditor.TABBING_VERTICAL
                                          | ColumnViewerEditor.KEYBOARD_ACTIVATION );

    }

    private void createTableViewerBuilder(Composite parent)
    {
        container = new Composite( parent, SWT.BORDER );
        container.setLayout( new GridLayout() );
        container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        filteredTable = new CommonFilteredTable( container,
                                                 SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER
                                                         | SWT.FULL_SELECTION | SWT.VIRTUAL,
                                                 TOOLBAR_ID );

        DataCollector dataCollector = new DataCollector()
        {
            @Override
            public List<Object> collectData()
            {
                return new ArrayList<Object>( timeMarkerManager.getAllTimeMarkers() );
            }
        };

        filteredTableInputNotifyUseCase = UseCaseFactoryInstance.get()
                .makeFilteredTableInputNotifyUseCase( this, dataCollector, columnsToApplyFilter );
        addDropListener();
    }

    @Override
    public void onInputChanged(List<Object> inputList)
    {
        filteredTable.setInput( inputList );
    }

    private void addDropListener()
    {
        DropTarget dropTarget = new DropTarget( filteredTable.getViewer().getTable(), DND.DROP_COPY | DND.DROP_MOVE );
        Transfer[] transferTypes = new Transfer[]{RuntimeEventTimstampTransfer.getInstance(),
                RuntimeEventChannelTransfer.getInstance()};
        dropTarget.setTransfer( transferTypes );
        dropTarget.addDropListener( new RuntimeeventTimestampDropTargetAdapter() );
    }

    @Override
    public void setFocus()
    {
        filteredTable.setFocus();
    }

    private void createViewContextMenu(Composite menuOwner, String id)
    {
        // Create menu manager.
        MenuManager menuMgr = new MenuManager();
        // Create menu.
        Menu menu = menuMgr.createContextMenu( menuOwner );
        menuOwner.setMenu( menu );
        // Register menu for extension.
        getSite().registerContextMenu( id, menuMgr, filteredTable.getViewer() );
        IEvaluationService evServ = PlatformUI.getWorkbench().getActiveWorkbenchWindow()

                .getService( IEvaluationService.class );
        evServ.requestEvaluation( "com.elektrobit.ebrace.viewer.common.timemarker.isTimeMarkerVisible.isTimeMarkerVisible" );

    }

    private void updateTableInput()
    {
        filteredTableInputNotifyUseCase.collectAndPostNewData();
    }

    @Override
    public void dispose()
    {
        unregisterListeneres();
        super.dispose();
    }

    private void unregisterListeneres()
    {
        filteredTableInputNotifyUseCase.unregister();
        filteredTable.getViewer().removeDoubleClickListener( deleteTimeMarkerDoubleClickListener );
        timeMarkerManager.unregisterListener( this );
        searchTermNotifyUseCase.unregister();
        searchTermInteractionUseCase.unregister();
    }

    @Override
    public ColumnViewer getTreeViewer()
    {
        return this.filteredTable.getViewer();
    }

    @Override
    public List<?> getContent()
    {
        return new ArrayList<Object>( timeMarkerManager.getAllTimeMarkers() );
    }

    @UseStatLog(UseStatLogTypes.TIME_MARKER_CREATED)
    @Override
    public void newTimeMarkerCreated(TimeMarker timeMarker)
    {
        updateTableInput();
    }

    @UseStatLog(UseStatLogTypes.TIME_MARKER_REMOVED)
    @Override
    public void timeMarkerRemoved(TimeMarker timeMarker)
    {
        updateTableInput();
    }

    @Override
    public void timeMarkerRenamed(TimeMarker timeMarker)
    {
        updateTableInput();
    }

    @Override
    public void timeMarkerSelected(TimeMarker timeMarker)
    {
        updateTableInput();
    }

    @Override
    public void timeMarkerVisibilityChanged(TimeMarker timeMarker)
    {
        updateTableInput();
    }

    @UseStatLog(UseStatLogTypes.ALL_TIME_MARKERS_REMOVED)
    @Override
    public void allTimeMarkersRemoved()
    {
        updateTableInput();
    }

    @Override
    public void allTimeMarkersVisibilityToggled()
    {
        updateTableInput();
    }

    @Override
    public void timeMarkerNameChanged(TimeMarker timeMarker)
    {
        updateTableInput();
    }

    @Override
    public void timeMarkerTimestampChanged(TimeMarker timeMarker)
    {
        updateTableInput();
    }

    @Override
    public void onTextEntered(String text)
    {
        searchTermInteractionUseCase.addSearchTerm( text );
        filteredTableInputNotifyUseCase.setFilterText( text );
    }

    @Override
    public void onComboItemSelected(int index)
    {
        String selectedTerm = searchTerms.get( index );
        filteredTableInputNotifyUseCase.setFilterText( selectedTerm );
    }

    @Override
    public void onClearComboSelected()
    {
        searchTermInteractionUseCase.deleteAllTerms();
    }

    @Override
    public void onSearchTermsChanged(List<String> searchTerms)
    {
        this.searchTerms = searchTerms;
        filteredTable.setComboContent( searchTerms );
    }
}
