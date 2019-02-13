package de.systemticks.solys.db.sqlite.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DetailedField {

	String name;
	String type;
	boolean complex;

}
