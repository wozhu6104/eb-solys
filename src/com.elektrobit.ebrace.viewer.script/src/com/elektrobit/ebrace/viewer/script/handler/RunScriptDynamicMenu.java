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

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.core.interactor.api.resources.model.script.RaceScriptResourceModel;
import com.elektrobit.ebrace.core.interactor.api.script.RaceScriptMethod;
import com.elektrobit.ebrace.viewer.script.ViewerScriptPlugin;
import com.elektrobit.ebrace.viewer.script.util.CallbackScriptExecutionHelper;
import com.elektrobit.ebrace.viewer.script.util.GlobalScriptExecutionHelper;
import com.elektrobit.ebrace.viewer.script.util.InjectedParamsDialog;

public class RunScriptDynamicMenu extends ContributionItem
{

    @Override
    public void fill(Menu menu, int index)
    {
        RaceScriptResourceModel selectedScript = getSelectedScript();

        if (selectedScript == null)
        {
            return;
        }
        if (selectedScript.getScriptInfo().isRunning())
        {
            buildStopMenu( menu, index, selectedScript );
        }
        else
        {
            buildRunMenu( menu, index, selectedScript );
        }
        new MenuItem( menu, SWT.SEPARATOR, menu.getItemCount() - 1 );
    }

    private RaceScriptResourceModel getSelectedScript()
    {
        ISelection selection = getSelection();
        return getScriptForSelection( selection );
    }

    private ISelection getSelection()
    {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        return window.getActivePage().getSelection();
    }

    private RaceScriptResourceModel getScriptForSelection(ISelection selection)
    {
        if (selection instanceof StructuredSelection)
        {
            StructuredSelection structuredSelection = (StructuredSelection)selection;
            Object firstElement = structuredSelection.getFirstElement();
            if (isOnlyOneScriptSelected( structuredSelection ) && isScript( firstElement ))
            {
                return (RaceScriptResourceModel)firstElement;
            }
        }
        return null;
    }

    private boolean isOnlyOneScriptSelected(StructuredSelection element)
    {
        return element.toList().size() == 1;
    }

    private boolean isScript(Object element)
    {
        return element instanceof RaceScriptResourceModel;
    }

    private void buildRunMenu(Menu parent, int index, final RaceScriptResourceModel selectedScript)
    {
        MenuItem menuItem = new MenuItem( parent, SWT.CASCADE, index );
        menuItem.setMenu( getRunChildMenu( parent, selectedScript ) );
        ImageDescriptor icon = ViewerScriptPlugin.getDefault().getImageDescriptor( "icons/script_run.png" );
        menuItem.setImage( icon.createImage() );
        menuItem.setText( "Run Script" );
    }

    private Menu getRunChildMenu(Menu parent, RaceScriptResourceModel selectedScript)
    {
        if (selectedScript.getScriptInfo().getGlobalMethods().isEmpty()
                && selectedScript.getScriptInfo().getCallbackMethods().isEmpty())
        {
            return getRunChildEmptyMenu( parent );
        }
        else
        {
            return getRunChildMenuWithMethod( parent, selectedScript );
        }
    }

    private Menu getRunChildEmptyMenu(Menu parent)
    {
        Menu menu = new Menu( parent );
        MenuItem menuItem = new MenuItem( menu, SWT.CHECK );
        menuItem.setText( "No Global or Callback Methods Available" );
        menuItem.setEnabled( false );
        return menu;
    }

    private Menu getRunChildMenuWithMethod(Menu parent, RaceScriptResourceModel selectedScript)
    {
        Menu menu = new Menu( parent );
        MenuItem menuItem;
        for (RaceScriptMethod method : selectedScript.getScriptInfo().getGlobalMethods())
        {
            menuItem = new MenuItem( menu, SWT.CHECK );
            menuItem.setText( method.getLabelText() + " (GLOBAL)" );

            menuItem.addSelectionListener( getGlobalRunSelectionListener( selectedScript, method.getMethodName() ) );
        }
        for (RaceScriptMethod method : selectedScript.getScriptInfo().getCallbackMethods())
        {
            menuItem = new MenuItem( menu, SWT.CHECK );
            menuItem.setText( method.getLabelText() + " (CALLBACK)" );

            menuItem.addSelectionListener( getCallbackRunSelectionListener( selectedScript, method.getMethodName() ) );
        }
        return menu;
    }

    private SelectionListener getGlobalRunSelectionListener(final RaceScriptResourceModel selectedScript,
            final String method)
    {
        return new SelectionListener()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                GlobalScriptExecutionHelper
                        .toggleExecution( selectedScript.getScriptInfo(), method, new InjectedParamsDialog() );
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        };
    }

    private SelectionListener getCallbackRunSelectionListener(final RaceScriptResourceModel selectedScript,
            final String method)
    {
        return new SelectionListener()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                CallbackScriptExecutionHelper.toggleExecution( selectedScript.getScriptInfo(), method );
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        };
    }

    private void buildStopMenu(Menu parent, int index, final RaceScriptResourceModel selectedScript)
    {
        MenuItem menuItem = new MenuItem( parent, SWT.PUSH, index );
        menuItem.setText( "Stop Script" );
        ImageDescriptor icon = ViewerScriptPlugin.getDefault().getImageDescriptor( "icons/terminate_obj.gif" );
        menuItem.setImage( icon.createImage() );
        menuItem.addSelectionListener( new SelectionListener()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                GlobalScriptExecutionHelper.stopScript( selectedScript.getScriptInfo() );
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        } );
    }

    @Override
    public boolean isDynamic()
    {
        return true;
    }

}
