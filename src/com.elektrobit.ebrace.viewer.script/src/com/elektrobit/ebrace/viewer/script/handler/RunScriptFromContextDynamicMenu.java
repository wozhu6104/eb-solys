/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourceModel;
import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.script.InjectedParamsCallback;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptInfo;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.core.interactor.api.script.execution.RunScriptInteractionUseCase;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebrace.viewer.channelsview.ChannelsView;
import com.elektrobit.ebrace.viewer.channelsview.handler.ChannelsViewHandlerUtil;
import com.elektrobit.ebrace.viewer.common.timemarker.views.TimeMarkersView;
import com.elektrobit.ebrace.viewer.common.view.ITableViewerView;
import com.elektrobit.ebrace.viewer.script.ViewerScriptPlugin;
import com.elektrobit.ebrace.viewer.script.util.InjectedParamsDialog;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ChannelTreeNode;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.timemarker.TimeMarker;
import com.elektrobit.ebsolys.script.external.Matches;

public class RunScriptFromContextDynamicMenu extends ContributionItem
{
    private static final String MENU_TEXT_RUN_SCRIPT = "More";
    private final GenericOSGIServiceTracker<ResourcesModelManager> resourceManagerTracker = new GenericOSGIServiceTracker<ResourcesModelManager>( ResourcesModelManager.class );
    private final ResourceManager resourceManager = new LocalResourceManager( JFaceResources.getResources() );
    private final RunScriptInteractionUseCase runScriptUseCase = UseCaseFactoryInstance.get()
            .makeRunScriptInteractionUseCase();
    private final InjectedParamsCallback callback;

    public RunScriptFromContextDynamicMenu()
    {
        callback = new InjectedParamsDialog();
        runScriptUseCase.setInjectedParamsCallback( callback );
    }

    @Override
    public void fill(Menu menu, int index)
    {
        TimeMarker selectedTimeMarker = getSelectedTimeMarker();
        if (selectedTimeMarker != null)
        {
            buildTimeMarkerContextMenu( menu, index, selectedTimeMarker );
            return;
        }

        ChannelTreeNode selectedChannelTreeNode = getSelectedChannelTreeNode();
        if (selectedChannelTreeNode != null)
        {
            RuntimeEventChannel<?> channel = selectedChannelTreeNode.getRuntimeEventChannel();
            if (channel != null)
            {
                buildChannelContextMenu( menu, index, channel );
                return;
            }
        }

        List<RuntimeEvent<?>> events = getEventsFromSelectedTable();
        if ((events != null) && (events.size() != 0))
        {
            if (events.size() == 1)
            {
                buildRuntimeEventContextMenu( menu, index, events.get( 0 ) );
            }
            else
            {
                buildTableContextMenu( menu, index, events );
            }
            return;
        }

        List<TimeMarker> timeMarkers = getSelectedTimeMarkers();
        if ((timeMarkers != null) && (timeMarkers.size() >= 1))
        {
            buildTimeMarkerListContextMenu( menu, index, timeMarkers );
            return;
        }

        List<RuntimeEventChannel<?>> channels = getChannelListFromSelectedTable();
        if ((channels != null) && (channels.size() >= 1))
        {
            buildChannelListContextMenu( menu, index, channels );
            return;
        }
    }

    private TimeMarker getSelectedTimeMarker()
    {
        return (TimeMarker)getSelectedObjectByClass( TimeMarker.class );
    }

    private ChannelTreeNode getSelectedChannelTreeNode()
    {
        return (ChannelTreeNode)getSelectedObjectByClass( ChannelTreeNode.class );
    }

    private Object getSelectedObjectByClass(Class<?> requestedClass)
    {

        ISelection selection = getSelection();
        // TODO: first part of if clause will be removed when the old channels view will be removed
        if (selection instanceof StructuredSelection)
        {
            StructuredSelection ss = (StructuredSelection)selection;
            Object first = ss.getFirstElement();
            if (ss.size() != 1)
            {
                return null;
            }

            if (first != null && requestedClass.isAssignableFrom( first.getClass() ))
            {
                return first;
            }
        }

        if (selection instanceof TreeSelection)
        {
            TreeSelection ss = (TreeSelection)selection;
            if (ss.size() != 1)
            {
                return null;
            }
            Object first = ss.getFirstElement();

            if (first != null && requestedClass.isAssignableFrom( first.getClass() ))
            {
                return first;
            }
        }

        return null;
    }

    private List<TimeMarker> getSelectedTimeMarkers()
    {

        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage page = workbenchWindow.getActivePage();
        IWorkbenchPart part = page.getActivePart();

        List<TimeMarker> timeMarkers = new ArrayList<TimeMarker>();;

        if (part instanceof TimeMarkersView)
        {
            TimeMarkersView timeMarkersView = (TimeMarkersView)part;
            ISelection selecteTimeMarkers = timeMarkersView.getTreeViewer().getSelection();

            if (selecteTimeMarkers instanceof StructuredSelection)
            {

                @SuppressWarnings("unchecked")
                List<Object> listSelectedTimeMarkers = ((StructuredSelection)selecteTimeMarkers).toList();

                for (Object timeMarkersElmnt : listSelectedTimeMarkers)
                {

                    if (timeMarkersElmnt != null && (TimeMarker.class).isAssignableFrom( timeMarkersElmnt.getClass() ))
                    {
                        timeMarkers.add( (TimeMarker)timeMarkersElmnt );
                    }
                }
                return timeMarkers;
            }

        }
        return null;
    }

    private ISelection getSelection()
    {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        return window.getActivePage().getSelection();
    }

    private List<RuntimeEvent<?>> getEventsFromSelectedTable()
    {
        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage page = workbenchWindow.getActivePage();
        IWorkbenchPart part = page.getActivePart();
        if (part instanceof ITableViewerView)
        {
            ISelection selectedEvents = ((ITableViewerView)part).getTreeViewer().getSelection();
            List<?> listSelectedEvents = ((StructuredSelection)selectedEvents).toList();
            List<RuntimeEvent<?>> events = new ArrayList<RuntimeEvent<?>>();
            for (int i = 0; i < listSelectedEvents.size(); i++)
            {
                if (listSelectedEvents.get( i ) instanceof RuntimeEvent<?>)
                {
                    events.add( (RuntimeEvent<?>)listSelectedEvents.get( i ) );
                }
            }
            return events;
        }
        return null;
    }

    private List<RuntimeEventChannel<?>> getChannelListFromSelectedTable()
    {
        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IWorkbenchPage page = workbenchWindow.getActivePage();
        IWorkbenchPart part = page.getActivePart();
        if (part instanceof ChannelsView)
        {
            ChannelsView channelsView = (ChannelsView)part;
            ISelection selectedChannels = channelsView.getSelection();

            List<RuntimeEventChannel<?>> result = ChannelsViewHandlerUtil
                    .getChannelListFromSelection( selectedChannels );
            return result;
        }
        return null;
    }

    private Menu getTimeMarkerListChildMenu(Menu parent, List<TimeMarker> selectedTimeMarkers)
    {
        Menu menu = new Menu( parent );
        ResourcesModelManager resourcesModelManager = resourceManagerTracker.getService();
        List<RaceScriptInfo> timeMarkersScripts = new ArrayList<>();
        for (ResourceModel m : resourcesModelManager.getAllScripts())
        {
            if (m instanceof RaceScriptResourceModel)
            {
                RaceScriptInfo script = ((RaceScriptResourceModel)m).getScriptInfo();

                List<String> channelMethods = new ArrayList<String>();
                for (RaceScriptMethod inp : script.getTimeMarkerListMethods())
                {
                    channelMethods.add( inp.getMethodName() );
                }

                if (!channelMethods.isEmpty())
                {
                    timeMarkersScripts.add( script );
                }
            }

        }

        if (timeMarkersScripts.isEmpty())
        {
            createEmptyMenuItemWithText( "No scripts with List of time markers method declared", menu );
        }
        else
        {
            for (RaceScriptInfo script : timeMarkersScripts)
            {
                createTimeMarkerListScriptMenuItems( menu, script, selectedTimeMarkers );
            }
        }

        return menu;
    }

    private void createTimeMarkerListScriptMenuItems(Menu menu, RaceScriptInfo script, List<TimeMarker> timeMarkers)
    {
        MenuItem menuItem;
        if (!script.isRunning())
        {
            for (RaceScriptMethod timeMarkersMethod : script.getTimeMarkerListMethods())
            {
                menuItem = new MenuItem( menu, SWT.CHECK );
                menuItem.setText( timeMarkersMethod.getLabelText() );

                ImageDescriptor icon = ViewerScriptPlugin.getDefault().getImageDescriptor( "icons/script.gif" );
                menuItem.setImage( icon.createImage() );
                menuItem.addSelectionListener( getTimeMarkerListMethodSelectionListener( script,
                                                                                         timeMarkersMethod
                                                                                                 .getMethodName(),
                                                                                         timeMarkers ) );
            }
        }
        else
        {
            createScriptRunningMenuItem( menu, script );
        }
    }

    private SelectionListener getTimeMarkerListMethodSelectionListener(final RaceScriptInfo script,
            final String timeMarkerMethod, final List<TimeMarker> selectedTimeMarkers)
    {
        return new SelectionAdapter()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                runScriptUseCase
                        .runScriptWithTimeMarkerListPreselection( script, timeMarkerMethod, selectedTimeMarkers );
            }
        };
    }

    private Menu getRuntimeEventChildMenu(Menu parent, RuntimeEvent<?> selectedRuntimeEvent)
    {

        Menu menu = new Menu( parent );
        ResourcesModelManager resourcesModelManager = resourceManagerTracker.getService();
        List<RaceScriptInfo> runtimeEventScripts = new ArrayList<>();
        for (ResourceModel m : resourcesModelManager.getAllScripts())
        {
            if (m instanceof RaceScriptResourceModel)
            {
                RaceScriptInfo script = ((RaceScriptResourceModel)m).getScriptInfo();

                List<String> runtimeEvnetMethods = new ArrayList<String>();
                for (RaceScriptMethod inp : script.getRuntimeEventMethods())
                {
                    runtimeEvnetMethods.add( inp.getMethodName() );
                }

                if (!runtimeEvnetMethods.isEmpty())
                {
                    runtimeEventScripts.add( script );
                }
            }
        }

        if (runtimeEventScripts.isEmpty())
        {
            createEmptyMenuItemWithText( "No scripts with List of RuntimeEventChannel method declared", menu );
        }
        else
        {
            for (RaceScriptInfo script : runtimeEventScripts)
            {
                createRuntimeEventScriptMenuItems( menu, script, selectedRuntimeEvent );
            }
        }
        return menu;
    }

    private void createRuntimeEventScriptMenuItems(Menu menu, RaceScriptInfo script, RuntimeEvent<?> event)
    {
        MenuItem menuItem;
        if (!script.isRunning())
        {
            for (RaceScriptMethod runtimeEventMethod : script.getRuntimeEventMethods())
            {
                menuItem = new MenuItem( menu, SWT.CHECK );
                menuItem.setText( runtimeEventMethod.getLabelText() );

                ImageDescriptor icon = ViewerScriptPlugin.getDefault().getImageDescriptor( "icons/script.gif" );
                menuItem.setImage( icon.createImage() );
                menuItem.addSelectionListener( getRuntimeEventMethodSelectionListener( script,
                                                                                       runtimeEventMethod
                                                                                               .getMethodName(),
                                                                                       event ) );
            }
        }
        else
        {
            createScriptRunningMenuItem( menu, script );
        }
    }

    private void buildTimeMarkerContextMenu(Menu parent, int index, TimeMarker selectedTimeMarker)
    {
        MenuItem menuItem = new MenuItem( parent, SWT.CASCADE, index );
        menuItem.setMenu( getTimeMarkerChildMenu( parent, selectedTimeMarker ) );
        Image image = getRunScriptImage();
        menuItem.setImage( image );
        menuItem.setText( MENU_TEXT_RUN_SCRIPT );
    }

    private void buildChannelContextMenu(Menu parent, int index, RuntimeEventChannel<?> selectedChannel)
    {
        MenuItem menuItem = new MenuItem( parent, SWT.CASCADE, index );
        menuItem.setMenu( getChannelChildMenu( parent, selectedChannel ) );
        Image image = getRunScriptImage();
        menuItem.setImage( image );
        menuItem.setText( MENU_TEXT_RUN_SCRIPT );
    }

    private void buildChannelListContextMenu(Menu parent, int index, List<RuntimeEventChannel<?>> selectedChannels)
    {
        MenuItem menuItem = new MenuItem( parent, SWT.CASCADE, index );
        menuItem.setMenu( getChannelListChildMenu( parent, selectedChannels ) );
        Image image = getRunScriptImage();
        menuItem.setImage( image );
        menuItem.setText( MENU_TEXT_RUN_SCRIPT );
    }

    private void buildTimeMarkerListContextMenu(Menu parent, int index, List<TimeMarker> selectedTimeMarkers)
    {
        MenuItem menuItem = new MenuItem( parent, SWT.CASCADE, index );
        menuItem.setMenu( getTimeMarkerListChildMenu( parent, selectedTimeMarkers ) );
        Image image = getRunScriptImage();
        menuItem.setImage( image );
        menuItem.setText( MENU_TEXT_RUN_SCRIPT );
    }

    private void buildRuntimeEventContextMenu(Menu parent, int index, RuntimeEvent<?> selectedRuntimeEvent)
    {
        MenuItem menuItem = new MenuItem( parent, SWT.CASCADE, index );
        menuItem.setMenu( getRuntimeEventChildMenu( parent, selectedRuntimeEvent ) );
        Image image = getRunScriptImage();
        menuItem.setImage( image );
        menuItem.setText( MENU_TEXT_RUN_SCRIPT );
    }

    private void buildTableContextMenu(Menu parent, int index, List<RuntimeEvent<?>> events)
    {
        MenuItem menuItem = new MenuItem( parent, SWT.CASCADE, index );
        menuItem.setMenu( getTableChildMenu( parent, events ) );
        Image image = getRunScriptImage();
        menuItem.setImage( image );
        menuItem.setText( MENU_TEXT_RUN_SCRIPT );
    }

    private Image getRunScriptImage()
    {
        ImageDescriptor imageDescriptor = ViewerScriptPlugin.getDefault().getImageDescriptor( "icons/script_run.png" );
        return resourceManager.createImage( imageDescriptor );
    }

    private Menu getTimeMarkerChildMenu(Menu parent, TimeMarker selectedTimeMarker)
    {
        Menu menu = new Menu( parent );
        List<RaceScriptInfo> timeMarkerScripts = getTimeMarkerContextScripts();

        if (timeMarkerScripts.isEmpty())
        {
            createEmptyMenuItemWithText( "No scripts with TimeMarker method declared", menu );
        }
        else
        {
            for (RaceScriptInfo script : timeMarkerScripts)
            {
                createTimeMarkerScriptMenuItems( menu, script, selectedTimeMarker );
            }
        }

        return menu;
    }

    private void createTimeMarkerScriptMenuItems(Menu menu, RaceScriptInfo script, TimeMarker selectedTimeMarker)
    {
        MenuItem menuItem;
        if (!script.isRunning())
        {
            for (RaceScriptMethod timeMarkerMethod : script.getTimeMarkerMethods())
            {
                menuItem = new MenuItem( menu, SWT.CHECK );
                ImageDescriptor icon = ViewerScriptPlugin.getDefault().getImageDescriptor( "icons/script.gif" );
                menuItem.setImage( icon.createImage() );
                menuItem.setText( timeMarkerMethod.getLabelText() );

                menuItem.addSelectionListener( getTimeMarkerMethodSelectionListener( script,
                                                                                     timeMarkerMethod.getMethodName(),
                                                                                     selectedTimeMarker ) );
            }
        }
        else
        {
            createScriptRunningMenuItem( menu, script );
        }
    }

    private void createScriptRunningMenuItem(Menu menu, final RaceScriptInfo script)
    {
        MenuItem menuItem = new MenuItem( menu, SWT.CHECK );
        menuItem.setText( script.getName() + "[running]" );
        ImageDescriptor icon = ViewerScriptPlugin.getDefault().getImageDescriptor( "icons/terminate_obj.gif" );
        menuItem.setImage( icon.createImage() );
        menuItem.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                runScriptUseCase.stopScript( script );
            }
        } );
    }

    private void createRuntimeEventListScriptMenuItems(Menu menu, RaceScriptInfo script, List<RuntimeEvent<?>> events)
    {
        MenuItem menuItem;
        if (!script.isRunning())
        {
            for (RaceScriptMethod runtimeEventMethod : script.getRuntimeEventListMethods())
            {
                menuItem = new MenuItem( menu, SWT.CHECK );
                menuItem.setText( runtimeEventMethod.getLabelText() );

                ImageDescriptor icon = ViewerScriptPlugin.getDefault().getImageDescriptor( "icons/script.gif" );
                menuItem.setImage( icon.createImage() );
                menuItem.addSelectionListener( getRuntimeEventMethodSelectionListener( script,
                                                                                       runtimeEventMethod
                                                                                               .getMethodName(),
                                                                                       events ) );
            }
        }
        else
        {
            createScriptRunningMenuItem( menu, script );
        }
    }

    private void createEmptyMenuItemWithText(String text, Menu menu)
    {
        MenuItem menuItem = new MenuItem( menu, SWT.CHECK );
        menuItem.setText( text );
        menuItem.setEnabled( false );
    }

    private Menu getChannelChildMenu(Menu parent, RuntimeEventChannel<?> selectedChannel)
    {
        Menu menu = new Menu( parent );
        List<RaceScriptInfo> scripts = getChannelContextScripts();
        if (scripts.isEmpty())
        {
            createEmptyMenuItemWithText( "No scripts with RuntimeEventChannel method declared", menu );
        }
        else
        {
            for (RaceScriptInfo script : scripts)
            {
                createChannelScriptMenuItems( menu, script, selectedChannel );
            }
        }

        return menu;
    }

    private Menu getChannelListChildMenu(Menu parent, List<RuntimeEventChannel<?>> selectedChannels)
    {
        Menu menu = new Menu( parent );
        ResourcesModelManager resourcesModelManager = resourceManagerTracker.getService();
        List<RaceScriptInfo> channelScripts = new ArrayList<>();
        for (ResourceModel m : resourcesModelManager.getAllScripts())
        {
            if (m instanceof RaceScriptResourceModel)
            {
                RaceScriptInfo script = ((RaceScriptResourceModel)m).getScriptInfo();

                List<String> channelMethods = new ArrayList<String>();
                for (RaceScriptMethod inp : script.getChannelListMethods())
                {
                    channelMethods.add( inp.getMethodName() );
                }

                if (!channelMethods.isEmpty())
                {
                    channelScripts.add( script );
                }
            }

        }

        if (channelScripts.isEmpty())
        {
            createEmptyMenuItemWithText( "No scripts with List of RuntimeEventChannel method declared", menu );
        }
        else
        {
            for (RaceScriptInfo script : channelScripts)
            {
                createChannelListScriptMenuItems( menu, script, selectedChannels );
            }
        }

        return menu;
    }

    private void createChannelListScriptMenuItems(Menu menu, RaceScriptInfo script,
            List<RuntimeEventChannel<?>> selectedChannels)
    {
        MenuItem menuItem;
        if (!script.isRunning())
        {
            for (RaceScriptMethod channelsMethod : script.getChannelListMethods())
            {
                menuItem = new MenuItem( menu, SWT.CHECK );
                menuItem.setText( channelsMethod.getLabelText() );

                ImageDescriptor icon = ViewerScriptPlugin.getDefault().getImageDescriptor( "icons/script.gif" );
                menuItem.setImage( icon.createImage() );
                menuItem.addSelectionListener( getChannelListMethodSelectionListener( script,
                                                                                      channelsMethod.getMethodName(),
                                                                                      selectedChannels ) );
            }
        }
        else
        {
            createScriptRunningMenuItem( menu, script );
        }
    }

    private SelectionListener getChannelListMethodSelectionListener(final RaceScriptInfo script,
            final String timeMarkerMethod, final List<RuntimeEventChannel<?>> selectedChannels)
    {
        return new SelectionAdapter()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                runScriptUseCase.runScriptWithChannelListPreselection( script, timeMarkerMethod, selectedChannels );
            }
        };
    }

    private void createChannelScriptMenuItems(Menu menu, RaceScriptInfo script, RuntimeEventChannel<?> selectedChannel)
    {
        MenuItem menuItem;
        if (!script.isRunning())
        {
            for (RaceScriptMethod channelMethod : script.getChannelMethods())
            {
                if (isApplicableForChannel( channelMethod, selectedChannel ))
                {
                    menuItem = new MenuItem( menu, SWT.CHECK );
                    menuItem.setText( channelMethod.getLabelText() );

                    ImageDescriptor icon = ViewerScriptPlugin.getDefault().getImageDescriptor( "icons/script.gif" );
                    menuItem.setImage( icon.createImage() );
                    menuItem.addSelectionListener( getChannelMethodSelectionListener( script,
                                                                                      channelMethod.getMethodName(),
                                                                                      selectedChannel ) );
                }
            }
        }
        else
        {
            createScriptRunningMenuItem( menu, script );
        }
    }

    private boolean isApplicableForChannel(RaceScriptMethod method, RuntimeEventChannel<?> selectedChannel)
    {
        String restriction = "";

        if (method.getMethod().getParameterCount() == 1)
        {
            Parameter arg = method.getMethod().getParameters()[0];
            for (Annotation argAnnotation : arg.getAnnotations())
            {
                if (argAnnotation instanceof Matches)
                {
                    restriction = ((Matches)argAnnotation).name();
                }
            }
        }

        return (restriction.equals( "" ) || restriction.equals( selectedChannel.getName() ));
    }

    private List<RaceScriptInfo> getChannelContextScripts()
    {
        ResourcesModelManager resourcesModelManager = resourceManagerTracker.getService();
        List<RaceScriptInfo> channelScripts = new ArrayList<>();
        for (ResourceModel m : resourcesModelManager.getAllScripts())
        {
            if (m instanceof RaceScriptResourceModel)
            {
                RaceScriptInfo script = ((RaceScriptResourceModel)m).getScriptInfo();

                List<String> channelMethods = new ArrayList<String>();
                for (RaceScriptMethod inp : script.getChannelMethods())
                {
                    channelMethods.add( inp.getMethodName() );
                }

                if (!channelMethods.isEmpty())
                {
                    channelScripts.add( script );
                }
            }

        }
        return channelScripts;
    }

    private List<RaceScriptInfo> getTimeMarkerContextScripts()
    {
        ResourcesModelManager resourcesModelManager = resourceManagerTracker.getService();
        List<RaceScriptInfo> timeMarkerScripts = new ArrayList<>();
        for (ResourceModel m : resourcesModelManager.getAllScripts())
        {
            if (m instanceof RaceScriptResourceModel)
            {
                RaceScriptInfo script = ((RaceScriptResourceModel)m).getScriptInfo();
                List<String> timeMarkerMethods = new ArrayList<String>();
                for (RaceScriptMethod inp : script.getTimeMarkerMethods())
                {
                    timeMarkerMethods.add( inp.getMethodName() );
                }

                if (!timeMarkerMethods.isEmpty())
                {
                    timeMarkerScripts.add( script );
                }
            }
        }
        return timeMarkerScripts;
    }

    private List<RaceScriptInfo> getRuntimeEventListScripts()
    {
        ResourcesModelManager resourcesModelManager = resourceManagerTracker.getService();
        List<RaceScriptInfo> runtimeEventListScripts = new ArrayList<>();
        for (ResourceModel m : resourcesModelManager.getAllScripts())
        {
            if (m instanceof RaceScriptResourceModel)
            {
                RaceScriptInfo script = ((RaceScriptResourceModel)m).getScriptInfo();
                List<String> eventListMethods = new ArrayList<String>();
                for (RaceScriptMethod inp : script.getRuntimeEventListMethods())
                {
                    eventListMethods.add( inp.getMethodName() );
                }

                if (!eventListMethods.isEmpty())
                {
                    runtimeEventListScripts.add( script );
                }
            }
        }
        return runtimeEventListScripts;
    }

    private Menu getTableChildMenu(Menu parent, List<RuntimeEvent<?>> events)
    {
        Menu menu = new Menu( parent );

        List<RaceScriptInfo> runtimeEventListScripts = getRuntimeEventListScripts();
        if (runtimeEventListScripts.isEmpty())
        {
            createEmptyMenuItemWithText( "No Script with List<RuntimeEvent<?>> method declared", menu );
        }
        else
        {
            for (RaceScriptInfo script : runtimeEventListScripts)
            {
                createRuntimeEventListScriptMenuItems( menu, script, events );
            }
        }

        return menu;
    }

    private SelectionListener getTimeMarkerMethodSelectionListener(final RaceScriptInfo script,
            final String timeMarkerMethod, final TimeMarker selectedTimeMarker)
    {
        return new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                runScriptUseCase.runScriptWithTimeMarkerPreselection( script, timeMarkerMethod, selectedTimeMarker );
            }
        };
    }

    private SelectionListener getChannelMethodSelectionListener(final RaceScriptInfo script,
            final String timeMarkerMethod, final RuntimeEventChannel<?> selectedChannel)
    {
        return new SelectionAdapter()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                runScriptUseCase.runScriptWithChannelPreselection( script, timeMarkerMethod, selectedChannel );
            }
        };
    }

    private SelectionListener getRuntimeEventMethodSelectionListener(final RaceScriptInfo script,
            final String runtimeEventMethod, final List<RuntimeEvent<?>> events)
    {
        return new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                runScriptUseCase.runScriptWithRuntimeEventsPreselection( script, runtimeEventMethod, events );
            }
        };
    }

    private SelectionListener getRuntimeEventMethodSelectionListener(final RaceScriptInfo script,
            final String runtimeEventMethod, final RuntimeEvent<?> event)
    {
        return new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                runScriptUseCase.runScriptWithRuntimeEventPreselection( script, runtimeEventMethod, event );
            }
        };
    }

    @Override
    public void dispose()
    {
        resourceManager.dispose();
        super.dispose();
    }

    @Override
    public boolean isDynamic()
    {
        return true;
    }
}
