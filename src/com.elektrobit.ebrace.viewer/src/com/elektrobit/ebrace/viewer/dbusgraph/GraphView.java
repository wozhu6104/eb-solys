/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.dbusgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.viewers.IZoomableWorkbenchPart;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectedStructureCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.SelectedStructureNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.structure.StructureNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.structure.StructureNotifyUseCase;
import com.elektrobit.ebrace.viewer.StructureSelectionUtil;
import com.elektrobit.ebrace.viewer.ViewerPlugin;
import com.elektrobit.ebrace.viewer.dbusgraph.layout.EBSpringLayoutAlgorithm;
import com.elektrobit.ebrace.viewer.dbusgraph.layout.LayoutNodeTools;
import com.elektrobit.ebrace.viewer.graph.GraphContentProvider;
import com.elektrobit.ebrace.viewer.graph.GraphLabelProvider;
import com.elektrobit.ebrace.viewer.views.ImageCombo;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationProvider;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodeCheckStateService.CHECKED_STATE;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNodesCheckState;

public class GraphView
        implements
            IZoomableWorkbenchPart,
            SelectedStructureCallback,
            StructureNotifyCallback,
            DisposeListener
{
    private static final String TREE_LEVEL = "TreeLevel";

    volatile boolean hideIsolatedNodesActive = true;
    private final EBSpringLayoutAlgorithm layoutAlgorithm = new EBSpringLayoutAlgorithm();
    TreeNodesCheckState nodesCheckState;
    private final List<TreeLevelDef> allTreeLevelDefs = new ArrayList<TreeLevelDef>();
    private final TreeLevelUtil treeLevelUtil = new TreeLevelUtil();

    private boolean disposed = false;
    private ResourceManager resManager = null;
    private FormToolkit formToolkit;
    private GraphViewer graphViewer;
    private Composite graphSettingsComposite;
    private Composite groupByComposite;
    private final DrawTask drawTask = new DrawTask();
    private ImageCombo groupByCombo;
    ImageCombo visibleTreeLayerCombo;

    private final ComRelationProvider comRelationProvider = new GenericOSGIServiceTracker<ComRelationProvider>( ComRelationProvider.class )
            .getService();
    private final SelectedStructureNotifyUseCase selectedStructureNotifyUseCase;
    private final StructureNotifyUseCase structureNotifyUseCase;

    public GraphView(Composite parent)
    {
        parent.addDisposeListener( this );
        createPartControl( parent );

        selectedStructureNotifyUseCase = UseCaseFactoryInstance.get().makeSelectedStructureNotifyUseCase( this );
        structureNotifyUseCase = UseCaseFactoryInstance.get().makeStructureNotifyUseCase( this );
    }

    private void createPartControl(Composite parent)
    {
        resManager = new LocalResourceManager( JFaceResources.getResources(), parent );

        Form form = createForm( parent );

        createSettingsComposite( form );
        createGroupByComposite();

        Composite graphComposite = createGraphCompositeAndView( form );

        ViewerFilter[] filters = {new NodeFilter( this ), new EdgesFilter( this, comRelationProvider )};
        graphViewer.setFilters( filters );

        graphComposite.layout( true );

        Timer timer = new Timer();
        timer.schedule( drawTask, 1, 1000 );

        LayoutNodeTools.cleanupGroupNodes( graphViewer.getGraphControl() );
        updateFilterGroup();
    }

    private Form createForm(Composite parent)
    {
        formToolkit = new FormToolkit( parent.getDisplay() );
        Form form = formToolkit.createForm( parent );
        formToolkit.decorateFormHeading( form );

        GridLayout gridLayout = new GridLayout( 1, true );
        form.getBody().setLayout( gridLayout );

        return form;
    }

    private void createSettingsComposite(Form form)
    {
        RowLayout settingsRowLayout = new RowLayout();
        settingsRowLayout.center = true;
        settingsRowLayout.pack = true;

        graphSettingsComposite = new Composite( form.getBody(), SWT.NONE );
        graphSettingsComposite.setLayout( settingsRowLayout );
        graphSettingsComposite.setBackground( Display.getCurrent().getSystemColor( SWT.COLOR_WHITE ) );

        addCheckBox( graphSettingsComposite );
        addSeparator( graphSettingsComposite, 30 );
        addVisibleTreeLayerComboBox( graphSettingsComposite );
        addSeparator( graphSettingsComposite, 30 );
        createGroupFilterComboBox( graphSettingsComposite );
    }

    private void createGroupByComposite()
    {
        RowLayout groupByRowLayout = new RowLayout();
        groupByRowLayout.center = true;
        groupByRowLayout.pack = true;

        groupByComposite = new Composite( graphSettingsComposite, SWT.NONE );
        groupByComposite.setLayout( groupByRowLayout );
        groupByComposite.setBackground( Display.getCurrent().getSystemColor( SWT.COLOR_WHITE ) );
    }

    private Composite createGraphCompositeAndView(Form form)
    {
        GridData gridData = new GridData();
        gridData.grabExcessVerticalSpace = true;
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = SWT.FILL;
        gridData.verticalAlignment = SWT.FILL;

        Composite graphComposite = new Composite( form.getBody(), SWT.NONE );
        graphComposite.setLayout( new FillLayout() );
        graphComposite.setLayoutData( gridData );

        graphViewer = new GraphViewer( graphComposite, SWT.NONE );
        graphViewer.setContentProvider( new GraphContentProvider() );
        graphViewer.setLabelProvider( new GraphLabelProvider() );

        graphViewer.setLayoutAlgorithm( layoutAlgorithm );

        return graphComposite;
    }

    private void setVisibleTreeLayer()
    {
        String visibleLayerString = ViewerPlugin.getPluginInstance().getPreferenceStore().getString( TREE_LEVEL );
        if (visibleLayerString == null || visibleLayerString.isEmpty())
        {
            visibleTreeLayerCombo.select( 0 );
        }
        else
        {
            int index = 0;
            for (TableItem item : visibleTreeLayerCombo.getItems())
            {
                if (item.getText().equals( visibleLayerString ))
                {
                    visibleTreeLayerCombo.select( index );
                    break;
                }
                index++;
            }

        }
        visibleTreeLayerCombo.notifyListeners( SWT.Selection, new Event() );
    }

    private void addVisibleTreeLayerComboBox(Composite parentComposite)
    {
        final Label label = new Label( parentComposite, SWT.NONE );
        label.setText( "Visible Tree Layer" );
        label.setBackground( Display.getCurrent().getSystemColor( SWT.COLOR_WHITE ) );
        visibleTreeLayerCombo = new ImageCombo( parentComposite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER );

        visibleTreeLayerCombo.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                updateGroupByComboBox();
                refresh();
                ViewerPlugin.getPluginInstance().getPreferenceStore().setValue( TREE_LEVEL,
                                                                                visibleTreeLayerCombo.getText() );
            }
        } );
    }

    private void addSeparator(Composite parent, int height)
    {
        Label sep = new Label( parent, SWT.SEPARATOR | SWT.VERTICAL );
        RowData sepData = new RowData();
        sepData.height = height;
        sep.setLayoutData( sepData );
    }

    private void addCheckBox(Composite parent)
    {
        final Button button = new Button( parent, SWT.CHECK );
        button.setText( "Hide isolated nodes" );
        button.setBackground( Display.getCurrent().getSystemColor( SWT.COLOR_WHITE ) );
        button.setSelection( hideIsolatedNodesActive );
        button.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                hideIsolatedNodesActive = !hideIsolatedNodesActive;
                refresh();
            }
        } );
    }

    private void createGroupFilterComboBox(Composite parent)
    {
        final Label label = new Label( parent, SWT.NONE );

        label.setText( "Group by:" );
        label.setBackground( Display.getCurrent().getSystemColor( SWT.COLOR_WHITE ) );

        groupByCombo = new ImageCombo( parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER );

        groupByCombo.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                updateFilterGroup();
            }
        } );
    }

    private void updateGroupByComboBox()
    {
        String selectedGrouping = groupByCombo.getText();
        groupByCombo.removeAll();

        List<TreeLevelDef> upperTreeLevels = treeLevelUtil
                .getUpperTreeLevelsOfTreeLevel( visibleTreeLayerCombo.getText() );

        for (TreeLevelDef nextTreeLevel : upperTreeLevels)
        {
            Image icon = resManager
                    .createImage( ImageDescriptor.createFromImage( new Image( null, nextTreeLevel.getIconPath() ) ) );
            groupByCombo.add( icon, nextTreeLevel.getName() );
        }

        if (visibleTreeLayerCombo.getText().isEmpty())
        {
            return;
        }

        int selectIndex = groupByCombo.getItemCount() - 1;
        for (int i = 0; i < groupByCombo.getItemCount(); i++)
        {
            if (groupByCombo.getItem( i ).equals( selectedGrouping ))
            {
                selectIndex = i;
                break;
            }
        }

        groupByCombo.select( selectIndex );
        graphSettingsComposite.pack();
        updateFilterGroup();
    }

    private String getCurrentLayoutFilterString()
    {
        return groupByCombo.getText();
    }

    private void updateFilterGroup()
    {
        layoutAlgorithm.setLayoutGroupFilterString( getCurrentLayoutFilterString() );
        refresh();
    }

    @Override
    public void widgetDisposed(DisposeEvent e)
    {
        disposed = true;
        formToolkit.dispose();
        if (resManager != null)
        {
            resManager.dispose();
        }
        if (selectedStructureNotifyUseCase != null)
        {
            selectedStructureNotifyUseCase.unregister();
        }
        if (structureNotifyUseCase != null)
        {
            structureNotifyUseCase.unregister();
        }
    }

    @Override
    public AbstractZoomableViewer getZoomableViewer()
    {
        return graphViewer;
    }

    private void refresh()
    {
        drawTask.add( new DrawCompleteTimerTask( this ) );
    }

    void drawComplete()
    {
        if (graphViewer != null && !disposed)
        {
            graphViewer.applyLayout();
            graphViewer.refresh();
        }
    }

    void update(TreeNode m_node)
    {
        if (graphViewer != null && !disposed)
        {
            graphViewer.update( m_node, null );
        }
    }

    @Override
    public void onNodesSelected(List<TreeNode> nodes)
    {
        ISelection selection = StructureSelectionUtil.getSelectionStructureFromNodes( nodes );
        graphViewer.setSelection( selection );
        refresh();
    }

    @Override
    public void onComRelationsSelected(List<ComRelation> comRelations)
    {
    }

    @Override
    public void onSelectionCleared()
    {
        graphViewer.setSelection( null );
    }

    @Override
    public void onStructureChanged(List<Tree> allTrees, TreeNodesCheckState nodesCheckState)
    {
        treeLevelUtil.setTreeList( allTrees );
        this.nodesCheckState = nodesCheckState;

        setLayerComboEntries();
        updateGroupByComboBox();

        updateGraphViewerInput( allTrees, nodesCheckState );
    }

    private void updateGraphViewerInput(List<Tree> allTrees, TreeNodesCheckState nodesCheckState)
    {
        List<TreeNode> allCheckedNodes = new ArrayList<TreeNode>();
        for (Tree tree : allTrees)
        {
            for (TreeNode treeNode : tree.toList())
            {
                CHECKED_STATE nodeCheckState = nodesCheckState.getNodeCheckState( treeNode );
                if (!nodeCheckState.equals( CHECKED_STATE.UNCHECKED ))
                {
                    allCheckedNodes.add( treeNode );
                }
            }
        }
        graphViewer.setInput( allCheckedNodes );
    }

    private void setLayerComboEntries()
    {
        List<TreeLevelDef> newLevelDefs = treeLevelUtil.extractTreeLevels();

        if (newLevelDefs.equals( allTreeLevelDefs ))
        {
            return;
        }

        allTreeLevelDefs.clear();
        allTreeLevelDefs.addAll( newLevelDefs );

        visibleTreeLayerCombo.removeAll();

        for (TreeLevelDef treeLevelDef : allTreeLevelDefs)
        {
            Image icon = resManager
                    .createImage( ImageDescriptor.createFromImage( new Image( null, treeLevelDef.getIconPath() ) ) );
            visibleTreeLayerCombo.add( icon, treeLevelDef.getName() );
        }
        setVisibleTreeLayer();
    }

    public void setFocus()
    {
        graphSettingsComposite.setFocus();
        forceLayout();
    }

    /**
     * When layout() is not called when switching back from another tab to Dependency Graph tab last 2 elements in the
     * settings row will not be visible.
     */
    private void forceLayout()
    {
        groupByComposite.getParent().getParent().layout( true );
    }
}
