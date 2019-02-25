/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.preferences.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;

import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings.LineChartRepresentation;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings.LineChartType;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings.LineChartYaxisScaleMode;
import com.elektrobit.ebrace.viewer.preferences.util.ChartPreferencesConstants;
import com.elektrobit.ebrace.viewer.preferences.util.MinMaxFieldEditor;

public abstract class LineChartSettingsBasePreferencePage extends PropertyPage implements IWorkbenchPreferencePage
{
    public static final String MESSAGE_FOR_MINMAX_ERROR = "Y-axis min must be a number smaller than Y-axis max";

    private Composite content;
    private Button chartTypeAsLineChartButton;
    private Button chartTypeAsBarChart;
    private Button chartRepresentationAsLineButton;
    private Button chartRepresentationAsFilledButton;
    private Button chartRepresentationAsStackedButton;
    private Button yScaleDynamicButton;
    private Button yScaleSemiDynamicButton;
    private Button yScaleFixButton;
    private Button showOverviewChartButton;
    private MinMaxFieldEditor yAxisPreferencesMinMax;

    private LineChartModelSettings lineChartModelSettings;

    @Override
    protected Control createContents(Composite parent)
    {
        createComposite( parent );
        createChartTypeComposite( parent );
        createChartRepresentationComposite( parent );
        createYAxisScaleTypeComposite( parent );
        setModelForSettings();
        return content;
    }

    private void setModelForSettings()
    {
        this.lineChartModelSettings = getModelToAdapt();
        loadSettingsFromModel();
    }

    public abstract LineChartModelSettings getModelToAdapt();

    private void loadSettingsFromModel()
    {
        loadLineChartTypeSettings();
        loadLineChartRepresentationSettings();
        loadYAxisScaleModeSettings();
        loadOverviewChartSettings();
        loadYaxisMinMaxSettings();
    }

    private void loadOverviewChartSettings()
    {
        boolean showOverviewChart = lineChartModelSettings.isShowOverviewChart();
        showOverviewChartButton.setSelection( showOverviewChart );
    }

    private void loadLineChartTypeSettings()
    {
        chartTypeAsBarChart.setSelection( false );
        chartTypeAsLineChartButton.setSelection( false );

        LineChartType lineChartType = lineChartModelSettings.getLineChartType();
        switch (lineChartType)
        {
            case BAR_CHART :
                chartTypeAsBarChart.setSelection( true );
                break;
            case LINE_CHART :
                chartTypeAsLineChartButton.setSelection( true );
                break;
            default :
                throw new UnsupportedOperationException( "Unexpected line chart type " + lineChartType );
        }
    }

    private void loadLineChartRepresentationSettings()
    {
        chartRepresentationAsLineButton.setSelection( false );
        chartRepresentationAsFilledButton.setSelection( false );
        chartRepresentationAsStackedButton.setSelection( false );

        LineChartRepresentation lineChartRepresentation = lineChartModelSettings.getLineChartRepresentation();
        switch (lineChartRepresentation)
        {
            case LINE_ONLY :
                chartRepresentationAsLineButton.setSelection( true );
                break;
            case FILLED :
                chartRepresentationAsFilledButton.setSelection( true );
                break;
            case STACKED :
                chartRepresentationAsStackedButton.setSelection( true );
                break;
            default :
                throw new UnsupportedOperationException( "Unexpected line chart representation "
                        + lineChartRepresentation );
        }
    }

    private void loadYAxisScaleModeSettings()
    {
        yScaleDynamicButton.setSelection( false );
        yScaleSemiDynamicButton.setSelection( false );
        yScaleFixButton.setSelection( false );

        LineChartYaxisScaleMode yaxisScaleMode = lineChartModelSettings.getLineChartYaxisScaleMode();
        switch (yaxisScaleMode)
        {
            case DYNAMIC :
                yScaleDynamicButton.setSelection( true );
                break;
            case SEMI_DYNAMIC :
                yScaleSemiDynamicButton.setSelection( true );
                break;
            case FIXED :
                yScaleFixButton.setSelection( true );
                break;
            default :
                throw new UnsupportedOperationException( "Unexpected yaxisScaleMode " + yaxisScaleMode );
        }
    }

    private void loadYaxisMinMaxSettings()
    {
        yAxisPreferencesMinMax.setMinValue( lineChartModelSettings.getYAxisMinValue() );
        yAxisPreferencesMinMax.setMaxValue( lineChartModelSettings.getYAxisMaxValue() );
    }

    @Override
    protected void performDefaults()
    {
        resetPropertiesDefault();
        super.performDefaults();
    }

    @Override
    protected void performApply()
    {
        saveModelToPreferences();
        super.performApply();
    }

    @Override
    public boolean performOk()
    {

        saveModelToPreferences();
        return super.performOk();
    }

    private void saveModelToPreferences()
    {
        setPropertiesToModel();
        saveAdaptedPreferences( lineChartModelSettings );
    }

    public abstract void saveAdaptedPreferences(LineChartModelSettings lineChartModelSettings);

    private void createComposite(Composite parent)
    {
        parent.setLayout( new GridLayout() );
        content = new Composite( parent, SWT.None );
        content.setLayout( new GridLayout() );
        content.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
    }

    private void createChartTypeComposite(Composite parent)
    {
        Label label = new Label( content, SWT.NONE );
        label.setText( ChartPreferencesConstants.LINE_CHART_DATA_PRESENTATION_LABEL );

        Composite chartTypeComposite = new Composite( content, SWT.NONE );
        chartTypeComposite.setLayout( new GridLayout() );

        chartTypeAsLineChartButton = new Button( chartTypeComposite, SWT.RADIO );
        chartTypeAsLineChartButton
                .setText( ChartPreferencesConstants.LINE_CHART_PRESENTATION_PREFERENCE_AS_AREA_STRING );

        chartTypeAsBarChart = new Button( chartTypeComposite, SWT.RADIO );
        chartTypeAsBarChart.setText( ChartPreferencesConstants.LINE_CHART_PRESENTATION_PREFERENCE_AS_BAR_STRING );
    }

    private void createChartRepresentationComposite(Composite parent)
    {
        Label label = new Label( this.content, SWT.NONE );
        label.setText( ChartPreferencesConstants.LINE_CHART_LINE_CHART_PRESENTATION_LABEL );

        Composite chartRepresentationComposite = new Composite( content, SWT.NONE );
        chartRepresentationComposite.setLayout( new GridLayout() );

        chartRepresentationAsFilledButton = new Button( chartRepresentationComposite, SWT.RADIO );
        chartRepresentationAsFilledButton
                .setText( ChartPreferencesConstants.LINE_CHART_PRESENTATION_PREFERENCE_AS_FILLED_TYPE_STRING );

        chartRepresentationAsLineButton = new Button( chartRepresentationComposite, SWT.RADIO );
        chartRepresentationAsLineButton
                .setText( ChartPreferencesConstants.LINE_CHART_PRESENTATION_PREFERENCE_AS_LINE_TYPE_STRING );

        chartRepresentationAsStackedButton = new Button( chartRepresentationComposite, SWT.RADIO );
        chartRepresentationAsStackedButton
                .setText( ChartPreferencesConstants.LINE_CHART_PRESENTATION_PREFERENCE_AS_STACKED_TYPE_STRING );
    }

    private void createYAxisScaleTypeComposite(Composite parent)
    {
        Label label = new Label( content, SWT.NONE );
        label.setText( ChartPreferencesConstants.LINE_CHART_Y_AXIS_FORMAT_LABEL );

        Composite yAxisScaleTypeComposite = new Composite( content, SWT.NONE );
        yAxisScaleTypeComposite.setLayout( new GridLayout( 2, false ) );
        yAxisScaleTypeComposite.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );

        GridData data = new GridData();
        data.horizontalSpan = 2;
        yScaleDynamicButton = new Button( yAxisScaleTypeComposite, SWT.RADIO );
        yScaleDynamicButton.setText( ChartPreferencesConstants.LINE_CHART_PRESENTATION_Y_AXIS_DYNAMIC );
        yScaleDynamicButton.setLayoutData( data );

        data = new GridData();
        data.horizontalSpan = 2;
        yScaleSemiDynamicButton = new Button( yAxisScaleTypeComposite, SWT.RADIO );
        yScaleSemiDynamicButton.setText( ChartPreferencesConstants.LINE_CHART_PRESENTATION_Y_AXIS_SEMI_DYNAMIC );
        yScaleSemiDynamicButton.setLayoutData( data );

        data = new GridData();
        data.horizontalSpan = 2;
        yScaleFixButton = new Button( yAxisScaleTypeComposite, SWT.RADIO );
        yScaleFixButton.setText( ChartPreferencesConstants.LINE_CHART_PRESENTATION_Y_AXIS_FIX );
        yScaleFixButton.setLayoutData( data );

        data = new GridData();
        data.horizontalSpan = 2;
        showOverviewChartButton = new Button( yAxisScaleTypeComposite, SWT.CHECK );
        showOverviewChartButton.setText( "Show Overview Chart in Analysis Mode" );
        showOverviewChartButton.setLayoutData( data );

        yAxisPreferencesMinMax = new MinMaxFieldEditor( yAxisScaleTypeComposite, "Y-axis min: ", "Y-axis max: " );
        yAxisPreferencesMinMax.setPage( this );
        yAxisPreferencesMinMax.setErrorMessage( MESSAGE_FOR_MINMAX_ERROR );
    }

    private void setPropertiesToModel()
    {
        if (chartTypeAsBarChart.getSelection())
        {
            lineChartModelSettings.setLineChartType( LineChartType.BAR_CHART );
        }
        else
        {
            lineChartModelSettings.setLineChartType( LineChartType.LINE_CHART );
        }

        if (chartRepresentationAsFilledButton.getSelection())
        {
            lineChartModelSettings.setLineChartRepresentation( LineChartRepresentation.FILLED );
        }
        else if (chartRepresentationAsLineButton.getSelection())
        {
            lineChartModelSettings.setLineChartRepresentation( LineChartRepresentation.LINE_ONLY );
        }
        else if (chartRepresentationAsStackedButton.getSelection())
        {
            lineChartModelSettings.setLineChartRepresentation( LineChartRepresentation.STACKED );
        }

        if (yScaleDynamicButton.getSelection())
        {
            lineChartModelSettings.setLineChartYaxisScaleMode( LineChartYaxisScaleMode.DYNAMIC );
        }
        if (yScaleSemiDynamicButton.getSelection())
        {
            lineChartModelSettings.setLineChartYaxisScaleMode( LineChartYaxisScaleMode.SEMI_DYNAMIC );
        }
        if (yScaleFixButton.getSelection())
        {
            lineChartModelSettings.setLineChartYaxisScaleMode( LineChartYaxisScaleMode.FIXED );
        }

        lineChartModelSettings.setShowOverviewChart( showOverviewChartButton.getSelection() );
        lineChartModelSettings.setYAxisMinValue( yAxisPreferencesMinMax.getMinValue() );
        lineChartModelSettings.setYAxisMaxValue( yAxisPreferencesMinMax.getMaxValue() );
    }

    private void resetPropertiesDefault()
    {
        lineChartModelSettings = getDefaultSettings();
        loadSettingsFromModel();
    }

    public abstract LineChartModelSettings getDefaultSettings();

    @Override
    public void dispose()
    {
        super.dispose();
    }

    @Override
    public void init(IWorkbench workbench)
    {
    }
}
