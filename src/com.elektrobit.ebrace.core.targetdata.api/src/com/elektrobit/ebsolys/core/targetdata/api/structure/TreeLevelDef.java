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

/**
 * A TreeLevel defines a layer in the hierarchical structure.
 * 
 * @author rage2903
 * @version 11.06
 */
public interface TreeLevelDef
{
    /**
     * Returns the name of this TreeLevel.
     * 
     * @return the name of this TreeLevel.
     */
    String getName();

    /**
     * Returns the Description of this TreeLevel. The Description should explain the user this TreeLevel in Detail.
     * 
     * @return the Description of this TreeLevel.
     */
    String getDescription();

    /**
     * Returns the path of the icon which should be used with this TreeLevel.
     * 
     * @return the icon that should be used with this TreeLevel.
     */
    String getIconPath();
}
