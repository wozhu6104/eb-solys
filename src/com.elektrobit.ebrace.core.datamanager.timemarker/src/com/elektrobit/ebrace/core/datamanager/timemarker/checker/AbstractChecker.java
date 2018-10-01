/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.timemarker.checker;

/**
 * Abstract class to make input validation easier
 * 
 * @param <T>
 *            The Type of the object to validate
 * @param <V>
 *            Type of the validator
 */
public abstract class AbstractChecker<T, V> implements Checker<T>
{
    protected final V validator;

    public AbstractChecker(V validator)
    {
        this.validator = validator;
    }

    @Override
    abstract public boolean validate(T object);
}
