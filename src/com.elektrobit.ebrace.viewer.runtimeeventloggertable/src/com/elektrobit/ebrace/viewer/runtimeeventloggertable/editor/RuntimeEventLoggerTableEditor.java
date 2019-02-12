/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.osgi.framework.ServiceRegistration;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceRegistration;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.allChannels.AllChannelsNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.allChannels.AllChannelsNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ColorPreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ColorPreferencesNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ModelNameNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ModelNameNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.runtimeeventdecoder.RuntimeEventDecoderNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.selectelement.SelectElementsInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.selectelement.SelectElementsInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.table.SEARCH_MODE;
import com.elektrobit.ebrace.core.interactor.api.table.TableScriptFiltersNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.table.TableScriptFiltersNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.tableinput.RuntimeEventTableDataNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.tableinput.RuntimeEventTableDataNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableData;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermNotifyUseCase;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences;
import com.elektrobit.ebrace.core.preferences.api.AnalysisTimespanPreferences.ANALYSIS_TIMESPAN_CHANGE_REASON;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.preferences.listener.AnalysisTimespanChangedListener;
import com.elektrobit.ebrace.viewer.common.ImageCreator;
import com.elektrobit.ebrace.viewer.common.dnd.RuntimeEventTimestampDragSourceAdapter;
import com.elektrobit.ebrace.viewer.common.dnd.RuntimeeventChannelDropTargetAdapter;
import com.elektrobit.ebrace.viewer.common.listeners.TableLockListener;
import com.elektrobit.ebrace.viewer.common.swt.CommonFilteredTable;
import com.elektrobit.ebrace.viewer.common.swt.SearchComboListener;
import com.elektrobit.ebrace.viewer.common.swt.SearchModeChangedListener;
import com.elektrobit.ebrace.viewer.common.transfer.RuntimeEventChannelTransfer;
import com.elektrobit.ebrace.viewer.common.transfer.RuntimeEventTimstampTransfer;
import com.elektrobit.ebrace.viewer.common.view.IResourcesModelView;
import com.elektrobit.ebrace.viewer.common.view.ITableViewerView;
import com.elektrobit.ebrace.viewer.common.view.ToggleDecoderComposite;
import com.elektrobit.ebrace.viewer.resources.editor.ResourcesModelEditorInput;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.labelprovider.AnalysisTimespanColumnLabelProvider;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.labelprovider.ChannelColumnLabelProvider;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.labelprovider.ColorColumnLabelProvider;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.labelprovider.TagColumnLabelProvider;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.labelprovider.TimestampColumnLabelProvider;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.labelprovider.ValueColumnLabelProvider;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.ruler.TableRuler;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.util.TableCellBackgroundColorCreator;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.util.TableColumnWidthProvider;
import com.elektrobit.ebrace.viewer.runtimeeventloggertable.util.TableSelectionToClipboardHelper;
import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.TimebasedObject;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;
import com.elektrobit.viewer.dbus.decoder.swt.MessageDecoderComposite;

public class RuntimeEventLoggerTableEditor extends EditorPart
        implements
            ITableViewerView,
            TableLockListener,
            ToggleDecoderComposite,
            AnalysisTimespanChangedListener,
            IResourcesModelView,
            RuntimeEventTableDataNotifyCallback,
            PreferencesNotifyCallback,
            ChannelColorCallback,
            ModelNameNotifyCallback,
            ColorPreferencesNotifyCallback,
            TableSearchTermNotifyCallback,
            SearchComboListener,
            TableScriptFiltersNotifyCallback,
            SearchModeChangedListener,
            SelectElementsInteractionCallback,
            AllChannelsNotifyCallback
{
    private final String TOOLBAR_ID = "com.elektrobit.ebrace.viewer.runtimeeventloggertable.toolbar";

    private final AnalysisTimespanPreferences analysisTimespanPreferences = new GenericOSGIServiceTracker<AnalysisTimespanPreferences>( AnalysisTimespanPreferences.class )
            .getService();
    private final UserInteractionPreferences userInteractionPreferences = new GenericOSGIServiceTracker<UserInteractionPreferences>( UserInteractionPreferences.class )
            .getService();
    private final TimeMarkerManager timeMarkerManager = new GenericOSGIServiceTracker<TimeMarkerManager>( TimeMarkerManager.class )
            .getService();

    private final List<RowFormatter> columnsToApplyFilter = new ArrayList<RowFormatter>();
    private ResourcesModelEditorInput editorInput;
    private MessageDecoderComposite messageDecoder;
    private ServiceRegistration<?> tableLockService;
    private ISelectionChangedListener iSelectionChangedListener;
    private Composite tableContainer;
    private CommonFilteredTable filteredTable;
    private TableRuler ruler;
    private RuntimeEventTableDataNotifyUseCase tableInputNotifyUseCase;
    private ChannelColorUseCase channelColorUseCase;
    private ColorPreferencesNotifyUseCase colorPreferencesNotifyUseCase;
    private TimestampColumnLabelProvider timestampColumnLabelProvider;
    private final List<ValueColumnLabelProvider> valueColumnLabelProviders = new ArrayList<>();
    private TableViewerColumn timestampColumn;
    private final List<TableViewerColumn> valueColumns = new ArrayList<>();
    private TableViewerColumn channelNameColumn;
    private TableData filterResultData;
    private PreferencesNotifyUseCase preferencesNotifyCallback;
    private ColorColumnLabelProvider labelProvider;
    private ModelNameNotifyUseCase modelNameNotifyUseCase;
    private TableSearchTermInteractionUseCase tableSearchTermInteractionUseCase;
    private TableSearchTermNotifyUseCase searchTermNotifyUseCase;
    private List<String> searchTerms = Collections.emptyList();
    private List<RaceScriptMethod> filterMethods = Collections.emptyList();
    private final List<String> methodDescriptions = new ArrayList<String>();
    private TableScriptFiltersNotifyUseCase scriptFiltersNotifyUseCase;
    private SelectElementsInteractionUseCase selectElementsInteractionUseCase;
    private IAction toggleMessageDecoderAction;
    private final TableCellBackgroundColorCreator backgroundColorCreator = new TableCellBackgroundColorCreator();
    private RuntimeEventDecoderNotifyUseCase runtimeEventDecoderNotifyUseCase;
    private TableSelectionToClipboardHelper clipboard;
    private final Set<RuntimeEventChannel<?>> currentChannels = new HashSet<>();
    private AllChannelsNotifyUseCase allChannelsNotifyUseCase;

    private TableViewerColumn analysisColumn;

    private TableViewerColumn tagColumn;

    private TableViewerColumn colorColumn;

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        setSite( site );
        editorInput = (ResourcesModelEditorInput)input;
        setInput( input );
    }

    @Override
    public String getTitle()
    {
        if (editorInput != null)
        {
            return editorInput.getName();
        }
        return super.getTitle();
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
    public void createPartControl(final Composite parent)
    {
        createSashAndControls( parent );
        toggleDecoderComposite();

        registerDecoderUsecase();
        registerListeners();
        addDragAndDropSupport();
        addToggleActions();
        addMouseListener();
        clipboard = new TableSelectionToClipboardHelper( parent.getDisplay() );
    }

    private void createSashAndControls(final Composite parent)
    {
        SashForm sash = new SashForm( parent, SWT.NONE );
        createFilteredTable( sash );
        messageDecoder = new MessageDecoderComposite( sash, SWT.BORDER );
    }

    private void registerDecoderUsecase()
    {
        runtimeEventDecoderNotifyUseCase = UseCaseFactoryInstance.get()
                .makeRuntimeEventDecoderNotifyUseCase( messageDecoder );
    }

    private void addMouseListener()
    {
        getTable().addMouseListener( new RuntimeEventTableMouseListener( this ) );
        getTable().addMouseTrackListener( new RuntimeEventTableMouseListener( this ) );
        getTable().addListener( SWT.MouseWheel, new Listener()
        {
            long lastScrolling = 0;
            int linesToScroll = 0;

            @Override
            public void handleEvent(Event e)
            {
                e.doit = false;
                long now = System.currentTimeMillis();
                long timeSinceLastScroll = now - lastScrolling;
                linesToScroll -= e.count * 3;
                if (timeSinceLastScroll > 300)
                {
                    int lineIndex = getTable().getTopIndex() + linesToScroll;
                    getTable().setTopIndex( lineIndex );
                    linesToScroll = 0;
                    lastScrolling = System.currentTimeMillis();
                }
            }
        } );

    }

    Table getTable()
    {
        TableViewer tableViewer = filteredTable.getViewer();
        Table table = tableViewer.getTable();
        return table;
    }

    private void addToggleActions()
    {
        IAction toggleBackgroundAction = new RuntimeEventTableToggleBackgroundAction( this );
        filteredTable.getColorToolbar().add( toggleBackgroundAction );

        toggleMessageDecoderAction = new RuntimeEventTableToggleMessageDecoderAction( this );
        filteredTable.getColorToolbar().add( toggleMessageDecoderAction );

        filteredTable.getColorToolbar().update( true );
    }

    void toggleTableColor()
    {
        ((TableModel)getModel()).toggleBackgroundEnabled();
        filteredTable.getViewer().refresh();
    }

    private void createFilteredTable(Composite parent)
    {
        tableContainer = new Composite( parent, SWT.BORDER );
        tableContainer.setLayout( new GridLayout() );
        tableContainer.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        tableContainer.addControlListener( new RuntimeEventTableControlListener( this ) );

        int tableStyle = SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL;
        filteredTable = new CommonFilteredTable( tableContainer, tableStyle, TOOLBAR_ID, true );
        getSite().setSelectionProvider( filteredTable.getViewer() );

        Composite rulerComposite = createRulerComposite();
        createTimemarkerRuler( rulerComposite );

        createViewContextMenu();
        createColumns();

        filteredTable.registerComboListener( this );
        filteredTable.registerSearchModeListener( this );
    }

    private Composite createRulerComposite()
    {
        Composite rulerComposite = new Composite( filteredTable.getTableComposite(), SWT.FILL );
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = computeRulerMargin();
        rulerComposite.setLayout( gridLayout );
        rulerComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, true ) );
        return rulerComposite;
    }

    private void createTimemarkerRuler(Composite parentComposite)
    {
        ruler = new TableRuler( parentComposite, SWT.BORDER, filteredTable );
        GridData gridLayoutData = new GridData( SWT.FILL, SWT.FILL, false, true );
        gridLayoutData.widthHint = 20;
        ruler.setLayout( new GridLayout() );
        ruler.setLayoutData( gridLayoutData );
    }

    void resizeColumns()
    {
        filteredTable.getViewer().getTable().setRedraw( false );
        TableColumnWidthProvider widthProvider = new TableColumnWidthProvider( tableContainer.getClientArea().width,
                                                                               valueColumns.size() - 1 );
        analysisColumn.getColumn()
                .setWidth( TableColumnWidthProvider.FixedColumnWidths.ANALYSIS_TIMESPAN_COLUMN_WIDTH.getWidth() );
        timestampColumn.getColumn().setWidth( (int)(widthProvider.getTimestampColumnWidth()) );
        tagColumn.getColumn().setWidth( TableColumnWidthProvider.FixedColumnWidths.TAG_COLUMN_WIDTH.getWidth() );
        colorColumn.getColumn().setWidth( TableColumnWidthProvider.FixedColumnWidths.COLOR_COLUMN_WIDTH.getWidth() );
        valueColumns.stream().forEach( current -> {
            TableColumn currentColumn = current.getColumn();
            long width = currentColumn.getText().toLowerCase().equals( "value" )
                    ? widthProvider.getValueColumnWidth()
                    : widthProvider.getCustomColumnWidth();
            currentColumn.setWidth( (int)width );
        } );

        channelNameColumn.getColumn().setWidth( (int)widthProvider.getChannelColumnWidth() );
        filteredTable.getViewer().getTable().setRedraw( true );
    }

    private int computeRulerMargin()
    {
        ScrollBar verticalScrollbar = filteredTable.getViewer().getTable().getVerticalBar();
        Rectangle thumbTrackBounds = verticalScrollbar.getThumbTrackBounds();
        int scrollbarArrowHeight = thumbTrackBounds.width;
        return scrollbarArrowHeight;
    }

    private void createViewContextMenu()
    {
        Composite menuOwner = getTable();
        MenuManager menuManager = new MenuManager();
        Menu menu = menuManager.createContextMenu( menuOwner );
        menuOwner.setMenu( menu );
        ColumnViewer selectionProvider = filteredTable.getViewer();

        getSite().registerContextMenu( menuManager, selectionProvider );
    }

    private void createColumns()
    {
        createAnalysisTimespanColumn();
        createTimstampColumn();
        createTagColumn();
        createColorColumn();
        createValueColumn();
        createChannelColumn();
    }

    private void createAnalysisTimespanColumn()
    {
        CellLabelProvider labelProvider = new AnalysisTimespanColumnLabelProvider( analysisTimespanPreferences,
                                                                                   userInteractionPreferences );
        analysisColumn = filteredTable.createColumn( "", labelProvider );
        analysisColumn.getColumn().setResizable( false );
    }

    private void createTimstampColumn()
    {
        timestampColumnLabelProvider = new TimestampColumnLabelProvider( (TableModel)getModel(),
                                                                         timeMarkerManager,
                                                                         backgroundColorCreator );
        timestampColumn = filteredTable.createColumn( "Timestamp", timestampColumnLabelProvider );
        timestampColumn.getColumn().setAlignment( SWT.RIGHT );
    }

    private void createTagColumn()
    {
        TableModel model = (TableModel)getModel();
        CellLabelProvider tagColumnLabelProvider = new TagColumnLabelProvider( model,
                                                                               new ImageCreator( this.getTreeViewer()
                                                                                       .getControl() ),
                                                                               backgroundColorCreator );
        tagColumn = filteredTable.createColumn( "", tagColumnLabelProvider );
        tagColumn.getColumn().setResizable( false );

        ColumnViewerToolTipSupport.enableFor( tagColumn.getViewer() );
    }

    private void recreateColumns()
    {
        filteredTable.getViewer().getTable().setRedraw( false );
        filteredTable.removeColumn( "Channel" );
        removeAdditionalValueColumns();
        createValueColumn();
        createChannelColumn();
        filteredTable.getViewer().getTable().setRedraw( true );
        resizeColumns();
    }

    private void createValueColumn()
    {
        Collection<String> allColumnNames = new ArrayList<>();
        List<RuntimeEventChannel<?>> allChannels = getModel().getChannels();
        allChannels.stream().map( channel -> channel.getValueColumnNames() )
                .forEach( columnNames -> allColumnNames.addAll( columnNames ) );

        List<String> channelsWithoutDuplicates = allColumnNames.stream().distinct().collect( Collectors.toList() );

        channelsWithoutDuplicates.stream().filter( columnName -> !columnName.equals( "Value" ) )
                .forEach( columnName -> addAdditionalValueColumn( columnName ) );

        // "Value" column should be rightmost before the channel column
        addAdditionalValueColumn( "Value" );
    }

    private void addAdditionalValueColumn(String columnName)
    {
        ValueColumnLabelProvider labelProvider = new ValueColumnLabelProvider( filteredTable
                .getSearchNextPreviousProvider(),
                                                                               (TableModel)getModel(),
                                                                               timeMarkerManager,
                                                                               columnName,
                                                                               backgroundColorCreator );
        valueColumnLabelProviders.add( labelProvider );
        columnsToApplyFilter.add( labelProvider );
        TableViewerColumn column = filteredTable.createColumn( columnName, labelProvider );
        valueColumns.add( column );
    }

    private void removeAdditionalValueColumns()
    {
        valueColumns.stream().forEach( valueColumn -> filteredTable.removeColumn( valueColumn.getColumn().getText() ) );
        valueColumnLabelProviders.clear();
        columnsToApplyFilter.clear();
        valueColumns.clear();
    }

    private void createColorColumn()
    {
        labelProvider = new ColorColumnLabelProvider();
        colorColumn = filteredTable.createColumn( "", labelProvider );
        colorColumn.getColumn().setResizable( false );
    }

    private void createChannelColumn()
    {

        channelNameColumn = filteredTable
                .createColumn( "Channel",
                               new ChannelColumnLabelProvider( (TableModel)getModel(), backgroundColorCreator ) );
    }

    private void registerListeners()
    {
        tableLockService = GenericOSGIServiceRegistration.registerService( TableLockListener.class, this );
        analysisTimespanPreferences.addTimespanPreferencesChangedListener( this );
        iSelectionChangedListener = new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                refreshMessageDecoder();
            }
        };
        filteredTable.getViewer().addPostSelectionChangedListener( iSelectionChangedListener );
        TableModel tableModel = (TableModel)editorInput.getModel();
        tableInputNotifyUseCase = UseCaseFactoryInstance.get()
                .makeRuntimeEventTableDataNotifyUseCase( this, columnsToApplyFilter, tableModel );
        preferencesNotifyCallback = UseCaseFactoryInstance.get().makePreferencesNotifyUseCase( this );

        modelNameNotifyUseCase = UseCaseFactoryInstance.get().makeModelNameNotifyUseCase( this );
        modelNameNotifyUseCase.register( tableModel );

        channelColorUseCase = UseCaseFactoryInstance.get().makeChannelColorUseCase( this );
        colorPreferencesNotifyUseCase = UseCaseFactoryInstance.get().makeColorPreferencesNotifyUseCase( this );

        String tableSearchTermsID = this.getClass().getCanonicalName();
        tableSearchTermInteractionUseCase = UseCaseFactoryInstance.get()
                .makeTableSearchTermInteractionUseCase( tableSearchTermsID );
        scriptFiltersNotifyUseCase = UseCaseFactoryInstance.get().makeTableScriptFiltersNotifyUseCase( this );
        searchTermNotifyUseCase = UseCaseFactoryInstance.get().makeTableSearchTermNotifyUseCase( this,
                                                                                                 tableSearchTermsID );
        selectElementsInteractionUseCase = UseCaseFactoryInstance.get().makeSelectElementsInteractionUseCase( this );

        allChannelsNotifyUseCase = UseCaseFactoryInstance.get().makeAllChannelsNotifyUseCase( this );
    }

    private void refreshMessageDecoder()
    {
        IStructuredSelection selection = (IStructuredSelection)filteredTable.getViewer().getSelection();
        Object firstSelectedElement = selection.getFirstElement();
        if (firstSelectedElement == null || firstSelectedElement instanceof TimeMarker)
        {
            messageDecoder.clearDecoderView();
        }
        else if (firstSelectedElement instanceof RuntimeEvent<?>)
        {
            if (runtimeEventDecoderNotifyUseCase != null)
            {
                runtimeEventDecoderNotifyUseCase.decodeRuntimeEvent( (RuntimeEvent<?>)firstSelectedElement );
            }
        }
    }

    @Override
    public void toggleDecoderComposite()
    {
        if (messageDecoder.toggleAndReturnExclude())
        {
            tableContainer.getParent().setLayout( new GridLayout( 1, true ) );
            if (toggleMessageDecoderAction != null)
            {
                toggleMessageDecoderAction.setChecked( false );
            }
        }
        else
        {
            refreshMessageDecoder();
            tableContainer.getParent().setLayout( new GridLayout( 2, true ) );
            if (toggleMessageDecoderAction != null)
            {
                toggleMessageDecoderAction.setChecked( true );
            }
        }
        tableContainer.getParent().layout();
    }

    void toggleSelectedResources()
    {
        IStructuredSelection selection = (IStructuredSelection)filteredTable.getViewer().getSelection();
        List<TimebasedObject> rEvent = new ArrayList<TimebasedObject>();

        for (Object selectedObj : selection.toList())
        {
            if (selectedObj instanceof TimebasedObject)
            {
                rEvent.add( (TimebasedObject)selectedObj );
            }
        }

        if (!rEvent.isEmpty())
        {
            selectElementsInteractionUseCase.selectedResource( rEvent );
        }
    }

    @Override
    public void setFocus()
    {
        filteredTable.setFocus();
    }

    @Override
    public void dispose()
    {
        unregisterDecoderUseCase();
        backgroundColorCreator.dispose();
        unregisterListenersAndServices();
        ruler.dispose();
        clipboard.dispose();
        super.dispose();
    }

    private void unregisterDecoderUseCase()
    {
        runtimeEventDecoderNotifyUseCase.unregister();
        runtimeEventDecoderNotifyUseCase = null;
    }

    private void unregisterListenersAndServices()
    {
        tableInputNotifyUseCase.unregister();
        tableLockService.unregister();
        preferencesNotifyCallback.unregister();
        filteredTable.getViewer().removePostSelectionChangedListener( iSelectionChangedListener );
        analysisTimespanPreferences.removeTimespanPreferencesChangedListener( this );
        channelColorUseCase.unregister();
        colorPreferencesNotifyUseCase.unregister();
        modelNameNotifyUseCase.unregister();
        searchTermNotifyUseCase.unregister();
        tableSearchTermInteractionUseCase.unregister();
        scriptFiltersNotifyUseCase.unregister();
        filteredTable.unregisterComboListener( this );
        filteredTable.unregisterSearchModeListener( this );
        allChannelsNotifyUseCase.unregister();
    }

    @Override
    public ColumnViewer getTreeViewer()
    {
        return filteredTable.getViewer();
    }

    @Override
    public List<?> getContent()
    {
        return (List<?>)filteredTable.getRawInput();
    }

    @Override
    public void toggleScrollLock()
    {
        filteredTable.toggleScrollLock();
    }

    private void showItemsInTimespan()
    {
        TimebasedObject analysisTimespanStartElement = findElementOnAnalysisTimespanStart();
        filteredTable.centerElement( analysisTimespanStartElement );
    }

    private TimebasedObject findElementOnAnalysisTimespanStart()
    {
        if (filterResultData == null)
        {
            return null;
        }

        long analysisTimespanStart = analysisTimespanPreferences.getAnalysisTimespanStart();
        for (Object object : filterResultData.getItemsToBeDisplayed())
        {
            TimebasedObject item = (TimebasedObject)object;
            if (item.getTimestamp() >= analysisTimespanStart)
            {
                return item;
            }
        }
        return null;
    }

    @Override
    public void analysisTimespanLengthChanged(long newAnalysisTimespanInMillis)
    {
        if (!userInteractionPreferences.isLiveMode())
        {
            showItemsInTimespan();
        }
    }

    @Override
    public void fullTimespanEndTimeChanged(long newAnalysisTimespanEndTimeInMillis)
    {
    }

    @Override
    public void onAnalysisTimespanChanged(ANALYSIS_TIMESPAN_CHANGE_REASON reason)
    {
        if (!userInteractionPreferences.isLiveMode() && reason != ANALYSIS_TIMESPAN_CHANGE_REASON.TIME_MARKER_SELECTED)
        {
            Display.getDefault().syncExec( new Runnable()
            {

                @Override
                public void run()
                {
                    showItemsInTimespan();
                }
            } );
        }
    }

    private void addDragAndDropSupport()
    {
        Transfer[] dragTransferTypes = new Transfer[]{RuntimeEventTimstampTransfer.getInstance()};
        Transfer[] dropTransferTypes = new Transfer[]{RuntimeEventChannelTransfer.getInstance()};
        filteredTable.getViewer()
                .addDragSupport( DND.DROP_MOVE | DND.DROP_COPY,
                                 dragTransferTypes,
                                 new RuntimeEventTimestampDragSourceAdapter( filteredTable.getViewer() ) );

        filteredTable.getViewer().addDropSupport( DND.DROP_MOVE | DND.DROP_COPY,
                                                  dropTransferTypes,
                                                  new RuntimeeventChannelDropTargetAdapter( this.editorInput.getModel(),
                                                                                            Object.class ) );
    }

    @Override
    public ResourceModel getModel()
    {
        return editorInput.getModel();
    }

    @Override
    public void onFilteringStarted()
    {
        filteredTable.showSearchRunning();
    }

    @Override
    public void onTableInputCollected(TableData filterResultData, boolean jumpToTableEnd)
    {
        Set<RuntimeEventChannel<?>> channels = new HashSet<>( getModel().getChannels() );
        if (!currentChannels.equals( channels ))
        {
            currentChannels.clear();
            currentChannels.addAll( getModel().getChannels() );
            recreateColumns();
            tableContainer.layout( true, true );
            onTableInputCollected( filterResultData, jumpToTableEnd );
        }
        else
        {
            this.filterResultData = filterResultData;
            valueColumnLabelProviders.stream().forEach( provider -> provider.setTableData( filterResultData ) );
            ruler.onNewData( filterResultData );
            filteredTable.setTableData( filterResultData, jumpToTableEnd );
            labelProvider.assignColors( (TableModel)getModel() );
        }
    }

    @Override
    public void onTimestampFormatChanged(String format)
    {
        filteredTable.getViewer().refresh();
        refreshMessageDecoder();
    }

    @Override
    public void onColorAssigned(Collection<RuntimeEventChannel<?>> channels)
    {
        filteredTable.getViewer().refresh();
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
    public void onColorTransparencyChanged(double value)
    {
        filteredTable.getViewer().refresh();
    }

    @Override
    public void onColorPaletteChanged(List<SColor> newColorPalette)
    {
    }

    @Override
    public void onTimeMarkerRenamed(TimeMarker timeMarker)
    {
        filteredTable.getViewer().refresh();
    }

    @Override
    public void onJumpToTimeMarker(TimeMarker timeMarker)
    {
        TimeMarker selectedTimeMarker = timeMarkerManager.getCurrentSelectedTimeMarker();
        if (selectedTimeMarker != null)
        {
            filteredTable.centerElement( selectedTimeMarker );
        }
    }

    @Override
    public void onSearchTermsChanged(List<String> searchTerms)
    {
        this.searchTerms = searchTerms;
        setValuesToSearchCombo();
    }

    @Override
    public void onScriptFilterMethodsChanged(List<RaceScriptMethod> filterMethods)
    {
        this.filterMethods = filterMethods;
        setValuesToSearchCombo();
    }

    private void setValuesToSearchCombo()
    {
        List<String> newComboItems = new ArrayList<String>();
        newComboItems.addAll( searchTerms );
        methodDescriptions.clear();
        for (RaceScriptMethod method : filterMethods)
        {
            String methodEntryDescription = "Filter Script: " + method.getLabelText();
            methodDescriptions.add( methodEntryDescription );
            newComboItems.add( methodEntryDescription );
        }

        filteredTable.setComboContent( newComboItems );
    }

    @Override
    public void onTextEntered(String text)
    {
        if (methodDescriptions.contains( text ))
        {
            return;
        }
        tableSearchTermInteractionUseCase.addSearchTerm( text );
        tableInputNotifyUseCase.setFilterText( text );
    }

    @Override
    public void onComboItemSelected(int index)
    {
        if (index < searchTerms.size())
        {
            // String has been selected
            String selectedString = searchTerms.get( index );
            tableInputNotifyUseCase.setFilterText( selectedString );
        }
        else
        {
            // Script filter has been selected
            int indexInMethod = index - searchTerms.size();
            RaceScriptMethod raceScriptMethods = filterMethods.get( indexInMethod );
            tableInputNotifyUseCase.setFilterMethod( raceScriptMethods );
        }
    }

    @Override
    public void onClearComboSelected()
    {
        tableSearchTermInteractionUseCase.deleteAllTerms();
    }

    @Override
    public void searchModeChanged(SEARCH_MODE searchMode)
    {
        tableInputNotifyUseCase.setSearchMode( searchMode );
    }

    @Override
    public void doSave(IProgressMonitor monitor)
    {
    }

    @Override
    public void doSaveAs()
    {
    }

    @Override
    public void onAllChannelsChanged(List<RuntimeEventChannel<?>> allChannels)
    {

    }

    @Override
    public void onChannelRemoved(RuntimeEventChannel<?> deletedChannel)
    {
        if (getModel().getChannels().contains( deletedChannel ))
        {
            getModel().setChannels( getModel().getChannels() );
        }

    }

}
