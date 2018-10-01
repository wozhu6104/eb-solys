/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.timeline;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.TimeGraphViewer;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.model.TimeGraphEntry;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.widgets.TimeGraphControl;
import org.eclipse.tracecompass.tmf.ui.widgets.timegraph.widgets.Utils.TimeFormat;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.elektrobit.ebrace.core.interactor.api.actionexecution.ExecuteSTimeSegmentActionInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.allChannels.AllChannelsNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.chartdata.AnalysisTimespanNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.chartdata.AnalysisTimespanNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.chartdata.TimelineViewDataNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.chartdata.TimelineViewDataNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.resources.model.timelineview.TimelineViewModel;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ModelNameNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.resources.tree.ModelNameNotifyUseCase;
import com.elektrobit.ebrace.viewer.common.dnd.RuntimeeventChannelDropTargetAdapter;
import com.elektrobit.ebrace.viewer.common.timemarker.dnd.RuntimeeventTimestampDropTargetAdapter;
import com.elektrobit.ebrace.viewer.common.transfer.RuntimeEventChannelTransfer;
import com.elektrobit.ebrace.viewer.common.transfer.RuntimeEventTimstampTransfer;
import com.elektrobit.ebrace.viewer.resources.editor.ResourcesModelEditorInput;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.STimeSegment;

public class TimelineViewEditor extends EditorPart
        implements
            ModelNameNotifyCallback,
            TimelineViewDataNotifyCallback,
            AnalysisTimespanNotifyCallback,
            AllChannelsNotifyCallback
{
    private static final String CONTEXT_MENU_ID = "com.elektrobit.ebrace.viewer.chartengine.timelineview.menu";
    private static final int GRAPH_TOP_PADDING_FOR_TIMEMARKERS = 50;
    private ResourcesModelEditorInput editorInput;
    private TimeGraphViewer timeGraphViewer;
    private ModelNameNotifyUseCase modelNameNotifyUseCase;
    private TimelineViewDataNotifyUseCase timelineViewDataNotifyUseCase;
    private AnalysisTimespanNotifyUseCase timespanNotifyUseCase;
    private ExecuteSTimeSegmentActionInteractionUseCase executeSegmentActionInteractionUseCase;
    private TimelineViewPresentationProvider presentationProvider;
    private long fullTimeSpanStart;
    private long fullTimeSpanEnd;
    private final boolean timeBoundsInitiallySet = false;;

    public TimelineViewEditor()
    {
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
    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        setSite( site );
        editorInput = (ResourcesModelEditorInput)input;
        setInput( editorInput );
        registerListenersAndServices();
    }

    private void registerListenersAndServices()
    {
        modelNameNotifyUseCase = UseCaseFactoryInstance.get().makeModelNameNotifyUseCase( this );
        modelNameNotifyUseCase.register( editorInput.getModel() );
        executeSegmentActionInteractionUseCase = UseCaseFactoryInstance.get()
                .makeExecuteSTimeSegmentActionInteractionUseCase();
        UseCaseFactoryInstance.get().makeAllChannelsNotifyUseCase( this );
    }

    public TimelineViewModel getModel()
    {
        return (TimelineViewModel)editorInput.getModel();
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
        timespanNotifyUseCase = UseCaseFactoryInstance.get().makeAnalysisTimespanNotifyUseCase( this );

        parent.setLayout( new GridLayout( 1, false ) );

        ToolBarManager toolBarManager = new ToolBarManager();
        toolBarManager.createControl( parent );

        timeGraphViewer = new TimeGraphViewer( parent, SWT.NONE );
        Control control = timeGraphViewer.getControl();
        presentationProvider = new TimelineViewPresentationProvider( timeGraphViewer,
                                                                     (Composite)control,
                                                                     GRAPH_TOP_PADDING_FOR_TIMEMARKERS,
                                                                     timeGraphViewer.getNameSpace() );
        timeGraphViewer.setTimeGraphProvider( presentationProvider );

        activateFrameworkCustomFeatures();

        GridData graphLayoutData = new GridData( SWT.FILL, SWT.FILL, true, true );
        graphLayoutData.grabExcessHorizontalSpace = true;
        timeGraphViewer.setTimeFormat( TimeFormat.RELATIVE );
        timeGraphViewer.getControl().setLayoutData( graphLayoutData );

        addSelectionListener( timeGraphViewer );
        populateActionbar( toolBarManager, timeGraphViewer );

        timelineViewDataNotifyUseCase = UseCaseFactoryInstance.get().makeTimelineViewDataNotifyUseCase( this );
        timelineViewDataNotifyUseCase.register( (TimelineViewModel)editorInput.getModel() );

        registerCompositeForChannelDrop( timeGraphViewer.getTimeAlignedComposite() );
        TimeGraphControl timeGraphControl = timeGraphViewer.getTimeGraphControl();
        getSite().setSelectionProvider( timeGraphControl );
        registerContextMenu();
    }

    private void activateFrameworkCustomFeatures()
    {
        // timeGraphViewer.getTimeGraphControl().setGraphTopPadding( GRAPH_TOP_PADDING_FOR_TIMEMARKERS );
        // timeGraphViewer.getTimeGraphControl().setTimeSelectionEnabled( false );
        // timeGraphViewer.getTimeGraphScale().setDrawTimeDecorators( false );
    }

    private void registerCompositeForChannelDrop(Composite composite)
    {
        DropTarget dropTarget = new DropTarget( composite, DND.DROP_COPY | DND.DROP_MOVE );
        Transfer[] transferTypes = new Transfer[]{RuntimeEventTimstampTransfer.getInstance(),
                RuntimeEventChannelTransfer.getInstance()};
        dropTarget.setTransfer( transferTypes );
        dropTarget.addDropListener( new RuntimeeventTimestampDropTargetAdapter() );
        dropTarget.addDropListener( new RuntimeeventChannelDropTargetAdapter( editorInput.getModel(),
                                                                              STimeSegment.class ) );
    }

    private void registerContextMenu()
    {
        MenuManager menuManager = new MenuManager();
        Control menuOwner = timeGraphViewer.getTimeGraphControl();

        Menu menu = menuManager.createContextMenu( menuOwner );
        menuOwner.setMenu( menu );
        ISelectionProvider selectionProvider = timeGraphViewer.getSelectionProvider();
        getSite().registerContextMenu( CONTEXT_MENU_ID, menuManager, selectionProvider );
    }

    private void addSelectionListener(final TimeGraphViewer timeGraphViewer)
    {
        timeGraphViewer.getTimeGraphControl().addMouseListener( new MouseAdapter()
        {

            @Override
            /**
             * We have to listen to "mouseUp()", because normal selection listener is giving wrong values, because it is
             * internally triggered from framework on "mouseDown()", which is too soon, because selected time values
             * used for selection computation are only updated on "mouseUp()".
             */
            public void mouseUp(MouseEvent e)
            {
                StructuredSelection selection = (StructuredSelection)timeGraphViewer.getSelectionProvider()
                        .getSelection();
                Object[] selectionArray = selection.toArray();
                if (selectionArray.length == 2 && selectionArray[1] instanceof SolysTimeEvent)
                {
                    SolysTimeEvent clickedEvent = (SolysTimeEvent)selectionArray[1];
                    onTimeEventClicked( clickedEvent );
                }
            }
        } );

        new TimelineViewTimeMarkerClickListener( timeGraphViewer.getTimeGraphControl() );
    }

    private void onTimeEventClicked(SolysTimeEvent clickedEvent)
    {
        STimeSegment clickedSegment = clickedEvent.getTimeSegment();
        executeSegmentActionInteractionUseCase.executeClickAction( clickedSegment );
    }

    private void populateActionbar(ToolBarManager toolBarManager, TimeGraphViewer timeGraphViewer)
    {
        toolBarManager.add( timeGraphViewer.getZoomInAction() );
        toolBarManager.add( timeGraphViewer.getZoomOutAction() );
        toolBarManager.update( true );
    }

    @Override
    public void setFocus()
    {
        timeGraphViewer.setFocus();
    }

    @Override
    public void dispose()
    {
        timelineViewDataNotifyUseCase.unregister();
        modelNameNotifyUseCase.unregister();
        timespanNotifyUseCase.unregister();
        super.dispose();
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
    public void onNewTimelineData(Map<RuntimeEventChannel<STimeSegment>, List<STimeSegment>> newData)
    {
        TimelineDataConverter converter = new TimelineDataConverter( newData );
        List<TimeGraphEntry> input = converter.buildInput();
        presentationProvider.setPossibleColors( converter.getAllColors() );
        timeGraphViewer.setInput( input );

        if (!timeBoundsInitiallySet)
        {
            setTimeBounds( fullTimeSpanStart, fullTimeSpanEnd );
        }
        timeGraphViewer.refresh();
    }

    @Override
    public void onAnalysisTimespanChanged(long analysisTimespanStart, long analysisTimespanEnd)
    {
    }

    @Override
    public void onAnalysisTimespanLengthChanged(long timespanMicros)
    {
    }

    @Override
    public void onFullTimespanChanged(long start, long end)
    {
        this.fullTimeSpanStart = start;
        this.fullTimeSpanEnd = end;
    }

    private void setTimeBounds(long start, long end)
    {
        // if bounds are not set with this sequence of calls, horizontal scrollbar is not updated correctly
        timeGraphViewer.setTimeBounds( TimelineDataConverter.microsToNanos( start ),
                                       TimelineDataConverter.microsToNanos( end ) );
        timeGraphViewer.setTimeBounds();
        timeGraphViewer.setTimeBounds( TimelineDataConverter.microsToNanos( start ),
                                       TimelineDataConverter.microsToNanos( end ) );
    }

    public SolysTimeGraphEntry getSelection()
    {
        return (SolysTimeGraphEntry)timeGraphViewer.getSelection();
    }

    @Override
    public void onAllChannelsChanged(List<RuntimeEventChannel<?>> allChannels)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onChannelRemoved(RuntimeEventChannel<?> channel)
    {
        List<RuntimeEventChannel<?>> channelsOfModel = getModel().getChannels();
        channelsOfModel.remove( channel );
        getModel().setChannels( channelsOfModel );
    }

}
