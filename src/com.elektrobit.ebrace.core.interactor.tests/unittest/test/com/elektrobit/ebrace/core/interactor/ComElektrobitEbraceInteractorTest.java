/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.com.elektrobit.ebrace.core.interactor.allChannels.ChannelToNodeConverterTestSuite;
import test.com.elektrobit.ebrace.core.interactor.allChannels.NodeBuilderTestSuite;
import test.com.elektrobit.ebrace.core.interactor.allChannels.NodeFilterTestSuite;
import test.com.elektrobit.ebrace.core.interactor.allChannels.StructureExpanderTestSuite;
import test.com.elektrobit.ebrace.core.interactor.allChannels.TreeCompactorTest;
import test.com.elektrobit.ebrace.core.interactor.analysisTimespan.AnalysisTimespanInteractionUseCaseImplTest;
import test.com.elektrobit.ebrace.core.interactor.channelValues.comparator.DecodedRuntimeEventValueEntryComparatorTestSuite;
import test.com.elektrobit.ebrace.core.interactor.chartData.ChartDataNotifyUseCaseUnitTest;
import test.com.elektrobit.ebrace.core.interactor.chartData.TimelineViewDataNotifyUseCaseImplTest;
import test.com.elektrobit.ebrace.core.interactor.connect.ConnectionSettingsTest;
import test.com.elektrobit.ebrace.core.interactor.connect.ConnectionStateNotifyUseCaseTest;
import test.com.elektrobit.ebrace.core.interactor.connect.ConnectionToTargetInteractionUseCaseTest;
import test.com.elektrobit.ebrace.core.interactor.createResource.CreateResourceInteractionUseCaseUnitTest;
import test.com.elektrobit.ebrace.core.interactor.createResource.DefaultResourceNameNotifyUseCaseTest;
import test.com.elektrobit.ebrace.core.interactor.headlessexecutor.HeadlessExecutorInteractionUseCaseImplValidationTest;
import test.com.elektrobit.ebrace.core.interactor.headlessexecutor.HeadlessExecutorInteractionUseCaseImplWaitOnBuildTest;
import test.com.elektrobit.ebrace.core.interactor.headlessexecutor.ScriptMethodFinderTest;
import test.com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.ConnectionModeRunnerTest;
import test.com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.FileCallbackModeRunnerTest;
import test.com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.FileGlobalModeRunnerTest;
import test.com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.FileLoadRunnerTest;
import test.com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.ScriptOnlyModeRunnerTest;
import test.com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.methodfinder.impl.CallbackScriptMethodFinderTest;
import test.com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.methodfinder.impl.GenericScriptMethodFinderTest;
import test.com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.methodfinder.impl.GlobalScriptMethodFinderTest;
import test.com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.script.impl.GlobalScriptRunnerTest;
import test.com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.impl.DataSourceSyntaxValidatorTest;
import test.com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.impl.NumberOfParamsValidatorTest;
import test.com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.impl.ScriptParamHasNoExtensionValidatorTest;
import test.com.elektrobit.ebrace.core.interactor.headlessexecutor.validators.impl.ScriptParamIsFileValidatorTest;
import test.com.elektrobit.ebrace.core.interactor.loaddatachunk.LoadDataChunkInteractionUseCaseTest;
import test.com.elektrobit.ebrace.core.interactor.loaddatachunk.LoadDataChunkNotifyUseCaseTest;
import test.com.elektrobit.ebrace.core.interactor.loaddatachunk.SystemCPUValuesNotifyUseCaseTest;
import test.com.elektrobit.ebrace.core.interactor.loadfile.LoadFileInteractionUseCaseTest;
import test.com.elektrobit.ebrace.core.interactor.loadfile.LoadFileProgressNotifyUseCaseTest;
import test.com.elektrobit.ebrace.core.interactor.preferences.FileSizeLimitNotifyUseCaseImplTest;
import test.com.elektrobit.ebrace.core.interactor.preferences.LiveModeNotifyUseCaseTest;
import test.com.elektrobit.ebrace.core.interactor.preferences.PreferencesNotifyUseCaseTest;
import test.com.elektrobit.ebrace.core.interactor.preferences.SetColorPreferencesInteractionUseCaseTest;
import test.com.elektrobit.ebrace.core.interactor.preferences.SetTimestampPreferencesInteractionUnitTest;
import test.com.elektrobit.ebrace.core.interactor.reset.ResetUseCaseTest;
import test.com.elektrobit.ebrace.core.interactor.resourcetree.ModelNameNotifyUseCaseImplTest;
import test.com.elektrobit.ebrace.core.interactor.resourcetree.ResourceTreeNotifyUseCaseTest;
import test.com.elektrobit.ebrace.core.interactor.script.importing.ImportScriptInteractionUseCaseTest;
import test.com.elektrobit.ebrace.core.interactor.splitfile.SplitFileInteractionUseCaseTest;
import test.com.elektrobit.ebrace.core.interactor.tableinput.FilterUtilTest;
import test.com.elektrobit.ebrace.core.interactor.tableinput.RuntimeEventTableDataNotifyUseUnitTest;
import test.com.elektrobit.ebrace.core.interactor.tableinput.SplitSearchStringUtilTest;
import test.com.elektrobit.ebrace.core.interactor.tableinput.TableScriptFiltersNotifyUseCaseImplTest;
import test.com.elektrobit.ebrace.core.interactor.tableinput.TimemarkerMixerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({SplitFileInteractionUseCaseTest.class, LoadFileInteractionUseCaseTest.class,
        LoadFileProgressNotifyUseCaseTest.class, ConnectionToTargetInteractionUseCaseTest.class,
        ConnectionStateNotifyUseCaseTest.class, ChartDataNotifyUseCaseUnitTest.class,
        ResourceTreeNotifyUseCaseTest.class, ResetUseCaseTest.class, TimemarkerMixerTest.class,
        CreateResourceInteractionUseCaseUnitTest.class, SplitSearchStringUtilTest.class,
        SetTimestampPreferencesInteractionUnitTest.class, SetColorPreferencesInteractionUseCaseTest.class,
        PreferencesNotifyUseCaseTest.class, ModelNameNotifyUseCaseImplTest.class, FilterUtilTest.class,
        LoadDataChunkNotifyUseCaseTest.class, LoadDataChunkInteractionUseCaseTest.class,
        TableScriptFiltersNotifyUseCaseImplTest.class, SystemCPUValuesNotifyUseCaseTest.class,
        DefaultResourceNameNotifyUseCaseTest.class, RuntimeEventTableDataNotifyUseUnitTest.class,
        ImportScriptInteractionUseCaseTest.class, ConnectionSettingsTest.class,
        AnalysisTimespanInteractionUseCaseImplTest.class, ImportScriptInteractionUseCaseTest.class,
        StructureExpanderTestSuite.class, NodeBuilderTestSuite.class, NodeFilterTestSuite.class,
        ChannelToNodeConverterTestSuite.class, TreeCompactorTest.class, FileSizeLimitNotifyUseCaseImplTest.class,
        DecodedRuntimeEventValueEntryComparatorTestSuite.class, TimelineViewDataNotifyUseCaseImplTest.class,
        LiveModeNotifyUseCaseTest.class, DataSourceSyntaxValidatorTest.class, NumberOfParamsValidatorTest.class,
        ScriptParamHasNoExtensionValidatorTest.class, ScriptParamIsFileValidatorTest.class,
        HeadlessExecutorInteractionUseCaseImplValidationTest.class,
        HeadlessExecutorInteractionUseCaseImplWaitOnBuildTest.class, ScriptMethodFinderTest.class,
        CallbackScriptMethodFinderTest.class, GenericScriptMethodFinderTest.class, GlobalScriptMethodFinderTest.class,
        FileGlobalModeRunnerTest.class, FileLoadRunnerTest.class, ScriptOnlyModeRunnerTest.class,
        GlobalScriptRunnerTest.class, FileCallbackModeRunnerTest.class, ConnectionModeRunnerTest.class})

public class ComElektrobitEbraceInteractorTest
{
}
