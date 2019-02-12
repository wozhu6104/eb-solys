package de.systemticks.solys.db.sqlite.api;

import java.util.List;

public class Channel {

	public String storage;
	public String type;
	public String name;
	public int id;
	public List<FieldMapping> fieldMapping;

	public Channel(String name, int id, List<FieldMapping> fieldMapping) {
		super();
		this.storage = name.split("\\.")[0];
		this.name = name;
		this.fieldMapping = fieldMapping;
		this.id = id;
	}

}
