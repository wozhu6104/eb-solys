/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.common;

public class ProVersion
{
    private static ProVersionProvider instance = new ProVersionProvider()
    {
        private Boolean isActive;

        @Override
        public boolean isActive()
        {
            if (isActive == null)
            {
                throw new IllegalStateException( "PRO version flag has not beed initialized" );
            }
            return isActive;
        }

        @Override
        public void setActive(boolean isActive)
        {
            this.isActive = isActive;
        }
    };

    public static ProVersionProvider getInstance()
    {
        return instance;
    }

}
