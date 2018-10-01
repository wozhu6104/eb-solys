/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.console;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleAdministrator
{
    private static final int DISPLAY_CONSOLE_DELAY_MS = 500;
    private final static Map<String, Boolean> displayConsoleRunnablePendingMap = new ConcurrentHashMap<String, Boolean>();
    private final static Timer displayConsoleTimer = new Timer( "display script console timer" );

    public static MessageConsole findOrCreateConsole(String consoleName)
    {
        ConsolePlugin plugin = ConsolePlugin.getDefault();
        IConsoleManager conMan = plugin.getConsoleManager();
        IConsole[] existing = conMan.getConsoles();
        for (int i = 0; i < existing.length; i++)
        {
            if (consoleName.equals( existing[i].getName() ))
            {
                return (MessageConsole)existing[i];
            }
        }
        // no console found, so create a new one
        MessageConsole myConsole = new MessageConsole( consoleName, null );
        // limit console output to not run out of memory
        myConsole.setWaterMarks( 1000, 10000000 );
        conMan.addConsoles( new IConsole[]{myConsole} );
        return myConsole;
    }

    public static MessageConsoleStream getMessageConsoleStreamOfConsoleName(String consoleName)
    {
        MessageConsoleStream messageConsoleStream = findOrCreateConsole( consoleName ).newMessageStream();
        return messageConsoleStream;
    }

    public static void displayConsole(final String consoleName)
    {
        if (isDisplayConsolePending( consoleName ))
            return;

        noteDisplayConsolePosted( consoleName );
        displayConsoleTimer.schedule( new TimerTask()
        {
            @Override
            public void run()
            {
                postdisplayConsoleToDisplay( consoleName );
            }
        }, DISPLAY_CONSOLE_DELAY_MS );
    }

    private static void postdisplayConsoleToDisplay(final String consoleName)
    {
        Display.getDefault().asyncExec( new Runnable()
        {
            @Override
            public void run()
            {
                IConsole console = findOrCreateConsole( consoleName );
                IWorkbench wb = PlatformUI.getWorkbench();
                IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
                IWorkbenchPage page = win.getActivePage();
                String id = IConsoleConstants.ID_CONSOLE_VIEW;
                IConsoleView view;
                try
                {
                    if (page.findView( id ) == null)
                    {
                        view = (IConsoleView)page.showView( id );
                        view.display( console );
                    }
                }
                catch (PartInitException e)
                {
                    e.printStackTrace();
                }
                noteDisplayConsoleDone( consoleName );
            }
        } );
    }

    private static void noteDisplayConsolePosted(String consoleName)
    {
        displayConsoleRunnablePendingMap.put( consoleName, true );
    }

    private static void noteDisplayConsoleDone(String consoleName)
    {
        displayConsoleRunnablePendingMap.remove( consoleName );
    }

    private static boolean isDisplayConsolePending(String consoleName)
    {
        return displayConsoleRunnablePendingMap.containsKey( consoleName );
    }
}
