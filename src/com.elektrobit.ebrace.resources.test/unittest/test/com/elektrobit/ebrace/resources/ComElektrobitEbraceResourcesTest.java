/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.resources;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.com.elektrobit.ebrace.resources.api.model.BaseResourceModelTest;
import test.com.elektrobit.ebrace.resources.model.ResourceModelManagerResetTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ResourceModelManagerResetTest.class, BaseResourceModelTest.class
        /* Add here your test classes */})
public class ComElektrobitEbraceResourcesTest
{
}
