/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api;

/**
 * Data type to mark classes as model elements. A model element is a part of the head unit system, which a RuntimeEvent
 * is associated with. You must override equals and hashCode to guarantee that a stored model element equals the
 * original model element after storing and reconstructing it.
 * 
 * @version 12.06
 */
public interface ModelElement
{
    /**
     * If no model element is known for a RuntimeEvent, use this NULL_MODEL_ELEMENT to create a RuntimeEvent instead of
     * null.
     */
    public static final ModelElement NULL_MODEL_ELEMENT = new ModelElement()
    {
        @Override
        public long getUniqueModelElementID()
        {
            return 0;
        }

        @Override
        public String getName()
        {
            return "NULL_MODEL_ELEMENT";
        }

        @Override
        public String toString()
        {
            return "NULL_MODEL_ELEMENT";
        }

    };

    /**
     * This method is needed to find a stored model element in the database again. You've to ensure that the UUID is
     * system wide unique.
     */
    public long getUniqueModelElementID();

    /**
     * This method must be implemented to be able to compare RuntimeEvents
     */
    @Override
    public boolean equals(Object otherModelElement);

    /**
     * This method must be implemented to be able to compare RuntimeEvents
     */
    @Override
    public int hashCode();

    /**
     * Returns the name of the TreeNode as String. Names needn't to be unique in the tree.
     * 
     * @return Returns the name of the ITreeNode.
     */
    String getName();
}
