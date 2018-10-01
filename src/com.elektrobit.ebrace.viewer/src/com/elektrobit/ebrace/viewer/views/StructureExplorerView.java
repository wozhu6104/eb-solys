/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.views;

import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectStructureInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectStructureInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectedStructureCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectedStructureNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.structure.StructureNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.StructureNotifyUseCase;
import com.elektrobit.ebrace.viewer.CheckBoxColumnEditingSupport;
import com.elektrobit.ebrace.viewer.StructureSelectionUtil;
import com.elektrobit.ebrace.viewer.StructureTreesContentProvider;
import com.elektrobit.ebrace.viewer.graph.PropertyPatternFilter;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodesCheckState;

public class StructureExplorerView
        implements
            StructureNotifyCallback,
            SelectStructureInteractionCallback,
            SelectedStructureCallback,
            DisposeListener
{

    private final String CONTEXT_MENU_ID = "com.elektrobit.ebrace.viewer.editor.DependencyGraphEditor";

    private Composite rootComposite;

    private FilteredTree filteredTree;

    private TreeViewerColumnBuilder treeColumnBuilder;
    private StructureNotifyUseCase structureNotifyUseCase;
    private CheckboxColumnImagePainter checkboxColumnImagePainter;
    private CheckBoxColumnEditingSupport checkBoxColumnEditingSupport;

    private SelectStructureInteractionUseCase selectStructureInteractionUseCase;
    private SelectedStructureNotifyUseCase selectedStructureNotifyUseCase;

    private final IWorkbenchPartSite site;

    public StructureExplorerView(Composite parent, IWorkbenchPartSite site)
    {
        this.site = site;
        parent.addDisposeListener( this );
        createPartControl( parent );
    }

    private void createPartControl(Composite parent)
    {
        setupRootComposite( parent );
        createFilteredTree();
        createTreeColumn();
        createCheckBoxColumn();
        createTypeColumn();
        registerContextMenu();
        setupSelectionProvider();
        changeColorOfSelection();

        structureNotifyUseCase = UseCaseFactoryInstance.get().makeStructureNotifyUseCase( this );
        selectStructureInteractionUseCase = UseCaseFactoryInstance.get().makeSelectStructureInteractionUseCase( this );
        selectedStructureNotifyUseCase = UseCaseFactoryInstance.get().makeSelectedStructureNotifyUseCase( this );
    }

    private void setupRootComposite(final Composite parent)
    {
        rootComposite = new Composite( parent, SWT.BORDER );
        rootComposite.setLayout( new GridLayout() );
    }

    private void createFilteredTree()
    {
        PatternFilter filter = new PropertyPatternFilter();
        filteredTree = new FilteredTree( rootComposite,
                                         SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION,
                                         filter,
                                         true );

        filteredTree.getViewer().getTree().setHeaderVisible( true );
        filteredTree.getViewer().getTree().setLayoutData( new GridData( GridData.FILL_BOTH ) );
        filteredTree.getViewer().setContentProvider( new StructureTreesContentProvider() );

        TableTreeToolTipSupport.enableFor( filteredTree.getViewer(), ToolTip.NO_RECREATE );
    }

    private void createTreeColumn()
    {
        treeColumnBuilder = new TreeViewerColumnBuilder( filteredTree.getViewer() )
                .columnHeaderIcon( "GENIVI_Structure_Hierarchy", "png" ).columnWidth( 200 )
                .labelProvider( new TreeColumnLabelProvider() );
        treeColumnBuilder.build();
    }

    private void createTypeColumn()
    {
        new TreeViewerColumnBuilder( filteredTree.getViewer() ).columnHeaderName( "Layer" ).columnWidth( 100 )
                .labelProvider( new TypeColumnLabelProvider() ).build();
    }

    private void registerContextMenu()
    {
        MenuManager menuManager = new MenuManager();
        Menu menu = menuManager.createContextMenu( filteredTree.getViewer().getControl() );

        filteredTree.getViewer().getControl().setMenu( menu );
        site.registerContextMenu( CONTEXT_MENU_ID, menuManager, filteredTree.getViewer() );
    }

    private void createCheckBoxColumn()
    {
        TreeViewerColumnBuilder treeViewerColumnBuilder = new TreeViewerColumnBuilder( filteredTree.getViewer() );

        treeViewerColumnBuilder.columnHeaderIcon( "views/structure_explorer/visible", "png" )
                .columnHeaderTooltip( "Visible" ).columnWidth( 28 ).columnHeaderResizable( false );

        TreeColumn checkboxColumn = treeViewerColumnBuilder.getTreeViewerColumn().getColumn();
        checkboxColumnImagePainter = new CheckboxColumnImagePainter( checkboxColumn );
        treeViewerColumnBuilder.listener( SWT.PaintItem, checkboxColumnImagePainter );
        checkBoxColumnEditingSupport = new CheckBoxColumnEditingSupport( filteredTree.getViewer() );
        treeViewerColumnBuilder.columnEditingSupport( checkBoxColumnEditingSupport ).build();
    }

    private void setupSelectionProvider()
    {
        // TreeNode will be set as selection provider to
        // allow activation of
        // ShowConnectedNodesHandler
        site.setSelectionProvider( filteredTree.getViewer() );

        filteredTree.getViewer().addSelectionChangedListener( new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                List<TreeNode> selectedTreeNodes = StructureSelectionUtil
                        .getTreeNodesFromSelection( event.getSelection() );
                if (selectedTreeNodes != null && !selectedTreeNodes.isEmpty())
                {
                    selectStructureInteractionUseCase.setNodesSelected( selectedTreeNodes );
                }
            }
        } );
    }

    private void changeColorOfSelection()
    {
        filteredTree.getViewer().getTree().addListener( SWT.EraseItem, new Listener()
        {
            @Override
            public void handleEvent(Event event)
            {
                event.detail &= ~SWT.HOT;
                if ((event.detail & SWT.SELECTED) == 0)
                {
                    return; /* item not selected */
                }
                int clientWidth = filteredTree.getViewer().getTree().getClientArea().width;
                GC gc = event.gc;

                org.eclipse.swt.graphics.Color oldForeground = gc.getForeground();
                org.eclipse.swt.graphics.Color oldBackground = gc.getBackground();
                gc.setForeground( Display.getCurrent().getSystemColor( SWT.COLOR_WHITE ) );
                gc.setBackground( Display.getCurrent().getSystemColor( SWT.COLOR_GRAY ) );
                gc.fillRectangle( 0, event.y, clientWidth, event.height );

                gc.setForeground( oldForeground );
                gc.setBackground( oldBackground );
                event.detail &= ~SWT.SELECTED;
            }
        } );
    }

    public void setFocus()
    {
        filteredTree.setFocus();
    }

    @Override
    public void widgetDisposed(DisposeEvent e)
    {
        structureNotifyUseCase.unregister();
        selectStructureInteractionUseCase.unregister();
        selectedStructureNotifyUseCase.unregister();
    }

    @Override
    public void onStructureChanged(List<Tree> allTrees, TreeNodesCheckState nodesCheckState)
    {
        checkboxColumnImagePainter.setTreeCheckStates( nodesCheckState );
        Object currentInput = filteredTree.getViewer().getInput();
        if (currentInput == null)
        {
            filteredTree.getViewer().setInput( allTrees );
        }
        else
        {
            filteredTree.getViewer().refresh();
            filteredTree.getViewer().getTree().redraw();
        }
    }

    @Override
    public void onNodesSelected(List<TreeNode> nodes)
    {
        ISelection selection = StructureSelectionUtil.getSelectionStructureFromNodes( nodes );
        filteredTree.getViewer().setSelection( selection );
    }

    @Override
    public void onComRelationsSelected(List<ComRelation> comRelations)
    {
        filteredTree.getViewer().setSelection( null );
    }

    @Override
    public void onSelectionCleared()
    {
        filteredTree.getViewer().setSelection( null );
    }
}
