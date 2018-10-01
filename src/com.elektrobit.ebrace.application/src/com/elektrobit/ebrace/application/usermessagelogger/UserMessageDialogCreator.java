/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.application.usermessagelogger;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;

public class UserMessageDialogCreator implements UserMessageLoggerNotifyCallback
{
    private static final Logger LOG = Logger.getLogger( UserMessageDialogCreator.class );

    @Override
    public void onLogUserMessage(UserMessageLoggerTypes type, String message)
    {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

        switch (type)
        {
            case ERROR :
                LOG.error( message );
                MessageDialog.openError( shell, "Error", message );
                break;
            case WARNING :
                LOG.warn( message );
                MessageDialog.openWarning( shell, "Warning", message );
                break;
            case INFO :
                LOG.info( message );
                MessageDialog.openInformation( shell, "Info", message );
                break;
            default :
                LOG.error( "Unsupported UserLoggerMessageType: " + type );
                break;
        }
    }
}
