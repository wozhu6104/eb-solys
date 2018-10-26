import api.ScriptBase
import com.elektrobit.ebrace.core.systemmodel.api.SystemModel
import com.elektrobit.ebrace.core.systemmodel.api.SystemModelAccess
import com.elektrobit.ebrace.core.systemmodel.api.SystemModelChangedListener
import com.elektrobit.ebrace.core.systemmodel.api.SystemModelNode
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent
import com.elektrobit.ebsolys.script.external.BeforeScript
import com.elektrobit.ebsolys.script.external.Execute
import com.elektrobit.ebsolys.script.external.Execute.ExecutionContext
import com.elektrobit.ebsolys.script.external.SHtmlView
import com.elektrobit.ebsolys.script.external.ScriptContext
import java.util.List
import com.google.gson.JsonParser
import java.util.ArrayList

class SystemOverview implements SystemModelChangedListener {

	extension ScriptContext _scriptContext
	extension ScriptBase _scriptBase

	SHtmlView htmlView
	SystemModel model
	SystemModelAccess modelAccess
	List<String> channelNames = newArrayList()

	new(ScriptContext scriptContext) {
		_scriptContext = scriptContext
		_scriptBase = new ScriptBase(_scriptContext)
	}

	@BeforeScript
	def init() {
		htmlView = createOrGetHtmlView('SystemOverview')
		model = initSystemModelFromFile("path/to/model.json")
		modelAccess = addSystemModelChangedListener(this)
	}

	/**
	 * Add a meaningful content to the description tag to describe the feature, which is executed by this script
	 * The content of the description tag will be used in all UI widgets where the script can be invoked
	 * If the content is empty, then the classname.methodname will be used instead
	 */
	@Execute(context=ExecutionContext.GLOBAL, description="")
	def execute() {
		updateView(generateCytoscapeModel)
	}

	protected def String generateCytoscapeModel() {
		model.getInputModelRepresentation(new CytoscapeGenerator)
	}

	def updateView(String viewModel) {
		htmlView.content = toHTML(
			viewModel
		).toString
	}

	def toHTML(String initialGraph) {
		'''<!DOCTYPE html>
		<html>
		<head>
		<link href="componentgraph/style.css" rel="stylesheet" />
		<meta charset=utf-8 />
		<title>TCU</title>
		<script src="https://code.jquery.com/jquery-2.0.3.min.js"></script>
		<script src="componentgraph/cytoscape.js"></script>
		<script src="componentgraph/cytoscape-node-html-label.js"></script>
		<script src="componentgraph/popper.js"></script>
		<script src="componentgraph/cytoscape-popper.js"></script>
		<script src="componentgraph/cytoscape-expand-collapse.js"></script>
		<script src="componentgraph/tippy.all.js"></script>
		<script>
		let blockElements = JSON.parse('«initialGraph»');
		</script>
		<script src="componentgraph/cytoscape-cose-bilkent.js"></script>
		</head>
		<body>
		<div id="cy"></div>
		<script src="componentgraph/code.js"></script>
		</body>
		</html>'''
	}

	@Execute(context=ExecutionContext.CALLBACK, description="")
	def reactOnNewEvents(List<RuntimeEvent<?>> events) {
		events.map[channelName].forEach[
			if(!channelNames.contains(it) && it.contains("cpu.proc")){
				val newNode = new SystemModelNode()
				//val parentNode = new SystemModelNode()
				val systemNode = new SystemModelNode()
				systemNode.id = "TCU"

				//model.addNode(parentNode)


				val channelParts = it.split("\\.")
				val processName = channelParts.drop(4).join
				var parentNode = getParentForNode(processName)

				//consolePrintln("name "+it)
				//consolePrintln("processName "+processName)

				newNode.id = processName
				newNode.annotations.put("channel", it)
				//parentNode.id = channelParts.take(1).join
				parentNode.parent = systemNode
				newNode.parent = parentNode
				modelAccess.addNode(model, newNode)
				channelNames.add(it)
			}
		]
	}

	def getParentForNode(String processName)
	{
		var SystemModelNode result = model.nodes.findFirst[
			var elementPatternsString = it.annotations.get("elementPatterns")
			if (elementPatternsString != null)
			{
				var elementPatternsList = parseElementPatterns(elementPatternsString.toString);
				for (elementPattern : elementPatternsList)
				{
					var boolean matches = processName.matches(elementPattern)
					if (matches) return matches
				}
			}
			else false
		]

		if (result == null)
		{
			val systemNode = new SystemModelNode()
				systemNode.id = "NotMatching"
			return systemNode
		}
		else return result;
	}

	def parseElementPatterns(String value)
	{
		val elementPatternsList = new ArrayList;
	    var jsonArray = new JsonParser().parse(value).getAsJsonArray();
	    jsonArray.forEach[
	    	elementPatternsList.add(it.asString)
	    ]

		return elementPatternsList;
	}

	override onSystemModelChanged(SystemModel arg0) {
		model = arg0
		updateView(generateCytoscapeModel)
	}

}
