/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.util;

import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCaller;
import com.elektrobit.ebrace.common.utils.OSGIWhiteBoardPatternCommand;
import com.elektrobit.ebrace.viewer.common.listeners.TableLockListener;

public class TableLockerUtil
{

    public static void changeLockStateTable()
    {
        new OSGIWhiteBoardPatternCaller<TableLockListener>( TableLockListener.class )
                .callOSGIService( new OSGIWhiteBoardPatternCommand<TableLockListener>()
                {
                    public void callOSGIService(TableLockListener listener)
                    {
                        listener.toggleScrollLock();
                    }
                } );
    }
}
