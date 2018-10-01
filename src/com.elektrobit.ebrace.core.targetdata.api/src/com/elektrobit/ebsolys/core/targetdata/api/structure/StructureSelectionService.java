/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.structure;

import java.util.List;

import com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation;

public interface StructureSelectionService
{
    public void setNodesSelected(List<TreeNode> nodes);

    public void setComRelationsSelected(List<ComRelation> comRelations);

    public void clearSelection();

    public void registerListener(StructureSelectionListener listener);

    public void unregisterListener(StructureSelectionListener listener);

    public void notifyListenerWithCurrentState(StructureSelectionListener listener);

    public List<TreeNode> getSelectedNodes();

    public List<ComRelation> getSelectedComRelations();
}
