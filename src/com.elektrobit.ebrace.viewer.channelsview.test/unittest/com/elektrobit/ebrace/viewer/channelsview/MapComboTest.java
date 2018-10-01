/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.channelsview;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.viewer.common.view.MapCombo;
import com.elektrobit.ebrace.viewer.common.view.MapComboItem;

public class MapComboTest
{
    private final static int DLT_LOG_FATAL = 0x01;
    private final static int DLT_LOG_ERROR = 0x02;
    private final static int DLT_LOG_WARN = 0x03;
    private final static int DLT_LOG_INFO = 0x04;
    private final static int DLT_LOG_DEBUG = 0x05;
    private final static int DLT_LOG_VERBOSE = 0x06;

    private MapCombo combo;
    private List<MapComboItem> items;

    @Before
    public void setup()
    {
        combo = new MapCombo( new Shell(), SWT.NONE );
        items = new ArrayList<MapComboItem>();
        items.add( new MapComboItem( "FATAL", DLT_LOG_FATAL ) );
        items.add( new MapComboItem( "ERROR", DLT_LOG_ERROR ) );
        items.add( new MapComboItem( "WARN", DLT_LOG_WARN ) );
        items.add( new MapComboItem( "INFO", DLT_LOG_INFO ) );
        items.add( new MapComboItem( "DEBUG", DLT_LOG_DEBUG ) );
        items.add( new MapComboItem( "VERBOSE", DLT_LOG_VERBOSE ) );
        combo.setItems( items );
    }

    @Test
    public void getSelectedItemValue()
    {
        combo.select( 3 );
        assertEquals( DLT_LOG_INFO, (int)combo.getSelectedItemValue() );
    }

}
