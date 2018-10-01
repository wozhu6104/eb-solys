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

import com.elektrobit.ebsolys.core.targetdata.api.ModelElement;
import com.elektrobit.ebsolys.core.targetdata.api.Properties;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode;

/**
 * A {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} defines a relationship between two
 * {@link com.elektrobit.ebsolys.core.targetdata.api.structure.TreeNode}s which represents a kind of communication flow
 * / data flow. A {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation} has a sender, a receiver
 * and a name. All these three items make an {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelation}
 * unique. But through this definition, it is also possible to create multi-graphs.
 * 
 * The only way to add information to a relationship is to attach an
 * {@link com.elektrobit.ebsolys.core.targetdata.api.Properties} object to a relationship. So you have to use the
 * getComRelationsProperties method of the
 * {@link com.elektrobit.ebsolys.core.targetdata.api.comrelation.ComRelationProvider} to get the information details
 * specific to this relationship.
 * 
 * @author rage2903
 * @version 12.06
 */
public interface ComRelation extends ModelElement
{

    /**
     * Returns the sender of this relationship.
     * 
     * @return the sender of this relationship.
     */
    TreeNode getSender();

    /**
     * Returns the receiver of this relationship.
     * 
     * @return the receiver of this relationship.
     */
    TreeNode getReceiver();

    /**
     * Returns the {@link Properties} of this {@link ComRelation}.
     * 
     * @return the {@link Properties} of this {@link ComRelation}.
     */
    Properties getProperties();

}
