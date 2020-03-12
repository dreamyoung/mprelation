package com.github.dreamyoung.mprelation;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;


@SuppressWarnings({ "unused", "unchecked" })
public abstract class AbstractAutoMapper {

	public abstract <M> BaseMapper<M> getMapperBean(Class<M> entityClass);

	protected Map<String, String[]> entityMap = new HashMap<String, String[]>();

	public <T, E> T oneToMany(T entity) {
		return oneToManyEager(entity, null, false);
	}

	public <T, E> T oneToMany(T entity, boolean fetchEager) {
		return oneToManyEager(entity, null, fetchEager);
	}

	public <T, E> T oneToManyEager(T entity, String propertyName) {
		return oneToManyEager(entity, propertyName, true);
	}

	public <T, E> T oneToManyEager(T entity, String... propertyNames) {
		String[] names = propertyNames;
		if (names != null && names.length > 0) {
			for (int i = 0; i < names.length; i++) {
				oneToManyEager(entity, names[i], true);
			}
		}

		return entity;
	}

	public <T, E> T oneToManyEager(T entity, String propertyName, boolean fetchEager) {
		if (!entityMap.containsKey(entity.getClass().getName() + "." + RelationType.ONETOMANY.name())) {
			return entity;
		}

		Class<?> entityClass = entity.getClass();

		String[] proNames = null;
		if (entityMap.containsKey(entityClass.getName() + "." + RelationType.ONETOMANY.name())) {
			proNames = entityMap.get(entityClass.getName() + "." + RelationType.ONETOMANY.name());
		}

		Field[] fields = new Field[proNames.length];
		for (int i = 0; i < proNames.length; i++) {
			try {
				fields[i] = entityClass.getDeclaredField(proNames[i]);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}

		// Field[] fields = entityClass.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (propertyName != null && !propertyName.equals(field.getName())) {
					continue;
				}

				if (field.isAnnotationPresent(OneToMany.class)) {
					FieldCondition<T> fc = new FieldCondition<T>(entity, field);
					boolean lazy = false;
					if (fc.getLazy() == null) {
						if (fc.getOneToMany().fetch() == FetchType.LAZY) {
							lazy = true;
						}
					} else {
						lazy = fc.getLazy().value();
					}

					if (propertyName != null || fetchEager == true) {
						lazy = false;
					}
					if (!lazy) {
						JoinColumn joinColumn = fc.getJoinColumn();
						String column = JoinColumnUtil.getColumn(fc);
						String refColumn = JoinColumnUtil.getRefColumn(fc);
						String columnProperty = JoinColumnUtil.getColumnPropertyName(fc);
						Serializable columnPropertyValue = null;

						try {
							Field columnField = entityClass.getDeclaredField(columnProperty);
							columnField.setAccessible(true);
							columnPropertyValue = (Serializable) columnField.get(entity);
						} catch (Exception e) {
							throw new OneToManyException("refProperty/refPropertyValue one to many id is not correct!");
						}

						// Class<E> entityClass2 = (Class<E>) fc.getFieldClass();
						Class<?> mapperClass = fc.getMapperClass();
						BaseMapper<E> mapper = (BaseMapper<E>) getMapperBean(mapperClass);
						List<E> list = mapper.selectList(new QueryWrapper<E>().eq(refColumn, columnPropertyValue));
						fc.setFieldValueByList(list);
					}
				}
			}
		}

		return entity;

	}

	public <T, E> T oneToOne(T entity) {
		return oneToOneEager(entity, null, false);
	}

	public <T, E> T oneToOne(T entity, boolean fetchEager) {
		return oneToOneEager(entity, null, fetchEager);
	}

	public <T, E> T oneToOneEager(T entity, String propertyName) {
		return oneToOneEager(entity, propertyName, true);
	}

	public <T, E> T oneToOneEager(T entity, String... propertyNames) {
		String[] names = propertyNames;
		if (names != null && names.length > 0) {
			for (int i = 0; i < names.length; i++) {
				oneToOneEager(entity, names[i], true);
			}
		}

		return entity;
	}

	public <T, E> T oneToOneEager(T entity, String propertyName, boolean fetchEager) {
		if (!entityMap.containsKey(entity.getClass().getName() + "." + RelationType.ONETOONE.name())) {
			return entity;
		}

		Class<?> entityClass = entity.getClass();

		String[] proNames = null;
		if (entityMap.containsKey(entityClass.getName() + "." + RelationType.ONETOONE.name())) {
			proNames = entityMap.get(entityClass.getName() + "." + RelationType.ONETOONE.name());
		}

		Field[] fields = new Field[proNames.length];
		for (int i = 0; i < proNames.length; i++) {
			try {
				fields[i] = entityClass.getDeclaredField(proNames[i]);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}

		// Field[] fields = entityClass.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (propertyName != null && !propertyName.equals(field.getName())) {
					continue;
				}
				if (field.isAnnotationPresent(OneToOne.class)) {
					FieldCondition<T> fc = new FieldCondition<T>(entity, field);
					boolean lazy = false;
					if (fc.getLazy() == null) {
						if (fc.getOneToOne().fetch() == FetchType.LAZY) {
							lazy = true;
						}
					} else {
						lazy = fc.getLazy().value();
					}

					if (propertyName != null || fetchEager == true) {
						lazy = false;
					}
					if (!lazy) {
						JoinColumn joinColumn = fc.getJoinColumn();
						String column = JoinColumnUtil.getColumn(fc);
						String refColumn = JoinColumnUtil.getRefColumn(fc);
						String columnProperty = JoinColumnUtil.getColumnPropertyName(fc);
						Serializable columnPropertyValue = null;

						try {
							Field columnField = entityClass.getDeclaredField(columnProperty);
							columnField.setAccessible(true);
							columnPropertyValue = (Serializable) columnField.get(entity);
						} catch (Exception e) {
							throw new OneToOneException("refProperty/refPropertyValue one to one id is not correct!");
						}

						// Class<E> entityClass2 = (Class<E>) fc.getFieldClass();
						Class<?> mapperClass = fc.getMapperClass();
						BaseMapper<E> mapper = (BaseMapper<E>) getMapperBean(mapperClass);
						E e = (E) mapper.selectOne(new QueryWrapper<E>().eq(refColumn, columnPropertyValue));
						fc.setFieldValueByObject(e);
					}
				}

			}
		}

		return entity;

	}

	public <T, E> T manyToOne(T entity) {
		return manyToOneEager(entity, null, false);
	}

	public <T, E> T manyToOne(T entity, boolean fetchEager) {
		return manyToOneEager(entity, null, fetchEager);
	}

	public <T, E> T manyToOneEager(T entity, String propertyName) {
		return manyToOneEager(entity, propertyName, true);
	}

	public <T, E> T manyToOneEager(T entity, String... propertyNames) {
		String[] names = propertyNames;
		if (names != null && names.length > 0) {
			for (int i = 0; i < names.length; i++) {
				manyToOneEager(entity, names[i], true);
			}
		}

		return entity;
	}

	public <T, E> T manyToOneEager(T entity, String propertyName, boolean fetchEager) {
		if (!entityMap.containsKey(entity.getClass().getName() + "." + RelationType.MANYTOONE.name())) {
			return entity;
		}

		Class<?> entityClass = entity.getClass();

		String[] proNames = null;
		if (entityMap.containsKey(entityClass.getName() + "." + RelationType.MANYTOONE.name())) {
			proNames = entityMap.get(entityClass.getName() + "." + RelationType.MANYTOONE.name());
		}

		Field[] fields = new Field[proNames.length];
		for (int i = 0; i < proNames.length; i++) {
			try {
				fields[i] = entityClass.getDeclaredField(proNames[i]);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}

		// Field[] fields = entityClass.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (propertyName != null && !propertyName.equals(field.getName())) {
					continue;
				}
				if (field.isAnnotationPresent(ManyToOne.class)) {
					FieldCondition<T> fc = new FieldCondition<T>(entity, field);
					boolean lazy = false;
					if (fc.getLazy() == null) {
						if (fc.getManyToOne().fetch() == FetchType.LAZY) {
							lazy = true;
						}
					} else {
						lazy = fc.getLazy().value();
					}

					if (propertyName != null || fetchEager == true) {
						lazy = false;
					}
					if (!lazy) {
						JoinColumn joinColumn = fc.getJoinColumn();
						String column = JoinColumnUtil.getColumn(fc);
						String refColumn = JoinColumnUtil.getRefColumn(fc);
						String columnProperty = JoinColumnUtil.getColumnPropertyName(fc);
						Serializable columnPropertyValue = null;

						try {
							Field columnField = entityClass.getDeclaredField(columnProperty);
							columnField.setAccessible(true);
							columnPropertyValue = (Serializable) columnField.get(entity);
						} catch (Exception e) {
							throw new OneToOneException("refProperty/refPropertyValue many to one id is not correct!");
						}

						// Class<E> entityClass2 = (Class<E>) fc.getFieldClass();
						Class<?> mapperClass = fc.getMapperClass();

						BaseMapper<E> mapper = (BaseMapper<E>) getMapperBean(mapperClass);
						E e = (E) mapper.selectOne(new QueryWrapper<E>().eq(refColumn, columnPropertyValue));
						fc.setFieldValueByObject(e);
					}
				}

			}
		}

		return entity;
	}

	/////// many to many///////
	public <T, E> T manyToMany(T entity) {
		return manyToManyEager(entity, null, false);
	}

	public <T, E> T manyToMany(T entity, boolean fetchEager) {
		return manyToManyEager(entity, null, fetchEager);
	}

	public <T, E> T manyToManyEager(T entity, String propertyName) {
		return manyToManyEager(entity, propertyName, true);
	}

	public <T, E> T manyToManyEager(T entity, String... propertyNames) {
		String[] names = propertyNames;
		if (names != null && names.length > 0) {
			for (int i = 0; i < names.length; i++) {
				manyToManyEager(entity, names[i], true);
			}
		}

		return entity;
	}

	public <T, E, X> T manyToManyEager(T entity, String propertyName, boolean fetchEager) {
		if (!entityMap.containsKey(entity.getClass().getName() + "." + RelationType.MANYTOMANY.name())) {
			return entity;
		}

		Class<?> entityClass = entity.getClass();

		String[] proNames = null;
		if (entityMap.containsKey(entityClass.getName() + "." + RelationType.MANYTOMANY.name())) {
			proNames = entityMap.get(entityClass.getName() + "." + RelationType.MANYTOMANY.name());
		}

		Field[] fields = new Field[proNames.length];
		for (int i = 0; i < proNames.length; i++) {
			try {
				fields[i] = entityClass.getDeclaredField(proNames[i]);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}

		// Field[] fields = entityClass.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				if (propertyName != null && !propertyName.equals(field.getName())) {
					continue;
				}
				if (field.isAnnotationPresent(ManyToMany.class)) {
					FieldCondition<T> fc = new FieldCondition<T>(entity, field);
					boolean lazy = false;
					if (fc.getLazy() == null) {
						if (fc.getManyToMany().fetch() == FetchType.LAZY) {
							lazy = true;
						}
					} else {
						lazy = fc.getLazy().value();
					}

					if (propertyName != null || fetchEager == true) {
						lazy = false;
					}
					if (!lazy) {
						JoinColumn joinColumn = fc.getJoinColumn();
						String column = JoinColumnUtil.getColumn(fc);
						String refColumn = JoinColumnUtil.getRefColumn(fc);
						String columnProperty = JoinColumnUtil.getColumnPropertyName(fc);
						Serializable columnPropertyValue = null;

						try {
							Field columnField = entityClass.getDeclaredField(columnProperty);
							columnField.setAccessible(true);
							columnPropertyValue = (Serializable) columnField.get(entity);
						} catch (Exception e) {
							throw new OneToOneException("refProperty/refPropertyValue many to many id is not correct!");
						}

						
						String inverseRefColumn = InverseJoinColumnUtil.getInverseRefColumn(fc);
						List<Serializable> idList = null;
						
						Class<X> entityClassX = (Class<X>) fc.getJoinTable().entityClass();
						Class<?> mapperXClass = fc.getJoinTableMapperClass();
						
						BaseMapper<X> mapperX = (BaseMapper<X>) getMapperBean(mapperXClass);
						List<Object> xIds = mapperX.selectObjs((Wrapper<X>) new QueryWrapper<E>()
								.select(inverseRefColumn).eq(refColumn, columnPropertyValue));
						Serializable[] ids = xIds.toArray(new Serializable[] {});
						idList = Arrays.asList(ids);
						
						Class<E> entityClass2 = (Class<E>) fc.getFieldClass();
						Class<?> mapperClass = fc.getMapperClass();
						BaseMapper<E> mapper = (BaseMapper<E>) getMapperBean(mapperClass);
						List<E> list = mapper.selectBatchIds(idList);
						
						fc.setFieldValueByList(list);
						
					}
				}

			}
		}

		return entity;
	}

}
