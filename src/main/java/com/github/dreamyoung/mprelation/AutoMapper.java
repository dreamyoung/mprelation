package com.github.dreamyoung.mprelation;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * AutoMapper for one2one/one2many/many2many
 * 
 * @author dreamyoung
 *
 */
public class AutoMapper extends AbstractAutoMapper {
	@Autowired(required = false)
	ApplicationContext applicationContext;

	private String[] entityPackages;

	public AutoMapper() {
		throw new AutoMapperConfigurationException(
				"CONFIG ERR: ApplicationContext and EntityPackages are not configured!");
	}

	public AutoMapper(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		this.entityPackages = new String[] {};
	}

	public AutoMapper(ApplicationContext applicationContext, String[] entityPackages) {
		this.applicationContext = applicationContext;
		this.entityPackages = entityPackages;

		if (entityPackages != null && entityPackages.length > 0) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Class<?> clazz = null;
			String className = null;
			for (int i = 0; i < entityPackages.length; i++) {
				String entityPackage = entityPackages[i];
				String packagePath = entityPackage.replace(".", "/");

				URL url = loader.getResource(packagePath);
				if (url != null) {
					String protocol = url.getProtocol();
					if (protocol.equals("file")) {
						File file = new File(url.getPath());
						File[] files = file.listFiles();
						for (File childFile : files) {
							String fileName = childFile.getName();
							if (fileName.endsWith(".class") && !fileName.contains("$")) {
								className = entityPackage + "." + fileName.substring(0, fileName.length() - 6);

								ArrayList<String> oneToOneFields = new ArrayList<>();
								ArrayList<String> oneToManyFields = new ArrayList<>();
								ArrayList<String> manyToOneFields = new ArrayList<>();
								ArrayList<String> manyToManyFields = new ArrayList<>();

								try {
									clazz = Class.forName(className);

									Field[] fields = clazz.getDeclaredFields();
									if (fields != null && fields.length > 0) {
										for (int j = 0; j < fields.length; j++) {
											Field field = fields[j];

											OneToOne oneToOne = field.getAnnotation(OneToOne.class);
											if (oneToOne != null) {
												oneToOneFields.add(field.getName());
											}

											OneToMany oneToMany = field.getAnnotation(OneToMany.class);
											if (oneToMany != null) {
												oneToManyFields.add(field.getName());
											}

											ManyToOne manyToOne = field.getAnnotation(ManyToOne.class);
											if (manyToOne != null) {
												manyToOneFields.add(field.getName());
											}

											ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
											if (manyToMany != null) {
												manyToManyFields.add(field.getName());
											}

										}

										if (oneToOneFields.size() > 0) {
											entityMap.put(clazz.getName() + "." + RelationType.ONETOONE.name(),
													oneToOneFields.toArray(new String[] {}));
										}

										if (oneToManyFields.size() > 0) {
											entityMap.put(clazz.getName() + "." + RelationType.ONETOMANY.name(),
													oneToManyFields.toArray(new String[] {}));
										}
										if (manyToOneFields.size() > 0) {
											entityMap.put(clazz.getName() + "." + RelationType.MANYTOONE.name(),
													manyToOneFields.toArray(new String[] {}));
										}
										if (manyToManyFields.size() > 0) {
											entityMap.put(clazz.getName() + "." + RelationType.MANYTOMANY.name(),
													manyToManyFields.toArray(new String[] {}));
										}

									}

								} catch (ClassNotFoundException e) {
									e.printStackTrace();
									throw new AutoMapperException("Error in scan entity bean");
								}

							}
						}
					}
				}

			}
		}
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public <M> BaseMapper<M> getMapperBean(Class<M> entityClass) {
		return (BaseMapper<M>) applicationContext.getBean(entityClass);
	}

	/**
	 * an entity auto related
	 * 
	 * @param <T>
	 * @param t
	 * @return entity
	 */
	public <T> T mapperEntity(T t) {
		if (t != null) {
			t = super.manyToOne(t);
			t = super.oneToMany(t);
			t = super.oneToOne(t);
			t = super.manyToMany(t);
		}
		return t;
	}

	/**
	 * an antity auto related eager or not
	 * 
	 * @param <T>
	 * @param t
	 * @param fetchEager
	 * @return entity
	 */
	public <T> T mapperEntity(T t, boolean fetchEager) {
		if (t != null) {
			t = super.manyToOne(t, fetchEager);
			t = super.oneToMany(t, fetchEager);
			t = super.oneToOne(t, fetchEager);
			t = super.manyToMany(t, fetchEager);
		}
		return t;
	}

	/**
	 * an entity auto related by one of property name
	 * 
	 * @param <T>
	 * @param t
	 * @param propertyName
	 * @return entity
	 */
	public <T> T mapperEntity(T t, String propertyName) {
		if (t != null) {
			t = super.manyToOne(t, propertyName);
			t = super.oneToMany(t, propertyName);
			t = super.oneToOne(t, propertyName);
			t = super.manyToMany(t, propertyName);
		}
		return t;
	}

	/**
	 * an entity list auto related
	 * 
	 * @param <T>
	 * @param list
	 * @return entity list
	 */
	public <T> List<T> mapperEntityList(List<T> list) {
		if (list != null && list.size() > 0) {
			list = super.manyToOne(list, null, false);
			list = super.oneToMany(list, null, false);
			list = super.oneToOne(list, null, false);
			list = super.manyToMany(list, null, false);
		}

		return list;
	}

	/**
	 * an entity list auto related eager or not
	 * 
	 * @param <T>
	 * @param list
	 * @param fetchEager
	 * @return entity list
	 */
	public <T> List<T> mapperEntityList(List<T> list, boolean fetchEager) {
		if (list != null && list.size() > 0) {
			list = super.manyToOne(list, null, true);
			list = super.oneToMany(list, null, true);
			list = super.oneToOne(list, null, true);
			list = super.manyToMany(list, null, true);
		}

		return list;
	}

	/**
	 * an entity list auto related by one of property name
	 * 
	 * @param <T>
	 * @param list
	 * @param propertyName
	 * @return entity list
	 */
	public <T> List<T> mapperEntityList(List<T> list, String propertyName) {
		if (list != null && list.size() > 0) {
			list = super.manyToOne(list, propertyName, true);
			list = super.oneToMany(list, propertyName, true);
			list = super.oneToOne(list, propertyName, true);
			list = super.manyToMany(list, propertyName, true);
		}

		return list;
	}

	/**
	 * an entity set auto related
	 * 
	 * @param <T>
	 * @param set
	 * @return entity set
	 */
	public <T> Set<T> mapperEntitySet(Set<T> set) {
		if (set != null && set.size() > 0) {
			Iterator<T> iter = set.iterator();
			List<T> list = new ArrayList<T>();
			while (iter.hasNext()) {
				T t = iter.next();
				list.add(t);
			}

			mapperEntityList(list);
		}
		return set;
	}

	/**
	 * an entity set auto related eager or not
	 * 
	 * @param <T>
	 * @param set
	 * @param fetchEager
	 * @return entity set
	 */
	public <T> Set<T> mapperEntitySet(Set<T> set, boolean fetchEager) {
		if (set != null && set.size() > 0) {
			Iterator<T> iter = set.iterator();
			List<T> list = new ArrayList<T>();
			while (iter.hasNext()) {
				T t = iter.next();
				list.add(t);
			}

			mapperEntityList(list, true);
		}
		return set;
	}

	/**
	 * an entity set auto related by one of property name
	 * 
	 * @param <T>
	 * @param set
	 * @param propertyName
	 * @return entity set
	 */
	public <T> Set<T> mapperEntitySet(Set<T> set, String propertyName) {
		if (set != null && set.size() > 0) {
			Iterator<T> iter = set.iterator();
			List<T> list = new ArrayList<T>();
			while (iter.hasNext()) {
				T t = iter.next();
				list.add(t);
			}

			mapperEntityList(list, propertyName);
		}
		return set;
	}

	/**
	 * an entity page auto related
	 * 
	 * @param <E>
	 * @param <T>
	 * @param page
	 * @return entity page
	 */
	public <E extends IPage<T>, T> E mapperEntityPage(E page) {
		List<T> list = page.getRecords();
		list = mapperEntityList(list);

		return page;
	}

	/**
	 * an entity page auto related eager or not
	 * 
	 * @param <E>
	 * @param <T>
	 * @param page
	 * @param fetchEager
	 * @return entity page
	 */
	public <E extends IPage<T>, T> E mapperEntityPage(E page, boolean fetchEager) {
		List<T> list = page.getRecords();
		list = mapperEntityList(list, true);

		return page;
	}

	/**
	 * an entity page auto related by one of property name
	 * 
	 * @param <E>
	 * @param <T>
	 * @param page
	 * @param propertyName
	 * @return entity page
	 */
	public <E extends IPage<T>, T> E mapperEntityPage(E page, String propertyName) {
		List<T> list = page.getRecords();
		list = mapperEntityList(list, propertyName);

		return page;
	}

	/**
	 * an entity list auto related
	 * 
	 * @param <T>
	 * @param list
	 * @return entity list
	 */
	public <T> Collection<T> mapperEntityCollection(Collection<T> list) {
		if (list != null && list.size() > 0) {
			if (list.getClass() == List.class || list.getClass() == ArrayList.class) {
				list = mapperEntityList((List<T>) list);
			} else {
				list = mapperEntitySet((Set<T>) list);
			}
		}

		return list;
	}

	/**
	 * an entity list auto related eager or not
	 * 
	 * @param <T>
	 * @param list
	 * @param fetchEager
	 * @return entity list
	 */
	public <T> Collection<T> mapperEntityCollection(Collection<T> list, boolean fetchEager) {
		if (list != null && list.size() > 0) {
			if (list.getClass() == List.class || list.getClass() == ArrayList.class) {
				list = mapperEntityList((List<T>) list, fetchEager);
			} else {
				list = mapperEntitySet((Set<T>) list, fetchEager);
			}
		}

		return list;
	}

	/**
	 * an entity list auto related by one of property name
	 * 
	 * @param <T>
	 * @param list
	 * @param propertyName
	 * @return entity list
	 */
	public <T> Collection<T> mapperEntityCollection(Collection<T> list, String propertyName) {
		if (list != null && list.size() > 0) {
			if (list.getClass() == List.class || list.getClass() == ArrayList.class) {
				list = mapperEntityList((List<T>) list, propertyName);
			} else {
				list = mapperEntitySet((Set<T>) list, propertyName);
			}
		}
		return list;
	}

	/**
	 * an entity/entity list/entity set/entity page auto related eager or not
	 * 
	 * @param <E>
	 * @param <T>
	 * @param object
	 * @param fetchEager
	 */
	@SuppressWarnings("unchecked")
	public <E extends IPage<T>, T> void mapper(Object object) {
		if (object != null) {
			if (object.getClass() == List.class || object.getClass() == ArrayList.class) {
				mapperEntityList((List<T>) object);
			} else if (object.getClass() == Set.class || object.getClass() == HashSet.class) {
				mapperEntitySet((Set<T>) object);
			} else if (object instanceof IPage) {
				mapperEntityPage((E) object);
			} else {
				mapperEntity(object);
			}
		}
	}

	/**
	 * an entity/entity list/entity set/entity page auto related eager or not
	 * 
	 * @param <E>
	 * @param <T>
	 * @param object
	 * @param fetchEager
	 */
	@SuppressWarnings("unchecked")
	public <E extends IPage<T>, T> void mapper(Object object, boolean fetchEager) {
		if (object != null) {
			if (object.getClass() == List.class || object.getClass() == ArrayList.class) {
				mapperEntityList((List<T>) object, fetchEager);
			} else if (object.getClass() == Set.class || object.getClass() == HashSet.class) {
				mapperEntitySet((Set<T>) object, fetchEager);
			} else if (object instanceof IPage) {
				mapperEntityPage((E) object, fetchEager);
			} else {
				mapperEntity(object, fetchEager);
			}
		}
	}

	/**
	 * an entity/entity list/entity set/entity page auto related by one of property
	 * name
	 * 
	 * @param <E>
	 * @param <T>
	 * @param object
	 * @param fetchEager
	 */
	@SuppressWarnings("unchecked")
	public <E extends IPage<T>, T> void mapper(Object object, String propertyName) {
		if (object != null) {
			if (object.getClass() == List.class || object.getClass() == ArrayList.class) {
				mapperEntityList((List<T>) object, propertyName);
			} else if (object.getClass() == Set.class || object.getClass() == HashSet.class) {
				mapperEntitySet((Set<T>) object, propertyName);
			} else if (object instanceof IPage) {
				mapperEntityPage((E) object, propertyName);
			} else {
				mapperEntity(object, propertyName);
			}
		}
	}

	/**
	 * initialize one or more lazy property manually with transactional sqlsession
	 * @param <E>
	 * @param <T>
	 * @param object
	 * @param propertyNames
	 */
	@Transactional(readOnly = true)
	public <E extends IPage<T>, T> void initialize(Object object, String... propertyNames) {
		for (String propertyName : propertyNames) {
			mapper(object, propertyName);
		}
	}

	public String[] getEntityPackages() {
		return entityPackages;
	}

	public void setEntityPackages(String[] entityPackages) {
		this.entityPackages = entityPackages;
	}

	public Map<String, String[]> getEntityMap() {
		return entityMap;
	}

	public void setEntityMap(Map<String, String[]> entityMap) {
		this.entityMap = entityMap;
	}
}
