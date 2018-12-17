package de.systemticks.solys.db.sqlite.impl

class SQLHelper {


	def static createTableForAnyEvents(String origin, int id, Object obj) {
		'''
			CREATE TABLE «origin»_«id»
			(eId INTEGER PRIMARY KEY,
			 eTimestamp INT8 NOT NULL,
			 eValue «obj.toSQLType» NOT NULL)
		'''
	}

	private def static toSQLType(Object o)
	{
		switch o.class.simpleName {
			case 'Double' : 'REAL'
			case 'Integer' : 'INT'
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

	def static createAllEventsFromChannel(String storage, int channelId) {
		'''
		SELECT «storage».eId, «storage».eTimestamp, «storage».eValue, «storage».eChannelId 
		FROM «storage» 
		WHERE «storage».eChannelId = «channelId»
		ORDER BY «storage».eTimestamp
		'''
	}

	def static createAllEventsFromChannel(String storage, int channelId, long from, long to) 
	{
		'''
		SELECT «storage».eId, «storage».eTimestamp, «storage».eValue, «storage».eChannelId, channels.cName
		FROM «storage», channels 
		WHERE «storage».eChannelId = «channelId» «timestampFilter(storage, from, to)» AND «storage».eChannelId = channels.cId
		ORDER BY «storage».eTimestamp
		'''
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

	def static createStatistics(String storage, int channelId, int interval) {
		'''
		SELECT MIN(«storage».eTimestamp) as t, AVG(«storage».eValue) as avg_v, MAX(«storage».eValue) as max_v, MIN(«storage».eValue) as min_v, «storage».eChannelId
		from «storage»
		where «storage».eChannelId=«channelId»
		group by «storage».eTimestamp / «interval»
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
