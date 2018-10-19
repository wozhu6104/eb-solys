import com.elektrobit.ebrace.core.systemmodel.api.ViewModelGenerator
import java.util.Map

class CytoscapeGenerator implements ViewModelGenerator {
	override handleNode(String id, String parent, Map<String, Object> attributes) {
					if (attributes.containsKey("classes"))
						'''{"data":{"id":"«id»","parent":"«parent»"«attributes.tag("channel")»}«attributes.tag("classes")»}'''
					else '''{"data":{"id":"«id»","parent":"«parent»"«attributes.tag("channel")»}}'''
						
					
		//'''{"data":{"id":"«id»","parent":"«parent»"«attributes.tag("channel")»}}'''
		//{"data":{"id":"SoC","parent":"TCU"},"classes":"group"},
	}
	
	protected def String tag( Map<String, Object> attributes, String key) {
		if(attributes != null){
			val value = attributes.get(key)
			if (value != null) 
				",\""+key+"\":\""+value + "\""
			else ""
		}
	}

	override handleEdge(String from, String to, Map<String, Object> attributes) {
		'''{"data":{"id":"«to»", "source":"«from.split('\\.').last»","target":"«to.split('\\.').last»"}}'''
	}

	override edgesEnd() {
		"]"
	}

	override edgesStart() {
		'''"edges":['''
	}

	override end() {
		"}"
	}

	override nodesEnd() {
		"]"
	}

	override nodesStart() {
		'''"nodes":['''
	}

	override separator() {
		","
	}

	override start() {
		"{"
	}
}
