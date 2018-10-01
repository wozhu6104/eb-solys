/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.swt;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class SWTHelper
{
    // this method is a interim solution.
    // The trigger should either come from UI thread directly or
    // from a use case that is calling the callback also via UI thread
    public static void asyncRedraw(final Control control)
    {
        Display.getDefault().asyncExec( new Runnable()
        {

            @Override
            public void run()
            {
                if (!control.isDisposed())
                {
                    control.redraw();
                }
            }
        } );
    }

}
