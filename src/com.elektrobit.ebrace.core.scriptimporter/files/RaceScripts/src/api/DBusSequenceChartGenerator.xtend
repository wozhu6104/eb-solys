package api

import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent
import com.elektrobit.ebsolys.script.external.Execute
import com.elektrobit.ebsolys.script.external.Execute.ExecutionContext
import com.elektrobit.ebsolys.script.external.ScriptContext
import java.util.List
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.ProtoMessageValue

class DBusSequenceChartGenerator {

	extension ScriptContext _scriptContext
	extension ScriptBase _scriptBase
	extension DBusUtils _dbusUtils

	new(ScriptContext scriptContext) {
		_scriptContext = scriptContext
		_scriptBase = new ScriptBase(_scriptContext)
		_dbusUtils = new DBusUtils(_scriptContext)
	}

	/**
	 * Generates a sequence diagram based on the given Runtime Events and 
	 * opens the chart directly in a HTML view with the name DBus-Sequence-Chart. 
	 */
	@Execute(context=ExecutionContext.PRESELECTION, description="Generate Sequence Diagram")
	def generateSequenceDiagramText(List<RuntimeEvent<?>> _events) {

		val SequenceChartBuilder chartBuilder = new SequenceChartBuilder('Sequence Chart')

		val events = _events.filter[value instanceof ProtoMessageValue]
		if(events.size == 0) {
			consolePrintln("Can not generate sequence chart. DBus channel required. Exiting.")
			return
		}

		allTimemarkers.filter[timestamp >= events.head.timestamp && timestamp <= events.last.timestamp].forEach [
			chartBuilder.addMessage(timestamp, '', '', name, MessageType.DIVIDER)
		]

		events.map[decode].forEach [
			val ts = runtimeEvent.timestamp
			switch (it) {
				case isRequest: {
					val from = chartBuilder.addLifeline(sender)
					val to = chartBuilder.addLifeline(#[receiver, interface])
					chartBuilder.addMessage(ts, from, to, methodWithParams, MessageType.REQUEST)
				}
				case isResponse: {
					val from = chartBuilder.addLifeline(#[sender, interface])
					val to = chartBuilder.addLifeline(receiver)
					chartBuilder.addMessage(ts, from, to, methodWithParams, MessageType.REPLY)
				}
				case isBroadcast: {
					val from = chartBuilder.addLifeline(#[sender, interface])
					val to = chartBuilder.addLifeline('Broadcast')
					chartBuilder.addMessage(ts, from, to, methodWithParams, MessageType.REQUEST)
				}
			}
		]

		showPlantUmlInHtmlView(chartBuilder.toPlantUML, "DBus-Sequence-Chart")
	}

	private def methodWithParams(DecodedRuntimeEvent e) {

		val buf = new StringBuffer(e.method)
		if (e.decodedTree.rootNode.children.head.children.size > 0) {
			buf.append('(')
			e.decodedTree.rootNode.children.head.children.forEach [
				buf.append('\\n    ').append(name).append('=').append(detailedValue)
			]
			buf.append(')')
		}
		buf.toString
	}

	private def detailedValue(DecodedNode node) {
		if (node.value == null && node.children.size > 0) {
			'[...]'
		} else
			node.value
	}
	
	@Execute(context=ExecutionContext.PRESELECTION, description="Generate Sequence Diagram")
	def generateSequenceDiagramText(RuntimeEvent<?> _event) {
		newArrayList(_event).generateSequenceDiagramText
	}

}
