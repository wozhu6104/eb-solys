/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dev.unittestlauncher;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.elektrobit.ebrace.targetdata.android.impl.importer.ComElektrobitEbraceTargetdataImporterAndroidlogimporterTest;
import com.elektrobit.ebrace.ui.ecl.browser.test.ComElektrobitEbraceUIEclBrowserTest;
import com.elektrobit.ebrace.viewer.channelsview.ComElektrobitEbraceViewerChannelsviewTest;
import com.elektrobit.ebrace.viewer.targetconnector.ComElektrobitEbraceViewerTargetconnectorTest;

import test.com.elektrobit.ebrace.app.racescriptexecutor.ComElektrobitEbraceAppRacescriptexecutorTest;
import test.com.elektrobit.ebrace.chronograph.ComElektrobitEbraceChronographTest;
import test.com.elektrobit.ebrace.common.ComElektrobitEbraceCommonTest;
import test.com.elektrobit.ebrace.core.datamanager.ComElektrobitEbraceDatamanagerTest;
import test.com.elektrobit.ebrace.core.datamanager.timemarker.ComElektrobitEbraceDatamanagerTimemarkerTest;
import test.com.elektrobit.ebrace.core.interactor.ComElektrobitEbraceInteractorTest;
import test.com.elektrobit.ebrace.core.plantumlrenderer.ComElektrobitEbraceCorePlantumlrendererTest;
import test.com.elektrobit.ebrace.core.preferences.ComElektrobitEbraceCorePreferencesTest;
import test.com.elektrobit.ebrace.core.scriptimporter.ComElektrobitEbraceCoreScriptimporterTest;
import test.com.elektrobit.ebrace.core.systemmodel.ComElektrobitEbraceCoreSystemModelTest;
import test.com.elektrobit.ebrace.core.timesegmentmanager.ComElektrobitEbraceCoreTimesegmentmanagerTest;
import test.com.elektrobit.ebrace.core.tracefile.ComElektrobitEbraceCoreTracefileTest;
import test.com.elektrobit.ebrace.dbus.decoder.ComElektrobitEbraceDbusDecoderTest;
import test.com.elektrobit.ebrace.decoder.common.ComElektrobitEbraceDecoderCommonTest;
import test.com.elektrobit.ebrace.decoder.protobuf.ComElektrobitEbraceDecoderProtobufTest;
import test.com.elektrobit.ebrace.dev.kpimeasuring.api.ComElektrobitEbraceDevKpimeasuringTest;
import test.com.elektrobit.ebrace.franca.common.ComElektrobitEbraceFrancaCommonTest;
import test.com.elektrobit.ebrace.platform.commandlineparser.ComElektrobitEbracePlatformCommandlineparserTest;
import test.com.elektrobit.ebrace.resources.ComElektrobitEbraceResourcesTest;
import test.com.elektrobit.ebrace.targetadapter.communicator.ComElektrobitEbraceTargetadapterCommunicatorTest;
import test.com.elektrobit.ebrace.targetadapter.socketreader.ComElektrobitEbraceTargetadapterSocketreaderTest;
import test.com.elektrobit.ebrace.targetdata.adapter.linuxappstats.ComElektrobitEbraceTargetdataAdapterLinuxappstatsTest;
import test.com.elektrobit.ebrace.targetdata.adapter.networkpacketsniffer.ComElektrobitEbraceTargetdataAdapterNetworkpacketsnifferTest;
import test.com.elektrobit.ebrace.targetdata.decoder.xml.ComElektrobitEbraceTargetdataDecoderXmlTest;
import test.com.elektrobit.ebrace.targetdata.dlt.ComElektrobitEbraceGeniviTargetadapterDltmonitorpluginTest;
import test.com.elektrobit.ebrace.targetdata.importer.ComElektrobitEbraceTargetdataImporterTest;
import test.com.elektrobit.ebrace.targetdata.importer.csvimporter.ComElektrobitEbraceTargetdataImporterCsvimporterTest;
import test.com.elektrobit.ebrace.targetdata.importer.raceusecaselogimporter.ComElektrobitEbraceTargetdataImporterRaceusecaselogimporterTest;
import test.com.elektrobit.ebrace.targetdata.json.ComElektrobitEbraceTargetdataJsonTest;
import test.com.elektrobit.ebrace.viewer.chartengine.ComElektrobitEbraceViewerChartengineTest;
import test.com.elektrobit.ebrace.viewer.preferences.ComElektrobitEbraceViewerPreferencesTest;
import test.com.elektrobit.ebrace.viewer.runtimeeventloggertable.ComElektrobitEbraceViewerRuntimeeventloggertableTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ComElektrobitEbraceAppRacescriptexecutorTest.class, ComElektrobitEbraceCommonTest.class,
        ComElektrobitEbraceCoreTracefileTest.class, ComElektrobitEbraceDatamanagerTest.class,
        ComElektrobitEbraceDatamanagerTimemarkerTest.class, ComElektrobitEbraceDecoderCommonTest.class,
        ComElektrobitEbraceFrancaCommonTest.class, ComElektrobitEbraceInteractorTest.class,
        ComElektrobitEbracePlatformCommandlineparserTest.class, ComElektrobitEbraceResourcesTest.class,
        ComElektrobitEbraceTargetadapterCommunicatorTest.class, ComElektrobitEbraceTargetadapterSocketreaderTest.class,
        ComElektrobitEbraceViewerPreferencesTest.class, ComElektrobitEbraceViewerChannelsviewTest.class,
        ComElektrobitEbraceChronographTest.class, ComElektrobitEbraceTargetdataDecoderXmlTest.class,
        ComElektrobitEbraceTargetdataImporterTest.class, ComElektrobitEbraceTargetdataAdapterLinuxappstatsTest.class,
        ComElektrobitEbraceDevKpimeasuringTest.class, ComElektrobitEbraceViewerChartengineTest.class,
        ComElektrobitEbraceDbusDecoderTest.class, ComElektrobitEbraceViewerTargetconnectorTest.class,
        ComElektrobitEbraceViewerRuntimeeventloggertableTest.class,
        ComElektrobitEbraceTargetdataImporterAndroidlogimporterTest.class, ComElektrobitEbraceCorePreferencesTest.class,
        ComElektrobitEbraceCorePlantumlrendererTest.class, ComElektrobitEbraceCoreTimesegmentmanagerTest.class,
        ComElektrobitEbraceDecoderProtobufTest.class, ComElektrobitEbraceUIEclBrowserTest.class,
        ComElektrobitEbraceTargetdataAdapterNetworkpacketsnifferTest.class,
        ComElektrobitEbraceCoreScriptimporterTest.class,
        ComElektrobitEbraceTargetdataImporterRaceusecaselogimporterTest.class,
        ComElektrobitEbraceGeniviTargetadapterDltmonitorpluginTest.class, ComElektrobitEbraceTargetdataJsonTest.class,
        ComElektrobitEbraceTargetdataImporterCsvimporterTest.class, ComElektrobitEbraceCoreSystemModelTest.class})
public class AllTestSuites
{
}
