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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.services.ISourceProviderService;

import com.elektrobit.ebrace.common.utils.GenericListenerCaller;
import com.elektrobit.ebrace.common.utils.GenericListenerCaller.Notifier;
import com.elektrobit.ebrace.core.interactor.api.table.SEARCH_MODE;
import com.elektrobit.ebrace.core.interactor.api.tableinput.FilterChangedListener;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableData;
import com.elektrobit.ebrace.viewer.common.ViewerCommonPlugin;
import com.elektrobit.ebrace.viewer.common.constants.ActionIdConstants;
import com.elektrobit.ebrace.viewer.common.listeners.FilteredTextFieldModifyListener;
import com.elektrobit.ebrace.viewer.common.listeners.SearchTextComboHandler;
import com.elektrobit.ebrace.viewer.common.listeners.TableLockState;
import com.elektrobit.ebrace.viewer.common.util.TableLockMouseWheelListener;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;

public class CommonFilteredTable
{
    private final String toolbarId;

    private TableColumnLayout tableColumnLayout;
    private final int width = 1;
    private final boolean resizable = true;

    private TableViewer tableViewer;
    private boolean virtualTableViewer = false;
    private boolean checkboxTableViewer = false;
    private Table table;
    private boolean scrollLock = false;
    private boolean shouldShowFirst = false;

    private Composite tableComposite;

    private FilteredTextFieldModifyListener filteredTextFieldModifyListener;
    private Label resultCountLabel;
    private Composite searchFieldComposite;
    private CCombo searchTextCombo;
    private SearchNextPreviousProvider searchNextPreviousProvider;
    private TableData filterResultData;
    private ToolBarManager filterToolBar;
    private ToolBarManager toggleBackgroundColorToolBar;

    private IAction clearTextAction;
    private IAction searchRunningAction;
    private IAction toggleSearchFilterAction;
    private IAction previousFilterResultAction;
    private IAction nextFilterResultAction;

    private SearchTextComboHandler searchComboHandler;

    private final boolean isFilterSearchModeEnabled;
    private Object elementToJumpTo = null;
    private final GenericListenerCaller<SearchModeChangedListener> searchModeListeners = new GenericListenerCaller<SearchModeChangedListener>();

    public CommonFilteredTable(Composite parent)
    {
        this( parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL );
    }

    public CommonFilteredTable(Composite parent, int style)
    {
        this( parent, style, null, false );
    }

    public CommonFilteredTable(Composite parent, int style, String toolBarId)
    {
        this( parent, style, toolBarId, false );
    }

    public CommonFilteredTable(Composite parent, int style, String toolBarId, boolean isFilterSearchModeEnabled)
    {
        this.toolbarId = toolBarId;
        this.isFilterSearchModeEnabled = isFilterSearchModeEnabled;
        isTableViewerVirtual( style );
        IsCheckboxTableViewer( style );
        addSearchFieldComposite( parent );
        initializeTableViewer( parent, style );

        if (isTableLocked())
        {
            toggleScrollLock();
        }

        setSearchControlsVisible( false );
    }

    private boolean isTableLocked()
    {
        ISourceProviderService sourceProviderService = PlatformUI.getWorkbench()
                .getService( ISourceProviderService.class );
        TableLockState tableLockService = (TableLockState)sourceProviderService
                .getSourceProvider( TableLockState.TABLE_LOCK_STATE_ID );
        return tableLockService.isTableLocked();
    }

    private void isTableViewerVirtual(int style)
    {
        if (SWT.VIRTUAL == (style & SWT.VIRTUAL))
        {
            virtualTableViewer = true;
        }
    }

    private void IsCheckboxTableViewer(int style)
    {
        if (SWT.CHECK == (style & SWT.CHECK))
        {
            checkboxTableViewer = true;
        }
    }

    private void createTableViewer(Composite tableComposite, int style)
    {
        if (checkboxTableViewer)
        {
            table = new Table( tableComposite, style );
            tableViewer = new CheckboxTableViewer( table );
        }
        else
        {
            tableViewer = new TableViewer( tableComposite, style );
            table = tableViewer.getTable();
        }
    }

    private void initializeTableViewer(Composite parent, int style)
    {
        tableComposite = new Composite( parent, SWT.NONE );
        GridLayout tableLayout = new GridLayout( 2, false );
        tableLayout.marginHeight = 0;
        tableLayout.marginWidth = 0;
        tableComposite.setLayout( tableLayout );
        tableComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        Composite tableComp = new Composite( tableComposite, SWT.NONE );
        tableComp.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        tableColumnLayout = new TableColumnLayout();
        createTableViewer( tableComp, style );
        table.getParent().setLayout( tableColumnLayout );
        table.setHeaderVisible( true );
        table.setLinesVisible( true );
        IContentProvider contentProvider = new ArrayContentProvider();

        if (virtualTableViewer)
        {
            tableViewer.setUseHashlookup( true );
        }

        tableViewer.setContentProvider( contentProvider );

        table.addMouseWheelListener( new TableLockMouseWheelListener( this ) );

    }

    public void onScrollbarSelected()
    {
        elementToJumpTo = null;
    }

    public Composite getTableComposite()
    {
        return tableComposite;
    }

    public TableViewer getViewer()
    {
        return tableViewer;
    }

    public TableViewerColumn createColumn(String columnName, CellLabelProvider labelProvider)
    {
        TableViewerColumn tableViewerColumn = new TableViewerColumn( tableViewer, SWT.NONE );
        tableViewerColumn.getColumn().setText( columnName );
        tableViewerColumn.setLabelProvider( labelProvider );
        tableViewerColumn.getColumn().setResizable( resizable );
        tableColumnLayout.setColumnData( tableViewerColumn.getColumn(), new ColumnWeightData( width, resizable ) );
        return tableViewerColumn;
    }

    public void removeColumn(String columnName)
    {
        Stream<TableColumn> stream = Arrays.asList( table.getColumns() ).stream();

        Stream<TableColumn> filter = stream.filter( column -> column.getText().equals( columnName ) );
        Iterator<TableColumn> iterator = filter.iterator();
        if (iterator.hasNext())
        {
            TableColumn tableColumn = iterator.next();
            if (tableColumn != null)
            {
                tableColumn.dispose();
            }
        }
    }

    public void removeAllColumns()
    {
        table.setRedraw( false );
        Arrays.asList( table.getColumns() ).stream().forEach( column -> column.dispose() );
        table.setRedraw( true );
    }

    public void toggleScrollLock()
    {
        scrollLock = !scrollLock;
    }

    private void addSearchFieldComposite(Composite parent)
    {
        searchFieldComposite = new Composite( parent, SWT.NONE );
        GridLayout gridLayoutTop = new GridLayout( 5, false );
        gridLayoutTop.marginHeight = 0;
        gridLayoutTop.marginWidth = 0;
        searchFieldComposite.setLayout( gridLayoutTop );
        searchFieldComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );

        createSearchTextCombo( searchFieldComposite );
        createFilterToolBar( searchFieldComposite );
        createClearFilterTextActions( filterToolBar );

        createSearchRunningAction( filterToolBar );
        createToggleSearchFilterAction( filterToolBar );
        searchNextPreviousProvider = new SearchNextPreviousProvider();
        createPreviousFilterResultAction( filterToolBar );
        createNextFilterResultAction( filterToolBar );
        createResultCountLabel( searchFieldComposite );

        filterToolBar.update( true );

        if (toolbarId != null && toolbarId != "")
        {
            createToggleBackgroundToolBar( searchFieldComposite );
            createToolbar( searchFieldComposite, "toolbar:" + toolbarId );
        }
    }

    private void createToggleBackgroundToolBar(Composite parent)
    {
        toggleBackgroundColorToolBar = new ToolBarManager();
        toggleBackgroundColorToolBar.update( true );
        toggleBackgroundColorToolBar.createControl( parent );
    }

    public ToolBarManager getColorToolbar()
    {
        return toggleBackgroundColorToolBar;
    }

    private void createResultCountLabel(Composite parent)
    {
        resultCountLabel = new Label( parent, SWT.NONE );
        GridData gd = new GridData();
        gd.widthHint = 70;
        resultCountLabel.setLayoutData( gd );
    }

    private void createToggleSearchFilterAction(ToolBarManager filterToolBar)
    {
        toggleSearchFilterAction = new Action( "", IAction.AS_CHECK_BOX )
        {
            @Override
            public void run()
            {
                final SEARCH_MODE searchMode = isChecked() ? SEARCH_MODE.SEARCH : SEARCH_MODE.FILTER;
                if (searchMode == SEARCH_MODE.FILTER)
                {
                    toggleSearchFilterAction.setToolTipText( "Filter Mode" );
                }
                else if (searchMode == SEARCH_MODE.SEARCH)
                {
                    toggleSearchFilterAction.setToolTipText( "Search Mode" );
                }
                notifySearchModeListeners( searchMode );
            }
        };
        toggleSearchFilterAction.setId( ActionIdConstants.TOGGLE_SEARCH_FILTER );
        toggleSearchFilterAction.setToolTipText( "Filter Mode" );
        ImageDescriptor originalImageDescriptor = ViewerCommonPlugin.getDefault()
                .getImageDescriptor( "icons/search_toggle_filter_mode.png" );
        toggleSearchFilterAction.setImageDescriptor( originalImageDescriptor );
        filterToolBar.add( toggleSearchFilterAction );
    }

    private void notifySearchModeListeners(final SEARCH_MODE searchMode)
    {
        searchModeListeners.notifyListeners( new Notifier<SearchModeChangedListener>()
        {
            @Override
            public void notify(SearchModeChangedListener listener)
            {
                listener.searchModeChanged( searchMode );
            }
        } );
    }

    private void createPreviousFilterResultAction(ToolBarManager filterToolBar)
    {
        previousFilterResultAction = new Action( "", IAction.AS_PUSH_BUTTON )
        {
            @Override
            public void run()
            {
                searchNextPreviousProvider.previous();
                Object[] array = filterResultData.getSearchMatchingItems().toArray();
                TimebasedObject item = (TimebasedObject)array[searchNextPreviousProvider.getHighlightIndex()];
                centerElement( item );
                updatePreviousAndNextAndResultText();
            }
        };
        previousFilterResultAction.setId( ActionIdConstants.PREVIOUS_FILTER_RESULT );
        previousFilterResultAction.setToolTipText( "Previous Result" );
        ImageDescriptor originalImageDescriptor = ViewerCommonPlugin.getDefault()
                .getImageDescriptor( "icons/search_result_previous.png" );
        previousFilterResultAction.setImageDescriptor( originalImageDescriptor );
        ImageDescriptor disabledImageDescriptor = ImageDescriptor.createWithFlags( originalImageDescriptor,
                                                                                   SWT.IMAGE_GRAY );
        previousFilterResultAction.setDisabledImageDescriptor( disabledImageDescriptor );
        filterToolBar.add( previousFilterResultAction );
        previousFilterResultAction.setEnabled( isSearchModeOn() );
    }

    private void createNextFilterResultAction(ToolBarManager filterToolBar)
    {
        nextFilterResultAction = new Action( "", IAction.AS_PUSH_BUTTON )
        {
            @Override
            public void run()
            {
                searchNextPreviousProvider.next();
                Object[] array = filterResultData.getSearchMatchingItems().toArray();
                TimebasedObject item = (TimebasedObject)array[searchNextPreviousProvider.getHighlightIndex()];
                centerElement( item );
                updatePreviousAndNextAndResultText();
            }
        };
        nextFilterResultAction.setId( ActionIdConstants.NEXT_FILTER_RESULT );
        nextFilterResultAction.setToolTipText( "Next Result" );
        ImageDescriptor originalImageDescriptor = ViewerCommonPlugin.getDefault()
                .getImageDescriptor( "icons/search_result_next.png" );
        nextFilterResultAction.setImageDescriptor( originalImageDescriptor );
        ImageDescriptor disabledImageDescriptor = ImageDescriptor.createWithFlags( originalImageDescriptor,
                                                                                   SWT.IMAGE_GRAY );
        nextFilterResultAction.setDisabledImageDescriptor( disabledImageDescriptor );
        filterToolBar.add( nextFilterResultAction );
        nextFilterResultAction.setEnabled( isSearchModeOn() );
    }

    private void createSearchRunningAction(final ToolBarManager filterToolBar)
    {
        searchRunningAction = new Action( "", IAction.AS_PUSH_BUTTON )
        {
            @Override
            public void run()
            {
                System.out.println( "Terminate filter functionality not implemented yet" );
                setActionVisibile( searchRunningAction, false );
            }
        };
        searchRunningAction.setId( ActionIdConstants.FILTER_STATUS );
        searchRunningAction.setToolTipText( "Stop Filter" );
        ImageDescriptor originalImageDescriptor = ViewerCommonPlugin.getDefault()
                .getImageDescriptor( "icons/search_running.png" );
        searchRunningAction.setImageDescriptor( originalImageDescriptor );
        ImageDescriptor disabledImageDescriptor = ImageDescriptor.createWithFlags( originalImageDescriptor,
                                                                                   SWT.IMAGE_GRAY );
        searchRunningAction.setDisabledImageDescriptor( disabledImageDescriptor );
        searchRunningAction.setEnabled( false );
        filterToolBar.add( searchRunningAction );
    }

    private void createFilterToolBar(Composite parent)
    {
        filterToolBar = new ToolBarManager();
        filterToolBar.createControl( parent );
    }

    private void createSearchTextCombo(Composite parent)
    {
        filteredTextFieldModifyListener = new FilteredTextFieldModifyListener();

        searchTextCombo = new CCombo( parent, SWT.BORDER );
        searchComboHandler = new SearchTextComboHandler( searchTextCombo );

        searchTextCombo.addModifyListener( filteredTextFieldModifyListener );
        filteredTextFieldModifyListener.addFilterChangedListener( searchComboHandler );
        searchTextCombo.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ) );
    }

    private void createClearFilterTextActions(final ToolBarManager filterToolBar)
    {
        clearTextAction = new Action( "", IAction.AS_PUSH_BUTTON )
        {
            @Override
            public void run()
            {
                searchTextCombo.setText( "" );
            }
        };
        clearTextAction.setId( ActionIdConstants.CLEAR_FILTER );
        clearTextAction.setToolTipText( "Clear Text Field" );
        ImageDescriptor originalImageDescriptor = ViewerCommonPlugin.getDefault()
                .getImageDescriptor( "icons/search_clear.png" );
        clearTextAction.setImageDescriptor( originalImageDescriptor );
        filterToolBar.add( clearTextAction );
        setActionVisibile( clearTextAction, !searchTextCombo.getText().isEmpty() );

        filteredTextFieldModifyListener.addFilterChangedListener( new FilterChangedListener()
        {
            @Override
            public void onSearchModeChanged(SEARCH_MODE searchMode)
            {
            }

            @Override
            public void onFilterTextChanged(final String filterText)
            {
                Display.getDefault().syncExec( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        boolean textFieldNotEmpty = !filterText.isEmpty();
                        if (isDisposed())
                        {
                            return;
                        }
                        setSearchControlsVisible( textFieldNotEmpty );
                    }
                } );
            }
        } );
    }

    private void setSearchControlsVisible(boolean visible)
    {
        setActionVisibile( clearTextAction, visible );

        boolean extendedControlsVisible = visible && isFilterSearchModeEnabled;
        setActionVisibile( searchRunningAction, extendedControlsVisible );
        setActionVisibile( toggleSearchFilterAction, extendedControlsVisible );
        setActionVisibile( previousFilterResultAction, extendedControlsVisible );
        setActionVisibile( nextFilterResultAction, extendedControlsVisible );

        GridData labelLayoutData = (GridData)resultCountLabel.getLayoutData();
        resultCountLabel.setVisible( extendedControlsVisible );
        labelLayoutData.exclude = !extendedControlsVisible;

        filterToolBar.getControl().setVisible( visible );
        GridData layoutData = (GridData)filterToolBar.getControl().getLayoutData();
        if (layoutData != null)
        {
            layoutData.exclude = !visible;
        }

        filterToolBar.getControl().getParent().layout( true );
    }

    private void createToolbar(Composite parent, String id)
    {
        ToolBarManager localToolBarManager = new ToolBarManager();
        IMenuService menuService = PlatformUI.getWorkbench().getService( IMenuService.class );
        menuService.populateContributionManager( localToolBarManager, id );
        localToolBarManager.createControl( parent );
    }

    public boolean isDisposed()
    {
        return tableViewer.getTable().isDisposed();
    }

    public boolean isNotScrollLocked()
    {
        return !scrollLock;
    }

    private boolean isTableNotEmpty()
    {
        return tableViewer.getTable().getItemCount() > 0;
    }

    private void showLatestTableItem()
    {
        int indexOfLatestTableItem = tableViewer.getTable().getItemCount() - 1;
        if (shouldShowFirst)
        {
            indexOfLatestTableItem = 0;
        }
        TableItem latestTableItem = tableViewer.getTable().getItem( indexOfLatestTableItem );
        tableViewer.getTable().showItem( latestTableItem );
    }

    public void setShouldShowFirst(boolean shouldShowFirst)
    {
        this.shouldShowFirst = shouldShowFirst;
    }

    public Collection<?> getRawInput()
    {
        return (Collection<?>)tableViewer.getInput();
    }

    public void refresh()
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            @Override
            public void run()
            {
                tableViewer.refresh();
            }
        } );
    }

    private int numberOfVisibleItems()
    {
        return (table.getBounds().height - table.getHeaderHeight()) / table.getItemHeight();
    }

    public void centerElement(Object element)
    {
        @SuppressWarnings("unchecked")
        List<Object> input = (List<Object>)getViewer().getInput();
        if (input == null || element == null)
        {
            return;
        }
        int index = input.indexOf( element );
        if (index == -1)
        {
            elementToJumpTo = element;
        }
        else
        {
            centerIndex( index );
        }
    }

    private void centerIndex(int index)
    {
        int itemsInHalfPage = numberOfVisibleItems() / 2;
        getViewer().getTable().setTopIndex( index - itemsInHalfPage );
        getViewer().refresh();
    }

    public void setTableData(final TableData filterResultData, boolean jumpToTableEnd)
    {
        this.filterResultData = filterResultData;
        if (!isDisposed())
        {
            List<?> filterResultList = filterResultData.getItemsToBeDisplayed();
            tableViewer.setInput( filterResultList );
            tableViewer.setItemCount( filterResultList.size() );
            if (isFilterSearchModeEnabled)
            {
                searchNextPreviousProvider.setLimit( filterResultData.getSearchMatchingItems().size() - 1 );
                setActionVisibile( searchRunningAction, false );
                updatePreviousAndNextAndResultText();
            }
            if (isNotScrollLocked() && isTableNotEmpty() && jumpToTableEnd)
            {
                showLatestTableItem();
            }
            searchFieldComposite.layout();
        }

        if (filterResultData.getItemsToBeDisplayed().isEmpty())
        {
            getViewer().setSelection( null );
        }

        jumpToWaitingElementIfAny();
    }

    public void setInput(List<Object> inputList)
    {
        tableViewer.setInput( inputList );
        tableViewer.setItemCount( inputList.size() );
        if (isNotScrollLocked() && isTableNotEmpty())
        {
            showLatestTableItem();
        }
        searchFieldComposite.layout();
        jumpToWaitingElementIfAny();
    }

    private void jumpToWaitingElementIfAny()
    {
        if (elementToJumpTo != null)
        {
            centerElement( elementToJumpTo );
            elementToJumpTo = null;
        }
    }

    public void showSearchRunning()
    {
        resultCountLabel.setText( "" );
        setActionVisibile( searchRunningAction, false );
        searchFieldComposite.layout();
    }

    private void setActionVisibile(IAction action, boolean visible)
    {
        if (action != null)
        {
            IContributionItem item = filterToolBar.find( action.getId() );
            if (item != null)
            {
                item.setVisible( visible );
            }
        }
        filterToolBar.update( true );
    }

    public SearchNextPreviousProvider getSearchNextPreviousProvider()
    {
        return searchNextPreviousProvider;
    }

    public boolean isSearchModeOn()
    {
        if (toggleSearchFilterAction == null)
        {
            return false;
        }
        return toggleSearchFilterAction.isChecked();
    }

    private void updatePreviousAndNextAndResultText()
    {
        int resultSize = filterResultData.getSearchMatchingItems().size();
        boolean searchHasResults = resultSize > 0;
        if (isSearchModeOn())
        {
            int indexToHighlight = searchNextPreviousProvider.getHighlightIndex();
            if (searchHasResults)
            {
                indexToHighlight++;
            }
            resultCountLabel.setText( indexToHighlight + " of " + resultSize );
        }
        else
        {
            resultCountLabel.setText( "" + resultSize );
        }
        previousFilterResultAction.setEnabled( isSearchModeOn() && searchHasResults );
        nextFilterResultAction.setEnabled( isSearchModeOn() && searchHasResults );
    }

    public boolean setFocus()
    {
        return tableViewer.getControl().setFocus();
    }

    public void setComboContent(List<String> contents)
    {
        searchComboHandler.setComboContent( contents );
    }

    public void registerComboListener(SearchComboListener listener)
    {
        searchComboHandler.registerComboListener( listener );
    }

    public void unregisterComboListener(SearchComboListener listener)
    {
        searchComboHandler.unregisterComboListener( listener );
    }

    public void registerSearchModeListener(SearchModeChangedListener listener)
    {
        searchModeListeners.add( listener );
    }

    public void unregisterSearchModeListener(SearchModeChangedListener listener)
    {
        searchModeListeners.remove( listener );
    }
}
