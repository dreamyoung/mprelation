package com.github.dreamyoung.mprelation;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.LazyLoader;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.dreamyoung.mprelation.FieldCondition.FieldCollectionType;

@SuppressWarnings({ "unused", "unchecked" })
public abstract class AbstractAutoMapper {
	//@Autowired SqlSession sqlSession;
	@Autowired ObjectFactory<SqlSession> factory;
	
	protected Log log = LogFactory.getLog(getClass());

	public abstract <M> BaseMapper<M> getMapperBean(Class<M> entityClass);

	protected Map<String, String[]> entityMap = new HashMap<String, String[]>();

	public <T, E> T oneToMany(T entity) {
		return oneToMany(entity, null, false);
	}

	public <T, E> T oneToMany(T entity, boolean fetchEager) {
		return oneToMany(entity, null, fetchEager);
	}

	public <T, E> T oneToMany(T entity, String... propertyNames) {
		String[] names = propertyNames;
		if (names != null && names.length > 0) {
			for (int i = 0; i < names.length; i++) {
				oneToMany(entity, names[i], true);
			}
		}

		return entity;
	}

	public <T, E> T oneToMany(T entity, String propertyName, boolean fetchEager) {
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

					if (!lazy) {
						// Class<E> entityClass2 = (Class<E>) fc.getFieldClass();
						Class<?> mapperClass = fc.getMapperClass();
						//BaseMapper<E> mapper = (BaseMapper<E>) getMapperBean(mapperClass);
						BaseMapper<E> mapper = (BaseMapper<E>)factory.getObject().getMapper(mapperClass);
						
						List<E> list = mapper.selectList(new QueryWrapper<E>().eq(refColumn, columnPropertyValue));
						fc.setFieldValueByList(list);
					} else {
						final Serializable columnPropertyValueX = columnPropertyValue;
						Enhancer enhancer = new Enhancer();
						enhancer.setSuperclass(ArrayList.class);

						if (fc.getFieldCollectionType() == FieldCollectionType.SET) {
							@SuppressWarnings("static-access")
							Set<E> set = (Set<E>) enhancer.create(Set.class, new LazyLoader() {
								@Override
								public Set<E> loadObject() throws Exception {
									// Class<E> entityClass2 = (Class<E>) fc.getFieldClass();
									Class<?> mapperClass = fc.getMapperClass();
									//BaseMapper<E> mapper = (BaseMapper<E>) getMapperBean(mapperClass);
									BaseMapper<E> mapper = (BaseMapper<E>)factory.getObject().getMapper(mapperClass);
									List<E> list = mapper
											.selectList(new QueryWrapper<E>().eq(refColumn, columnPropertyValueX));

									Set<E> set = null;
									if (list != null) {
										set = new HashSet<E>();
										for (E e : list) {
											set.add(e);
										}
									}
									return set;
								}

							});

							fc.setFieldValueBySet(set);
						} else {
							@SuppressWarnings("static-access")
							List<E> list = (List<E>) enhancer.create(List.class, new LazyLoader() {
								@Override
								public List<E> loadObject() throws Exception {
									// Class<E> entityClass2 = (Class<E>) fc.getFieldClass();
									Class<?> mapperClass = fc.getMapperClass();
									//BaseMapper<E> mapper = (BaseMapper<E>) getMapperBean(mapperClass);
									BaseMapper<E> mapper = (BaseMapper<E>)factory.getObject().getMapper(mapperClass);
									List<E> list = mapper
											.selectList(new QueryWrapper<E>().eq(refColumn, columnPropertyValueX));
									return list;
								}

							});

							fc.setFieldValueByList(list);
						}
					}
				}
			}
		}

		return entity;

	}

	public <T, E> T oneToOne(T entity) {
		return oneToOne(entity, null, false);
	}

	public <T, E> T oneToOne(T entity, boolean fetchEager) {
		return oneToOne(entity, null, fetchEager);
	}

	public <T, E> T oneToOne(T entity, String propertyName) {
		return oneToOne(entity, propertyName, true);
	}

	public <T, E> T oneToOne(T entity, String... propertyNames) {
		String[] names = propertyNames;
		if (names != null && names.length > 0) {
			for (int i = 0; i < names.length; i++) {
				oneToOne(entity, names[i], true);
			}
		}

		return entity;
	}

	public <T, E> T oneToOne(T entity, String propertyName, boolean fetchEager) {
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

					if (!lazy) {
						// Class<E> entityClass2 = (Class<E>) fc.getFieldClass();
						Class<?> mapperClass = fc.getMapperClass();
						//BaseMapper<E> mapper = (BaseMapper<E>) getMapperBean(mapperClass);
						BaseMapper<E> mapper = (BaseMapper<E>)factory.getObject().getMapper(mapperClass);
						E e = (E) mapper.selectOne(new QueryWrapper<E>().eq(refColumn, columnPropertyValue));
						fc.setFieldValueByObject(e);
					} else {
						System.out.println("lazy............."+field.getName());
						final Serializable columnPropertyValueX = columnPropertyValue;
						E e = (E) Enhancer.create(fc.getFieldClass(), new LazyLoader() {
							
							@Override
							public E loadObject() throws Exception {
								System.out.println("lazy...DO..........");
								
								Class<?> mapperClass = fc.getMapperClass();
								//BaseMapper<E> mapper = (BaseMapper<E>) getMapperBean(mapperClass);
								BaseMapper<E> mapper = (BaseMapper<E>)factory.getObject().getMapper(mapperClass);
								E e = (E) mapper.selectOne(new QueryWrapper<E>().eq(refColumn, columnPropertyValueX));
								return e;
							}

						});

						fc.setFieldValueByObject(e);
					}
				}

			}
		}

		return entity;

	}

	public <T, E> T manyToOne(T entity) {
		return manyToOne(entity, null, false);
	}

	public <T, E> T manyToOne(T entity, boolean fetchEager) {
		return manyToOne(entity, null, fetchEager);
	}

	public <T, E> T manyToOne(T entity, String propertyName) {
		return manyToOne(entity, propertyName, true);
	}

	public <T, E> T manyToOne(T entity, String... propertyNames) {
		String[] names = propertyNames;
		if (names != null && names.length > 0) {
			for (int i = 0; i < names.length; i++) {
				manyToOne(entity, names[i], true);
			}
		}

		return entity;
	}

	public <T, E> T manyToOne(T entity, String propertyName, boolean fetchEager) {
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

					if (!lazy) {
						// Class<E> entityClass2 = (Class<E>) fc.getFieldClass();
						Class<?> mapperClass = fc.getMapperClass();
						//BaseMapper<E> mapper = (BaseMapper<E>) getMapperBean(mapperClass);
						BaseMapper<E> mapper = (BaseMapper<E>)factory.getObject().getMapper(mapperClass);
						E e = (E) mapper.selectOne(new QueryWrapper<E>().eq(refColumn, columnPropertyValue));
						fc.setFieldValueByObject(e);
					} else {
						final Serializable columnPropertyValueX = columnPropertyValue;
						E e = (E) Enhancer.create(fc.getFieldClass(), new LazyLoader() {
							@Override
							public E loadObject() throws Exception {
								Class<?> mapperClass = fc.getMapperClass();
								//BaseMapper<E> mapper = (BaseMapper<E>) getMapperBean(mapperClass);
								BaseMapper<E> mapper = (BaseMapper<E>)factory.getObject().getMapper(mapperClass);
								E e = (E) mapper.selectOne(new QueryWrapper<E>().eq(refColumn, columnPropertyValueX));
								return e;
							}

						});

						fc.setFieldValueByObject(e);
					}
				}

			}
		}

		return entity;
	}

	/////// many to many///////
	public <T, E> T manyToMany(T entity) {
		return manyToMany(entity, null, false);
	}

	public <T, E> T manyToMany(T entity, boolean fetchEager) {
		return manyToMany(entity, null, fetchEager);
	}

	public <T, E> T manyToMany(T entity, String propertyName) {
		return manyToMany(entity, propertyName, true);
	}

	public <T, E> T manyToMany(T entity, String... propertyNames) {
		String[] names = propertyNames;
		if (names != null && names.length > 0) {
			for (int i = 0; i < names.length; i++) {
				manyToMany(entity, names[i], true);
			}
		}

		return entity;
	}

	public <T, E, X> T manyToMany(T entity, String propertyName, boolean fetchEager) {
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

					if (!lazy) {
						String inverseRefColumn = InverseJoinColumnUtil.getInverseRefColumn(fc);
						List<Serializable> idList = null;

						Class<X> entityClassX = (Class<X>) fc.getJoinTable().entityClass();
						Class<?> mapperXClass = fc.getJoinTableMapperClass();

						//BaseMapper<X> mapperX = (BaseMapper<X>) getMapperBean(mapperXClass);
						BaseMapper<X> mapperX = (BaseMapper<X>)factory.getObject().getMapper(mapperXClass);
						List<Object> xIds = mapperX.selectObjs((Wrapper<X>) new QueryWrapper<E>()
								.select(inverseRefColumn).eq(refColumn, columnPropertyValue));
						Serializable[] ids = xIds.toArray(new Serializable[] {});
						idList = Arrays.asList(ids);

						Class<E> entityClass2 = (Class<E>) fc.getFieldClass();
						Class<?> mapperClass = fc.getMapperClass();
						//BaseMapper<E> mapper = (BaseMapper<E>) getMapperBean(mapperClass);
						BaseMapper<E> mapper = (BaseMapper<E>)factory.getObject().getMapper(mapperClass);
						List<E> list = mapper.selectBatchIds(idList);
						fc.setFieldValueByList(list);
					} else {
						final Serializable columnPropertyValueX = columnPropertyValue;
						Enhancer enhancer = new Enhancer();
						enhancer.setSuperclass(List.class);

						// in FieldConditiono.setFieldValueByList() : set to list will call lazy load
						// fail,so do like this!
						if (fc.getFieldCollectionType() == FieldCollectionType.SET) {
							@SuppressWarnings("static-access")
							Set<E> set = (Set<E>) enhancer.create(Set.class, new LazyLoader() {
								@Override
								public Set<E> loadObject() throws Exception {
									String inverseRefColumn = InverseJoinColumnUtil.getInverseRefColumn(fc);
									List<Serializable> idList = null;

									Class<X> entityClassX = (Class<X>) fc.getJoinTable().entityClass();
									Class<?> mapperXClass = fc.getJoinTableMapperClass();

									//BaseMapper<X> mapperX = (BaseMapper<X>) getMapperBean(mapperXClass);
									BaseMapper<X> mapperX = (BaseMapper<X>)factory.getObject().getMapper(mapperXClass);
									List<Object> xIds = mapperX.selectObjs((Wrapper<X>) new QueryWrapper<E>()
											.select(inverseRefColumn).eq(refColumn, columnPropertyValueX));
									Serializable[] ids = xIds.toArray(new Serializable[] {});
									idList = Arrays.asList(ids);

									Class<E> entityClass2 = (Class<E>) fc.getFieldClass();
									Class<?> mapperClass = fc.getMapperClass();
									//BaseMapper<E> mapper = (BaseMapper<E>) getMapperBean(mapperClass);
									BaseMapper<E> mapper = (BaseMapper<E>)factory.getObject().getMapper(mapperClass);
									List<E> list = mapper.selectBatchIds(idList);

									Set<E> set = null;
									if (list != null) {
										set = new HashSet<E>();
										for (E e : list) {
											set.add(e);
										}
									}

									return set;

								}

							});

							fc.setFieldValueBySet(set);
						} else {
							@SuppressWarnings("static-access")
							List<E> list = (List<E>) enhancer.create(List.class, new LazyLoader() {
								@Override
								public List<E> loadObject() throws Exception {
									String inverseRefColumn = InverseJoinColumnUtil.getInverseRefColumn(fc);
									List<Serializable> idList = null;

									Class<X> entityClassX = (Class<X>) fc.getJoinTable().entityClass();
									Class<?> mapperXClass = fc.getJoinTableMapperClass();

									//BaseMapper<X> mapperX = (BaseMapper<X>) getMapperBean(mapperXClass);
									BaseMapper<X> mapperX = (BaseMapper<X>)factory.getObject().getMapper(mapperXClass);
									List<Object> xIds = mapperX.selectObjs((Wrapper<X>) new QueryWrapper<E>()
											.select(inverseRefColumn).eq(refColumn, columnPropertyValueX));
									Serializable[] ids = xIds.toArray(new Serializable[] {});
									idList = Arrays.asList(ids);

									Class<E> entityClass2 = (Class<E>) fc.getFieldClass();
									Class<?> mapperClass = fc.getMapperClass();
									//BaseMapper<E> mapper = (BaseMapper<E>) getMapperBean(mapperClass);
									BaseMapper<E> mapper = (BaseMapper<E>)factory.getObject().getMapper(mapperClass);
									List<E> proxyList = mapper.selectBatchIds(idList);

									return proxyList;
								}

							});

							fc.setFieldValueByList(list);
						}
					}

				}

			}
		}

		return entity;
	}

}
