/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.channelsview;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ViewPart;

import com.elektrobit.ebrace.core.interactor.api.allChannels.ChannelTreeNodeNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.allChannels.ChannelTreeNodeNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorCallback;
import com.elektrobit.ebrace.core.interactor.api.channelcolor.ChannelColorUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.createresource.CreateResourceInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ResouceTreeNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ResourceTreeNotifyCallback;
import com.elektrobit.ebrace.viewer.channelsview.treemodel.ChannelLazyTreeContentProvider;
import com.elektrobit.ebrace.viewer.channelsview.treemodel.ChannelLazyTreeLabelProvider;
import com.elektrobit.ebrace.viewer.common.swt.CommonFilteredTree;
import com.elektrobit.ebrace.viewer.common.transfer.RuntimeEventChannelTransfer;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class ChannelsView extends ViewPart
        implements
            ChannelTreeNodeNotifyCallback,
            ChannelColorCallback,
            ResourceTreeNotifyCallback,
            CreateResourceInteractionCallback
{

    private final String CONTEXT_MENU_ID = "com.elektrobit.ebrace.viewer.ChannelsView.contextmenu";
    private static final String SEARCHBAR_DEFAULT_TEXT = "Search..";

    private final Map<RuntimeEventChannel<?>, Rectangle> channelColors = new LinkedHashMap<RuntimeEventChannel<?>, Rectangle>();
    private CommonFilteredTree filteredTree;
    private ChannelTreeNodeNotifyUseCase channelTreeNodeNotifyUseCase;
    private ChannelColorUseCase channelColorUseCase;
    private final ResourceManager resourceManager = new LocalResourceManager( JFaceResources.getResources() );
    private ResouceTreeNotifyUseCase resouceTreeUseCase;
    private CreateResourceInteractionUseCase createResourceUseCase;
    private String searchTerm;
    private TreeViewerColumn colorColumn;

    @Override
    public void createPartControl(Composite parent)
    {
        parent.setLayout( new GridLayout() );

        createUseCases();
        createTreeViewer( parent );
        addDragSupport();
        createViewContextMenu();
        addListeners();
    }

    private void createUseCases()
    {
        channelTreeNodeNotifyUseCase = UseCaseFactoryInstance.get().makeChannelTreeNodeNotifyUseCase( this );
        channelColorUseCase = UseCaseFactoryInstance.get().makeChannelColorUseCase( this );
        resouceTreeUseCase = UseCaseFactoryInstance.get().makeResouceTreeNotifyUseCase( this );
        createResourceUseCase = UseCaseFactoryInstance.get().makeCreateResourceUseCase( this );
    }

    private void createTreeViewer(Composite parent)
    {
        TreeColumnLayout layout = new TreeColumnLayout();

        int treeStyle = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION | SWT.VIRTUAL;
        filteredTree = new CommonFilteredTree( parent, treeStyle, new PatternFilter() )
        {
            @Override
            protected Control createTreeControl(Composite parent, int style)
            {
                Control c = super.createTreeControl( parent, style );
                c.setLayoutData( null );
                c.getParent().setLayout( layout );
                return c;
            }
        };
        filteredTree.setInitialText( SEARCHBAR_DEFAULT_TEXT );

        TreeViewer viewer = filteredTree.getViewer();
        viewer.setContentProvider( new ChannelLazyTreeContentProvider( viewer ) );
        viewer.setUseHashlookup( true );

        ColumnViewerToolTipSupport.enableFor( viewer );
        getSite().setSelectionProvider( viewer );

        Tree tree = viewer.getTree();
        tree.setHeaderVisible( true );
        tree.setLinesVisible( false );

        TreeViewerColumn mainColumn = createTreeColumn( viewer, "Name" );
        TreeViewerColumn logLevelColumn = createTreeColumn( viewer, "Log Level" );
        TreeViewerColumn unitColumn = createTreeColumn( viewer, "Unit" );
        colorColumn = createTreeColumn( viewer, "Color" );

        layout.setColumnData( mainColumn.getColumn(), new ColumnWeightData( 70, 70 ) );
        layout.setColumnData( logLevelColumn.getColumn(), new ColumnWeightData( 30, 30 ) );
        layout.setColumnData( unitColumn.getColumn(), new ColumnWeightData( 30, 30 ) );
        layout.setColumnData( colorColumn.getColumn(), new ColumnWeightData( 30, 30 ) );

        mainColumn
                .setLabelProvider( new ChannelLazyTreeLabelProvider( RuntimeEventChannel.CommonParameterNames.NAME ) );
        logLevelColumn
                .setLabelProvider( new ChannelLazyTreeLabelProvider( RuntimeEventChannel.CommonParameterNames.LOG_LEVEL ) );
        unitColumn
                .setLabelProvider( new ChannelLazyTreeLabelProvider( RuntimeEventChannel.CommonParameterNames.TYPE ) );
        colorColumn.setLabelProvider( new ChannelLazyTreeLabelProvider( RuntimeEventChannel.CommonParameterNames.COLOR,
                                                                        channelColorUseCase,
                                                                        resourceManager ) );
    }

    private TreeViewerColumn createTreeColumn(TreeViewer tree, String textColumn)
    {
        TreeViewerColumn column = new TreeViewerColumn( tree, SWT.NONE );
        column.getColumn().setText( textColumn );
        column.getColumn().setWidth( 100 );
        return column;
    }

    private void addDragSupport()
    {
        Transfer[] transferTypes = new Transfer[]{RuntimeEventChannelTransfer.getInstance()};
        filteredTree.getViewer().addDragSupport( DND.DROP_MOVE | DND.DROP_COPY,
                                                 transferTypes,
                                                 new RuntimeEventChannelDragSourceAdapter( filteredTree.getViewer() ) );
    }

    private void createViewContextMenu()
    {
        TreeViewer treeViewer = filteredTree.getViewer();
        Tree tree = treeViewer.getTree();

        MenuManager menuManager = new MenuManager();
        Menu contextMenu = menuManager.createContextMenu( tree );
        tree.setMenu( contextMenu );

        getSite().registerContextMenu( CONTEXT_MENU_ID, menuManager, treeViewer );
    }

    private void addListeners()
    {
        addMeasureItemListener();
        addPaintItemListener();
        addMouseListener();
        addModifyListenerToFilterControl();
    }

    private void addMeasureItemListener()
    {
        colorColumn.getViewer().getControl().addListener( SWT.MeasureItem, new Listener()
        {
            @Override
            public void handleEvent(Event event)
            {
                event.width += SColorImagePainter.IMAGE_BORDER_LENGTH;
            }
        } );
    }

    private void addPaintItemListener()
    {
        colorColumn.getViewer().getControl()
                .addListener( SWT.PaintItem, new ColorFieldListener( channelColorUseCase, channelColors ) );
    }

    private void addMouseListener()
    {
        filteredTree.getViewer().getTree()
                .addMouseListener( new ChannelsViewMouseListener( createResourceUseCase,
                                                                  channelColorUseCase,
                                                                  channelColors,
                                                                  resourceManager,
                                                                  this ) );
    }

    private void addModifyListenerToFilterControl()
    {
        filteredTree.getFilterControl().addModifyListener( new ModifyListener()
        {
            private boolean previousTermNotEmpty = false;

            @Override
            public void modifyText(ModifyEvent e)
            {
                searchTerm = filteredTree.getFilterControl().getText();
                collapseNodesIfSearchTermCleared();
                previousTermNotEmpty = !searchTerm.isEmpty();
                channelTreeNodeNotifyUseCase.setSearchTerm( searchTerm );
            }

            private void collapseNodesIfSearchTermCleared()
            {
                if (searchTerm.isEmpty() && previousTermNotEmpty)
                {
                    filteredTree.getViewer().collapseAll();
                }
            }
        } );
    }

    @Override
    public void onChartChannelsTypeMismatch()
    {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        MessageBox box = new MessageBox( shell, SWT.ICON_ERROR | SWT.OK );
        box.setMessage( "Could not create chart or table. The channel type is not supported." );
        box.setText( "Wrong channel type" );
        box.open();
    }

    @Override
    public void setFocus()
    {
        filteredTree.setFocus();
    }

    @Override
    public void dispose()
    {
        if (channelTreeNodeNotifyUseCase != null)
        {
            channelTreeNodeNotifyUseCase.unregister();
        }
        if (channelColorUseCase != null)
        {
            channelColorUseCase.unregister();
        }
        resouceTreeUseCase.unregister();
        resourceManager.dispose();
        createResourceUseCase.unregister();

        super.dispose();
    }

    @Override
    public void onChannelTreeChanged(ChannelTreeNode channelRootNode)
    {
        filteredTree.setInput( channelRootNode );
        expandAllNodesIfSearchActive();
    }

    private void expandAllNodesIfSearchActive()
    {
        if (searchTerm != null && !searchTerm.isEmpty())
        {
            filteredTree.getViewer().expandAll();
        }
    }

    @Override
    public void onColorAssigned(Collection<RuntimeEventChannel<?>> channels)
    {
        filteredTree.getViewer().refresh();
    }

    public ISelection getSelection()
    {
        return filteredTree.getViewer().getSelection();
    }

    public String getSearchTerm()
    {
        return searchTerm;
    }

    @Override
    public void onDerivedResourceAlreadyExists()
    {
    }

    @Override
    public void onNewResourceTreeData(List<ResourcesFolder> folders)
    {
    }

    @Override
    public void revealResource(ResourceModel resourceModel)
    {
    }

    @Override
    public void openResource(ResourceModel resourceModel)
    {
    }

    @Override
    public void onProVersionNotAvailable()
    {
    }

}
