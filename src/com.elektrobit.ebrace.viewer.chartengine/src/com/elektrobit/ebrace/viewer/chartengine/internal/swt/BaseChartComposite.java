/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.chartengine.internal.swt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.ServiceRegistration;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceRegistration;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.allChannels.AllChannelsNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.allChannels.AllChannelsNotifyUseCase;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.preferences.api.UserInteractionPreferences;
import com.elektrobit.ebrace.core.preferences.listener.UserInteractionPreferencesListener;
import com.elektrobit.ebrace.viewer.chartengine.internal.ChartEditor;
import com.elektrobit.ebrace.viewer.common.propertySupport.EBRacePropertyChangeSupport;
import com.elektrobit.ebrace.viewer.common.propertySupport.PropertyChangeConstants;
import com.elektrobit.ebrace.viewer.common.view.ICyclicPainterComponent;
import com.elektrobit.ebrace.viewer.common.view.IResourcesModelView;
import com.elektrobit.ebrace.viewer.preferences.ViewerPreferencesPlugin;
import com.elektrobit.ebrace.viewer.preferences.util.ChartPreferencesConstants;

/** Base chart composite. All chart composites have to extend this abstract class. */
public abstract class BaseChartComposite extends Composite
        implements
            ICyclicPainterComponent,
            ISelectionProvider,
            IPropertyChangeListener,
            UserInteractionPreferencesListener,
            IResourcesModelView,
            PropertyChangeListener,
            AllChannelsNotifyCallback
{
    /** The layout of the chart container composite. */
    protected StackLayout chartCompositeLayout;
    protected final UserInteractionPreferences userInteractionPreferences = new GenericOSGIServiceTracker<UserInteractionPreferences>( UserInteractionPreferences.class )
            .getService();

    /** The live chart part of the line chart. */
    protected BaseLiveChartComposite<?> liveChartComposite;
    /** the analysis part of the chart. */
    protected BaseAnalysisChartComposite<?> analysisChartComposite;

    private DropTarget dropTarget;
    private final ChartEditor chartEditor;
    protected final ChartModel modelToDisplay;

    private final AllChannelsNotifyUseCase allChannelsNotifyUseCase;

    public BaseChartComposite(ChartEditor editor, Composite parent, int style)
    {
        super( parent, style );
        this.chartEditor = editor;
        this.modelToDisplay = (ChartModel)this.chartEditor.getChartEditorInput().getModel();
        createChartCompositeLayout();
        setupChartComposite();
        repaintChart();
        userInteractionPreferenceListener = GenericOSGIServiceRegistration
                .registerService( UserInteractionPreferencesListener.class, this );
        ViewerPreferencesPlugin.getDefault().getPreferenceStore().addPropertyChangeListener( this );
        EBRacePropertyChangeSupport.addPropertyChangeListener( this );
        allChannelsNotifyUseCase = UseCaseFactoryInstance.get().makeAllChannelsNotifyUseCase( this );
    }

    private void setupChartComposite()
    {
        switchChartComposite( isLiveMode() );
    }

    public DropTarget getDropTarget()
    {
        return dropTarget;
    }

    protected void createChartCompositeLayout()
    {
        this.chartCompositeLayout = new StackLayout();
        this.setLayout( chartCompositeLayout );
    }

    private boolean isLiveMode()
    {
        return userInteractionPreferences.isLiveMode();
    }

    private void setupLiveChartCompositeIfNeeded()
    {
        if (liveChartComposite == null)
        {
            liveChartComposite = setupLiveChartComposite();
        }
    }

    protected abstract BaseLiveChartComposite<?> setupLiveChartComposite();

    private void setupAnalysisChartCompositeIfNeeded()
    {
        if (analysisChartComposite == null)
        {
            analysisChartComposite = setupAnalysisChartComposite();
        }
    }

    protected abstract BaseAnalysisChartComposite<?> setupAnalysisChartComposite();

    @Override
    public void dispose()
    {
        EBRacePropertyChangeSupport.removePropertyChangeListener( this );
        ViewerPreferencesPlugin.getDefault().getPreferenceStore().removePropertyChangeListener( this );
        userInteractionPreferenceListener.unregister();
        disposeLiveChartComposite();
        disposeAnalysisChartComposite();
        allChannelsNotifyUseCase.unregister();
        super.dispose();
    }

    public void switchChartComposite(boolean liveChart)
    {
        if (liveChart || !modelToDisplay.getLineChartModelSettings().isShowOverviewChart())
        {
            setupLiveChartCompositeIfNeeded();
            disposeAnalysisChartComposite();
            liveChartComposite.repaint();
            setChartCompositeLayoutTopControl( liveChartComposite.getChartComposite() );
        }
        else
        {
            setupAnalysisChartCompositeIfNeeded();
            disposeLiveChartComposite();
            analysisChartComposite.recreateCharts();
            setChartCompositeLayoutTopControl( analysisChartComposite.getChartComposite() );
        }

    }

    private void disposeLiveChartComposite()
    {
        if (liveChartComposite != null)
        {
            liveChartComposite.dispose();
            liveChartComposite = null;
        }
    }

    private void disposeAnalysisChartComposite()
    {
        if (analysisChartComposite != null)
        {
            analysisChartComposite.disposeCharts();
            analysisChartComposite = null;
        }
    }

    public void repaintChart()
    {
        this.layout( true );
    }

    /**
     * Returns the type of the data which can be displayed in this chart.
     * 
     * @return the channel data type. e.g. Number, Boolean, etc.
     */
    public abstract Class<?> getAssignableChannelDataType();

    protected void setChartCompositeLayoutTopControl(Composite newTopControl)
    {
        chartCompositeLayout.topControl = newTopControl;
    }

    public BaseAnalysisChartComposite<?> getAnalysisChartComposite()
    {
        return analysisChartComposite;
    }

    public BaseLiveChartComposite<?> getLiveChartComposite()
    {
        return liveChartComposite;
    }

    public ChartEditor getChartEditor()
    {
        return chartEditor;
    }

    @Override
    public Control getControl()
    {
        return this;
    }

    @Override
    public void updateParentComponentValues()
    {
    }

    List<ISelectionChangedListener> listeners = new ArrayList<ISelectionChangedListener>();
    ISelection selection;
    private final ServiceRegistration<?> userInteractionPreferenceListener;

    @Override
    public void setSelection(ISelection selection)
    {
        this.selection = selection;
        final SelectionChangedEvent event = new SelectionChangedEvent( this, selection );
        for (ISelectionChangedListener listener : listeners)
        {
            listener.selectionChanged( event );
        }

    }

    @Override
    public ISelection getSelection()
    {
        return selection;
    }

    @Override
    public void addSelectionChangedListener(ISelectionChangedListener listener)
    {
        listeners.add( listener );

    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener listener)
    {
        listeners.remove( listener );

    }

    @Override
    public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event)
    {
        if (event.getProperty().equals( ChartPreferencesConstants.CHART_TIME_PRESENTATION_PREFERENCE_ID ))
        {
            switchChartComposite( userInteractionPreferences.isLiveMode() );
            repaintChart();
        }
        else if (event.getProperty().equals( ChartPreferencesConstants.DATA_CHART_PRESENTATION_PREFERENCE_ID )
                || event.getProperty().equals( ChartPreferencesConstants.LINE_CHART_PRESENTATION_PREFERENCE_ID )
                || event.getProperty().equals( ChartPreferencesConstants.CHART_TIME_PRESENTATION_PREFERENCE_ID )
                || event.getProperty().equals( ChartPreferencesConstants.LINE_CHART_PRESENTATION_Y_AXIS_PREFERENCE_ID )
                || event.getProperty().equals( ChartPreferencesConstants.LINE_CHART_PRESENTATION_Y_AXIS_FIX_MAX )
                || event.getProperty().equals( ChartPreferencesConstants.LINE_CHART_PRESENTATION_Y_AXIS_FIX_MIN ))
        {
            modelToDisplay.loadGlobalSettings();
            switchChartComposite( userInteractionPreferences.isLiveMode() );
            repaintChart();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (evt.getPropertyName().equals( PropertyChangeConstants.MODEL_PROPERTIES_CHANGED ))
        {
            if (evt.getSource().equals( this.modelToDisplay ))
            {
                switchChartComposite( userInteractionPreferences.isLiveMode() );
                repaintChart();
            }
        }
    }

    @Override
    public void onIsLiveModeChanged(final boolean isLiveMode)
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            @Override
            public void run()
            {
                switchChartComposite( isLiveMode );
                repaintChart();
            }
        } );
    }

    @Override
    public ResourceModel getModel()
    {
        return chartEditor.getChartEditorInput().getModel();
    }

    public ChartModel getModelToDisplay()
    {
        return modelToDisplay;
    }

    @Override
    public boolean setFocus()
    {
        if (analysisChartComposite != null)
        {
            return analysisChartComposite.getAnalysisChartCanvas().setFocus();
        }
        else if (liveChartComposite != null)
        {
            return liveChartComposite.getLiveChartCanvas().setFocus();
        }
        return false;
    }
}
