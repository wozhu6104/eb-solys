/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors         : Raphael Geissler
 * name                 : Generate Test Plugin
 * image                : workspace:/com.elektrobit.ebrace.dev.plugingenerators/icons/testfile_obj.gif
 * toolbar              : Project Explorer;Package Explorer
 * popup                : enableFor(org.eclipse.core.resources.IProject)
 * description          : Generates a EB solys Test Plugin in current workspace
 ******************************************************************************/
package com.elektrobit.ebrace.dev.plugingenerators

import com.elektrobit.ebrace.dev.plugingenerators.helper.ScriptConsole
import java.io.File
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import org.apache.commons.io.FileUtils
import org.eclipse.core.resources.IProject
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.core.runtime.Path
import org.eclipse.jface.viewers.ISelection
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.ui.ISelectionService
import org.eclipse.ui.PlatformUI
import org.eclipse.jdt.core.IJavaProject

class EBsolysTestPluginGeneratorScript {
	extension static ScriptConsole console = new ScriptConsole(EBsolysTestPluginGeneratorScript.simpleName)

	def static void main(String[] args) {
		var String hostPluginName = hostPluginName
		var String pluginsFolder = getSelectedProjectParentDir(selectedProject)

		if (hostPluginName === null || pluginsFolder === null) {
			return
		}
		
		val String hostPluginFolderPath = pluginsFolder + "/" + hostPluginName.toLowerCase

		val String pluginName = hostPluginName + ".tests"
		val String pluginFolderPath = pluginsFolder + "/" + pluginName.toLowerCase

		if(new File(pluginFolderPath).exists)
		{
			"WARN: Do not generate Test Plug-in, because it is already there.".println
			return
		}

		print("Creating Test Plug-in Folder...")
		new File(pluginFolderPath + "/src").mkdirs()
		println("done!")

		print("Copying settings from Host Plug-in...")
		FileUtils.copyDirectory(new File(hostPluginFolderPath + "/.settings"),
			new File(pluginFolderPath + "/.settings"))
		println("done!")

		print("Writing project file...")
		pluginFolderPath.createProjectFile('''
			<?xml version="1.0" encoding="UTF-8"?>
			<projectDescription>
				<name>«pluginName.toLowerCase»</name>
				<comment></comment>
				<projects>
				</projects>
				<buildSpec>
					<buildCommand>
						<name>org.eclipse.jdt.core.javabuilder</name>
						<arguments>
						</arguments>
					</buildCommand>
					<buildCommand>
						<name>org.eclipse.pde.ManifestBuilder</name>
						<arguments>
						</arguments>
					</buildCommand>
					<buildCommand>
						<name>org.eclipse.pde.SchemaBuilder</name>
						<arguments>
						</arguments>
					</buildCommand>
					<buildCommand>
						<name>de.raphaelgeissler.dependencychecker.ui.builder.dependencyCheckerBuilder</name>
						<arguments>
						</arguments>
					</buildCommand>
				</buildSpec>
				<natures>
					<nature>org.eclipse.pde.PluginNature</nature>
					<nature>org.eclipse.jdt.core.javanature</nature>
					<nature>de.raphaelgeissler.dependencychecker.nature</nature>
				</natures>
			</projectDescription>
		''')
		println("done!")

		print("Writing classpath file...")
		pluginFolderPath.createClassPathFile('''
			<?xml version="1.0" encoding="UTF-8"?>
			<classpath>
				<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-1.8"/>
				<classpathentry kind="con" path="org.eclipse.pde.core.requiredPlugins"/>
				<classpathentry kind="src" path="src"/>
				<classpathentry kind="output" path="bin"/>
			</classpath>
		''')
		println("done!")

		print("Writing build.properties file...")
		pluginFolderPath.createBuildPropertiesFile('''
			source.. = src/
			output.. = bin/
			bin.includes = META-INF/,\
			               .
		''')
		println("done!")

		print("Writing MANIFEST.MF file...")
		pluginFolderPath.createBuildManifestFile('''
			Manifest-Version: 1.0
			Bundle-ManifestVersion: 2
			Bundle-Name: «pluginName»
			Bundle-SymbolicName: «pluginName»
			Bundle-Version: 1.0.0.qualifier
			Bundle-Vendor: ELEKTROBIT
			Fragment-Host: «hostPluginName»;bundle-version="1.0.0"
			Bundle-RequiredExecutionEnvironment: JavaSE-1.8
		''')
		println("done!")

		print("Modifying Host Plug-in MANIFEST.MF file...")
		appendExtensibleAttributeToHostManifest(hostPluginFolderPath)
		println("done!")

		print("Importing Test Plug-in in Eclipse Workspace...")
		pluginFolderPath.importProject
		println("done!")
	}

	def static getHostPluginName()
	{
		val selectedProject = getSelectedProject()
		if(selectedProject !== null)
		{
			selectedProject.name
		}
		else
		{
			println("Error: Couldn't find selected project. Did you select a project in Package or Project Explorer?")
		}
	}

	def static getSelectedProject() {
		val calleePartId = PlatformUI.getWorkbench().getWorkbenchWindows().get(0).pages.get(0).activePart.site.id
		val ISelectionService selectionService = PlatformUI.getWorkbench().workbenchWindows.get(0).getSelectionService()
		var ISelection selection = selectionService.getSelection(calleePartId)

		var IProject project
		if (selection instanceof IStructuredSelection) {
			val select = (selection as IStructuredSelection).firstElement
			if (select instanceof IJavaProject) {
				project = (select as IJavaProject).project
			} else if (select instanceof IProject) {
				project = select as IProject
			}
		}

		return project
	}

	def static getSelectedProjectParentDir(IProject project) {
		return new File(project.rawLocationURI.path).parentFile.toString
	}

	def static createProjectFile(String pluginFolderPath, String content) {
		writeToFile(".project", pluginFolderPath, content)
	}

	def static writeToFile(String fileName, String folder, String content) {
		val writer = new PrintWriter(folder + "/" + fileName)
		writer.write(content)
		writer.close
	}

	def static createClassPathFile(String pluginFolderPath, String content) {
		writeToFile(".classpath", pluginFolderPath, content)
	}

	def static createBuildPropertiesFile(String pluginFolderPath, String content) {
		writeToFile("build.properties", pluginFolderPath, content)
	}

	def static createBuildManifestFile(String pluginFolderPath, String content) {
		val String metaInfFolder = pluginFolderPath + "/META-INF"
		new File(metaInfFolder).mkdirs
		writeToFile("MANIFEST.MF", metaInfFolder, content)
	}

	def static appendExtensibleAttributeToHostManifest(String hostPluginFolderPath) {
		val hostPluginManifest = FileUtils.readFileToString(new File(hostPluginFolderPath + "/META-INF/MANIFEST.MF"))

		if (!hostPluginManifest.contains("Eclipse-ExtensibleAPI"))
			Files.write(Paths.get(hostPluginFolderPath + "/META-INF/MANIFEST.MF"),
				"Eclipse-ExtensibleAPI: true\n".getBytes(), StandardOpenOption.APPEND)
	}

	def static importProject(String pluginFolderPath) {
		val projectDescription = ResourcesPlugin.getWorkspace().loadProjectDescription(
			new Path(pluginFolderPath + "/.project"))
		val project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectDescription.getName());

		project.create(projectDescription, null);
		project.open(null);

		project.refreshLocal(IProject.DEPTH_INFINITE, new NullProgressMonitor)
	}

}
