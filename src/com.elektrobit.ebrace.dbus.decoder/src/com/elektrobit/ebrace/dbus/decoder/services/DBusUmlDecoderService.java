/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.dbus.decoder.services;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;
import com.elektrobit.ebsolys.decoder.common.services.DecoderService;
import com.elektrobit.ebsolys.decoder.common.services.DecoderServiceManagerImpl;
import com.elektrobit.ebsolys.decoder.common.services.UmlDecoderService;

public class DBusUmlDecoderService implements UmlDecoderService
{

    private static final String EMPTY_STRING = "";
    private String selectedTreeLevel = "DBusProcess";

    List<RuntimeEvent<?>> listOfEvents = new ArrayList<RuntimeEvent<?>>();

    @Override
    public String getSequenceChartSummary(List<RuntimeEvent<?>> runtimeEvents, final String selectedTreeLevel)
    {
        listOfEvents.clear();
        String result = EMPTY_STRING;
        Assert.isNotNull( runtimeEvents );
        listOfEvents.addAll( runtimeEvents );
        this.selectedTreeLevel = selectedTreeLevel;
        result = generatePlantUmlSequenceChartInputString( runtimeEvents );
        return result;
    }

    private String generateUniqueTreeNodeName(TreeNode treeNode)
    {

        StringBuilder nameBuilder = new StringBuilder();
        boolean isRelevant = false;
        while (treeNode != null)
        {
            if (treeNode.getTreeLevel().getName().equals( selectedTreeLevel ))
            {
                isRelevant = true;
            }
            if (isRelevant)
            {
                nameBuilder.insert( 0, treeNode.getName() + "." );
            }
            treeNode = treeNode.getParent();
        }

        nameBuilder.deleteCharAt( nameBuilder.length() - 1 );

        return nameBuilder.toString();
    }

    private String generatePlantUmlSequenceChartInputString(List<RuntimeEvent<?>> shownRuntimeEvents)
    {
        String plantUmlSeqeunceChartInputString;
        plantUmlSeqeunceChartInputString = "@startuml\n";
        for (RuntimeEvent<?> nextRuntimeEvent : shownRuntimeEvents)
        {
            if (nextRuntimeEvent.getModelElement() instanceof ComRelation)
            {
                DecoderService decoderService = DecoderServiceManagerImpl.getInstance()
                        .getDecoderServiceForEvent( nextRuntimeEvent );
                DecodedRuntimeEvent decodedEvent = decoderService.decode( nextRuntimeEvent );
                String direction = getDirection( decodedEvent );
                ComRelation comRel = (ComRelation)nextRuntimeEvent.getModelElement();
                TreeNode sender = comRel.getSender();
                TreeNode receiver = comRel.getReceiver();
                plantUmlSeqeunceChartInputString += "\"" + generateUniqueTreeNodeName( sender ) + direction
                        + generateUniqueTreeNodeName( receiver ) + "\" : " + decodedEvent.getSummary() + "\n";
            }
        }
        plantUmlSeqeunceChartInputString += "@enduml\n";
        return plantUmlSeqeunceChartInputString;
    }

    private String getDirection(DecodedRuntimeEvent event)
    {
        String caller = "\" -> \"";
        switch (event.getRuntimeEventType())
        {
            case RESPONSE :
                caller = "\" <- \"";
                break;
            default :
                break;
        }
        return caller;
    }
}
