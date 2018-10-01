/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.part.EditorPart;
import org.osgi.framework.ServiceRegistration;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceRegistration;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ModelNameNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ModelNameNotifyUseCase;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.preferences.listener.UserInteractionPreferencesListener;
import com.elektrobit.ebrace.resources.api.model.BaseResourceModel;
import com.elektrobit.ebrace.viewer.chartengine.internal.handler.ChartLegendChannelSelectionHandler;
import com.elektrobit.ebrace.viewer.chartengine.internal.handler.ClearSelectionInLegendMouseListener;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.BaseAnalysisChartCanvas;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.BaseAnalysisChartComposite;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.BaseChartCanvas;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.BaseChartComposite;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.BaseFullChartCanvas;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.BaseLiveChartComposite;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.gantt.GanttChartComposite;
import com.elektrobit.ebrace.viewer.chartengine.internal.swt.line.LineChartComposite;
import com.elektrobit.ebrace.viewer.chartengine.yAxis.YAxisLegendWidthService;
import com.elektrobit.ebrace.viewer.common.ViewerCommonPlugin;
import com.elektrobit.ebrace.viewer.common.dnd.RuntimeeventChannelDropTargetAdapter;
import com.elektrobit.ebrace.viewer.common.timemarker.dnd.RuntimeeventTimestampDropTargetAdapter;
import com.elektrobit.ebrace.viewer.common.transfer.RuntimeEventChannelTransfer;
import com.elektrobit.ebrace.viewer.common.transfer.RuntimeEventTimstampTransfer;
import com.elektrobit.ebrace.viewer.common.view.IResourcesModelView;
import com.elektrobit.ebrace.viewer.resources.editor.ResourcesModelEditorInput;
import com.elektrobit.ebrace.viewer.snapshot.editor.ChannelsSnapshotDecoderComposite;

public class ChartEditor extends EditorPart
        implements
            UserInteractionPreferencesListener,
            IResourcesModelView,
            IPartListener2,
            ModelNameNotifyCallback
{
    private final String CONTEXT_MENU_ID = "chartEditor.legend.contextMenu";

    private final UserInteractionPreferences userInteractionPreferences = new GenericOSGIServiceTracker<UserInteractionPreferences>( UserInteractionPreferences.class )
            .getService();
    private final YAxisLegendWidthService yAxisLegendWidthService = new GenericOSGIServiceTracker<YAxisLegendWidthService>( YAxisLegendWidthService.class )
            .getService();
    private BaseChartComposite rootComposite;
    private ServiceRegistration<?> runningStateChangedServiceRegistration;
    private ResourcesModelEditorInput editorInput;
    private SashForm sash;
    private ChannelsSnapshotDecoderComposite legend;
    private Composite content;
    private ModelNameNotifyUseCase modelNameNotifyUseCase;
    private final double WIDTH_PERCENT_OF_CHART_COMPOSITE = 0.75;
    private final double WIDTH_PERCENT_OF_CHART_LEGEND_COMPOSITE = 0.25;

    private ChartLegendChannelSelectionHandler chartLegendChannelSelectionHandler;

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
        editorInput = (ResourcesModelEditorInput)input;
        setInput( editorInput );
        registerListenersAndServices();
    }

    private void registerListenersAndServices()
    {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener( this );
        runningStateChangedServiceRegistration = GenericOSGIServiceRegistration
                .registerService( UserInteractionPreferencesListener.class, this );
        modelNameNotifyUseCase = UseCaseFactoryInstance.get().makeModelNameNotifyUseCase( this );
        modelNameNotifyUseCase.register( editorInput.getModel() );
    }

    @Override
    public void dispose()
    {
        yAxisLegendWidthService.notifyChartClosed( (ChartModel)editorInput.getModel() );
        rootComposite.dispose();
        unregisterListenersAndServices();
        super.dispose();
    }

    private void unregisterListenersAndServices()
    {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().removePartListener( this );
        runningStateChangedServiceRegistration.unregister();
        modelNameNotifyUseCase.unregister();
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
        parent.setLayout( new GridLayout( 1, false ) );
        sash = new SashForm( parent, SWT.HORIZONTAL );

        GridData layoutData = new GridData( SWT.FILL, SWT.FILL, true, true );
        layoutData.grabExcessHorizontalSpace = true;
        sash.setLayout( new GridLayout( 2, false ) );
        sash.setLayoutData( layoutData );

        content = new Composite( sash, SWT.BORDER );
        content.setLayout( new GridLayout( 1, false ) );
        content.setLayoutData( layoutData );
        initializeRootComposite( content );
        content.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_WHITE ) );

        legend = new ChannelsSnapshotDecoderComposite( sash, SWT.BORDER, false );
        legend.setResourceModel( (BaseResourceModel)getModel() );
        chartLegendChannelSelectionHandler = new ChartLegendChannelSelectionHandler( this, editorInput.getModel() );

        legend.getTreeViewerOfSnapshot().addSelectionChangedListener( chartLegendChannelSelectionHandler );
        firePropertyChange( PROP_TITLE );

        createViewContextMenu( legend.getTreeViewerOfSnapshot(),
                               legend.getTreeViewerOfSnapshot().getTree(),
                               CONTEXT_MENU_ID );
        registerMouseListenerToClearSelection();
        registerCompositeForChannelDrop( sash );
    }

    private void registerMouseListenerToClearSelection()
    {
        BaseAnalysisChartComposite<?> analysisChartComposite = rootComposite.getAnalysisChartComposite();
        BaseLiveChartComposite<?> liveChartComposite = rootComposite.getLiveChartComposite();
        ClearSelectionInLegendMouseListener clearSelectionInLegendMouseListener = new ClearSelectionInLegendMouseListener( legend );
        if (analysisChartComposite != null)
        {
            BaseAnalysisChartCanvas<?> analysisChartCanvas = analysisChartComposite.getAnalysisChartCanvas();
            BaseFullChartCanvas<?> fullChartCanvas = analysisChartComposite.getFullChartCanvas();

            analysisChartCanvas.addMouseListener( clearSelectionInLegendMouseListener );
            fullChartCanvas.addMouseListener( clearSelectionInLegendMouseListener );
        }

        if (liveChartComposite != null)
        {
            BaseChartCanvas<?> liveChartCanvas = liveChartComposite.getLiveChartCanvas();
            liveChartCanvas.addMouseListener( clearSelectionInLegendMouseListener );
        }
    }

    private void registerCompositeForChannelDrop(Composite composite)
    {
        DropTarget dropTarget = new DropTarget( composite, DND.DROP_COPY | DND.DROP_MOVE );
        Transfer[] transferTypes = new Transfer[]{RuntimeEventTimstampTransfer.getInstance(),
                RuntimeEventChannelTransfer.getInstance()};
        dropTarget.setTransfer( transferTypes );
        dropTarget.addDropListener( new RuntimeeventTimestampDropTargetAdapter() );
        dropTarget.addDropListener( new RuntimeeventChannelDropTargetAdapter( editorInput
                .getModel(), rootComposite.getAssignableChannelDataType() ) );
    }

    private void createViewContextMenu(ColumnViewer selProv, Composite menuOwner, String id)
    {
        MenuManager menuMgr = new MenuManager();
        Menu menu = menuMgr.createContextMenu( menuOwner );
        menuOwner.setMenu( menu );
        getSite().registerContextMenu( id, menuMgr, selProv );
    }

    private void createToolbar(Composite parent)
    {
        ToolBarManager localToolBarManager = new ToolBarManager();
        IMenuService menuService = PlatformUI.getWorkbench().getService( IMenuService.class );
        menuService.populateContributionManager( localToolBarManager, "toolbar:chartEditorToolBar" );
        Composite toolBarParent = new Composite( parent, SWT.FLAT );

        toolBarParent.setLayoutData( new GridData( SWT.RIGHT, SWT.FILL, true, false ) );
        toolBarParent.setLayout( new FillLayout() );
        localToolBarManager.createControl( toolBarParent )
                .setBackground( Display.getDefault().getSystemColor( SWT.COLOR_WHITE ) );
    }

    private void initializeRootComposite(Composite parent)
    {
        if (editorInput.getModel() instanceof ChartModel)
        {
            createToolbar( parent );
            parent.setBackground( Display.getDefault().getSystemColor( SWT.COLOR_WHITE ) );
            ChartModel chart = (ChartModel)editorInput.getModel();
            switch (chart.getType())
            {
                case GANTT_CHART :
                    rootComposite = new GanttChartComposite( this, parent, SWT.FLAT );
                    break;
                case LINE_CHART :
                    rootComposite = new LineChartComposite( this, parent, SWT.FLAT );
                    break;
                default :
                    break;
            }
            rootComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        }
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
    public void setFocus()
    {
        rootComposite.setFocus();
    }

    @Override
    public Image getTitleImage()
    {
        ChartModel model = (ChartModel)editorInput.getModel();
        switch (model.getType())
        {
            case GANTT_CHART :
                return ViewerCommonPlugin.getDefault().getImage( "chart_gantt", "png" );
            case LINE_CHART :
                return ViewerCommonPlugin.getDefault().getImage( "chart_line", "png" );
            default :
                break;
        }
        return super.getTitleImage();
    }

    public ResourcesModelEditorInput getChartEditorInput()
    {
        return editorInput;
    }

    private void repaintBaseChartComposite()
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            @Override
            public void run()
            {
                rootComposite.switchChartComposite( userInteractionPreferences.isLiveMode() );
                rootComposite.repaintChart();
            }
        } );
    }

    @Override
    public void onIsLiveModeChanged(boolean isLiveMode)
    {
        repaintBaseChartComposite();
    }

    public BaseChartComposite getRootComposite()
    {
        return rootComposite;
    }

    @Override
    public ResourceModel getModel()
    {
        return this.editorInput.getModel();
    }

    @Override
    public void partActivated(IWorkbenchPartReference partRef)
    {
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef)
    {
    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef)
    {
    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef)
    {
    }

    @Override
    public void partOpened(IWorkbenchPartReference partRef)
    {
        int[] weights = sash.getWeights();
        weights[0] = (int)(weights[1] * WIDTH_PERCENT_OF_CHART_COMPOSITE);
        weights[1] = (int)(weights[1] * WIDTH_PERCENT_OF_CHART_LEGEND_COMPOSITE);
        sash.setWeights( weights );
    }

    @Override
    public void partHidden(IWorkbenchPartReference partRef)
    {
        if (partRef.getPart( false ) == this)
        {
            yAxisLegendWidthService.addToHidden( (ChartModel)editorInput.getModel() );
        }
    }

    @Override
    public void partVisible(IWorkbenchPartReference partRef)
    {
        if (partRef.getPart( false ) == this)
        {
            yAxisLegendWidthService.removeFromHidden( (ChartModel)editorInput.getModel() );
        }
    }

    @Override
    public void partInputChanged(IWorkbenchPartReference partRef)
    {
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

    public ChannelsSnapshotDecoderComposite getChartLegend()
    {
        return legend;
    }
}
