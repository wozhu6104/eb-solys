package api

import java.util.List
import org.eclipse.xtend.lib.annotations.Data

@Data
class SequenceChartBuilder
{
	
	String title
	
	val List<Pair<String, String>> lifelines = newArrayList()
	val List<Message> messages = newArrayList()
	val List<Pair<String, String>> config = newArrayList(#['monochrome' -> 'true'])

	/**
	 * Adds a lifeline to the sequence diagram
	 */
	def String addLifeline(String token) {
		lifelines.add(token.toAlias -> token.toName)
		token.toAlias
	}

	/**
	 * Adds a lifeline to the sequence diagram with multiple tokens for a name
	 * The tokens are printed line by line into the lifeline header
	 */
	def String addLifeline(List<String> tokens) {
		lifelines.add(tokens.toAlias -> tokens.toName)
		tokens.toAlias
	}

	private def toName(String token) {
		'"'+token+'"'
	}

	private def toName(List<String> tokens) {
		'"'+tokens.reduce[p1, p2| p1 + '\\n' + p2]+'"'
	}
	
	private def toAlias(String token) {
		token.replaceAll(':','_').replaceAll('-','_').replaceAll('\\.', '_').replaceAll(' ', '_')
	}

	private def toAlias(List<String> tokens) {
		tokens.map[toAlias].reduce[p1, p2| p1 + '_' + p2]
	}	
		
	/**
	 * Adds a message to the sequence diagram with an explicit time-stamp
	 * The time-stamp is used for sorting the messages 
	 * 
	 * @param timestamp The timestamp of the message
	 * @param from The sender (lifeline) of the message
	 * @param to The receiver (lifeline) of the message
	 * @param message The message content itself
	 * @param type The message type: REQUEST, REPLY, DIVIDER, DELAY
	 */
	def addMessage(long timestamp, String from, String to, String message, MessageType type) {
		messages.add(new Message(timestamp, from, to, message, type))
	}

	/**
	 * Adds a message to the sequence diagram without an explicit time-stamp
	 *  
	 * @param timestamp The timestamp of the message
	 * @param from The sender (lifeline) of the message
	 * @param to The receiver (lifeline) of the message
	 * @param message The message content itself
	 * @param type The message type: REQUEST, REPLY, DIVIDER, DELAY
	 */
	def addMessage(String from, String to, String message, MessageType type) {
		messages.add(new Message(0, from, to, message, type))
	}

	/**
	 * Adds a skin configuration item to the chart
	 * Each key-value pair is transformed into 'skinparam <key> <value>' in the plantUML header
	 * There is no check if the key and value is valid. Please check the plantuml documentation for further information 
	 */
	def addSkinConfigurationItem(String _key, String _value) {
		val c = config.filter[key.equals(_key)].head
		if(c != null) { config.remove(c)}
		config.add(_key -> _value)
	}

	/** 
	 *	Transforms the sequence chart objects into plantuml markup language 
	 */
	def toPlantUML() {
		'''
		@startuml
		title «title»
		«FOR c: config»
		skinparam «c.key» «c.value»
		«ENDFOR»
		hide footbox
		
		«FOR l: lifelines.toSet»
		participant «l.value» as «l.key»
		«ENDFOR»
		
		«FOR m: messages.sortBy[timestamp].toList»
		«IF m.type.equals(MessageType.REQUEST)»
		«m.from» -> «m.to» : «m.name»
		«ELSEIF m.type.equals(MessageType.REPLY)»
		«m.from» --> «m.to» : «m.name»
		«ELSEIF m.type.equals(MessageType.DIVIDER)»
		== «m.name» ==
		«ELSEIF m.type.equals(MessageType.DELAY)»
		...«m.name»...
		«ENDIF»
		«ENDFOR»
		
		@enduml
		'''.asUTF8		
	}

	private def asUTF8(CharSequence sequence) {
		new String(sequence.toString.bytes, 'UTF-8')
	}

}

enum MessageType { REQUEST, REPLY, DIVIDER, DELAY }

@Data
class Message {
	val long timestamp
	val String from
	val String to
	val String name
	val MessageType type
}

