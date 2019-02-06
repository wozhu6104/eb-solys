package de.systemticks.solys.db.sqlite.impl

import java.util.List
import com.google.gson.Gson
import com.google.gson.JsonElement

class SQLHelper {


	def static createTableForPrimitiveEvents(String origin, int id, String javaType) {
		'''
			CREATE TABLE «buildTableName(origin, id)»
			(eId INTEGER PRIMARY KEY,
			 eTimestamp INT8 NOT NULL,
			 eValue «javaType.toSQLType» NOT NULL)
		'''
	}

	def static createTableForStructuredEvents(String origin, int id, List<String> details) {
		'''
			CREATE TABLE «buildTableName(origin, id)»
			(eId INTEGER PRIMARY KEY,
			 eTimestamp INT8 NOT NULL,
			 eValue TEXT,
			«FOR key: details SEPARATOR ","»
			d«key.toFirstUpper» TEXT
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
			 cType INT2 NOT NULL)
		'''		
	}
	
	def static createIndex(String table, String... fields) {
		'''
			CREATE INDEX idx_«table»_«fields.join('_')» ON «table» («fields.join(",")»)
		'''
	}

	def static insertValueIntoEventTableUnprepared(String table, int id, long timestamp, Object value)
	{
		'''
		INSERT INTO «table» (eId, eTimestamp, eValue) values («id», «timestamp», «value»)
		'''
	}

	def static insertValueIntoEventTableUnprepared(String table, int id, long timestamp, String jsonString, List<String> keySet)
	{
		val gson = new Gson
		val valueElement = gson.fromJson(jsonString, JsonElement).asJsonObject.get("value").asJsonObject
		val value = valueElement.get("summary").asString
		
		val detailKeys = keySet.map['d'+toFirstUpper].join(', ')
		val detailedValues = keySet.map[k | "'"+valueElement.get("details").asJsonObject.get(k)?.asString+"'" ].join(', ')		
				
		'''
		INSERT INTO «table» (eId, eTimestamp, eValue, «detailKeys») values («id», «timestamp», '«value.replace("'", "''")»', «detailedValues» )
		'''
	}

	def static insertValueIntoEventTable(String table)
	{
		'''
		INSERT INTO «table» (eId, eTimestamp, eValue) values (?, ?, ?)
		'''
	}

	def static insertIntoChannelMappingTable(int id, String name, String nature, String type)
	{
		'''
		INSERT INTO channels (cId, cName, cNature, cType) values («id», '«name»', '«nature»', '«type»')
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
		SELECT «table».eId, «table».eTimestamp, «table».eValue, channels.cId, channels.cName
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
		SELECT channels.cId, channels.cName, channels.cNature, channels.cType FROM channels ORDER BY channels.cName
		'''
	}
	
}
