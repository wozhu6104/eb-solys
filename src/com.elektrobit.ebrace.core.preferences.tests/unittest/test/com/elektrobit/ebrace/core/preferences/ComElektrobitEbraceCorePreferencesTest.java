/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.preferences;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({PreferencesServiceImplTest.class, TimeFormatPreferencesTest.class, PropertiesStoreTest.class,
        ConnectionPreferencesTest.class, ScriptFolderPathPreferencesTest.class/* Add here your test classes */})
public class ComElektrobitEbraceCorePreferencesTest
{
}
