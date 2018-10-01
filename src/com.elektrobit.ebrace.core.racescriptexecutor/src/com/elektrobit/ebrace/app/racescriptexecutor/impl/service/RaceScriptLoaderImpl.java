/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.app.racescriptexecutor.impl.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.common.utils.GenericListenerCaller;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScript;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptData;
import com.elektrobit.ebrace.core.plantumlrenderer.api.PlantUmlRendererService;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptInfoChangedListener;
import com.elektrobit.ebrace.core.racescriptexecutor.api.RaceScriptLoader;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptMethodLoader;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptsReloadedListener;
import com.elektrobit.ebrace.core.scriptconsolefactory.api.ScriptConsoleFactoryService;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptProjectBuildListener;
import com.elektrobit.ebrace.core.scriptimporter.api.ScriptProjectBuilderService;
import com.elektrobit.ebrace.core.systemmodel.api.SystemModelAccess;
import com.elektrobit.ebrace.core.timesegmentmanager.api.TimeSegmentAcceptorService;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.franca.common.validator.api.FrancaTraceValidator;
import com.elektrobit.ebrace.franca.common.validator.api.FrancaTraceValidatorFactory;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.color.ChannelColorProviderService;
import com.elektrobit.ebsolys.core.targetdata.api.importer.JsonEventHandler;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarkerManager;
import com.elektrobit.ebsolys.decoder.common.api.DecodedRuntimeEventStringConverter;
import com.elektrobit.ebsolys.decoder.common.api.DecodedRuntimeEventStringConverterFactory;
import com.elektrobit.ebsolys.decoder.common.services.DecoderServiceManagerImpl;
import com.elektrobit.ebsolys.script.external.Console;

@Component(service = {RaceScriptLoader.class, RaceScriptInfoChangedListener.class})
public class RaceScriptLoaderImpl implements RaceScriptLoader, RaceScriptInfoChangedListener, ScriptProjectBuildListener
{
    static final String DEFAULT_PATH_TO_SCRIPT_FOLDER = Platform.getLocation().toOSString() + File.separator + ".."
            + File.separator + "scripts";
    static final String RACE_SCRIPT_EXTENTION = ".jar";

    private RuntimeEventAcceptor runtimeEventAcceptor;
    private final File scriptFolderDirectory;
    private TimeMarkerManager timeMarkerManager;
    private ChannelColorProviderService channelColorProvider;
    private final FrancaTraceValidator francaTraceValidator = FrancaTraceValidatorFactory.createFrancaTraceValidator();
    private final DecodedRuntimeEventStringConverter decodedRuntimeEventStringConverter = DecodedRuntimeEventStringConverterFactory
            .createUniqueKeyDecodedRuntimeEventStringConverter();
    private ResourcesModelManager resourcesModelManager;
    private UserMessageLogger userMessageLogger;
    private TimeSegmentAcceptorService timeSegmentAcceptor;
    private PlantUmlRendererService plantUmlRendererService;

    private final GenericListenerCaller<RaceScriptInfoChangedListener> scriptInfoChangedListenerCaller = new GenericListenerCaller<>();
    private final GenericListenerCaller<ScriptsReloadedListener> scriptsReloadedListenerCaller = new GenericListenerCaller<>();
    private ScriptConsoleFactoryService outputConsoleFactoryService;
    private CommandLineParser commandlineParser;
    private ScriptProjectBuilderService scriptProjectBuilderService;
    private ScriptMethodLoader scriptMethodLoader;
    private JsonEventHandler jsonEventHandler;
    private SystemModelAccess systemModel;

    public RaceScriptLoaderImpl()
    {
        scriptFolderDirectory = new File( System.getProperty( "raceScriptFolderPath", DEFAULT_PATH_TO_SCRIPT_FOLDER ) );
        if (!scriptFolderDirectory.exists())
        {
            scriptFolderDirectory.mkdir();
        }

        outputConsoleFactoryService = new DefaultScriptConsoleFactoryService();
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    public void bindOutputConsoleFactoryService(ScriptConsoleFactoryService outputConsoleFactoryService)
    {
        this.outputConsoleFactoryService = outputConsoleFactoryService;
    }

    public void unbindOutputConsoleFactoryService(ScriptConsoleFactoryService outputConsoleFactoryService)
    {
        this.outputConsoleFactoryService = null;
    }

    @Reference
    public void bindScriptProjectBuilderService(ScriptProjectBuilderService scriptProjectBuilderService)
    {
        this.scriptProjectBuilderService = scriptProjectBuilderService;
    }

    public void unbindScriptProjectBuilderService(ScriptProjectBuilderService scriptProjectBuilderService)
    {
        this.scriptProjectBuilderService = null;
    }

    @Reference
    public void bindScriptMethodLoader(ScriptMethodLoader scriptMethodLoader)
    {
        this.scriptMethodLoader = scriptMethodLoader;
    }

    public void unbindScriptMethodLoader(ScriptMethodLoader scriptMethodLoader)
    {
        this.scriptMethodLoader = null;
    }

    @Reference
    public void bindJsonEventHandler(JsonEventHandler jsonEventHandler)
    {
        this.jsonEventHandler = jsonEventHandler;
    }

    public void unbindJsonEventHandler(JsonEventHandler jsonEventHandler)
    {
        this.jsonEventHandler = null;
    }

    @Reference
    public void bindSystemModelAccess(SystemModelAccess systemModel)
    {
        this.systemModel = systemModel;
    }

    public void unbindSystemModelAccess(SystemModelAccess systemModel)
    {
        this.systemModel = null;
    }

    @Activate
    public void start()
    {
        scriptProjectBuilderService.addScriptBuildListener( this );
    }

    @Override
    public RaceScript loadRaceScript(ScriptData scriptData)
    {
        return loadRaceScript( scriptData, outputConsoleFactoryService.createNewConsole( scriptData.getName() ) );
    }

    @Override
    public RaceScript loadRaceScript(ScriptData scriptData, Console console)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "Script data", scriptData );

        final ScriptContextImpl scriptContext = new ScriptContextImpl( runtimeEventAcceptor,
                                                                       console,
                                                                       timeMarkerManager,
                                                                       francaTraceValidator,
                                                                       decodedRuntimeEventStringConverter,
                                                                       DecoderServiceManagerImpl.getInstance(),
                                                                       channelColorProvider,
                                                                       resourcesModelManager,
                                                                       userMessageLogger,
                                                                       timeSegmentAcceptor,
                                                                       plantUmlRendererService,
                                                                       commandlineParser,
                                                                       jsonEventHandler,
                                                                       systemModel );
        RaceScript scriptRunnable = new RaceScriptImpl( scriptContext,
                                                        getPathToJar( scriptData.getJarPath() ),
                                                        getScriptName( scriptData.getName() ),
                                                        console,
                                                        scriptData.isPreinstalledScript(),
                                                        scriptData.getSourcePath() );
        return scriptRunnable;
    }

    private String getPathToJar(String nameOrPath)
    {
        if (isPathToJar( nameOrPath ))
        {
            return nameOrPath;
        }
        else
        {
            return scriptFolderDirectory + File.separator + nameOrPath + RACE_SCRIPT_EXTENTION;
        }
    }

    private boolean isPathToJar(String nameOrPath)
    {
        int jarPos = nameOrPath.lastIndexOf( ".jar" );
        int jarExpectedPos = nameOrPath.length() - ".jar".length();
        return jarPos != -1 && jarPos == jarExpectedPos;
    }

    private String getScriptName(String nameOrPath)
    {
        if (!isPathToJar( nameOrPath ))
        {
            return nameOrPath;
        }
        else
        {
            return RaceScriptHelper.getScriptNameFromPath( nameOrPath );
        }
    }

    @Reference
    protected void setRuntimeEventAcceptor(final RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = runtimeEventAcceptor;
    }

    protected void unsetRuntimeEventAcceptor(final RuntimeEventAcceptor runtimeEventAcceptor)
    {
        this.runtimeEventAcceptor = null;
    }

    @Reference
    protected void setChannelColorProvider(final ChannelColorProviderService channelColorProvider)
    {
        this.channelColorProvider = channelColorProvider;
    }

    protected void unsetChannelColorProvider(final ChannelColorProviderService channelColorProvider)
    {
        this.channelColorProvider = null;
    }

    @Reference
    protected void setTimeMarkerManager(final TimeMarkerManager timeMarkerManager)
    {
        this.timeMarkerManager = timeMarkerManager;
    }

    protected void unsetTimeMarkerManager(final TimeMarkerManager timeMarkerManager)
    {
        this.timeMarkerManager = null;
    }

    @Reference
    protected void setResourcesModelManager(final ResourcesModelManager resourcesModelManager)
    {
        this.resourcesModelManager = resourcesModelManager;
    }

    protected void unsetResourcesModelManager(final ResourcesModelManager resourcesModelManager)
    {
        this.resourcesModelManager = null;
    }

    @Reference
    protected void setUserMessageLogger(final UserMessageLogger userMessageLogger)
    {
        this.userMessageLogger = userMessageLogger;
    }

    protected void unsetUserMessageLogger(final UserMessageLogger userMessageLogger)
    {
        this.userMessageLogger = null;
    }

    @Reference
    protected void setTimeSegmentAcceptorService(final TimeSegmentAcceptorService timeSegmentAcceptor)
    {
        this.timeSegmentAcceptor = timeSegmentAcceptor;
    }

    protected void unsetTimeSegmentAcceptorService(final TimeSegmentAcceptorService timeSegmentAcceptor)
    {
        this.timeSegmentAcceptor = null;
    }

    @Reference
    protected void setPlantUmlRendererService(final PlantUmlRendererService plantUmlRendererService)
    {
        this.plantUmlRendererService = plantUmlRendererService;
    }

    protected void unsetPlantUmlRendererService(final PlantUmlRendererService plantUmlRendererService)
    {
        this.plantUmlRendererService = null;
    }

    @Reference
    protected void setCommandLineParser(final CommandLineParser commandlineParser)
    {
        this.commandlineParser = commandlineParser;
    }

    protected void unsetCommandLineParser(final CommandLineParser commandlineParser)
    {
        this.commandlineParser = null;
    }

    @Override
    public void registerRaceScriptChangedListener(RaceScriptInfoChangedListener listener)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "listener", listener );

        scriptInfoChangedListenerCaller.add( listener );
    }

    @Override
    public void unregisterRaceScriptChangedListener(RaceScriptInfoChangedListener listener)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "listener", listener );

        scriptInfoChangedListenerCaller.remove( listener );
    }

    @Override
    public void scriptInfoChanged(RaceScript script)
    {
        scriptInfoChangedListenerCaller.notifyListeners( (listener) -> listener.scriptInfoChanged( script ) );
    }

    @Override
    public void filterMethodsChanged(RaceScript script, List<RaceScriptMethod> filterMethods)
    {
        scriptInfoChangedListenerCaller
                .notifyListeners( (listener) -> listener.filterMethodsChanged( script, filterMethods ) );
    }

    @Override
    public void onScriptsBuildAndExported(List<ScriptData> scriptData)
    {
        List<RaceScriptInfo> currentScripts = new ArrayList<>();

        for (ScriptData nextScript : scriptData)
        {

            RaceScriptInfo raceScript = loadRaceScript( nextScript );
            currentScripts.add( raceScript );
        }

        scriptMethodLoader.reloadScriptMethods( currentScripts );
        scriptsReloadedListenerCaller.notifyListeners( (listener) -> listener.onScriptsReloaded( currentScripts ) );

        // FIXME rage2903: resourceModelManager should register as listener
        resourcesModelManager.updateScripts( currentScripts );
    }

    @Deactivate
    public void stop()
    {
        scriptProjectBuilderService.removeScriptBuildListener( this );
    }

    @Override
    public void addScriptsReloadedListener(ScriptsReloadedListener listener)
    {
        scriptsReloadedListenerCaller.add( listener );
    }

    @Override
    public void removeScriptsReloadedListener(ScriptsReloadedListener listener)
    {
        scriptsReloadedListenerCaller.remove( listener );
    }

}
