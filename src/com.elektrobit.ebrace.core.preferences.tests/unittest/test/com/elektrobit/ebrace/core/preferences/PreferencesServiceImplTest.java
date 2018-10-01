/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.preferences;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;

import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings.LineChartRepresentation;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings.LineChartType;
import com.elektrobit.ebrace.core.interactor.api.chart.LineChartModelSettings.LineChartYaxisScaleMode;
import com.elektrobit.ebrace.core.preferences.impl.PreferencesServiceImpl;
import com.elektrobit.ebrace.core.preferences.impl.PropertiesStore;
import com.elektrobit.ebrace.core.preferences.listener.PreferencesListener;

public class PreferencesServiceImplTest
{
    private static final String VIEW_ID = "test_view_id";
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private PreferencesServiceImpl sutPreferencesService;
    private PreferencesListener mockedPreferencesListener;
    private File tmpPreferenceFile;

    @Before
    public void setup() throws Exception
    {
        tmpPreferenceFile = tempFolder.newFile( "preferences.properties" );
        PropertiesStore propertiesStore = new PropertiesStore( tmpPreferenceFile );
        sutPreferencesService = new PreferencesServiceImpl( propertiesStore, () -> "default path" );
        mockedPreferencesListener = mock( PreferencesListener.class );
        sutPreferencesService.registerPreferencesListener( mockedPreferencesListener );
    }

    @Test
    public void testTableSearchTermHistory() throws Exception
    {
        String[] array = new String[]{"term one", "term two"};

        String viewID = "viewID";
        sutPreferencesService.setTableSearchTermsHistory( Arrays.asList( array ), viewID );
        sutPreferencesService.setTableSearchTermsHistory( Arrays.asList( new String[]{"XX", "YY"} ),
                                                          viewID + "another" );
        List<String> tableSearchTermsHistory = sutPreferencesService.getTableSearchTermsHistory( viewID );

        Assert.assertArrayEquals( array, tableSearchTermsHistory.toArray() );
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testTableSearchTermHistoryNotify() throws Exception
    {
        List<String> terms = Arrays.asList( new String[]{"term one", "term two"} );
        verifyNoMoreInteractions( mockedPreferencesListener );
        sutPreferencesService.setTableSearchTermsHistory( terms, VIEW_ID );

        ArgumentCaptor<List> termsListCaptor = ArgumentCaptor.forClass( List.class );
        verify( mockedPreferencesListener ).onTableSearchTermsHistoryChanged( termsListCaptor.capture(),
                                                                              eq( VIEW_ID ) );
        List passedList = termsListCaptor.getValue();
        Assert.assertTrue( passedList.equals( terms ) );
    }

    @Test
    public void testUnregister() throws Exception
    {
        sutPreferencesService.unregisterPreferencesListener( mockedPreferencesListener );
        sutPreferencesService.setTableSearchTermsHistory( Collections.<String> emptyList(), VIEW_ID );
        verifyNoMoreInteractions( mockedPreferencesListener );
    }

    @Test
    public void testLineChartModelSettingsSetGet()
    {
        LineChartModelSettings settings = new LineChartModelSettings( LineChartType.LINE_CHART,
                                                                      LineChartRepresentation.FILLED,
                                                                      LineChartYaxisScaleMode.FIXED,
                                                                      0,
                                                                      10 );

        sutPreferencesService.setLineChartModelSettings( settings );
        LineChartModelSettings retrievedSettings = sutPreferencesService.getGlobalLineChartSettings();

        Assert.assertEquals( settings, retrievedSettings );
    }

    @Test
    public void testDefaultLineChartModelSettings()
    {
        LineChartModelSettings retrievedSettings = sutPreferencesService.getGlobalLineChartSettings();
        LineChartModelSettings defaultSettings = new LineChartModelSettings();
        Assert.assertEquals( defaultSettings, retrievedSettings );
    }

    @Test
    public void testLineChartModelSettingsNotify()
    {
        LineChartModelSettings settings = new LineChartModelSettings( LineChartType.LINE_CHART,
                                                                      LineChartRepresentation.FILLED,
                                                                      LineChartYaxisScaleMode.FIXED,
                                                                      0,
                                                                      10 );
        verifyNoMoreInteractions( mockedPreferencesListener );
        sutPreferencesService.setLineChartModelSettings( settings );

        ArgumentCaptor<LineChartModelSettings> settingsCaptor = ArgumentCaptor.forClass( LineChartModelSettings.class );
        verify( mockedPreferencesListener ).onLineChartModelSettingsChanged( settingsCaptor.capture() );
        LineChartModelSettings capturedSettings = settingsCaptor.getValue();
        Assert.assertEquals( settings, capturedSettings );
    }
}
