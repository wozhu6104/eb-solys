/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.comrelation;

import java.util.List;
import java.util.Set;

import com.elektrobit.ebsolys.core.targetdata.api.Properties;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

/**
 * This interface provides all existing {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} in
 * the data layer and their related {@link com.elektrobit.ebsolys.core.targetdata.api.Properties}.
 * 
 * Additionally it provides the possibility to register an
 * {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationChangedListener} which gets informed if a
 * connection is added or removed.
 * 
 * @author rage2903
 * @version 11.05
 */
public interface ComRelationProvider
{

    /**
     * Returns all existing {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}s which have the
     * given {@link com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode} as sender or receiver.
     * 
     * @param node
     *            which {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}s should be returned
     * @return an array of {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}s which contains
     *         the given node.
     */
    public ComRelation[] getComRelations(TreeNode node);

    /**
     * Returns the {@link com.elektrobit.ebsolys.core.targetdata.api.Properties} to the given
     * {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}.
     * 
     * @param comRelation
     *            the {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}, whose
     *            {@link com.elektrobit.ebsolys.core.targetdata.api.Properties} should be returned.
     * @return the {@link com.elektrobit.ebsolys.core.targetdata.api.Properties}, which are related to the given
     *         {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}
     */
    public Properties getComRelationsProperties(ComRelation comRelation);

    /**
     * Registers the given {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationChangedListener},
     * which has to be notified, if a {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} was
     * changed in the data layer.
     * 
     * @param listener
     *            the {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationChangedListener} which
     *            should be informed, if a {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}
     *            was changed.
     */
    public void addComRelationChangedListener(ComRelationChangedListener listener);

    /**
     * Unregisters the given {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationChangedListener}.
     * 
     * @param listener
     *            which should be
     *            {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationChangedListener}
     *            unregistered.
     */
    public void removeComRelationChangedListener(ComRelationChangedListener listener);

    /**
     * Returns a list of all children ComRelations recursively (including the original ComRelation)
     */
    public List<ComRelation> getChildrenComRelationsRecusivly(ComRelation startComRelation);

    /**
     * Convenience method for calling {@link #getChildrenComRelationsRecusivly(ComRelation startComRelation)};
     * 
     * @param startComRelations
     * @return
     */
    public List<ComRelation> getChildrenComRelationsRecusivly(List<ComRelation> startComRelations);

    public Set<ComRelation> getComRelations();

    public List<TreeNode> getConnectedTreeNodes(TreeNode treeNode);

    public List<TreeNode> getConnectedReceivers(TreeNode treeNode);
}
