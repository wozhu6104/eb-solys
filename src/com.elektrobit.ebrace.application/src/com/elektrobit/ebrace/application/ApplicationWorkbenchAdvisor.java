/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.application;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.IActivityManager;
import org.eclipse.ui.activities.IWorkbenchActivitySupport;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.osgi.framework.Bundle;

import com.elektrobit.ebrace.application.usermessagelogger.UserMessageDialogCreator;
import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLog;
import com.elektrobit.ebrace.dev.usestatlogsannotationloader.api.UseStatLogTypes;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebrace.ui.ecl.splitfile.util.FileProgressWindowOpener;
import com.elektrobit.ebrace.viewer.targetconnector.ConnectToTargetModalWindow;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor
{

    private static final String DEFAULT_PERSPECTIVE_ID = "com.elektrobit.ebrace.resourceconsumptionanalysis";

    private final CommandLineParser commandLineParser = new GenericOSGIServiceTracker<CommandLineParser>( CommandLineParser.class )
            .getService();

    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
    {
        return new ApplicationWorkbenchWindowAdvisor( configurer );
    }

    @Override
    public void initialize(IWorkbenchConfigurer configurer)
    {
        configurer.setSaveAndRestore( true );
        setPlatformIcons( configurer );

        toggleBetaFeatures( configurer, commandLineParser.hasArg( "-beta" ) );
    }

    private void setPlatformIcons(IWorkbenchConfigurer configurer)
    {
        IDE.registerAdapters();
        final String ICONS_PATH = "icons/full/";
        Bundle ideBundle = EBRaceApplicationPlugin.getDefault().getBundle();

        declareWorkbenchImage( configurer,
                               ideBundle,
                               IDE.SharedImages.IMG_OBJ_PROJECT,
                               ICONS_PATH + "obj16/prj_obj.gif",
                               true );

        declareWorkbenchImage( configurer,
                               ideBundle,
                               IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED,
                               ICONS_PATH + "obj16/cprj_obj.gif",
                               true );

        declareWorkbenchImage( configurer,
                               ideBundle,
                               Constants.IMG_ETOOL_PROBLEMS_VIEW,
                               ICONS_PATH + "eview16/problems_view.gif",
                               true );

        declareWorkbenchImage( configurer,
                               ideBundle,
                               Constants.IMG_ETOOL_PROBLEMS_VIEW_ERROR,
                               ICONS_PATH + "eview16/problems_view_error.gif",
                               true );

        declareWorkbenchImage( configurer,
                               ideBundle,
                               Constants.IMG_ETOOL_PROBLEMS_VIEW_WARNING,
                               ICONS_PATH + "eview16/problems_view_warning.gif",
                               true );

        declareWorkbenchImage( configurer,
                               ideBundle,
                               Constants.IMG_OBJS_ERROR_PATH,
                               ICONS_PATH + "obj16/error_tsk.gif",
                               true );
        declareWorkbenchImage( configurer,
                               ideBundle,
                               Constants.IMG_OBJS_WARNING_PATH,
                               ICONS_PATH + "obj16/warn_tsk.gif",
                               true );
    }

    private void declareWorkbenchImage(IWorkbenchConfigurer configurer_p, Bundle ideBundle, String symbolicName,
            String path, boolean shared)
    {
        URL url = ideBundle.getEntry( path );
        ImageDescriptor desc = ImageDescriptor.createFromURL( url );
        configurer_p.declareImage( symbolicName, desc, shared );
    }

    private void toggleBetaFeatures(IWorkbenchConfigurer configurer, boolean activate)
    {
        IWorkbenchActivitySupport activitySupport = configurer.getWorkbench().getActivitySupport();
        IActivityManager activityManager = activitySupport.getActivityManager();
        Set<String> enabledActivities = new HashSet<String>();
        String id = "com.elektrobit.ebrace.feature.type.beta";
        if (activityManager.getActivity( id ).isDefined())
        {
            if (activate)
            {
                enabledActivities.add( id );
            }
            else
            {
                enabledActivities.remove( id );
            }
        }
        activitySupport.setEnabledActivityIds( enabledActivities );
    }

    @Override
    public String getInitialWindowPerspectiveId()
    {
        return DEFAULT_PERSPECTIVE_ID;
    }

    @Override
    public void postStartup()
    {
        createCoreActionHandlers();
        removeNotNeededPreferences();
        openConnectToTargetWindow();
    }

    private void createCoreActionHandlers()
    {
        UserMessageDialogCreator window = new UserMessageDialogCreator();
        UseCaseFactoryInstance.get().makeUserLoggerMessageNotifyUseCase( window );

        new FileProgressWindowOpener();
    }

    private void removeNotNeededPreferences()
    {
        // No idea where these preferences come from
        // we have to remove them otherwise they will be displayed in preferences window
        PreferenceManager pm = PlatformUI.getWorkbench().getPreferenceManager();
        pm.remove( "org.eclipse.birt.chart.ui.swt.fieldassist.preferences.FieldAssistPreferencePage" );
        pm.remove( "org.eclipse.mat.hprof.ui.HPROFPreferences" );
        pm.remove( "org.eclipse.linuxtools.tmf.ui.TmfTracingPreferences" );
    }

    @UseStatLog(UseStatLogTypes.CONNECT_TO_TARGET_WINDOW_OPENED)
    private void openConnectToTargetWindow()
    {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        ConnectToTargetModalWindow dialog = new ConnectToTargetModalWindow( shell );
        dialog.create();
        dialog.setBlockOnOpen( false );
        dialog.open();
    }

    @Override
    public boolean preShutdown()
    {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors( false );
        return super.preShutdown();
    }
}
