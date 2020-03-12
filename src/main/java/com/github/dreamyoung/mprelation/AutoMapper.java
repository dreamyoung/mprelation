package com.github.dreamyoung.mprelation;


import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

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
	 * 即刻一次产生关联加载
	 * 
	 * @param <T>
	 * @param t
	 * @return
	 */
	public <T> T mapperEntity(T t) {
		t = super.manyToOne(t);
		t = super.oneToMany(t);
		t = super.oneToOne(t);
		return t;
	}

	public <T> List<T> mapperEntityList(List<T> list) {
		ListIterator<T> iter = list.listIterator();
		while (iter.hasNext()) {
			T t = iter.next();
			t = super.manyToOne(t);
			t = super.oneToMany(t);
			t = super.oneToOne(t);
		}

		return list;
	}

	public <E extends IPage<T>, T> E mapperEntityPage(E page) {
		List<T> list = page.getRecords();
		ListIterator<T> iter = list.listIterator();
		while (iter.hasNext()) {
			T t = iter.next();
			t = super.manyToOne(t);
			t = super.oneToMany(t);
			t = super.oneToOne(t);
		}

		return page;
	}

	public <T> T mapperEntityNormal(T t) {
		t = super.manyToOne(t, true);
		t = super.oneToMany(t, false);
		t = super.oneToOne(t, true);
		return t;
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
