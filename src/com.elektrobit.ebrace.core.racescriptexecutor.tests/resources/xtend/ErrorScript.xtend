import api.ScriptBase
import com.elektrobit.ebsolys.script.external.Execute
import com.elektrobit.ebsolys.script.external.Execute.ExecutionContext
import com.elektrobit.ebsolys.script.external.ScriptContext

class ErrorScript
{
 
	extension ScriptContext _scriptContext
    extension ScriptBase _scriptBase
     
    new (ScriptContext scriptContext) {
    	_scriptContext = scriptContext
        _scriptBase = new ScriptBase(_scriptContext)
    }

	
    @Execute(context=ExecutionContext.GLOBAL, description="")
	def executeS() {
	"hello".consolePrintln
	}


}



