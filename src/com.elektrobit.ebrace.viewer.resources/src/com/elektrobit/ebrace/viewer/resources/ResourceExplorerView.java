/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources;

import java.util.List;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ViewPart;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionStateNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ResouceTreeNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ResourceTreeNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.changed.ScriptChangedNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.script.changed.ScriptChangedNotifyUseCase;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.viewer.common.provider.ResourceModelLabelProvider;
import com.elektrobit.ebrace.viewer.common.provider.ResourcesModelContentProvider;
import com.elektrobit.ebrace.viewer.common.swt.CommonFilteredTree;
import com.elektrobit.ebrace.viewer.resources.handler.OpenRaceResourceHandler;
import com.elektrobit.ebrace.viewer.resources.listeners.OpenResourcesModelDoubleClickListener;

import lombok.extern.log4j.Log4j;

@Log4j
public class ResourceExplorerView extends ViewPart
        implements
            ScriptChangedNotifyCallback,
            ResourceTreeNotifyCallback,
            IPartListener
{
    private static final String VIEW_ID = "com.elektrobit.ebrace.viewer.resourceExplorer";
    private static final String SEARCHBAR_DEFAULT_TEXT = "Search..";

    private CommonFilteredTree filterTree;
    private OpenResourcesModelDoubleClickListener openResDblClickListener;
    private ResourcesModelContentProvider contentProvider;

    private ResouceTreeNotifyUseCase resouceTreeUseCase;
    private ConnectionStateNotifyUseCase connectionStateNotifyUseCase;

    private final GenericOSGIServiceTracker<ResourcesModelManager> resourceManagerTracker = new GenericOSGIServiceTracker<ResourcesModelManager>( ResourcesModelManager.class );
    private final OpenRaceResourceHandler openResourceHandler = new OpenRaceResourceHandler( VIEW_ID );
    private ResourceModelLabelProvider labelProvider;
    private ScriptChangedNotifyUseCase scriptChangedNotifyUseCase;

    @Override
    public void createPartControl(Composite parent)
    {
        parent.setLayout( new GridLayout() );
        PatternFilter filter = new ShowChildrenPatternFilter();
        filter.setIncludeLeadingWildcard( true );
        filterTree = new CommonFilteredTree( parent,
                                             SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION,
                                             filter );
        setUpFilterTree();
        createViewContextMenu( filterTree.getViewer(), filterTree.getViewer().getTree(), "chartList.menu" );
        getSite().setSelectionProvider( filterTree.getViewer() );
        registerListeners();
        resouceTreeUseCase = UseCaseFactoryInstance.get().makeResouceTreeNotifyUseCase( this );
        connectionStateNotifyUseCase = UseCaseFactoryInstance.get()
                .makeConnectionStateNotifyUseCase( openResourceHandler );
        scriptChangedNotifyUseCase = UseCaseFactoryInstance.get().makeScriptChangedNotifyUseCase( this );
    }

    private void setUpFilterTree()
    {
        contentProvider = new ResourcesModelContentProvider();
        filterTree.setInitialText( SEARCHBAR_DEFAULT_TEXT );
        labelProvider = new ResourceModelLabelProvider();

        TreeViewer treeViewer = filterTree.getViewer();
        Tree tree = treeViewer.getTree();
        treeViewer.setContentProvider( contentProvider );
        treeViewer.setLabelProvider( labelProvider );
        treeViewer.setInput( "root" );
        tree.setHeaderVisible( false );
        tree.setLinesVisible( false );
        ColumnViewerToolTipSupport.enableFor( treeViewer );
        getSite().setSelectionProvider( treeViewer );
        setUpEditingSupport( treeViewer );
    }

    private void setUpEditingSupport(TreeViewer treeViewer)
    {
        ColumnViewerEditorActivationStrategy strategy = new ColumnViewerEditorActivationStrategy( treeViewer )
        {
            @Override
            protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event)
            {
                return event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
            }
        };

        TreeViewerEditor.create( treeViewer, strategy, ColumnViewerEditor.DEFAULT );

        TreeViewerColumn treeViewerColumn = filterTree.createColumn( "Resources", labelProvider );
        EditingSupport editingSupport = new ResourceExplorerViewEditingSupport( treeViewer,
                                                                                resourceManagerTracker.getService() );
        treeViewerColumn.setEditingSupport( editingSupport );
    }

    private void createViewContextMenu(ColumnViewer selProv, Composite menuOwner, String id)
    {
        // Create menu manager.
        MenuManager menuMgr = new MenuManager();
        // Create menu.
        Menu menu = menuMgr.createContextMenu( menuOwner );
        menuOwner.setMenu( menu );
        // Register menu for extension.
        getSite().registerContextMenu( id, menuMgr, selProv );
        menuMgr.addMenuListener( new IMenuListener()
        {

            @Override
            public void menuAboutToShow(IMenuManager manager)
            {
                ICommandService commandService = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getService( ICommandService.class );
                commandService.refreshElements( "com.elektrobit.ebrace.viewer.script.runScript", null );
            }
        } );
    }

    @Override
    public void dispose()
    {
        unregisterListeneres();
        resouceTreeUseCase.unregister();
        connectionStateNotifyUseCase.unregister();
        scriptChangedNotifyUseCase.unregister();
        super.dispose();
    }

    @Override
    public void setFocus()
    {
        filterTree.setFocus();
    }

    @Override
    public void onNewResourceTreeData(List<ResourcesFolder> folders)
    {
        contentProvider.setInput( folders );
        filterTree.getViewer().refresh();
    }

    @Override
    public void openResource(ResourceModel resourceModel)
    {
        openResourceHandler.openResource( resourceModel );
    }

    @Override
    public void revealResource(ResourceModel resourceModel)
    {
        filterTree.getViewer().reveal( resourceModel );
        filterTree.getViewer().setSelection( new StructuredSelection( new Object[]{resourceModel} ) );
    }

    private void unregisterListeneres()
    {
        filterTree.getViewer().removeDoubleClickListener( openResDblClickListener );
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().removePartListener( this );
    }

    private void registerListeners()
    {
        openResDblClickListener = new OpenResourcesModelDoubleClickListener( openResourceHandler, filterTree );
        filterTree.getViewer().addDoubleClickListener( openResDblClickListener );
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener( this );
    }

    @Override
    public void scriptInfoChanged(final RaceScriptInfo scriptName)
    {
        final RaceScriptResourceModel raceScriptResourceModel = resourceManagerTracker.getService()
                .getRaceScriptResourceModel( scriptName );
        Display.getDefault().asyncExec( new Runnable()
        {
            @Override
            public void run()
            {
                if (raceScriptResourceModel != null)
                {
                    filterTree.getViewer().refresh( raceScriptResourceModel );
                }
                else
                {
                    log.warn( "Couldn't find script to refresh. Name was " + scriptName.getName() );
                }
            }
        } );
    }

    public void editElement(Object element, int column)
    {
        filterTree.getViewer().editElement( element, column );
    }

    @Override
    public void partOpened(IWorkbenchPart part)
    {
    }

    @Override
    public void partClosed(IWorkbenchPart part)
    {
        openResourceHandler.onCloseEditor( part );
    }

    @Override
    public void partActivated(IWorkbenchPart part)
    {
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part)
    {
    }

    @Override
    public void partDeactivated(IWorkbenchPart part)
    {
    }

}
