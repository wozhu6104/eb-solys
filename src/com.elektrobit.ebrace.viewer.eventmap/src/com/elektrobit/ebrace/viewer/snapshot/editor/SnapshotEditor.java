/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.snapshot.editor;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.services.IEvaluationService;

import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.snapshot.SnapshotModel;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ModelNameNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ModelNameNotifyUseCase;
import com.elektrobit.ebrace.resources.api.model.BaseResourceModel;
import com.elektrobit.ebrace.viewer.common.dnd.RuntimeeventChannelDropTargetAdapter;
import com.elektrobit.ebrace.viewer.common.transfer.RuntimeEventChannelTransfer;
import com.elektrobit.ebrace.viewer.common.view.IResourcesModelView;
import com.elektrobit.ebrace.viewer.resources.editor.ResourcesModelEditorInput;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class SnapshotEditor extends EditorPart
        implements
            IResourcesModelView,
            ISelectionProvider,
            ChannelColorCallback,
            ModelNameNotifyCallback
{
    private final String CONTEXT_MENU_ID = "com.elektrobit.ebrace.viewer.eventmap.editor.contextmenu";
    private final ListenerList<ISelectionChangedListener> listeners = new ListenerList<>();
    private ChannelColorUseCase channelColorUseCase;
    private ChannelsSnapshotDecoderComposite eventMapDecoder;
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
        setInput( input );
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
        parent.setLayout( new GridLayout() );
        parent.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        eventMapDecoder = new ChannelsSnapshotDecoderComposite( parent, SWT.BORDER );
        createViewContextMenu( eventMapDecoder.getFilteredTree().getViewer().getTree(), CONTEXT_MENU_ID );
        registerListeners();
        addDropSupport();
        firePropertyChange( PROP_TITLE );
        updateChannelList();
    }

    private void createViewContextMenu(Composite menuOwner, String id)
    {
        // Create menu manager.
        MenuManager menuMgr = new MenuManager();
        // Create menu.
        Menu menu = menuMgr.createContextMenu( menuOwner );
        menuOwner.setMenu( menu );
        // Register menu for extension.
        getSite().registerContextMenu( id, menuMgr, eventMapDecoder.getFilteredTree().getViewer() );
        IEvaluationService evServ = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getService( IEvaluationService.class );
        evServ.requestEvaluation( "com.elektrobit.ebrace.viewer.common.timemarker.isTimeMarkerVisible.isTimeMarkerVisible" );
    }

    private void registerListeners()
    {
        getSite().setSelectionProvider( eventMapDecoder.getFilteredTree().getViewer() );
        channelColorUseCase = UseCaseFactoryInstance.get().makeChannelColorUseCase( this );

        modelNameNotifyUseCase = UseCaseFactoryInstance.get().makeModelNameNotifyUseCase( this );
        SnapshotModel model = (SnapshotModel)((ResourcesModelEditorInput)getEditorInput()).getModel();
        modelNameNotifyUseCase.register( model );
    }

    @Override
    public void setFocus()
    {
        eventMapDecoder.setFocus();
    }

    @Override
    public String getTitle()
    {
        if (getEditorInput() != null)
        {
            return getEditorInput().getName();
        }
        return super.getTitle();
    }

    private void addDropSupport()
    {
        Transfer[] dropTransferTypes = new Transfer[]{RuntimeEventChannelTransfer.getInstance()};

        eventMapDecoder.getFilteredTree().getViewer()
                .addDropSupport( DND.DROP_MOVE | DND.DROP_COPY,
                                 dropTransferTypes,
                                 new RuntimeeventChannelDropTargetAdapter( ((ResourcesModelEditorInput)getEditorInput())
                                         .getModel(), Object.class ) );
    }

    private void updateChannelList()
    {
        eventMapDecoder.setResourceModel( (BaseResourceModel)((ResourcesModelEditorInput)getEditorInput()).getModel() );

        assignColorsToAllUsedChannels();
    }

    private void assignColorsToAllUsedChannels()
    {
        List<RuntimeEventChannel<?>> channels = ((ResourcesModelEditorInput)getEditorInput()).getModel().getChannels();
        for (RuntimeEventChannel<?> channel : channels)
        {
            channelColorUseCase.getColorOfChannel( channel );
        }
    }

    @Override
    public void dispose()
    {
        unregisterListenersAndServices();
        eventMapDecoder.dispose();
        super.dispose();
    }

    private void unregisterListenersAndServices()
    {
        channelColorUseCase.unregister();
        modelNameNotifyUseCase.unregister();
    }

    public ResourcesModelEditorInput getChartEditorInput()
    {
        return (ResourcesModelEditorInput)getEditorInput();
    }

    @Override
    public ResourceModel getModel()
    {
        return ((ResourcesModelEditorInput)getEditorInput()).getModel();
    }

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
        listeners.add( listener );
    }

    @Override
    public ISelection getSelection()
    {
        return new StructuredSelection( ((ResourcesModelEditorInput)getEditorInput()).getModel() );
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener)
    {
        listeners.remove( listener );
    }

    @Override
    public void setSelection(ISelection select)
    {
        Object[] list = listeners.getListeners();
        for (int i = 0; i < list.length; i++)
        {
            ((ISelectionChangedListener)list[i]).selectionChanged( new SelectionChangedEvent( this, select ) );
        }
    }

    @Override
    public void onColorAssigned(Collection<RuntimeEventChannel<?>> channels)
    {
        eventMapDecoder.getFilteredTree().getViewer().refresh();
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
}
