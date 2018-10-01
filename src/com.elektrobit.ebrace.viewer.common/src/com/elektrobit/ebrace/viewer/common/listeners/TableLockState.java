/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.listeners;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.osgi.framework.ServiceRegistration;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceRegistration;

public class TableLockState extends AbstractSourceProvider implements TableLockListener
{

    public final static String TABLE_LOCK_STATE_ID = "com.elektrobit.ebrace.viewer.tablelockstate.active";

    public final static String LOCKED = "LOCKED";
    public final static String UNLOCKED = "UNLOCKED";

    private boolean tableLocked = false;

    private final ServiceRegistration<?> tableLockService;

    public TableLockState()
    {
        tableLockService = GenericOSGIServiceRegistration.registerService( TableLockListener.class, this );
    }

    @Override
    public void dispose()
    {
        GenericOSGIServiceRegistration.unregisterService( tableLockService );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Map getCurrentState()
    {
        Map map = new HashMap( 1 );
        String value = tableLocked ? LOCKED : UNLOCKED;
        map.put( TABLE_LOCK_STATE_ID, value );
        return map;
    }

    public boolean isTableLocked()
    {
        if (getCurrentState().get( TABLE_LOCK_STATE_ID ).equals( LOCKED ))
            return true;
        return false;
    }

    @Override
    public String[] getProvidedSourceNames()
    {
        return new String[]{TABLE_LOCK_STATE_ID};
    }

    @Override
    public void toggleScrollLock()
    {
        Display.getDefault().asyncExec( new Runnable()
        {

            @Override
            public void run()
            {
                tableLocked = !tableLocked;
                String value = tableLocked ? LOCKED : UNLOCKED;
                fireSourceChanged( ISources.WORKBENCH, TABLE_LOCK_STATE_ID, value );
            }
        } );
    }

}
