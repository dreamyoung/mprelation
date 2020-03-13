package com.github.dreamyoung.mprelation;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

public class FieldCondition<T> {
	enum FieldCollectionType {
		LIST, SET, NONE
	};

	private T entity;

	private Field field;
	private String name;
	private Boolean isCollection;
	private FieldCollectionType fieldCollectionType;
	private Class<?> fieldClass;
	private Boolean isLazy;

	private TableId tableId;
	private Field fieldOfTableId;
	private TableField tableField;
	private OneToMany oneToMany;
	private OneToOne oneToOne;
	private ManyToOne manyToOne;
	private ManyToMany manyToMany;
	private Lazy lazy;
	private JoinColumn joinColumn;
	private JoinColumns joinColumns;
	private JoinTable joinTable;
	private InverseJoinColumn inverseJoinColumn;
	private EntityMapper entityMapper;
	private Class<?> mapperClass;
	private Class<?> joinTableMapperClass;

	public FieldCondition(T entity, Field field) {
		this.entity = entity;
		this.field = field;
		this.field.setAccessible(true);

		this.name = field.getName();
		this.isCollection = field.getType() == List.class || field.getType() == ArrayList.class
				|| field.getType() == Set.class || field.getType() == HashSet.class;
		this.fieldClass = field.getType();
		if (isCollection) {
			Type genericType = field.getGenericType();
			if (genericType != null && genericType instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) genericType;
				this.fieldClass = (Class<?>) pt.getActualTypeArguments()[0];
			}
		}

		if (field.getType() == List.class || field.getType() == ArrayList.class) {
			this.fieldCollectionType = FieldCollectionType.LIST;
		} else if (field.getType() == Set.class || field.getType() == HashSet.class) {
			this.fieldCollectionType = FieldCollectionType.SET;
		} else {
			this.fieldCollectionType = FieldCollectionType.NONE;
		}

		this.tableField = field.getAnnotation(TableField.class);
		this.oneToMany = field.getAnnotation(OneToMany.class);
		this.oneToOne = field.getAnnotation(OneToOne.class);
		this.manyToOne = field.getAnnotation(ManyToOne.class);
		this.manyToMany = field.getAnnotation(ManyToMany.class);
		this.lazy = field.getAnnotation(Lazy.class);
		this.joinColumn = field.getAnnotation(JoinColumn.class);
		this.joinColumns = field.getAnnotation(JoinColumns.class);
		this.joinTable = field.getAnnotation(JoinTable.class);
		this.inverseJoinColumn = field.getAnnotation(InverseJoinColumn.class);
		this.entityMapper = field.getAnnotation(EntityMapper.class);
		this.isLazy = (lazy != null && lazy.value() == true);

		TableIdCondition tidCondition = new TableIdCondition(entity.getClass());
		this.tableId = tidCondition.getTableId();
		this.fieldOfTableId = tidCondition.getFieldOfTableId();

		this.mapperClass = null;
		if (entityMapper != null && entityMapper.targetMapper() != void.class) {
			mapperClass = entityMapper.targetMapper();
		} else {
			StringBuffer className = new StringBuffer();
			String pkgName = this.getFieldClass().getName();
			String[] pkgNamesArr = pkgName.split("\\.");
			for (int p = 0; p < pkgNamesArr.length - 2; p++) {
				className.append(pkgNamesArr[p]);
				className.append(".");
			}
			className.append("mapper.");
			className.append(this.getFieldClass().getSimpleName());
			className.append("Mapper");

			try {
				mapperClass = Class.forName(className.toString());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		this.joinTableMapperClass = null;
		if (joinTable != null) {
			if (joinTable.targetMapper() != null && joinTable.targetMapper() != void.class) {
				joinTableMapperClass = joinTable.targetMapper();
			} else {
				StringBuffer className = new StringBuffer();
				String pkgName = this.getFieldClass().getName();
				String[] pkgNamesArr = pkgName.split("\\.");
				for (int p = 0; p < pkgNamesArr.length - 2; p++) {
					className.append(pkgNamesArr[p]);
					className.append(".");
				}
				className.append("mapper.");

				if (joinTable.entityClass() != null && joinTable.entityClass() != void.class) {
					className.append(joinTable.entityClass().getSimpleName());
				} else {
					String joinEntityName = null;
					if (joinTable.name() != null && !joinTable.name().equals("")) {
						joinEntityName = joinTable.name();
						String[] names = joinEntityName.split("_");
						joinEntityName = "";
						for (int i = 0; i < names.length; i++) {
							joinEntityName += names[i].substring(0, 1).toUpperCase();
							joinEntityName += names[i].substring(1);
						}
						className.append(joinEntityName);
					} else {
						joinEntityName = entity.getClass().getSimpleName() + this.getFieldClass().getSimpleName();
						className.append(joinEntityName);
					}
				}

				className.append("Mapper");

				try {
					joinTableMapperClass = Class.forName(className.toString());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsCollection() {
		return isCollection;
	}

	public void setIsCollection(Boolean isCollection) {
		this.isCollection = isCollection;
	}

	public Class<?> getFieldClass() {
		return fieldClass;
	}

	public void setFieldClass(Class<?> fieldClass) {
		this.fieldClass = fieldClass;
	}

	public TableField getTableField() {
		return tableField;
	}

	public void setTableField(TableField tableField) {
		this.tableField = tableField;
	}

	public OneToMany getOneToMany() {
		return oneToMany;
	}

	public void setOneToMany(OneToMany oneToMany) {
		this.oneToMany = oneToMany;
	}

	public Lazy getLazy() {
		return lazy;
	}

	public void setLazy(Lazy lazy) {
		this.lazy = lazy;
	}

	public JoinColumn getJoinColumn() {
		return joinColumn;
	}

	public void setJoinColumn(JoinColumn joinColumn) {
		this.joinColumn = joinColumn;
	}

	public EntityMapper getEntityMapper() {
		return entityMapper;
	}

	public void setEntityMapper(EntityMapper entityMapper) {
		this.entityMapper = entityMapper;
	}

	public OneToOne getOneToOne() {
		return oneToOne;
	}

	public void setOneToOne(OneToOne oneToOne) {
		this.oneToOne = oneToOne;
	}

	public ManyToOne getManyToOne() {
		return manyToOne;
	}

	public void setManyToOne(ManyToOne manyToOne) {
		this.manyToOne = manyToOne;
	}

	public ManyToMany getManyToMany() {
		return manyToMany;
	}

	public void setManyToMany(ManyToMany manyToMany) {
		this.manyToMany = manyToMany;
	}

	public JoinColumns getJoinColumns() {
		return joinColumns;
	}

	public void setJoinColumns(JoinColumns joinColumns) {
		this.joinColumns = joinColumns;
	}

	public Boolean getIsLazy() {
		return isLazy;
	}

	public void setIsLazy(Boolean isLazy) {
		this.isLazy = isLazy;
	}

	public FieldCollectionType getFieldCollectionType() {
		return fieldCollectionType;
	}

	public void setFieldCollectionType(FieldCollectionType fieldCollectionType) {
		this.fieldCollectionType = fieldCollectionType;
	}

	public JoinTable getJoinTable() {
		return joinTable;
	}

	public void setJoinTable(JoinTable joinTable) {
		this.joinTable = joinTable;
	}

	@Override
	public String toString() {
		return "FieldCondition [field=" + field + ", name=" + name + ", isCollection=" + isCollection
				+ ", fieldCollectionType=" + fieldCollectionType + ", fieldClass=" + fieldClass + ", isLazy=" + isLazy
				+ ", tableField=" + tableField + ", oneToMany=" + oneToMany + ", oneToOne=" + oneToOne + ", manyToOne="
				+ manyToOne + ", manyToMany=" + manyToMany + ", lazy=" + lazy + ", joinColumn=" + joinColumn
				+ ", joinColumns=" + joinColumns + ", joinTable=" + joinTable + ", entityMapper=" + entityMapper + "]";
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

	public T getEntity() {
		return entity;
	}

	public void setEntity(T entity) {
		this.entity = entity;
	}

	public InverseJoinColumn getInverseJoinColumn() {
		return inverseJoinColumn;
	}

	public void setInverseJoinColumn(InverseJoinColumn inverseJoinColumn) {
		this.inverseJoinColumn = inverseJoinColumn;
	}

	public Class<?> getMapperClass() {
		return mapperClass;
	}

	public void setMapperClass(Class<?> mapperClass) {
		this.mapperClass = mapperClass;
	}

	public <E> void setFieldValueByList(List<E> list) {
		if (list != null) {
			field.setAccessible(true);
			try {
				if (this.getFieldCollectionType() == FieldCollectionType.SET) {
					// list to set
					Set<E> set = new HashSet<E>();
					for (E e : list) {// list 被访问，导致延迟立即加载，延迟失败！
						set.add(e);
					}
					field.set(entity, set);

				} else {
					field.set(entity, list);
				}

			} catch (Exception e) {
				throw new OneToManyException(
						String.format("{0} call setter {1} is not correct!", entity, field.getName()));
			}
		}
	}

	public <E> void setFieldValueBySet(Set<E> set) {
		if (set != null) {
			field.setAccessible(true);
			try {
				field.set(entity, set);
			} catch (Exception e) {
				throw new OneToManyException(
						String.format("{0} call setter {1} is not correct!", entity, field.getName()));
			}
		}
	}

	public <E> void setFieldValueByObject(E e) {
		field.setAccessible(true);
		try {
			field.set(entity, e);
		} catch (Exception ex) {
			throw new OneToOneException(String.format("{0} call setter {1} is not correct!", entity, field.getName()));
		}
	}

	public Class<?> getJoinTableMapperClass() {
		return joinTableMapperClass;
	}

	public void setJoinTableMapperClass(Class<?> joinTableMapperClass) {
		this.joinTableMapperClass = joinTableMapperClass;
	}

}
