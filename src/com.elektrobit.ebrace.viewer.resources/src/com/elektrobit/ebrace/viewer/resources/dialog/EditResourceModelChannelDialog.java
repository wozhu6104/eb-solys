/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.tableinput.DataCollector;
import com.elektrobit.ebrace.core.interactor.api.tableinput.FilteredTableInputNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.tableinput.FilteredTableNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableSearchTermNotifyUseCase;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.viewer.common.provider.RuntimeEventChannelColumnLabelProvider;
import com.elektrobit.ebrace.viewer.common.provider.RuntimeEventChannelTypeColumnLabelProvider;
import com.elektrobit.ebrace.viewer.common.swt.CommonFilteredTable;
import com.elektrobit.ebrace.viewer.common.swt.FilteredRuntimeEventChannelListComposite;
import com.elektrobit.ebrace.viewer.common.swt.SearchComboListener;
import com.elektrobit.ebrace.viewer.resources.constants.ResourcesConstants;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

@SuppressWarnings("unchecked")
public abstract class EditResourceModelChannelDialog extends EditResourceModelNameDialog
        implements
            FilteredTableNotifyCallback,
            DisposeListener,
            SearchComboListener,
            TableSearchTermNotifyCallback

{
    private static final String VIEW_ID = "com.elektrobit.ebrace.viewer.resources.dialog.EditResourceModelChannelDialog";
    private static final String CHANNEL_TYPE = "Type";
    private static final String CHANNEL_NAME = "Name";
    public static final String[] PROPS = {CHANNEL_NAME, CHANNEL_TYPE};

    protected FilteredRuntimeEventChannelListComposite filteredRuntimeEventChannelListComposite;
    protected Set<RuntimeEventChannel<?>> checkedChannels = new LinkedHashSet<RuntimeEventChannel<?>>();
    private Button showSelectedOnlyCheckbox;
    private Button selectAllCheckbox;
    private CheckboxTableViewer checkboxTableViewer;
    protected static GenericOSGIServiceTracker<ResourcesModelManager> resourceManagerTracker = new GenericOSGIServiceTracker<ResourcesModelManager>( ResourcesModelManager.class );
    private CommonFilteredTable filteredTable;
    private Composite container;

    private FilteredTableInputNotifyUseCase filteredTableInputNotifyUseCase;
    private final List<RowFormatter> columnsToApplyFilter = new ArrayList<RowFormatter>();

    private boolean isShowSelectedOnlyCheckboxSelected;
    private TableSearchTermNotifyUseCase searchTermNotifyUseCase;
    private TableSearchTermInteractionUseCase searchTermInteractionUseCase;
    private List<String> searchTerms = Collections.emptyList();

    /**
     * @param parentShell
     *            the parentShell
     * @param modelToEdit
     *            if null is passed the method {@link #createResourceModel()} is called
     */
    public EditResourceModelChannelDialog(Shell parentShell, ResourceModel modelToEdit)
    {
        super( parentShell, modelToEdit );
        if (modelToEdit != null)
        {
            checkedChannels.addAll( resourceModel.getChannels() );
        }
    }

    /**
     * @param parentShell
     *            the parentShell
     * @param modelToEdit
     *            if null is passed the method {@link #createResourceModel()} is called
     * @param selectedChannels
     *            preselected Channels
     */
    public EditResourceModelChannelDialog(Shell parentShell, ResourceModel modelToEdit,
            List<RuntimeEventChannel<?>> selectedChannels)
    {
        super( parentShell, modelToEdit );
        checkedChannels.addAll( selectedChannels );
    }

    @Override
    protected Control createContents(Composite parent)
    {
        Composite container = new Composite( parent, SWT.BORDER );
        container.addDisposeListener( this );
        container.setLayout( new GridLayout() );
        container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        super.createContents( container );
        Composite content = new Composite( (Composite)getDialogArea(), SWT.NONE );
        content.setLayout( new GridLayout( 2, false ) );
        content.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        createChannelsPart( content );
        createShowSelectedOnlyCheckedButton( (Composite)getDialogArea() );
        createSelectAllCheckbox( (Composite)getDialogArea() );
        addListenersAndProviders();
        filteredTableInputNotifyUseCase.collectAndPostNewData();
        return container;
    }

    @SuppressWarnings("rawtypes")
    private void createChannelsPart(Composite parent)
    {
        container = new Composite( parent, SWT.BORDER );
        container.setLayout( new GridLayout() );
        container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        filteredTable = new CommonFilteredTable( container,
                                                 SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER
                                                         | SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.CHECK,
                                                 "" );
        filteredTable.setShouldShowFirst( true );

        DataCollector dataCollector = new DataCollector()
        {
            @Override
            public List<Object> collectData()
            {
                if (isShowSelectedOnlyCheckboxSelected)
                {
                    return new ArrayList<Object>( checkedChannels );
                }
                return (List)getAllValidChannelsForResourceModel();
            }
        };

        filteredTableInputNotifyUseCase = UseCaseFactoryInstance.get()
                .makeFilteredTableInputNotifyUseCase( this, dataCollector, columnsToApplyFilter );

        checkboxTableViewer = (CheckboxTableViewer)filteredTable.getViewer();
        createColumns();

        filteredTable.registerComboListener( this );
        searchTermNotifyUseCase = UseCaseFactoryInstance.get().makeTableSearchTermNotifyUseCase( this, VIEW_ID );
        searchTermInteractionUseCase = UseCaseFactoryInstance.get().makeTableSearchTermInteractionUseCase( VIEW_ID );
    }

    @Override
    public void onInputChanged(List<Object> inputList)
    {
        filteredTable.setInput( inputList );
        validateFields();
    }

    private void createColumns()
    {
        RuntimeEventChannelColumnLabelProvider nameColumnLabelProvider = new RuntimeEventChannelColumnLabelProvider();
        filteredTable.createColumn( CHANNEL_NAME, nameColumnLabelProvider );
        columnsToApplyFilter.add( nameColumnLabelProvider );

        filteredTable.createColumn( CHANNEL_TYPE, new RuntimeEventChannelTypeColumnLabelProvider() );
        filteredTable.getViewer().setColumnProperties( PROPS );
    }

    @Override
    protected void changeResourceModel()
    {
        resourceModel.setChannels( new ArrayList<RuntimeEventChannel<?>>( checkedChannels ) );
    }

    private void createShowSelectedOnlyCheckedButton(Composite parent)
    {
        Composite content = new Composite( parent, SWT.NONE );
        content.setLayout( new GridLayout() );
        showSelectedOnlyCheckbox = new Button( content, SWT.CHECK );
        showSelectedOnlyCheckbox.setText( ResourcesConstants.SHOW_SELECTED_ONLY );
        if (resourceModel != null)
        {
            showSelectedOnlyCheckbox.setSelection( true );
            showOnlySelectedChannels( true );
        }
    }

    private void createSelectAllCheckbox(Composite parent)
    {
        Composite content = new Composite( parent, SWT.NONE );
        content.setLayout( new GridLayout() );
        selectAllCheckbox = new Button( content, SWT.CHECK );
        selectAllCheckbox.setText( ResourcesConstants.SELECT_ALL );
        selectAllCheckbox.setSelection( false );
    }

    /**
     * By default this method returns a list of all runtime event channels
     * 
     * @return
     */
    protected List<RuntimeEventChannel<?>> getAllValidChannelsForResourceModel()
    {
        RuntimeEventAcceptor runtimeEventAcceptor = new GenericOSGIServiceTracker<RuntimeEventAcceptor>( RuntimeEventAcceptor.class )
                .getService();
        return new ArrayList<RuntimeEventChannel<?>>( runtimeEventAcceptor.getRuntimeEventChannels() );
    }

    private void addListenersAndProviders()
    {
        addCheckStateProviderToCheckboxTableViewer();
        addCheckStateListenerToCheckboxTableViewer();
        addSelectionListenerToShowSelectedOnlyCheckbox();
        addSelectionListenerToSelectAllCheckbox();
    }

    private void addCheckStateProviderToCheckboxTableViewer()
    {
        checkboxTableViewer.setCheckStateProvider( new ICheckStateProvider()
        {
            @Override
            public boolean isChecked(Object element)
            {
                return checkedChannels.contains( element );
            }

            @Override
            public boolean isGrayed(Object element)
            {
                return false;
            }
        } );
    }

    private void addCheckStateListenerToCheckboxTableViewer()
    {
        checkboxTableViewer.addCheckStateListener( new ICheckStateListener()
        {
            @Override
            public void checkStateChanged(CheckStateChangedEvent event)
            {
                Object o = event.getElement();
                if (o instanceof RuntimeEventChannel<?>)
                {
                    RuntimeEventChannel<?> channel = (RuntimeEventChannel<?>)o;
                    boolean checked = event.getChecked();
                    // fix for the bug EBRACE-1949: checkbox cannot be ticked when a row is selected;
                    // check is inverted to permit the selection of the checkbox
                    if (isCheckedChannelSelected( channel ))
                    {
                        checked = !checked;
                    }
                    if (checked)
                    {
                        checkedChannels.add( channel );
                    }
                    else
                    {
                        checkedChannels.remove( channel );
                    }
                }
                validateFields();
            }

            private boolean isCheckedChannelSelected(RuntimeEventChannel<?> checkedChannel)
            {
                IStructuredSelection selectionOfTable = (IStructuredSelection)checkboxTableViewer.getSelection();
                for (Object o : selectionOfTable.toList())
                {
                    if (o.equals( checkedChannel ))
                    {
                        return true;
                    }
                }
                return false;
            }
        } );
    }

    private void addSelectionListenerToShowSelectedOnlyCheckbox()
    {
        showSelectedOnlyCheckbox.addSelectionListener( new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                showOnlySelectedChannels( ((Button)e.getSource()).getSelection() );
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        } );
    }

    private void showOnlySelectedChannels(boolean isShowOnlySelectedCheckboxSelected)
    {
        this.isShowSelectedOnlyCheckboxSelected = isShowOnlySelectedCheckboxSelected;
        filteredTableInputNotifyUseCase.collectAndPostNewData();
        validateFields();
    }

    private void addSelectionListenerToSelectAllCheckbox()
    {
        selectAllCheckbox.addSelectionListener( new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                if (e.getSource() instanceof Button)
                {
                    Button button = (Button)e.getSource();
                    boolean isButtonSelected = button.getSelection();
                    Collection<RuntimeEventChannel<?>> input = (Collection<RuntimeEventChannel<?>>)checkboxTableViewer
                            .getInput();
                    checkboxTableViewer.setAllChecked( isButtonSelected );
                    if (isButtonSelected)
                    {
                        checkedChannels.addAll( input );
                    }
                    else
                    {
                        checkedChannels.removeAll( input );
                    }
                }
                validateFields();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        } );
    }

    @Override
    public void validateFields()
    {
        super.validateFields();
        validateCheckedChannels();
        validateSelectAllCheckbox();
        checkOkButton();
    }

    private void validateCheckedChannels()
    {
        if (checkedChannels.isEmpty())
        {
            setErrorMessage( ResourcesConstants.ERROR_TEXT_CHANNEL );
            setOkButtonFlagToFalse();
        }
    }

    private void validateSelectAllCheckbox()
    {
        if (selectAllCheckbox != null && checkboxTableViewer != null)
        {
            Collection<RuntimeEventChannel<?>> input = (Collection<RuntimeEventChannel<?>>)checkboxTableViewer
                    .getInput();
            if (input != null)
            {
                selectAllCheckbox.setSelection( checkedChannels.containsAll( input ) );
            }
        }
    }

    @Override
    public void onSearchTermsChanged(List<String> searchTerms)
    {
        this.searchTerms = searchTerms;
        filteredTable.setComboContent( searchTerms );
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
    public void widgetDisposed(DisposeEvent e)
    {
        filteredTableInputNotifyUseCase.unregister();
        searchTermNotifyUseCase.unregister();
        searchTermInteractionUseCase.unregister();
    }
}
