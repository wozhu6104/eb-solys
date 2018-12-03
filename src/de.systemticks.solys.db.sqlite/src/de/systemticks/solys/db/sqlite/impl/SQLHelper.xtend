package de.systemticks.solys.db.sqlite.impl

class SQLHelper {

//        CREATE TABLE «desc.storage»
//        («FOR f: desc.mapping SEPARATOR ","»
//         «f.name» «f.toDBType» «f.toNotNull»
//        «ENDFOR»);
	
	
	def static createTableForIntEvents(String name) {
		'''
			CREATE TABLE «name»
			(eId INTEGER PRIMARY KEY,
			 eTimestamp INT8 NOT NULL,
			 eValue INT NOT NULL,
			 eChannelId INT NOT NULL)
		'''
	}

	def static dropTable(String name) {
		'DROP TABLE IF EXISTS '+name
	}

	def static createTableForDoubleEvents(String name) {
		'''
			CREATE TABLE «name»
			(eId INTEGER PRIMARY KEY,
			 eTimestamp INT8 NOT NULL,
			 eValue REAL NOT NULL,
			 eChannelId INT NOT NULL)
		'''
	}
	
	def static createTableForChannelMapping(String name) {
		'''
			CREATE TABLE «name»
			(cId INTEGER PRIMARY KEY,
			 cName TEXT NOT NULL)
		'''		
	}
	
	def static createIndex(String table, String... fields) {
		'''
			CREATE INDEX idx_«table»_«fields.join('_')» ON «table» («fields.join(",")»)
		'''
	}

//	def static createPreparedStmt(EventDescriptor desc) {
//		'''
//		INSERT INTO «desc.storage» («FOR f:desc.mapping SEPARATOR ','»«f.name»«ENDFOR») values («FOR f: desc.mapping SEPARATOR","»?«ENDFOR»);
//		'''.toString
//	}

	def static createAllEventsFromChannel(String storage, int channelId) {
		'''
		SELECT eId, eTimestamp, eValue, eChannelId FROM «storage» WHERE eChannelId = «channelId»
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
		SELECT channels.cId, channels.cName FROM channels ORDER BY channels.cName
		'''
	}
	
}
