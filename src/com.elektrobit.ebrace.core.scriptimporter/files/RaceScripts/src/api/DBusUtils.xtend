package api

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent
import com.elektrobit.ebsolys.script.external.ScriptContext

class DBusUtils {

	extension ScriptContext _scriptContext
	extension ScriptBase _scriptBase

	new(ScriptContext scriptContext) {
		_scriptContext = scriptContext
		_scriptBase = new ScriptBase(_scriptContext)
	}

	/**
	 * Checks if decoded DBus Runtime Event is of type Request.
	 * 
	 * @param evt Decoded Runtime Event.
	 */
	def isRequest(DecodedRuntimeEvent evt) {
		evt.getFirstNode('MetaData').getFirstValue('Message Type').equals('Request')
	}

	/**
	 * Checks if decoded DBus Runtime Event is of type Response.
	 * 
	 * @param evt Decoded Runtime Event.
	 */
	def isResponse(DecodedRuntimeEvent evt) {
		evt.getFirstNode('MetaData').getFirstValue('Message Type').equals('Response')
	}

	/**
	 * Checks if decoded DBus Runtime Event is of type Broadcast.
	 * 
	 * @param evt Decoded Runtime Event.
	 */
	def isBroadcast(DecodedRuntimeEvent evt) {
		evt.getFirstNode('MetaData').getFirstValue('Message Type').equals('Broadcast')
	}

	/**
	 * Returns the DBus Message Serial ID.
	 * 
	 * @param evt Decoded Runtime Event.
	 */
	def getSerial(DecodedRuntimeEvent evt) {
		evt.getFirstNode('MetaData').getFirstValue('Message Serial')
	}

	/**
	 * Returns the DBus Message Reply Serial ID.
	 * 
	 * @param evt Decoded Runtime Event.
	 */
	def getReplySerial(DecodedRuntimeEvent evt) {
		evt.getFirstNode('MetaData').getFirstValue('Reply Message Serial')
	}

	/**
	 * Returns the DBus Message Sender name.
	 * 
	 * @param evt Decoded Runtime Event.
	 */
	def getSender(DecodedRuntimeEvent evt) {
		evt.getFirstNode('MetaData').getFirstValue('Sender')
	}

	/**
	 * Returns the DBus Message Receiver name.
	 * 
	 * @param evt Decoded Runtime Event.
	 */
	def getReceiver(DecodedRuntimeEvent evt) {
		evt.getFirstNode('MetaData').getFirstValue('Receiver')
	}

	/**
	 * Returns the DBus Message Receiver Interface name.
	 * 
	 * @param evt Decoded Runtime Event.
	 */
	def getInterface(DecodedRuntimeEvent evt) {
		evt.decodedTree.rootNode.children.head.name.split('\\.').reverse.tail.reduce[p1, p2|p2 + '.' + p1]
	}

	/**
	 * Returns the DBus Message Receiver Method name.
	 * 
	 * @param evt Decoded Runtime Event.
	 */
	def getMethod(DecodedRuntimeEvent evt) {
		evt.decodedTree.rootNode.children.head.name.split('\\.').last
	}

}
