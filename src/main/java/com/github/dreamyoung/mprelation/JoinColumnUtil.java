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
		String columnProperty = null;

		if (joinColumn != null && !joinColumn.property().equals("")) {
			columnProperty = joinColumn.property();
		} else {
			if (fc.getIsCollection()) {
				if (fc.getTableId() != null) {
					columnProperty = fc.getFieldOfTableId().getName();
				} else {
					columnProperty = getColumn(fc);
				}
			} else {
				columnProperty = getColumn(fc);
			}
		}
		return columnProperty;
	}

	public static <T> String getRefColumnProperty(FieldCondition<T> fc) {
		JoinColumn joinColumn = fc.getJoinColumn();
		String refColumnProperty = null;

		if (joinColumn != null && !joinColumn.referencedColumnName().equals("")) {
			refColumnProperty = joinColumn.referencedColumnName();
			if (refColumnProperty.indexOf("_") != -1) {
				String[] columnNameArr = refColumnProperty.split("_");
				refColumnProperty = "";
				for (int k = 0; k < columnNameArr.length; k++) {
					if (k == 0) {
						refColumnProperty += columnNameArr[k];
					} else {
						refColumnProperty += columnNameArr[k].substring(0, 1).toUpperCase()
								+ columnNameArr[k].substring(1);
					}
				}
			}
		} else {
			if (fc.getIsCollection()) {
				if (fc.getTableId() != null) {
					refColumnProperty = fc.getFieldOfTableId().getName();
				} else {
					refColumnProperty = getColumn(fc);
				}
			} else {
				refColumnProperty = getColumn(fc);
			}
		}
		return refColumnProperty;
	}
	
	
	public static <T>  String getInverseRefColumnProperty(FieldCondition<T> fc) {
		InverseJoinColumn inverseJoinColumn = fc.getInverseJoinColumn();
		String inverseRefColumnProperty = null;

		if (inverseJoinColumn != null && !inverseJoinColumn.referencedColumnName().equals("")) {
			inverseRefColumnProperty = inverseJoinColumn.referencedColumnName();
			if (inverseRefColumnProperty.indexOf("_") != -1) {
				String[] columnNameArr = inverseRefColumnProperty.split("_");
				inverseRefColumnProperty = "";
				for (int k = 0; k < columnNameArr.length; k++) {
					if (k == 0) {
						inverseRefColumnProperty += columnNameArr[k];
					} else {
						inverseRefColumnProperty += columnNameArr[k].substring(0, 1).toUpperCase()
								+ columnNameArr[k].substring(1);
					}
				}
			}
		} else {
			if (fc.getIsCollection()) {
				if (fc.getTableId() != null) {
					inverseRefColumnProperty = fc.getFieldOfTableId().getName();
				} else {
					inverseRefColumnProperty = getColumn(fc);
				}
			} else {
				inverseRefColumnProperty = getColumn(fc);
			}
		}
		return inverseRefColumnProperty;
	}
}
