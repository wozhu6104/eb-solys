/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.IEvaluationService;
import org.osgi.framework.ServiceRegistration;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceRegistration;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.PreferencesNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.structure.RuntimeEventsOfSelectedComrelationNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.RuntimeEventsOfSelectedComrelationNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermNotifyUseCase;
import com.elektrobit.ebrace.viewer.common.ViewerCommonPlugin;
import com.elektrobit.ebrace.viewer.common.listeners.TableLockListener;
import com.elektrobit.ebrace.viewer.common.provider.ValueColumnLabelProvider;
import com.elektrobit.ebrace.viewer.common.provider.messagesView.MessagesViewTimestampColumnLabelProvider;
import com.elektrobit.ebrace.viewer.common.swt.CommonFilteredTable;
import com.elektrobit.ebrace.viewer.common.swt.SearchComboListener;
import com.elektrobit.ebrace.viewer.common.view.ITableViewerView;
import com.elektrobit.ebrace.viewer.common.view.ToggleDecoderComposite;
import com.elektrobit.ebrace.viewer.dbusgraph.DependencyGraphEditor;
import com.elektrobit.ebrace.viewer.dbusgraph.GraphView;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.viewer.dbus.decoder.swt.MessageDecoderComposite;

public class MessagesView
        implements
            ISelectionListener,
            ITableViewerView,
            TableLockListener,
            ToggleDecoderComposite,
            RuntimeEventsOfSelectedComrelationNotifyCallback,
            PreferencesNotifyCallback,
            DisposeListener,
            SearchComboListener,
            TableSearchTermNotifyCallback
{
    private static final String COM_ELEKTROBIT_EBRACE_VIEWER_COMMON_EXPORT_EVENTS_IS_ANY_TABLE_CONTENT = "com.elektrobit.ebrace.viewer.common.exportEvents.isAnyTableContent";

    @SuppressWarnings("unused")
    private final static Logger LOG = Logger.getLogger( MessagesView.class );

    private CommonFilteredTable filteredTable;

    private final String TOOLBAR_ID = "com.elektrobit.ebrace.viewer.comrelationruntimeevent.logger";
    private final String CONTEXT_MENU_ID = "comrelation.runtime.events.menu";
    ServiceRegistration<?> tableLockService;
    private Composite container;

    private MessageDecoderComposite messageDecoder;
    private ISelectionChangedListener selectionListener;
    private RuntimeEventsOfSelectedComrelationNotifyUseCase eventsOfComrelationUseCase;
    private List<?> currentInput;
    private PreferencesNotifyUseCase preferencesNotifyUseCase;
    private MessagesViewTimestampColumnLabelProvider timestampColumnLabelProvider;
    private final IWorkbenchPartSite site;

    private TableSearchTermNotifyUseCase tableSearchTermNotifyUseCase;
    private TableSearchTermInteractionUseCase tableSearchTermInteractionUseCase;

    private List<String> searchTerms = Collections.emptyList();
    private IAction toggleMessageDecoderAction;

    public MessagesView(Composite parent, IWorkbenchPartSite site)
    {
        this.site = site;
        parent.addDisposeListener( this );
        createPartControl( parent );
    }

    private void createPartControl(Composite parent)
    {
        SashForm container = new SashForm( parent, SWT.NONE );
        createTableViewerBuilder( container );
        List<RowFormatter> labelProviders = createTableColumns();

        messageDecoder = new MessageDecoderComposite( container, SWT.BORDER );
        createViewContextMenu();
        registerListeners();
        site.getWorkbenchWindow().getSelectionService().addSelectionListener( this );
        eventsOfComrelationUseCase = UseCaseFactoryInstance.get()
                .makeRuntimeEventsOfSelectedComrelationNotifyUseCaseImpl( this, labelProviders );
        filteredTable.registerComboListener( this );
        preferencesNotifyUseCase = UseCaseFactoryInstance.get().makePreferencesNotifyUseCase( this );

        String tableSearchTermsID = this.getClass().getCanonicalName();
        tableSearchTermNotifyUseCase = UseCaseFactoryInstance.get()
                .makeTableSearchTermNotifyUseCase( this, tableSearchTermsID );
        tableSearchTermInteractionUseCase = UseCaseFactoryInstance.get()
                .makeTableSearchTermInteractionUseCase( tableSearchTermsID );

        setToggleMessageDecoderAction();
        toggleDecoderComposite();
    }

    private void setToggleMessageDecoderAction()
    {
        toggleMessageDecoderAction = new Action( "", IAction.AS_CHECK_BOX )
        {
            @Override
            public void run()
            {
                IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .getActivePart();
                if (workbenchPart instanceof DependencyGraphEditor)
                {
                    toggleDecoderComposite();
                }
            }
        };

        toggleMessageDecoderAction.setToolTipText( "Toggle Message Decoder" );
        ImageDescriptor originalImageDescriptor = ViewerCommonPlugin.getDefault()
                .getImageDescriptor( "icons/decoder.png" );
        toggleMessageDecoderAction.setImageDescriptor( originalImageDescriptor );
        toggleMessageDecoderAction.setChecked( false );

        filteredTable.getColorToolbar().add( toggleMessageDecoderAction );
        filteredTable.getColorToolbar().update( true );
    }

    @Override
    public void widgetDisposed(DisposeEvent e)
    {
        unregisterListeners();
        messageDecoder.dispose();
        eventsOfComrelationUseCase.unregister();
        preferencesNotifyUseCase.unregister();
        tableSearchTermNotifyUseCase.unregister();
        tableSearchTermInteractionUseCase.unregister();
    }

    private void createViewContextMenu()
    {
        MenuManager menuManager = new MenuManager();
        Composite menuOwner = filteredTable.getViewer().getTable();
        Menu menu = menuManager.createContextMenu( menuOwner );
        menuOwner.setMenu( menu );
        ColumnViewer selectionProvider = filteredTable.getViewer();

        site.registerContextMenu( CONTEXT_MENU_ID, menuManager, selectionProvider );
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
    }

    private List<RowFormatter> createTableColumns()
    {
        List<RowFormatter> columnProviders = new ArrayList<RowFormatter>();

        timestampColumnLabelProvider = new MessagesViewTimestampColumnLabelProvider( getTreeViewer() );
        filteredTable.createColumn( "Timestamp", timestampColumnLabelProvider );

        ValueColumnLabelProvider valueColumnLabelProvider = new ValueColumnLabelProvider();
        columnProviders.add( valueColumnLabelProvider );
        filteredTable.createColumn( "Message", valueColumnLabelProvider );
        columnProviders.add( valueColumnLabelProvider );

        return columnProviders;
    }

    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection)
    {
        if ((part instanceof GraphView) && selection instanceof IStructuredSelection && !selection.isEmpty())
        {
            refreshPropertyTesterEvaluation();// TODO is this necessary?
        }
    }

    private void refreshPropertyTesterEvaluation()
    {
        IEvaluationService evalServ = getEvaluationServiceToUpdatePropertyOfHandler();
        if (evalServ != null)
        {
            getEvaluationServiceToUpdatePropertyOfHandler()
                    .requestEvaluation( COM_ELEKTROBIT_EBRACE_VIEWER_COMMON_EXPORT_EVENTS_IS_ANY_TABLE_CONTENT );
        }
    }

    private IEvaluationService getEvaluationServiceToUpdatePropertyOfHandler()
    {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService( IEvaluationService.class );
    }

    @Override
    public TableViewer getTreeViewer()
    {
        return filteredTable.getViewer();
    }

    @Override
    public List<?> getContent()
    {
        return currentInput;
    }

    private void registerListeners()
    {
        selectionListener = new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                IStructuredSelection selection = (IStructuredSelection)event.getSelection();
                Object first = selection.getFirstElement();
                if (first == null)
                {
                    messageDecoder.decodeRuntimeEvent( null );
                }
                else if (first instanceof RuntimeEvent<?>)
                {
                    messageDecoder.decodeRuntimeEvent( (RuntimeEvent<?>)first );
                }
            }
        };
        filteredTable.getViewer().addPostSelectionChangedListener( selectionListener );
        tableLockService = GenericOSGIServiceRegistration.registerService( TableLockListener.class, this );
    }

    private void unregisterListeners()
    {
        filteredTable.getViewer().removePostSelectionChangedListener( selectionListener );
        tableLockService.unregister();
    }

    @Override
    public void toggleDecoderComposite()
    {
        if (messageDecoder.toggleAndReturnExclude())
        {
            container.getParent().setLayout( new GridLayout( 1, true ) );
            toggleMessageDecoderAction.setChecked( false );
        }
        else
        {
            container.getParent().setLayout( new GridLayout( 2, true ) );
            toggleMessageDecoderAction.setChecked( true );
        }
        container.getParent().layout();
    }

    @Override
    public void toggleScrollLock()
    {
        filteredTable.toggleScrollLock();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onNewComRelationEventData(List<?> events)
    {
        currentInput = events;
        filteredTable.setInput( (List<Object>)events );
    }

    @Override
    public void onTimestampFormatChanged(String timestampFormatPreferences)
    {
        filteredTable.getViewer().refresh();
    }

    @Override
    public void onJumpToTimeMarker(TimeMarker timeMarker)
    {
        filteredTable.centerElement( timeMarker );
    }

    @Override
    public void onTimeMarkerRenamed(TimeMarker timeMarker)
    {
        filteredTable.getViewer().refresh();
    }

    @Override
    public void onTextEntered(String text)
    {
        tableSearchTermInteractionUseCase.addSearchTerm( text );
        eventsOfComrelationUseCase.setFilterText( text );
    }

    @Override
    public void onComboItemSelected(int index)
    {
        String selectedItem = searchTerms.get( index );
        eventsOfComrelationUseCase.setFilterText( selectedItem );
    }

    @Override
    public void onClearComboSelected()
    {
        tableSearchTermInteractionUseCase.deleteAllTerms();
    }

    @Override
    public void onSearchTermsChanged(List<String> searchTerms)
    {
        this.searchTerms = searchTerms;
        filteredTable.setComboContent( searchTerms );
    }
}
