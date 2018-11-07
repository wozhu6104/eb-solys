/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.toolbar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import com.elektrobit.ebrace.viewer.common.constants.ViewIDs;

public class SlidingWindowShowButton extends WorkbenchWindowControlContribution
{
    private static final String SHOW_LABEL = "Show File Overview";
    private static final String HIDE_LABEL = "Hide File Overview";

    private boolean slidingWindowVisible = false;
    private Button button;

    @Override
    protected Control createControl(Composite parent)
    {
        if (!isSlidingWindowActive())
        {
            return null;
        }

        Composite composite = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        composite.setLayout( new FillLayout() );
        button = new Button( composite, SWT.PUSH );
        button.setText( SHOW_LABEL );

        button.addSelectionListener( new SelectionListener()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                slidingWindowVisible = !slidingWindowVisible;
                updateToState();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        } );

        return composite;
    }

    private boolean isSlidingWindowActive()
    {
        return false;
    }

    private void updateToState()
    {
        try
        {
            if (!button.isDisposed())
            {
                button.setText( slidingWindowVisible ? HIDE_LABEL : SHOW_LABEL );
            }
            if (slidingWindowVisible)
            {
                IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                activePage.showView( ViewIDs.SLIDING_WINDOW_VIEW_ID );
            }
            else
            {
                IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                        .findView( ViewIDs.SLIDING_WINDOW_VIEW_ID );
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView( view );
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }
}
