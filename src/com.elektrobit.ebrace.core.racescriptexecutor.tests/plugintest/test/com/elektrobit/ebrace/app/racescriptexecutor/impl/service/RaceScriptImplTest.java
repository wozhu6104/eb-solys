/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.app.racescriptexecutor.impl.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.app.racescriptexecutor.impl.service.RaceScriptLoaderImpl;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptData;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.script.external.Console;

@SuppressWarnings("unused")
public class RaceScriptImplTest
{
    private final String pathToRaceScripts = "resources/jars/";
    private final Console scriptConsole = Mockito.mock( Console.class );
    private RaceScriptLoaderImpl raceScriptLoader;
    private ResourcesModelManager resourcesModelManager;

    @Before
    public void configureLog4j()
    {
        Properties properties = new Properties();
        properties.setProperty( "log4j.rootLogger", "warn, default" );
        properties.setProperty( "log4j.appender.default", "org.apache.log4j.ConsoleAppender" );
        properties.setProperty( "log4j.appender.default.layout", "org.apache.log4j.PatternLayout" );
        properties.setProperty( "log4j.appender.default.layout.ConversionPattern", "%-4r [%t] %-5p %c %x - %m%n" );
        properties.setProperty( "log4j.logger.com.elektrobit.ebrace", "warn" );
        properties.setProperty( "log4j.logger.com.elektrobit.ebrace.resources.util.JarExporter", "info" );
        PropertyConfigurator.configure( properties );
    }

    @Before
    public void setup()
    {
        raceScriptLoader = (RaceScriptLoaderImpl)CoreServiceHelper.getRaceScriptLoader();
        resourcesModelManager = new GenericOSGIServiceTracker<ResourcesModelManager>( ResourcesModelManager.class )
                .getService();

        List<ScriptData> allScriptsToLoad = new ArrayList<>();
        allScriptsToLoad.add( ScriptTestHelper.mockScriptData( "aa", "aa.xtend", "aa.jar" ) );
        allScriptsToLoad
                .add( ScriptTestHelper.mockScriptData( "OneGlobalMethodCalledAbcScript",
                                                       "OneGlobalMethodCalledAbcScript.xtend",
                                                       pathToRaceScripts + "OneGlobalMethodCalledAbcScript.jar" ) );

        allScriptsToLoad.add( ScriptTestHelper.mockScriptData( "ThreeGlobalMethodsScript",
                                                               "ThreeGlobalMethodsScript.xtend",
                                                               pathToRaceScripts + "ThreeGlobalMethodsScript.jar" ) );
        allScriptsToLoad.add( ScriptTestHelper.mockScriptData( "AllMethodsTypeScript",
                                                               "AllMethodsTypeScript.xtend",
                                                               pathToRaceScripts + "AllMethodsTypeScript.jar" ) );

        allScriptsToLoad
                .add( ScriptTestHelper.mockScriptData( "AllMethodTypesCallbackScript",
                                                       "AllMethodTypesCallbackScript.xtend",
                                                       pathToRaceScripts + "AllMethodTypesCallbackScript.jar" ) );

        allScriptsToLoad.add( ScriptTestHelper.mockScriptData( "FilterMethodsScript",
                                                               "FilterMethodsScript.xtend",
                                                               pathToRaceScripts + "FilterMethodsScript.jar" ) );

        raceScriptLoader.onScriptsBuildAndExported( allScriptsToLoad );
    }

    @Test
    public void loadNotExistingJarTest()
    {
        RaceScriptResourceModel scriptModel = ScriptTestHelper.getScriptModelByName( "aa", resourcesModelManager );
        RaceScriptInfo script = scriptModel.getScriptInfo();

        Assert.assertFalse( script.isLoadSuccessful() );
    }

    @Test
    public void loadExistingJarTest()
    {
        RaceScriptResourceModel scriptModel = ScriptTestHelper.getScriptModelByName( "OneGlobalMethodCalledAbcScript",
                                                                                     resourcesModelManager );
        RaceScriptInfo script = scriptModel.getScriptInfo();

        Assert.assertEquals( "OneGlobalMethodCalledAbcScript", script.getName() );
        Assert.assertTrue( script.isLoadSuccessful() );
    }

    @Test
    public void globalMethodLoadedTest()
    {
        RaceScriptResourceModel scriptModel = ScriptTestHelper.getScriptModelByName( "OneGlobalMethodCalledAbcScript",
                                                                                     resourcesModelManager );
        RaceScriptInfo script = scriptModel.getScriptInfo();

        Assert.assertEquals( 1, script.getGlobalMethods().size() );
        Assert.assertEquals( "Abc", script.getGlobalMethods().get( 0 ).getMethodName() );
    }

    @Test
    public void threeGlobalMethodsLoadedTest()
    {
        RaceScriptResourceModel scriptModel = ScriptTestHelper.getScriptModelByName( "ThreeGlobalMethodsScript",
                                                                                     resourcesModelManager );
        RaceScriptInfo script = scriptModel.getScriptInfo();

        Assert.assertEquals( script.getGlobalMethods().size(), 3 );

        List<RaceScriptMethod> globalMethods = script.getGlobalMethods();

        List<String> methodNames = new ArrayList<>();
        for (RaceScriptMethod method : globalMethods)
        {
            methodNames.add( method.getMethodName() );
        }

        Assert.assertTrue( methodNames.contains( "Method1" ) );
        Assert.assertTrue( methodNames.contains( "Method2" ) );
        Assert.assertTrue( methodNames.contains( "Method3" ) );
    }

    @Test
    public void contextMethodsLoadedTest()
    {
        RaceScriptResourceModel scriptModel = ScriptTestHelper.getScriptModelByName( "AllMethodsTypeScript",
                                                                                     resourcesModelManager );
        RaceScriptInfo script = scriptModel.getScriptInfo();

        Assert.assertEquals( script.getChannelMethods().size(), 1 );
        Assert.assertEquals( script.getRuntimeEventListMethods().size(), 1 );
        Assert.assertEquals( script.getTimeMarkerMethods().size(), 1 );
        Assert.assertEquals( script.getGlobalMethods().size(), 1 );
    }

    @Test
    public void callbackMethodsLoadedTest()
    {
        RaceScriptResourceModel scriptModel = ScriptTestHelper.getScriptModelByName( "AllMethodTypesCallbackScript",
                                                                                     resourcesModelManager );
        RaceScriptInfo script = scriptModel.getScriptInfo();

        Assert.assertEquals( script.getCallbackMethods().size(), 1 );
    }

    @Test
    public void filterMethodsLoadedTest()
    {
        RaceScriptResourceModel scriptModel = ScriptTestHelper.getScriptModelByName( "FilterMethodsScript",
                                                                                     resourcesModelManager );
        RaceScriptInfo script = scriptModel.getScriptInfo();

        List<RaceScriptMethod> filterMethods = script.getFilterMethods();
        Assert.assertEquals( filterMethods.size(), 2 );
        String firstName = filterMethods.get( 0 ).getMethodName();
        String secondName = filterMethods.get( 1 ).getMethodName();

        Assert.assertTrue( firstName.equals( "filterMethodOk1" ) || secondName.equals( "filterMethodOk1" ) );
        Assert.assertTrue( firstName.equals( "filterMethodOk2" ) || secondName.equals( "filterMethodOk2" ) );
    }
}
