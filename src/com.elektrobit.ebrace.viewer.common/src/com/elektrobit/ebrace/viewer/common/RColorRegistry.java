/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class RColorRegistry
{
    private static final ColorRegistry COLOR_REGISTRY = new ColorRegistry();

    public static Color createOrGetColor(RGB rgb)
    {
        if (!COLOR_REGISTRY.hasValueFor( rgb.toString() ))
        {
            COLOR_REGISTRY.put( rgb.toString(), rgb );
        }
        return COLOR_REGISTRY.get( rgb.toString() );
    }

}
