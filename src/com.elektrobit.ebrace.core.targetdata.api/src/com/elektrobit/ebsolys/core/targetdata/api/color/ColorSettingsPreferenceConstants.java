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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface ColorSettingsPreferenceConstants
{
    // LIGHT_BLUE, LIGHT_GREEN, FUCHSIA, RED , LIGHT_ORANGE, ORANGE, STRONG_PURPLE, YELLOW, BROWN,
    // DARK_BLUE, DARK_GREEN, PURPLE
    public static final List<SColor> defaultChannelColors = new ArrayList<SColor>( Arrays
            .asList( new SColor( 166, 206, 227 ),
                     new SColor( 178, 223, 138 ),
                     new SColor( 255, 0, 255 ),
                     new SColor( 227, 26, 28 ),
                     new SColor( 253, 191, 111 ),
                     new SColor( 255, 127, 0 ),
                     new SColor( 106, 61, 154 ),
                     new SColor( 219, 227, 68 ),
                     new SColor( 177, 89, 40 ),
                     new SColor( 31, 120, 180 ),
                     new SColor( 51, 160, 44 ),
                     new SColor( 202, 178, 214 ) ) );
}
