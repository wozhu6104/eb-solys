/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class MapCombo extends Combo
{
    private final Map<Integer, MapComboItem> itemsMap = new HashMap<>();
    private int index = 0;

    public MapCombo(Composite parent, int style)
    {
        super( parent, style );
    }

    public void setItems(List<MapComboItem> items)
    {
        resetMapCombo();
        fillMapCombo( items );
    }

    @Override
    protected void checkSubclass()
    {
    }

    @Override
    public void setItems(String... items)
    {
        throw new UnsupportedOperationException( "Please use setItems( List<MapComboItem> items )" );
    }

    private void resetMapCombo()
    {
        index = 0;
        itemsMap.clear();
    }

    private void fillMapCombo(List<MapComboItem> items)
    {
        Stream<MapComboItem> stream = items.stream();
        stream.forEach( item -> {
            itemsMap.put( index, item );
            add( item.getKey(), index );
            index++;
        } );
    }

    public Object getSelectedItemValue()
    {
        return itemsMap.get( getSelectionIndex() ).getValue();
    }

    public void select(String entry)
    {
        int index = 0;
        for (Entry<Integer, MapComboItem> item : itemsMap.entrySet())
        {
            if (item.getValue().getKey().equals( entry ))
            {
                index = item.getKey();
            }
        }
        super.select( index );
    }
}
