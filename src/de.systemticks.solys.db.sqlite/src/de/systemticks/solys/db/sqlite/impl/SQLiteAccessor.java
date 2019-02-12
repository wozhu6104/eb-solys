package de.systemticks.solys.db.sqlite.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.systemticks.solys.db.sqlite.api.Channel;
import de.systemticks.solys.db.sqlite.api.DataStorageAccess;
import de.systemticks.solys.db.sqlite.api.FieldMapping;
import de.systemticks.solys.db.sqlite.api.GenericJsonEvent;

@Component
public class SQLiteAccessor implements DataStorageAccess {

	private Connection connection;
	private final Map<Integer, Channel> channels = new HashMap<>();
	private int channelId = 1;
	private Gson gson = new Gson();

	private boolean open(String name) {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + name);
			connection.setAutoCommit(false);
			return true;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	private boolean close() {
		try {
			connection.commit();
			connection.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean executeSingleStatement(CharSequence sqlStmt) {

		System.out.println("Executing SQL: " + sqlStmt);

		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(sqlStmt.toString());
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public boolean openReadAndWrite(String id) {

		if (!id.equals(":memory:")) {
			File f = new File(id);
			if (f.exists()) {
				f.delete();
			}
		}

		if (!open(id)) {
			return false;
		}

		executeSingleStatement(SQLHelper.createTableForChannelMapping("channels"));

		return true;
	}

	@Override
	public boolean openReadOnly(String id) {
		return open(id);
	}

	@Override
	public boolean shutDown() {
		return close();
	}

	@Override
	public boolean bulkImportAnyBaseEvents(List<GenericJsonEvent> events) {

		try {
			Statement stmt = connection.createStatement();

			for (GenericJsonEvent e : events) {
				Channel ch = channels.get(e.getChannelId());
				if (ch != null) {
					String batchCommand = SQLHelper.insertValueIntoEventTableUnprepared(e, ch.fieldMapping).toString();
					stmt.addBatch(batchCommand);
				}
			}

			stmt.executeBatch();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public boolean commit() {
		try {
			connection.commit();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean backup(String filename) {

		try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("backup to " + filename);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private List<String> getJsonResultFromQuery(CharSequence query) {
		List<String> result = new ArrayList<String>();
		
		System.out.println("DB Query: "+query);

		Statement stmt;
		try {
			stmt = connection.createStatement();
			stmt.setFetchSize(100);

			ResultSet rs = stmt.executeQuery(query.toString());

			result = resultSetToJson(rs);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public List<String> getAllEventsFromChannel(String storage, int channelId, long fromTimestamp, long toTimestamp) {

		return getJsonResultFromQuery(
				SQLHelper.createAllEventsFromChannel(storage, channelId, fromTimestamp, toTimestamp));

	}

	@Override
	public List<String> getStatisticOverTime(String storage, int cId, int interval) {

		return getJsonResultFromQuery(SQLHelper.createStatistics(storage, cId, interval));

	}

	@Override
	public List<String> getAllChannels() {
		
		return getJsonResultFromQuery(SQLHelper.createAllChannels());
	}

	@Override
	public int createChannel(String fullname, List<FieldMapping> mapping) {

		channelId += 1;

		String container = getContainer(fullname);

		executeSingleStatement(SQLHelper.createTableForEvents(container, channelId, mapping));

		executeSingleStatement(SQLHelper.insertIntoChannelMappingTable(channelId, fullname, container, mapping));

		channels.put(channelId, new Channel(fullname, channelId, mapping));

		return channelId;

	}

	private String getContainer(String fullChannelname) {
		return fullChannelname.split("\\.")[0];
	}

	private List<String> resultSetToJson(ResultSet rs) {
		List<String> jsonResultList = new ArrayList<String>();

		try {
			ResultSetMetaData meta = rs.getMetaData();
			int cols = meta.getColumnCount();
			String[] colNames = new String[cols];
			int[] colTypes = new int[cols];
			for (int i = 0; i < cols; i++) {
				colNames[i] = meta.getColumnName(i + 1);
				colTypes[i] = meta.getColumnType(i + 1);
			}
			while (rs.next()) {
				JsonObject obj = new JsonObject();
				for (int i = 0; i < cols; i++) {
					String property = colNames[i];

					switch (colTypes[i]) {
					case Types.INTEGER:
					case Types.REAL:
					case Types.FLOAT:
					case Types.BIGINT:
						obj.addProperty(property, (Number) rs.getObject(i + 1));
						break;
					case Types.VARCHAR:
						obj.addProperty(property, (String) rs.getObject(i + 1));
						break;
					default:
						break;
					}

				}
				jsonResultList.add(gson.toJson(obj));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return jsonResultList;
	}

}

