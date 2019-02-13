package de.systemticks.solys.db.sqlite.api;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldMapping {

	List<DetailedField> details;
	String valueType;

}
