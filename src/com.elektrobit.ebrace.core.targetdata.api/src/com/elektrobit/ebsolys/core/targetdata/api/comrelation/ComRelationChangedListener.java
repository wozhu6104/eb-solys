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

import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

/**
 * This interface has to be implemented, if you want to get informed, if a
 * {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} is added to the data layer. Before you get
 * informed, you have to register a {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationProvider}.
 * 
 * If you don't want to get informed anymore, you just unregister.
 * 
 * @author rage2903
 * @version 11.05
 */
public interface ComRelationChangedListener
{
    /**
     * The method which is called, if a {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} is
     * added to the data layer.
     * 
     * @param comRelation
     *            the {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}, which was added to the
     *            data layer.
     */
    void added(ComRelation comRelation);

    /**
     * Notifies the listener that a {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} is
     * removed from the DataManager.
     * 
     * @param comRelation
     *            the {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} which was removed from
     *            the DataManager.
     */
    void removed(ComRelation comRelation);

    /**
     * This method is called, if the given {@link ComRelation} has done a action.
     * 
     * For example an action could be a sent message between to {@link TreeNode}s.
     * 
     * @param comRelation
     *            The {@link ComRelation} which has done the action.
     */
    void done(ComRelation comRelation);

    /**
     * This method is called, if the given {@link ComRelation} has undone a action.
     * 
     * For example an action could be a sent message between to {@link TreeNode}s.
     * 
     * @param comRelation
     *            The {@link ComRelation} which has done the action.
     */
    void undone(ComRelation comRelation);
}
