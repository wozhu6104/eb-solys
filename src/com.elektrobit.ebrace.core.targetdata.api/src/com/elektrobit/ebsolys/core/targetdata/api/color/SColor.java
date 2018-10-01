/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.color;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;

import lombok.Data;

@Data
public class SColor
{
    private static final int RGB_MAX_VALUE = 255;
    private static final int RGB_MIN_VALUE = 0;
    private final int red;
    private final int green;
    private final int blue;

    public SColor(int red, int green, int blue)
    {
        RangeCheckUtils.assertIntegerInInterval( "rgb value", red, RGB_MIN_VALUE, RGB_MAX_VALUE );
        RangeCheckUtils.assertIntegerInInterval( "rgb value", green, RGB_MIN_VALUE, RGB_MAX_VALUE );
        RangeCheckUtils.assertIntegerInInterval( "rgb value", blue, RGB_MIN_VALUE, RGB_MAX_VALUE );

        this.red = red;
        this.green = green;
        this.blue = blue;
    }
}
