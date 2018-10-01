import api.ScriptBase
import com.elektrobit.ebsolys.script.external.Execute
import com.elektrobit.ebsolys.script.external.Execute.ExecutionContext
import com.elektrobit.ebsolys.script.external.ScriptContext
import com.elektrobit.ebsolys.script.external.BeforeScript
import com.elektrobit.ebsolys.script.external.AfterScript

class ThreeGlobalMethodsScript
{

	extension ScriptContext _scriptContext
    extension ScriptBase _scriptBase
     
    new (ScriptContext scriptContext) {
    	_scriptContext = scriptContext
        _scriptBase = new ScriptBase(_scriptContext)
    }
	
	@BeforeScript
    def setup() {}
	
    @Execute(context=ExecutionContext.GLOBAL, description="My description")
	def Method1() {
    	// Your script code
	}
	@Execute(context=ExecutionContext.GLOBAL, description="My description")
	def Method2() {
    	// Your script code
	}
    @Execute(context=ExecutionContext.GLOBAL, description="My description")
	def Method3() {
    	// Your script code
	}
	
	@AfterScript
    def cleanup() {}
}
