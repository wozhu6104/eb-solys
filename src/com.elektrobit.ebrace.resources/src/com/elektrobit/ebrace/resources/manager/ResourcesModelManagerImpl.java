/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.resources.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionSettings;
import com.elektrobit.ebrace.core.interactor.api.datainput.DataInputResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.EditRight;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceTreeNode;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.chart.ChartTypes;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.core.interactor.api.resources.model.dependencygraph.DependencyGraphModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.file.FileModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlViewModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.snapshot.SnapshotModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.table.TableModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.timelineview.TimelineViewModel;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.preferences.api.PreferencesService;
import com.elektrobit.ebrace.core.racescriptexecutor.api.ScriptExecutorService;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.BetaFeatureConfigurator;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebrace.resources.api.ResourceChangedNotifier;
import com.elektrobit.ebrace.resources.api.ResourceModelManagerConstants;
import com.elektrobit.ebrace.resources.api.manager.ResourceChangedListener;
import com.elektrobit.ebrace.resources.api.manager.ResourceTreeChangedListener;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.resources.api.model.ResourcesFolderImpl;
import com.elektrobit.ebrace.resources.api.model.connection.ConnectionModelImpl;
import com.elektrobit.ebrace.resources.model.ChartModelImpl;
import com.elektrobit.ebrace.resources.model.DataInputResourceModelImpl;
import com.elektrobit.ebrace.resources.model.DependencyGraphModelImpl;
import com.elektrobit.ebrace.resources.model.FileModelImpl;
import com.elektrobit.ebrace.resources.model.HtmlViewModelImpl;
import com.elektrobit.ebrace.resources.model.RaceScriptResourceModelImpl;
import com.elektrobit.ebrace.resources.model.SnapshotModelImpl;
import com.elektrobit.ebrace.resources.model.TableModelImpl;
import com.elektrobit.ebrace.resources.model.TimelineViewModelImpl;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetListener;

import lombok.extern.log4j.Log4j;

@Log4j
@Component(service = {ResourcesModelManager.class, ResetListener.class})
public final class ResourcesModelManagerImpl implements ResourceChangedNotifier, ResourcesModelManager, ResetListener
{
    private final Set<ResourceChangedListener> resourceListeners = new HashSet<ResourceChangedListener>();
    private final Set<ResourceTreeChangedListener> treeListeners = new HashSet<ResourceTreeChangedListener>();

    private ResourcesFolder dataInputFolder;
    private ResourcesFolder connectionsFolder;
    private ResourcesFolder chartFolder;
    private ResourcesFolder tableFolder;
    private ResourcesFolder snapshotFolder;
    private ResourcesFolder dependencyGraphFolder;
    private ResourcesFolder scriptFolder;
    private ResourcesFolder traceFileFolder;
    private ResourcesFolder htmlViewFolder;
    private ResourcesFolder myScriptsFolder;
    private ResourcesFolder preinstalledScriptsFolder;
    private List<ResourcesFolder> rootFolders;
    private PreferencesService preferencesService;
    private HtmlViewModel htmlViewModelDefault;
    private ScriptExecutorService scriptExecutorService;
    private final List<ConnectionType> connectionTypes = new ArrayList<>();

    public ResourcesModelManagerImpl()
    {
    }

    @Activate
    public void activate()
    {
        createDefaultResources();
    }

    /**
     * Binding of CommandLineParser is needed, otherwise the check of BetaFeatureConfigurator will fail as
     * CommandLineParser will not be available yet. When BetaFeatureConfigurator is removed from this class, this
     * binding can be removed.
     */
    @Reference
    public void bindCommandLineParser(CommandLineParser commandlineParser)
    {
    }

    public void unbindCommandLineParser(CommandLineParser commandlineParser)
    {
    }

    @Reference
    public void bindPreferencesService(PreferencesService preferencesService)
    {
        this.preferencesService = preferencesService;
    }

    public void unbindPreferencesService(PreferencesService preferencesService)
    {
        this.preferencesService = null;
    }

    @Reference
    public void bindScriptExecutorService(ScriptExecutorService scriptExecutorService)
    {
        this.scriptExecutorService = scriptExecutorService;
    }

    public void unbindScriptExecutorService(ScriptExecutorService preferencesService)
    {
        this.scriptExecutorService = null;
    }

    @Override
    public void registerResourceListener(ResourceChangedListener listener)
    {
        resourceListeners.add( listener );
    }

    @Override
    public void unregisterResourceListener(ResourceChangedListener listener)
    {
        resourceListeners.remove( listener );
    }

    @Override
    public void registerTreeListener(ResourceTreeChangedListener listener)
    {
        treeListeners.add( listener );
    }

    @Override
    public void unregisterTreeListener(ResourceTreeChangedListener listener)
    {
        treeListeners.remove( listener );
    }

    private void createDefaultResources()
    {
        rootFolders = new ArrayList<ResourcesFolder>();
        connectionsFolder = createRootFolder( ResourceModelManagerConstants.CONNECTIONS_FOLDER_NAME );
        traceFileFolder = createRootFolder( ResourceModelManagerConstants.FILES_FOLDER_NAME );
        if (BetaFeatureConfigurator.Features.DATA_INPUT.isActive())
        {
            dataInputFolder = createRootFolder( "Data Inputs" );
        }
        chartFolder = createRootFolder( "Charts" );
        tableFolder = createRootFolder( "Tables" );
        snapshotFolder = createRootFolder( "Snapshots" );
        dependencyGraphFolder = createRootFolder( "Dependency Graphs" );
        htmlViewFolder = createRootFolder( "HTML Views" );
        scriptFolder = createRootFolder( "Scripts" );
        preinstalledScriptsFolder = createSubFolder( "Pre-installed Scripts", scriptFolder );
        scriptFolder.setEditRight( EditRight.READ_ONLY );
        myScriptsFolder = createSubFolder( "My Scripts", scriptFolder );

        createDefaultDependencyGraph();
        restoreSavedConnections();
    }

    private void restoreSavedConnections()
    {
        for (ConnectionSettings settings : preferencesService.getConnections())
        {
            ConnectionModel connection = new ConnectionModelImpl( settings, connectionsFolder, this );
            addResourcesModel( connection, connectionsFolder );
        }
        notifyResourceTreeChanged();
    }

    private void addResourcesModel(ResourceModel newResource, ResourcesFolder group)
    {
        group.addChild( newResource );
        notifyResourceAdded( newResource );
    }

    @Override
    public boolean isNameUsed(String name, List<ResourceModel> models)
    {
        for (ResourceModel m : models)
        {
            if (m.getName().equals( name ))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<String> getUsedConnectionNames()
    {
        List<ResourceModel> resourceModelsForFolder = getResourceModelsForFolder( connectionsFolder );
        Set<String> result = new HashSet<>();
        for (ResourceModel model : resourceModelsForFolder)
        {
            result.add( model.getName() );
        }
        return result;
    }

    @Override
    public ChartModel createChart(String name, ChartTypes type)
    {
        ChartModel model = new ChartModelImpl( name, type, chartFolder, this );
        addResourcesModel( model, chartFolder );
        return model;
    }

    @Override
    public TimelineViewModel createTimelineView(String name)
    {
        TimelineViewModel model = new TimelineViewModelImpl( name, chartFolder, this );
        addResourcesModel( model, chartFolder );
        return model;
    }

    @Override
    public TableModel createTable(String name)
    {
        TableModel model = new TableModelImpl( name, tableFolder, this );
        addResourcesModel( model, tableFolder );
        return model;
    }

    @Override
    public SnapshotModel createSnapshot(String name)
    {
        SnapshotModel snapshotModel = new SnapshotModelImpl( name, snapshotFolder, this );
        addResourcesModel( snapshotModel, snapshotFolder );
        return snapshotModel;
    }

    private void createDefaultDependencyGraph()
    {
        DependencyGraphModel dependecyGraphModel = new DependencyGraphModelImpl( dependencyGraphFolder, this );
        addResourcesModel( dependecyGraphModel, dependencyGraphFolder );
        notifyResourceTreeChanged();
        return;
    }

    @Override
    public ConnectionModel createConnection(String name, String host, int port, boolean saveToFile,
            ConnectionType connectionType)
    {
        ConnectionModel connection = new ConnectionModelImpl( name,
                                                              host,
                                                              port,
                                                              saveToFile,
                                                              connectionType,
                                                              connectionsFolder,
                                                              this );
        addResourcesModel( connection, connectionsFolder );
        notifyResourceTreeChanged();
        storeConnections();

        return connection;
    }

    @Override
    public HtmlViewModel createHtmlView(String name, String url)
    {
        HtmlViewModel htmlViewModel = new HtmlViewModelImpl( name, url, htmlViewFolder, this );

        if (!htmlViewFolder.getChildren().contains( htmlViewModel ))
        {
            addResourcesModel( htmlViewModel, htmlViewFolder );
            notifyResourceTreeChanged();
        }
        return htmlViewModel;
    }

    @Override
    public FileModel createFileModel(String name, String path)
    {
        FileModel traceFileModel = new FileModelImpl( name, path, traceFileFolder, this );
        addResourcesModel( traceFileModel, traceFileFolder );
        return traceFileModel;
    }

    private ResourcesFolder createRootFolder(String name)
    {
        ResourcesFolder folder = new ResourcesFolderImpl( name );
        rootFolders.add( folder );
        return folder;
    }

    private ResourcesFolder createSubFolder(String name, ResourcesFolder parentFolder)
    {
        ResourcesFolder subFolder = new ResourcesFolderImpl( name, parentFolder );
        parentFolder.addChild( subFolder );
        return subFolder;
    }

    private RaceScriptResourceModel createUserScript(File file, RaceScriptInfo raceScriptInfo)
    {
        return createScript( file, raceScriptInfo, myScriptsFolder );
    }

    private RaceScriptResourceModel createPreinstalledScript(File file, RaceScriptInfo raceScriptInfo)
    {
        RaceScriptResourceModel scriptModel = createScript( file, raceScriptInfo, preinstalledScriptsFolder );
        scriptModel.setEditRight( EditRight.READ_ONLY );
        return scriptModel;
    }

    private RaceScriptResourceModel createScript(File file, RaceScriptInfo raceScriptInfo, ResourcesFolder folder)
    {

        RaceScriptResourceModel model = new RaceScriptResourceModelImpl( file, raceScriptInfo, folder, this );
        addResourcesModel( model, folder );

        return model;
    }

    @Override
    public List<ResourcesFolder> getRootFolders()
    {
        return rootFolders;
    }

    private List<ResourceModel> getResourceModelsForFolder(ResourcesFolder folder)
    {
        List<ResourceModel> list = new ArrayList<ResourceModel>();
        for (ResourceTreeNode node : folder.getChildren())
        {
            if (node instanceof ResourceModel)
            {
                list.add( (ResourceModel)node );
            }
        }
        return list;
    }

    @Override
    public List<ResourceModel> getConnections()
    {
        return getResourceModelsForFolder( connectionsFolder );
    }

    @Override
    public List<ResourceModel> getCharts()
    {
        List<ResourceModel> charts = getResourceModelsForFolder( chartFolder );
        charts.removeIf( (model) -> model instanceof TimelineViewModel );
        return charts;
    }

    @Override
    public List<ResourceModel> getTimelineViews()
    {
        List<ResourceModel> charts = getResourceModelsForFolder( chartFolder );
        charts.removeIf( (model) -> !(model instanceof TimelineViewModel) );
        return charts;
    }

    @Override
    public List<ResourceModel> getFiles()
    {
        return getResourceModelsForFolder( traceFileFolder );
    }

    @Override
    public List<ResourceModel> getChartsWithCertainType(ChartTypes type)
    {
        List<ResourceModel> chartModels = new ArrayList<ResourceModel>();
        for (ResourceTreeNode node : chartFolder.getChildren())
        {
            if (node instanceof ChartModel)
            {
                ChartModel model = (ChartModel)node;
                if (model.getType().equals( type ))
                {
                    chartModels.add( model );
                }
            }
        }
        return chartModels;
    }

    @Override
    public List<ResourceModel> getSnapshots()
    {
        return getResourceModelsForFolder( snapshotFolder );
    }

    @Override
    public List<ResourceModel> getTables()
    {
        return getResourceModelsForFolder( tableFolder );
    }

    @Override
    public List<ResourceModel> getResources()
    {
        List<ResourceModel> result = new ArrayList<ResourceModel>();
        result.addAll( getConnections() );
        result.addAll( getCharts() );
        result.addAll( getTables() );
        result.addAll( getSnapshots() );
        result.addAll( getHtmlViews() );
        result.addAll( getTimelineViews() );

        return result;
    }

    @Override
    public List<RaceScriptResourceModel> getAllScripts()
    {
        final List<RaceScriptResourceModel> allScripts = new ArrayList<>( getUserScripts() );
        allScripts.addAll( getPreinstalledScripts() );
        return allScripts;
    }

    @Override
    public List<RaceScriptResourceModel> getUserScripts()
    {
        return convert( getResourceModelsForFolder( myScriptsFolder ) );
    }

    @Override
    public List<RaceScriptResourceModel> getPreinstalledScripts()
    {
        return convert( getResourceModelsForFolder( preinstalledScriptsFolder ) );
    }

    private List<RaceScriptResourceModel> convert(List<ResourceModel> models)
    {
        List<RaceScriptResourceModel> result = new ArrayList<>( models.size() );

        for (ResourceModel nextModel : models)
        {
            if (nextModel instanceof RaceScriptResourceModel)
            {
                result.add( (RaceScriptResourceModel)nextModel );
            }
        }

        return result;
    }

    @Override
    public RaceScriptResourceModel getRaceScriptResourceModel(RaceScriptInfo raceScriptInfo)
    {
        for (ResourceModel model : getAllScripts())
        {
            RaceScriptResourceModel raceScriptResourceModel = (RaceScriptResourceModel)model;
            if (raceScriptResourceModel.getScriptInfo().getName().equals( raceScriptInfo.getName() ))
            {
                return raceScriptResourceModel;
            }
        }
        return null;
    }

    @Override
    public void deleteResourcesModels(List<ResourceModel> modelsToBeDeleted)
    {
        List<ResourceModel> modelsCopy = new ArrayList<ResourceModel>( modelsToBeDeleted );
        for (ResourceModel resourceModel : modelsCopy)
        {
            ResourcesFolder parentGroup = resourceModel.getParent();
            parentGroup.getChildren().remove( resourceModel );
            if (resourceModel instanceof ConnectionModel)
            {
                storeConnections();
            }
            onResourceDeleted( resourceModel );
        }
    }

    @Override
    public boolean isCallbackScriptRunning()
    {
        for (ResourceTreeNode node : myScriptsFolder.getChildren())
        {
            if (node instanceof RaceScriptResourceModel)
            {
                RaceScriptResourceModel script = (RaceScriptResourceModel)node;
                if (script.getScriptInfo().isRunningAsCallbackScript())
                {
                    return true;
                }
            }
            else
            {
                log.warn( "Script resource is not instance of " + RaceScriptResourceModel.class );
            }
        }
        for (ResourceTreeNode node : preinstalledScriptsFolder.getChildren())
        {
            if (node instanceof RaceScriptResourceModel)
            {
                RaceScriptResourceModel script = (RaceScriptResourceModel)node;
                if (script.getScriptInfo().isRunningAsCallbackScript())
                {
                    return true;
                }
            }
            else
            {
                log.warn( "Script resource is not instance of " + RaceScriptResourceModel.class );
            }
        }
        return false;
    }

    @Override
    public void notifyResourceChannelsChanged(ResourceModel resourceModel)
    {
        for (ResourceChangedListener listener : resourceListeners)
        {
            listener.onResourceModelChannelsChanged( resourceModel );
        }
    }

    private void onResourceDeleted(ResourceModel resourceModel)
    {
        for (ResourceTreeChangedListener listener : treeListeners)
        {
            listener.onResourceDeleted( resourceModel );
        }
    }

    @Override
    public void notifyResourceRenamed(ResourceModel resourceModel)
    {
        for (ResourceTreeChangedListener listener : treeListeners)
        {
            listener.onResourceRenamed( resourceModel );
        }
    }

    private void notifyResourceAdded(ResourceModel newResource)
    {
        for (ResourceTreeChangedListener listener : treeListeners)
        {
            listener.onResourceAdded( newResource );
        }
    }

    private void notifyResourceTreeChanged()
    {
        for (ResourceTreeChangedListener listener : treeListeners)
        {
            listener.onResourceTreeChanged();
        }
    }

    @Override
    public void onReset()
    {
        List<ResourceModel> traceFiles = getResourceModelsForFolder( traceFileFolder );
        deleteResourcesModels( traceFiles );
        stopRunningCallbackScripts();
    }

    private void stopRunningCallbackScripts()
    {
        for (ResourceModel scriptResourceModel : getAllScripts())
        {
            if (scriptResourceModel instanceof RaceScriptResourceModel)
            {
                RaceScriptInfo script = ((RaceScriptResourceModel)scriptResourceModel).getScriptInfo();
                if (script.isRunningAsCallbackScript())
                {
                    scriptExecutorService.stopScript( script );
                }
            }
        }
    }

    @Override
    public void openDefaultHtmlView()
    {
        if (htmlViewModelDefault != null)
        {
            ResourceModel htmlView = htmlViewModelDefault;
            openResourceModel( htmlView );
        }
    }

    @Override
    public List<ResourceModel> getHtmlViews()
    {
        return getResourceModelsForFolder( htmlViewFolder );
    }

    @Override
    public void openResourceModel(ResourceModel resModel)
    {
        for (ResourceTreeChangedListener listener : treeListeners)
        {
            listener.onOpenResourceModel( resModel );
        }
    }

    @Override
    public void notifyResourceStateChanged(ResourceModel resourceModel)
    {
        storeConnections();
        notifyResourceTreeChanged();
    }

    @Override
    public void notifySelectedChannelsChanged(ResourceModel resourceModel)
    {
        for (ResourceChangedListener listener : resourceListeners)
        {
            listener.onResourceModelSelectedChannelsChanged( resourceModel );
        }
    }

    @Override
    public DataInputResourceModel createDataInput(String name)
    {
        DataInputResourceModel model = new DataInputResourceModelImpl( name,
                                                                       dataInputFolder,
                                                                       EditRight.READ_ONLY,
                                                                       null );
        addResourcesModel( model, dataInputFolder );
        notifyResourceTreeChanged();
        return model;
    }

    @Override
    public List<ResourceModel> getDataInputs()
    {
        return getResourceModelsForFolder( dataInputFolder );
    }

    private void storeConnections()
    {
        List<ConnectionSettings> connections = new ArrayList<>();
        for (ResourceModel resourceModel : getConnections())
        {
            ConnectionModel connectionModel = (ConnectionModel)resourceModel;
            ConnectionSettings settings = connectionModel.toConnectionSettings();
            connections.add( settings );
        }
        if (preferencesService != null)
        {
            preferencesService.setConnections( connections );
        }
    }

    @Override
    public boolean scriptAlreadyExists(String scriptName)
    {
        for (ResourceModel resourceModel : getAllScripts())
        {
            if (resourceModel.getName().equals( scriptName ))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateScripts(List<RaceScriptInfo> currentScripts)
    {
        List<RaceScriptResourceModel> allScripts = getAllScripts();
        List<ResourceModel> notRunningScripts = new ArrayList<>();

        for (RaceScriptResourceModel nextScript : allScripts)
        {
            if (!nextScript.getScriptInfo().isRunning() && !nextScript.getScriptInfo().isRunningAsCallbackScript())
            {
                notRunningScripts.add( nextScript );
            }
        }

        List<ResourceModel> currentScriptsModels = new ArrayList<>();

        for (RaceScriptInfo nextScript : currentScripts)
        {
            RaceScriptResourceModel resourceModel = getRaceScriptResourceModel( nextScript );
            if (resourceModel != null)
            {
                currentScriptsModels.add( resourceModel );
            }
        }

        List<ResourceModel> scriptsToRemove = new ArrayList<>();
        scriptsToRemove.addAll( notRunningScripts );
        scriptsToRemove.removeAll( currentScriptsModels );

        List<RaceScriptInfo> scriptsToAdd = new ArrayList<>();

        for (RaceScriptInfo currentScript : currentScripts)
        {

            RaceScriptResourceModel raceScriptResourceModel = getRaceScriptResourceModel( currentScript );
            if (raceScriptResourceModel == null)
            {
                scriptsToAdd.add( currentScript );
            }
            else
            {
                raceScriptResourceModel.updateScriptInfo( currentScript );
            }
        }

        deleteResourcesModels( scriptsToRemove );
        addScripts( scriptsToAdd );
    }

    private void addScripts(List<RaceScriptInfo> scripts)
    {
        for (RaceScriptInfo nextScriptToAdd : scripts)
        {
            if (nextScriptToAdd.isPreinstalled())
            {
                createPreinstalledScript( new File( nextScriptToAdd.getSourcePath() ), nextScriptToAdd );
            }
            else
            {
                createUserScript( new File( nextScriptToAdd.getSourcePath() ), nextScriptToAdd );
            }
        }
    }

    @Override
    public List<ConnectionType> getAllConnectionTypes()
    {
        return new ArrayList<>( connectionTypes );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void bindConnectionTypes(ConnectionType connectionType)
    {
        connectionTypes.add( connectionType );
    }

    public void unbindConnectionTypes(ConnectionType connectionType)
    {
        connectionTypes.remove( connectionType );
    }

}
