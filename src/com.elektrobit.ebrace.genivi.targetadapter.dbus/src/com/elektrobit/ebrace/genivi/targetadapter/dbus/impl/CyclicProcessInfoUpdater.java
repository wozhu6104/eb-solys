/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.genivi.targetadapter.dbus.impl;

import java.util.Timer;
import java.util.TimerTask;

import com.elektrobit.ebrace.genivi.targetadapter.dbus.DBusPluginConstants;
import com.elektrobit.ebrace.genivi.targetadapter.resourcemonitor.api.ReadProcessRegistryIF;
import com.elektrobit.ebsolys.core.targetdata.api.Properties;
import com.elektrobit.ebsolys.core.targetdata.api.adapter.DataSourceContext;
import com.elektrobit.ebsolys.core.targetdata.api.structure.StructureAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.structure.Tree;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

public class CyclicProcessInfoUpdater
{
    private Timer timer;
    private final ReadProcessRegistryIF processRegistry;
    private final Tree dbusAnalysisTree;
    private final StructureAcceptor structureAcceptor;
    private final DataSourceContext sourceContext;

    public CyclicProcessInfoUpdater(final StructureAcceptor structureAcceptor,
            final ReadProcessRegistryIF processRegistry, final Tree dbusAnalysisTree, DataSourceContext sourceContext)
    {
        this.structureAcceptor = structureAcceptor;
        this.processRegistry = processRegistry;
        this.dbusAnalysisTree = dbusAnalysisTree;
        this.sourceContext = sourceContext;
    }

    public void startProcessInfoUpdating()
    {
        timer = new Timer();
        timer.schedule( new ProcessInfoUpdatingTask(), 1000, 1000 );
    }

    public void stopProcessInfoUpdating()
    {
        timer.cancel();
    }

    private class ProcessInfoUpdatingTask extends TimerTask
    {
        @Override
        public void run()
        {

            TreeLevelDef processTreeLevel = findTreeLevelWithName( dbusAnalysisTree.getTreeDef(),
                                                                   DBusPluginConstants.DBUS_TREE_LEVEL_2 );
            if (processTreeLevel != null)
            {
                for (TreeNode nextProcessNode : dbusAnalysisTree.getTreeNodesForTreeLevelDef( processTreeLevel ))
                {
                    int pidOfNextProcess = 0;
                    try
                    {
                        pidOfNextProcess = getPIDOfProcessNode( nextProcessNode );
                    }
                    catch (Exception e)
                    {
                        continue;
                    }

                    if (processRegistry.isInfoForProcessAvailable( pidOfNextProcess, sourceContext ))
                    {
                        String nameOfProcess = processRegistry.getProcessInfo( pidOfNextProcess, sourceContext )
                                .getName();
                        if (!nextProcessNode.getName().equals( nameOfProcess ))
                        {
                            structureAcceptor.changeNameOfTreeNode( nextProcessNode, nameOfProcess );
                        }
                    }

                }
            }
        }

        private int getPIDOfProcessNode(TreeNode nextProcessNode)
        {
            Properties properties = nextProcessNode.getProperties();

            if (properties.getKeys().contains( "ProcessID" ))
            {
                return (Integer)properties.getValue( "ProcessID" );
            }

            return Integer.parseInt( nextProcessNode.getName() );
        }

        private TreeLevelDef findTreeLevelWithName(final TreeDef treeDef, final String nameOfSearchedTreeLevel)
        {
            TreeLevelDef searchedTreeLevel = null;
            for (TreeLevelDef nextTreeLevelDef : treeDef.getTreeLevelDefs())
            {
                if (nextTreeLevelDef.getName().equals( nameOfSearchedTreeLevel ))
                {
                    searchedTreeLevel = nextTreeLevelDef;
                    break;
                }
            }
            return searchedTreeLevel;
        }
    }
}
