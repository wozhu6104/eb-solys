import api.ScriptBase
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent
import com.elektrobit.ebsolys.script.external.AfterScript
import com.elektrobit.ebsolys.script.external.BeforeScript
import com.elektrobit.ebsolys.script.external.Execute
import com.elektrobit.ebsolys.script.external.Execute.CallbackTime
import com.elektrobit.ebsolys.script.external.Execute.ExecutionContext
import com.elektrobit.ebsolys.script.external.ScriptContext
import java.util.List

class AllMethodTypesCallbackScript {

	extension ScriptContext _scriptContext
    extension ScriptBase _scriptBase
     
    new (ScriptContext scriptContext) {
    	_scriptContext = scriptContext
        _scriptBase = new ScriptBase(_scriptContext)
    }

	@BeforeScript
	def testBeforeMethod() {
	}

	@Execute(context=ExecutionContext.GLOBAL, description="Global method")
	def testExecuteScriptMethod() {
	}

	@Execute(context=ExecutionContext.CALLBACK, time=CallbackTime.MILLIS_100, description="Callback method")
	def testCallbackMethod(List<RuntimeEvent<?>> events) {
	}

	@AfterScript
	def testAfterMethod() {
	}
}
