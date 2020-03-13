package com.github.dreamyoung.mprelation;

import java.lang.reflect.Field;

import com.baomidou.mybatisplus.annotation.TableId;

public class TableIdCondition {
	private TableId tableId;
	private Field fieldOfTableId;

	public TableIdCondition(Class<?> entityClass) {
		Field[] fields = entityClass.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (field.isAnnotationPresent(TableId.class)) {
					tableId = field.getDeclaredAnnotation(TableId.class);
					fieldOfTableId = field;
					break;
				}

			}
		}
	}

	public TableId getTableId() {
		return tableId;
	}

	public void setTableId(TableId tableId) {
		this.tableId = tableId;
	}

	public Field getFieldOfTableId() {
		return fieldOfTableId;
	}

	public void setFieldOfTableId(Field fieldOfTableId) {
		this.fieldOfTableId = fieldOfTableId;
	}
}
