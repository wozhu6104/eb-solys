/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.headlessexecutor.impl;

import com.elektrobit.ebrace.core.interactor.api.headlessexecutor.HeadlessParams;

public class HeadlessParamsValidator
{
    private final ScriptTypeValidator scriptTypeValidator;

    public HeadlessParamsValidator(ScriptTypeValidator scriptTypeValidator)
    {
        this.scriptTypeValidator = scriptTypeValidator;
    }

    public boolean validateSwitchesCombinationAndLogError(HeadlessParams params)
    {
        if (params.isHelpSwitchSet)
        {
            return true;
        }

        if (isConnectModeActive( params ))
        {
            return checkParamsForConnectMode( params );
        }

        if (isScriptExecutionModeActive( params ))
        {
            return checkParamsForScriptExecutionMode( params );
        }

        if (isRunScriptModeActive( params ))
        {
            return checkParamsForScriptMode( params );
        }

        if (isLoadFileActive( params ))
        {
            return checkParamsForLoadFileMode( params );
        }

        System.out.println( "Error: no mode defined" );
        return false;
    }

    private boolean isConnectModeActive(HeadlessParams params)
    {
        return params.targetIpAdress != null;
    }

    private boolean checkParamsForConnectMode(HeadlessParams params)
    {
        if ((params.targetIpAdress == null) || (params.targetPort == null))
        {
            System.out.println( "\nError: IP address or port not defined" );
            return false;
        }
        if (isLoadFileActive( params ))
        {
            System.out.println( "\nError: Invalid combination of parameters: -c and -f. " );
            return false;
        }
        return true;
    }

    private boolean isScriptExecutionModeActive(HeadlessParams params)
    {
        return params.scriptMethodName != null;
    }

    private boolean checkParamsForScriptExecutionMode(HeadlessParams params)
    {
        if (!isConnectModeActive( params ) && !isLoadFileActive( params ))
        {
            if (!scriptTypeValidator.isMethodGlobal( params.scriptMethodName ))
            {
                System.out.println( "\nError: When only loading a script this should contain only one global method." );
                return false;
            }
        }

        if (isRunScriptModeActive( params ))
        {
            if (scriptTypeValidator.isCallbackModeActive( params.scriptMethodName )
                    || scriptTypeValidator.isGlobalModeActive( params.scriptMethodName ))
            {
                return true;
            }

            ifPreselectionMode( params );
            return false;
        }
        else
        {
            System.out.println( "\nError: Please load a script in order to choose a method to run." );
            return false;
        }
    }

    private void ifPreselectionMode(HeadlessParams params)
    {
        if (scriptTypeValidator.isPreselectionModeActive( params.scriptMethodName ))
        {
            System.out.println( "\nError: No global or callback method selected." );
        }
        else
        {
            System.out.println( "\nError: The selected method does not exist in script." );
        }
    }

    private boolean isRunScriptModeActive(HeadlessParams params)
    {
        return params.scriptName != null;
    }

    private boolean checkParamsForScriptMode(HeadlessParams params)
    {
        if (!isConnectModeActive( params ) && !isLoadFileActive( params ))
        {
            if (!scriptTypeValidator.isOnlyExecutableMethodGlobal())
            {
                System.out.println( "\nError: When only loading a script this should contain only one global method." );
                return false;
            }
            else
            {
                if (scriptTypeValidator.isCallbackModeActive( params.scriptMethodName )
                        || scriptTypeValidator.isGlobalModeActive( params.scriptMethodName ))
                {
                    return true;
                }

                ifPreselectionMode( params );
                return false;
            }
        }
        return true;
    }

    private boolean isLoadFileActive(HeadlessParams params)
    {
        return params.fileToLoadName != null;
    }

    private boolean checkParamsForLoadFileMode(HeadlessParams params)
    {
        if (isRunScriptModeActive( params ))
        {
            return true;
        }
        System.out.println( "\nError: Cannot load a file without executing a script" );
        return false;
    }
}
