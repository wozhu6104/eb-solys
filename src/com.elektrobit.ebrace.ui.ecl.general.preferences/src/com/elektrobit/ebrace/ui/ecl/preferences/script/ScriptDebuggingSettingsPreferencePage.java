/*******************************************************************************
 * Copyright (C) 2019 systemticks GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.preferences.script;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.common.utils.FileHelper;

public class ScriptDebuggingSettingsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    private static final String START_TAG = "#GENERATED-REMOTE-DEBUG-OPTION-START";
    private static final String DEBUG_OPTION_PARAM = "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005";
    private static final String END_TAG = "#GENERATED-REMOTE-DEBUG-OPTION-START";
    private Composite parentComposite;
    private boolean debuggingActive = false;
    private Button button;

    public ScriptDebuggingSettingsPreferencePage()
    {
    }

    public ScriptDebuggingSettingsPreferencePage(String title)
    {
        super( title );
    }

    public ScriptDebuggingSettingsPreferencePage(String title, ImageDescriptor image)
    {
        super( title, image );
    }

    @Override
    public void init(IWorkbench workbench)
    {
        debuggingActive = isDebugOptionInIni();
    }

    private boolean isDebugOptionInIni()
    {
        try
        {
            URL url = new URL( Platform.getInstallLocation().getURL() + "ebsolys.ini" );
            File ebSolysIni = new File( url.toURI() );
            if (ebSolysIni.exists())
            {
                String ebSolysIniContent = FileHelper.readFileToString( ebSolysIni );
                if (ebSolysIniContent.contains( START_TAG ))
                {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected Control createContents(Composite parent)
    {
        System.out.println( "createContents" );
        createParentComposite( parent );
        createPageContent();
        return parentComposite;
    }

    private void createParentComposite(Composite parent)
    {
        parentComposite = new Composite( parent, SWT.NONE );
        GridLayout gridLayout = GridLayoutFactory.fillDefaults().create();
        gridLayout.numColumns = 1;
        gridLayout.marginTop = 10;
        parentComposite.setLayout( gridLayout );
    }

    private void createPageContent()
    {
        Label description = new Label( parentComposite, SWT.NONE );
        description
                .setText( "Active script debugging has potential performance impacts for the whole application. \nWe recommend to activate it only for writting scripts and deactivate it when data analysis is done." );
        GridData descriptionLabelData = new GridData( GridData.FILL_HORIZONTAL );
        descriptionLabelData.horizontalSpan = 2;
        description.setLayoutData( descriptionLabelData );

        button = new Button( parentComposite, SWT.CHECK );
        button.setText( "Script Debugging Enabled" );
        button.setSelection( debuggingActive );

        Label restartHintLabel = new Label( parentComposite, SWT.NONE );
        restartHintLabel
                .setText( "Note:\nEB solys will be shutdown on enabling/disabling script debugging and\nmust be started manually again due to technical issues." );

        if (!iniFileExists())
        {
            Label devModeWarning = new Label( parentComposite, SWT.NONE );
            devModeWarning
                    .setText( "Warning:\nIt looks like you're started EB solys from eclipse.\nTo enable script debugging please add following line to vmargs of your product configuration:\n"
                            + DEBUG_OPTION_PARAM );
        }
    }

    private boolean iniFileExists()
    {
        try
        {
            URL url = new URL( Platform.getInstallLocation().getURL() + "ebsolys.ini" );
            File ebSolysIni = new File( url.toURI() );
            return ebSolysIni.exists();
        }
        catch (URISyntaxException e)
        {
        }
        catch (MalformedURLException e)
        {
        }
        return false;
    }

    @Override
    public boolean performOk()
    {
        if (!isDebugOptionInIni() && button.getSelection() == true)
        {
            addDebugOptionAndRestart();
        }
        else if (isDebugOptionInIni() && button.getSelection() == false)
        {
            removeDebugOptionAndRestart();
        }

        System.out.println( "performOk" );
        return true;
    }

    private void addDebugOptionAndRestart()
    {
        addDebugOption();
        triggerRestart();

    }

    private void addDebugOption()
    {
        try
        {
            URL url = new URL( Platform.getInstallLocation().getURL() + "ebsolys.ini" );
            File ebSolysIni = new File( url.toURI() );
            if (ebSolysIni.exists())
            {
                String ebSolysIniContent = FileHelper.readFileToString( ebSolysIni );
                if (!ebSolysIniContent.contains( START_TAG ))
                {
                    BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( ebSolysIni, true ) );
                    bufferedWriter.append( "\n" + START_TAG + "\n" );
                    bufferedWriter.append( DEBUG_OPTION_PARAM + "\n" );
                    bufferedWriter.append( END_TAG + "\n" );
                    bufferedWriter.close();
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void triggerRestart()
    {
        Display.getCurrent().timerExec( 1000, () -> closeApplication() );
    }

    private void closeApplication()
    {
        IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.close();
    }

    private void removeDebugOptionAndRestart()
    {
        removeDebugOption();
        triggerRestart();
    }

    private void removeDebugOption()
    {
        try
        {
            URL url = new URL( Platform.getInstallLocation().getURL() + "ebsolys.ini" );
            File ebSolysIni = new File( url.toURI() );
            if (ebSolysIni.exists())
            {
                String ebSolysIniContent = FileHelper.readFileToString( ebSolysIni );
                if (ebSolysIniContent.contains( START_TAG ))
                {

                    String ebSolysIniContentNew = ebSolysIniContent.replaceFirst( START_TAG, "" );
                    ebSolysIniContentNew = ebSolysIniContentNew.replaceFirst( DEBUG_OPTION_PARAM, "" );
                    ebSolysIniContentNew = ebSolysIniContentNew.replaceFirst( END_TAG, "" );
                    BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( ebSolysIni, false ) );
                    bufferedWriter.append( ebSolysIniContentNew );
                    bufferedWriter.close();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
