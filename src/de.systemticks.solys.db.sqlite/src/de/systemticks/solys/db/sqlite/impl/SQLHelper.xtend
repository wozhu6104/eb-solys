package de.systemticks.solys.db.sqlite.impl

import java.util.List
import com.google.gson.Gson
import com.google.gson.JsonObject
import de.systemticks.solys.db.sqlite.api.GenericJsonEvent
import de.systemticks.solys.db.sqlite.api.DetailedField
import de.systemticks.solys.db.sqlite.api.FieldMapping

class SQLHelper {

	def static createTableForEvents(String origin, int id, FieldMapping fieldMapping) {
		'''
			CREATE TABLE «buildTableName(origin, id)»
			(eId INTEGER PRIMARY KEY,
			 eTimestamp INT8 NOT NULL,
			 eValue «fieldMapping.valueType.toSQLType» NOT NULL«IF fieldMapping.details.size > 0», «ENDIF»
			 «FOR f: fieldMapping.details SEPARATOR ","»
			 d«f.getName.toFirstUpper» «f.getType.toSQLType»
			 «ENDFOR»
			)
		'''
	}

	private def static toSQLType(String type)
	{
		switch type {
			case 'Double' : 'REAL'
			case 'Integer' : 'INT'
			//FIXME
			case 'Long' : 'INT'
			case 'String' : 'TEXT'
			default : 'ERROR'
		}
	}
	
	def static dropTable(String name) {
		'DROP TABLE IF EXISTS '+name
	}
	
	def static createTableForChannelMapping(String name) {
		'''
			CREATE TABLE «name»
			(cId INTEGER PRIMARY KEY,
			 cName TEXT NOT NULL,
			 cNature TEXT NOT NULL,
			 cMapping TEXT NOT NULL)
		'''		
	}
	
	def static createIndex(String table, String... fields) {
		'''
			CREATE INDEX idx_«table»_«fields.join('_')» ON «table» («fields.join(",")»)
		'''
	}

	def static insertValueIntoEventTableUnprepared(GenericJsonEvent event, FieldMapping fieldMapping)
	{
		
		if(!fieldMapping.hasDetails)		
			'''
			INSERT INTO «event.buildTableName» (eId, eTimestamp, eValue) values («event.eventId», «event.timestamp», «event.value.toSQLValue» )
			'''		
		else {
			val detailKeys = fieldMapping.details.map['d'+getName.toFirstUpper].join(', ')
			val detailedValues = fieldMapping.details.map[k | event.details.toRawJson(k.getName).toSQLValue ].join(', ')		
			'''
			INSERT INTO «event.buildTableName» (eId, eTimestamp, eValue, «detailKeys») values («event.eventId», «event.timestamp», «event.value.toString.toSQLValue», «detailedValues» )
			'''			
		}				

	}

	private static def hasDetails(FieldMapping f)
	{
		f.details.size > 0
	}

	private dispatch def static toSQLValue(Object value)
	{
		value.toString
	}

	private dispatch def static toSQLValue(String value)
	{
		"'"+value.replace("'", "''")+"'"
	}

	private def static toRawJson(JsonObject obj, String key)
	{
	
		if(obj.get(key) === null || obj.get(key).isJsonNull) 
			""
		else if(obj.get(key).jsonPrimitive)
			obj.get(key).asString
		else 
			obj.get(key).toString
	}

	def static insertValueIntoEventTable(String table)
	{
		'''
		INSERT INTO «table» (eId, eTimestamp, eValue) values (?, ?, ?)
		'''
	}

	def static insertIntoChannelMappingTable(int id, String name, String nature, FieldMapping mapping)
	{
		val gson = new Gson
//		'''
//		INSERT INTO channels (cId, cName, cNature, cMapping) values («id», '«name»', '«nature»', '«gson.toJson(mapping)»')
//		'''
		'''
		INSERT INTO channels (cId, cName, cNature, cMapping) values («id», '«name»', '«nature»', '')
		'''
	}

	//FIXME - To be adapted to new ER-structure
	def static createAllEventsFromChannel(String storage, int channelId) {
		'''
		SELECT «storage».eId, «storage».eTimestamp, «storage».eValue, «storage».eChannelId 
		FROM «storage» 
		WHERE «storage».eChannelId = «channelId»
		ORDER BY «storage».eTimestamp
		'''
	}

	def static createAllEventsFromChannel(String origin, int channelId, long from, long to) 
	{
		val table = buildTableName(origin, channelId)
		'''
		SELECT «table».eId AS eventId, «table».eTimestamp AS timestamp, «table».eValue AS value, channels.cId AS channelId, channels.cName AS channel
		FROM «table», channels 
		WHERE channels.cId = «channelId» «timestampFilter(table, from, to)»
		ORDER BY «table».eTimestamp
		'''
	}

	def static createAllEventsFromChannel(String origin, int channelId, long from, long to, List<String> keySet) 
	{
		val table = buildTableName(origin, channelId)
		val detailKeys = keySet.map['d'+toFirstUpper].join(', ')
		
		'''
		SELECT «table».eId, «table».eTimestamp, «table».eValue, channels.cId, channels.cName, «detailKeys»
		FROM «table», channels 
		WHERE channels.cId = «channelId» «timestampFilter(table, from, to)»
		ORDER BY «table».eTimestamp
		'''
	}

	private def static buildTableName(GenericJsonEvent event) 
	{
		event.channel.split('\\.').head + "_" + event.channelId;
	}

	private def static buildTableName(String origin, int channelId) 
	{
		origin + "_" + channelId;
	}

	def static createEventsAtTimestamp(String storage, long timestamp) 
	{
		'''
		SELECT o.eId, o.eTimestamp, o.eValue, o.eChannelId, channels.cName
		  FROM «storage» o, channels
		  JOIN ( 
			SELECT DISTINCT(snap.eTimestamp) AS ts, MIN(ABS(snap.eTimestamp - «timestamp»))
		           FROM «storage» snap
		       ) s
		    ON s.ts = o.eTimestamp AND o.eValue > 0 AND o.eChannelId = channels.cId
			ORDER BY o.eValue DESC		
		'''	
	}

	def static timestampFilter(String storage, long from, long to)
	{
		'''
		«IF from != -1»AND «storage».eTimestamp >= «from»«ENDIF»«IF to != -1» AND «storage».eTimestamp <= «to»«ENDIF»
		'''
	}

	def static createStatistics(String origin, int channelId, int interval) {
		val table = buildTableName(origin, channelId)
		'''
		SELECT MIN(«table».eTimestamp) as timestamp, AVG(«table».eValue) as avg_v, MAX(«table».eValue) as max_v, MIN(«table».eValue) as min_v, channels.cId
		from «table», channels
		where channels.cId=«channelId»
		group by «table».eTimestamp / «interval»
		'''
	}
	
	def static createMaxFromAllChannels(String storage) {
		'''
		select «storage».eId, «storage».eTimestamp, MAX(«storage».eValue) as max_v, «storage».eChannelId
		from «storage»
		group by «storage».eChannelId
		having max_v > 0
		order by max_v desc
		'''
	}
	
	def static createAllChannels() {
		'''
		SELECT channels.cId, channels.cName, channels.cNature, channels.cMapping FROM channels ORDER BY channels.cName
		'''
	}
	
}
