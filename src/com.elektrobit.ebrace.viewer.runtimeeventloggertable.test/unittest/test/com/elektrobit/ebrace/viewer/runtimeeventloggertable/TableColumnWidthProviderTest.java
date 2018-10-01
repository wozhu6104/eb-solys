/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.viewer.runtimeeventloggertable;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elektrobit.ebrace.viewer.runtimeeventloggertable.util.TableColumnWidthProvider;

public class TableColumnWidthProviderTest
{
    private final TableColumnWidthProvider provider = new TableColumnWidthProvider( 1000, 3 );
    private final TableColumnWidthProvider providerWithoutCustom = new TableColumnWidthProvider( 1000, 0 );

    @Test
    public void getTimestampColumnWidth()
    {
        assertEquals( 100, provider.getTimestampColumnWidth() );
    }

    @Test
    public void getChannelColumnWidth()
    {
        assertEquals( 200, provider.getChannelColumnWidth() );
    }

    @Test
    public void getValueColumnWidth()
    {
        assertEquals( 489, provider.getValueColumnWidth() );
    }

    @Test
    public void getCustomColumnWidth()
    {
        assertEquals( 54, provider.getCustomColumnWidth() );
    }

    @Test
    public void getCustomColumnWidthWithoutCustomColumns()
    {
        assertEquals( 0, providerWithoutCustom.getCustomColumnWidth() );
    }
}
