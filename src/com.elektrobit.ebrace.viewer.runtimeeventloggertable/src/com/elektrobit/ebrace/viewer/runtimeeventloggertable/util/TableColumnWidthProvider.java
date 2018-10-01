/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.runtimeeventloggertable.util;

import java.util.Arrays;

import lombok.Getter;

public class TableColumnWidthProvider
{
    private final double TIMESTAMP_COLUMN_RATIO = .1;
    private final double CHANNEL_COLUMN_RATIO = .2;

    public enum FixedColumnWidths {
        TAG_COLUMN_WIDTH(17), ANALYSIS_TIMESPAN_COLUMN_WIDTH(15), COLOR_COLUMN_WIDTH(15);

        private final int width;

        FixedColumnWidths(int width)
        {
            this.width = width;
        }

        public int getWidth()
        {
            return width;
        }
    };

    private final long availableWidth;
    private final int nrOfCustomColumns;

    private int fixedColumnWidths;

    @Getter
    private long timestampColumnWidth;
    @Getter
    private long channelColumnWidth;
    @Getter
    private long valueColumnWidth;
    @Getter
    private long customColumnWidth;

    public TableColumnWidthProvider(long availableWidth, int nrOfCustomColumns)
    {
        this.availableWidth = availableWidth;
        this.nrOfCustomColumns = nrOfCustomColumns;
        calculateTimestampColumnWidth();
        calculateChannelColumnWidth();
        calculateFixedColumnWidths();
        calculateValueColumnWidth();
        calculateCustomColumnWidth();
    }

    private void calculateTimestampColumnWidth()
    {
        timestampColumnWidth = Math.round( availableWidth * TIMESTAMP_COLUMN_RATIO );
    }

    private void calculateChannelColumnWidth()
    {
        channelColumnWidth = Math.round( availableWidth * CHANNEL_COLUMN_RATIO );
    }

    private void calculateFixedColumnWidths()
    {
        fixedColumnWidths = Arrays.stream( FixedColumnWidths.values() )
                .map( fixedColumnWidth -> fixedColumnWidth.width ).reduce( (sum, next) -> sum + next ).get();
    }

    private void calculateValueColumnWidth()
    {
        int remainingWidth = (int)availableWidth;
        valueColumnWidth = (remainingWidth - getTimestampColumnWidth() - getChannelColumnWidth() - fixedColumnWidths);
        if (nrOfCustomColumns > 0)
        {
            valueColumnWidth = valueColumnWidth / 4 * 3;
        }
    }

    private void calculateCustomColumnWidth()
    {
        if (nrOfCustomColumns > 0)
        {
            customColumnWidth = (availableWidth - timestampColumnWidth - channelColumnWidth - valueColumnWidth
                    - fixedColumnWidths) / nrOfCustomColumns;
        }
        else
        {
            customColumnWidth = 0;
        }
    }

}
