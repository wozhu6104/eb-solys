/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

enum UserMessageLoggerTypes {
    ERROR, WARNING, INFO;
}

public class UserMessageDialog
{
    public static void messageDialog(UserMessageLoggerTypes type, String message, String title)
    {
        Shell child = new Shell( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() );

        switch (type)
        {
            case ERROR :
                MessageDialog.open( MessageDialog.ERROR, child, title, message, SWT.NONE );
                break;

            case WARNING :
                MessageDialog.open( MessageDialog.WARNING, child, title, message, SWT.NONE );
                break;

            case INFO :
                MessageDialog.open( MessageDialog.INFORMATION, child, title, message, SWT.NONE );
                break;

            default :
                break;
        }
    }

    public static void UserProMessageDialog()
    {
        messageDialog( UserMessageLoggerTypes.INFO,
                       "Using this feature requires EB solys pro version.",
                       "Activation Licence-Key" );
    }

}
