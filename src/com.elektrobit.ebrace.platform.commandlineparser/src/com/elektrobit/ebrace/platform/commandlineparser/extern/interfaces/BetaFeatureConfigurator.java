/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.google.common.annotations.VisibleForTesting;

public class BetaFeatureConfigurator
{
    private static GenericOSGIServiceTracker<CommandLineParser> commandLineParserTracker = null;

    private static Boolean testSetupActive = null;

    public enum Features {
        DATA_CHUNK("com.elektrobit.ebrace.feature.datachunk"), DATA_INPUT("com.elektrobit.ebrace.feature.datainput");

        private Features(String key)
        {
        }

        public boolean isActive()
        {
            if (testSetupActive != null)
            {
                return testSetupActive;
            }

            if (getCommandLineParser().hasArg( "-beta" ))
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        private CommandLineParser getCommandLineParser()
        {
            if (commandLineParserTracker == null)
            {
                commandLineParserTracker = new GenericOSGIServiceTracker<CommandLineParser>( CommandLineParser.class );
            }
            CommandLineParser commandLineParser = commandLineParserTracker.getService();
            if (commandLineParser == null)
            {
                throw new IllegalStateException( "CommandLineParser is not available" );
            }
            return commandLineParser;
        }
    }

    /**
     * For tests only
     * 
     */
    @VisibleForTesting
    public static void setBetaActive(boolean active)
    {
        testSetupActive = active;
    }

}
