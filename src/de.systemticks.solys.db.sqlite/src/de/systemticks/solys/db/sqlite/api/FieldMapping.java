package de.systemticks.solys.db.sqlite.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldMapping {

	String name;
	String type;
	boolean complex;

}
