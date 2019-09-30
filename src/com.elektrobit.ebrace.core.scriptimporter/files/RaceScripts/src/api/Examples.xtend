package api

import api.ScriptBase
import com.elektrobit.ebsolys.script.external.ScriptContext
import com.elektrobit.ebsolys.script.external.Execute
import com.elektrobit.ebsolys.script.external.Execute.ExecutionContext

class Examples
{

	extension ScriptContext _scriptContext
    extension ScriptBase _scriptBase
    
     
    new (ScriptContext scriptContext) {
    	_scriptContext = scriptContext
        _scriptBase = new ScriptBase(_scriptContext)
    }

	
	@Execute(context=ExecutionContext.GLOBAL, description="")
	def exampleExtract() {
		
		'trace.dlt.DA1.DC1'.channel?.removeChannel

		// create a json event for testing purposes
		'''
			{
				"uptime": 957226000,
				"channel": "trace.dlt.DA1.DC1",
				"value": {
					"summary": "{\"0\":\"RESPONSE Set_TraceStatus [OK]\"}",
					"details": {
						"appId": "DA1",
						"contextId": "DC1",
						"numArgs": 1,
						"logLevel": "INFO",
						"payload": {
							"0": "RESPONSE Set_TraceStatus [OK]"
						}
					}
				},
				"duration": 0
			}
		'''.toString.addJsonEvent

		// get the event object from the channel then
		val evt = 'trace.dlt.DA1.DC1'.channel.events.head

		// now do some checks, if the extract method returns the expected results
		assertTrue(evt.extract('channel').value.equals('trace.dlt.DA1.DC1'))
		assertTrue(evt.extract('duration').value.equals('0'))

		assertTrue(evt.extract('$.value.details.appId').value.equals('DA1'))
		assertTrue(evt.extract('value.details').children.size.equals(5))

		assertTrue(evt.extract('value.details.payload.0').value.equals('RESPONSE Set_TraceStatus [OK]'))
		assertTrue(evt.extract('$.value.details').extract('logLevel') !== null)

	}

	def assertTrue(boolean condition) {
		if(!condition)
		{
			val ste = Thread.currentThread.stackTrace.get(2)
			consolePrintln('Check failed at: '+ste.className+':'+ste.methodName+":"+ste.lineNumber)			
		}
	}
}



