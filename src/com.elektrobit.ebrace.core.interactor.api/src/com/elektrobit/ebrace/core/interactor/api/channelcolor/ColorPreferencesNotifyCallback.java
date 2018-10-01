/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.api.channelcolor;

import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.color.SColor;

public interface ColorPreferencesNotifyCallback
{
    public void onColorPaletteChanged(List<SColor> newColorPalette);

    public void onColorTransparencyChanged(double value);
}
