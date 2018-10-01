/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.allChannels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StructureExpander
{
    public static final char TREE_LEVEL_SEPARATOR = '.';

    private final List<String> prefixes = new ArrayList<>();

    public List<String> createStructureWithAllSubgroups(List<String> treeLeafes)
    {
        List<String> allLevels = new ArrayList<>( treeLeafes );

        processNextLevelPrefixes( treeLeafes );

        Set<String> prefixesWithoutDuplicates = new HashSet<>();
        prefixesWithoutDuplicates.addAll( prefixes );
        allLevels.addAll( prefixesWithoutDuplicates );

        Set<String> resultWithoutDuplicates = new HashSet<>( allLevels );
        allLevels.clear();
        allLevels.addAll( resultWithoutDuplicates );
        Collections.sort( allLevels );
        return allLevels;
    }

    private void processNextLevelPrefixes(Collection<String> inputList)
    {
        for (String item : inputList)
        {
            Set<String> upperLevelPrefixes = getAllUpperLevelPrefixes( item );
            prefixes.addAll( upperLevelPrefixes );
        }

    }

    private Set<String> getAllUpperLevelPrefixes(String item)
    {
        Set<String> allUpperPrefixes = new HashSet<>();
        int matchIndex = 0;

        boolean found = true;
        do
        {
            matchIndex = item.indexOf( TREE_LEVEL_SEPARATOR, matchIndex );

            found = (matchIndex != -1) && (matchIndex != item.length() - 1);
            if (found)
            {
                allUpperPrefixes.add( item.substring( 0, matchIndex ) );
            }
            matchIndex++;
        }
        while (found);

        return allUpperPrefixes;

    }
}
