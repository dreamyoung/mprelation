package com.github.dreamyoung.mprelation;

import java.lang.reflect.Field;

public class JoinColumnUtil {
	public static <T> String getColumn(FieldCondition<T> fc) {
		JoinColumn joinColumn = fc.getJoinColumn();
		String column = null;

		if (joinColumn != null && !joinColumn.name().equals("")) {
			column = joinColumn.name();
		} else {
			column = fc.getName() + "Id";
		}

		if (column.indexOf("_") != -1) {
			String[] columnNameArr = column.split("_");
			column = "";
			for (int k = 0; k < columnNameArr.length; k++) {
				if (k == 0) {
					column += columnNameArr[k];
				} else {
					column += columnNameArr[k].substring(0, 1).toUpperCase() + columnNameArr[k].substring(1);
				}
			}
		}

		if (fc.getIsCollection()) {
			boolean isFound = false;
			Field[] fields = fc.getEntity().getClass().getFields();
			if (fields != null && fields.length > 0) {
				for (Field field : fields) {
					if (field.getName().equals(column)) {
						isFound = true;
						break;
					}
				}
			}

			if (!isFound) {
				column = "id";
			}
		}

		return column;
	}

	public static <T> String getRefColumn(FieldCondition<T> fc) {
		JoinColumn joinColumn = fc.getJoinColumn();
		String refColumn = null;

		if (joinColumn != null && !joinColumn.referencedColumnName().equals("")) {
			refColumn = joinColumn.referencedColumnName();
		} else {
			if (fc.getIsCollection()) {
				refColumn = fc.getFieldClass().getSimpleName();
				refColumn = refColumn.substring(0, 1).toLowerCase() + refColumn.substring(1);
				refColumn = refColumn + "_id";
			} else {
				if (joinColumn != null && !joinColumn.name().equals("")) {
					refColumn = joinColumn.name();
				} else {
					refColumn = fc.getName() + "_id";
				}
			}
		}

		return refColumn;
	}

	public static <T> String getColumnPropertyName(FieldCondition<T> fc) {
		JoinColumn joinColumn = fc.getJoinColumn();
		String refProperty = null;

		if (joinColumn != null && !joinColumn.property().equals("")) {
			refProperty = joinColumn.property();
		} else {
			if (fc.getIsCollection()) {
				if (fc.getTableId() != null) {
					refProperty = fc.getFieldOfTableId().getName();
				} else {
					refProperty = getColumn(fc);
				}
			} else {
				refProperty = getColumn(fc);
			}
		}
		return refProperty;
	}
}
