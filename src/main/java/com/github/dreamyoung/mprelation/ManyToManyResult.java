package com.github.dreamyoung.mprelation;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.LazyLoader;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.dreamyoung.mprelation.FieldCondition.FieldCollectionType;

@SuppressWarnings("unused")
public class ManyToManyResult<T, E, X> {
	private List<T> list;
	private Map<String, List<X>> entityXListMap;
	private Field[] fields;
	private Collection<E> CollectionAll;
	private boolean lazy;
	private String fieldCode;
	private String refColumn;
	private String inverseRefColumn;
	private BaseMapper<E> mapperE;
	private BaseMapper<X> mapperX;
	private FieldCollectionType fieldCollectionType;
	private ArrayList<Serializable> columnPropertyValueList;

	private Map<String, String> columnPropertyMap;
	private Map<String, String> refColumnPropertyMap;
	private Map<String, String> inverseColumnPropertyMap;
	private Map<String, String> inverseRefColumnPropertyMap;

	private FieldCondition<T> fc;
	private Map<String, Boolean> isExeSqlMap;
	private Map<String, Collection<E>> collectionMap;

	public ManyToManyResult(Field[] fields) {
		isExeSqlMap = new HashMap<String, Boolean>();
		collectionMap = new HashMap<String, Collection<E>>();
		for (int i = 0; i < fields.length; i++) {
			isExeSqlMap.put(fields[i].getName(), false);
			Collection<E> c = null;
			collectionMap.put(fields[i].getName(), c);
		}
	}

	public void handle(Field field) {
		List<E> listAll = null;
		if (!lazy) {
			if (fieldCollectionType == FieldCollectionType.SET) {
				Set<E> setAll = (Set<E>) CollectionAll;
				if (setAll != null) {
					listAll = new ArrayList<E>();
					for (E e : setAll) {
						listAll.add(e);
					}
				}
			} else {
				listAll = (List<E>) CollectionAll;
			}
		}

		if (listAll != null && listAll.size() > 0) {
			for (int j = 0; j < list.size(); j++) {
				T entity = list.get(j);
				String columnProperty = columnPropertyMap.get(fieldCode);
				String refColumnProperty = refColumnPropertyMap.get(fieldCode);
				String inverseColumnProperty = inverseColumnPropertyMap.get(fieldCode);
				String inverseRefColumnProperty = inverseRefColumnPropertyMap.get(fieldCode);
				List<X> entityXList = entityXListMap.get(fieldCode);

				Collection<E> listForThisEntity = new ArrayList<E>();
				if (fieldCollectionType == FieldCollectionType.SET) {
					listForThisEntity = new HashSet<E>();
				}

				for (int k = 0; k < listAll.size(); k++) {
					E entityE = listAll.get(k);
					Field entityField = null;
					Field entity2Field = null;
					Field entityXField = null;
					Field entityXField2 = null;
					try {
						entityField = entity.getClass().getDeclaredField(columnProperty);
						entityField.setAccessible(true);
						Object columnValue = entityField.get(entity);

						entity2Field = entityE.getClass().getDeclaredField(inverseColumnProperty);
						entity2Field.setAccessible(true);
						Object refCoumnValue = entity2Field.get(entityE);

						// table1~table3&&table2~table3

						for (int x = 0; x < entityXList.size(); x++) {
							X entityX = entityXList.get(x);

							entityXField = entityX.getClass()
									.getDeclaredField(JoinTableUtil.getRefColumnProperty(refColumn));
							entityXField.setAccessible(true);
							Object columnValueX = entityXField.get(entityX);

							entityXField2 = entityX.getClass()
									.getDeclaredField(JoinTableUtil.getInverseRefColumnProperty(inverseRefColumn));
							entityXField2.setAccessible(true);
							Object refColumnValueX = entityXField2.get(entityX);

							if (columnValueX.toString().equals(columnValue.toString())
									&& refColumnValueX.toString().equals(refCoumnValue.toString())) {
								listForThisEntity.add(entityE);
							}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				}

				try {
					field.set(entity, listForThisEntity);
				} catch (

				Exception e) {
					e.printStackTrace();
				}
			} // end loop-entity
		} // end if

	}

	public void handleLazy(Field field) {
		final BaseMapper<E> mapper = (BaseMapper<E>) this.mapperE;
		Map<String, List<X>> entityXListMap = new HashMap<String, List<X>>();

		ArrayList<Serializable> idListDistinct = new ArrayList<Serializable>();
		if (columnPropertyValueList.size() > 0) {
			for (int s = 0; s < columnPropertyValueList.size(); s++) {
				boolean isExists = false;
				for (int ss = 0; ss < idListDistinct.size(); ss++) {
					if (columnPropertyValueList.get(s).toString().equals(idListDistinct.get(ss).toString())) {
						isExists = true;
						break;
					}
				}

				if (!isExists) {
					idListDistinct.add(columnPropertyValueList.get(s));
				}
			}
		}
		columnPropertyValueList = idListDistinct;

		if (fieldCollectionType == FieldCollectionType.SET) {

			for (int i = 0; i < this.list.size(); i++) {
				T entity = list.get(i);

				String columnProperty = columnPropertyMap.get(fieldCode);
				String refColumnProperty = refColumnPropertyMap.get(fieldCode);
				String inverseColumnProperty = inverseColumnPropertyMap.get(fieldCode);
				String inverseRefColumnProperty = inverseRefColumnPropertyMap.get(fieldCode);
				// List<X> entityXList = entityXListMap.get(fieldCode);

				@SuppressWarnings("unchecked")
				Set<E> setForThisEntityProxy = (Set<E>) Enhancer.create(Set.class, new LazyLoader() {

					@Override
					public Set<E> loadObject() throws Exception {

						List<X> entityXList = null;
						if (isExeSqlMap.get(field.getName()) == false) {
							isExeSqlMap.put(field.getName(), true);

							entityXList = mapperX
									.selectList(new QueryWrapper<X>().in(refColumn, columnPropertyValueList));
							if (!entityXListMap.containsKey(fieldCode)) {
								entityXListMap.put(fieldCode, entityXList);
							}

							List<Serializable> idList = new ArrayList<Serializable>();
							for (int ii = 0; ii < entityXList.size(); ii++) {
								X entityX = entityXList.get(ii);
								try {
									Field fieldX = entityX.getClass().getDeclaredField(inverseRefColumnProperty);
									fieldX.setAccessible(true);
									Serializable id = (Serializable) fieldX.get(entityX);
									if (!idList.contains(id)) {
										idList.add(id);
									}
								} catch (Exception e1) {
									e1.printStackTrace();
								}

							}
							List<Serializable> idListDistinct = new ArrayList<Serializable>();
							if (idList.size() > 0) {
								for (int s = 0; s < idList.size(); s++) {
									boolean isExists = false;
									for (int ss = 0; ss < idListDistinct.size(); ss++) {
										if (idList.get(s).toString().equals(idListDistinct.get(ss).toString())) {
											isExists = true;
											break;
										}
									}

									if (!isExists) {
										idListDistinct.add(idList.get(s));
									}
								}
							}
							idList = idListDistinct;

							collectionMap.put(field.getName(), mapper.selectList(
									new QueryWrapper<E>().in(inverseRefColumn, (ArrayList<Serializable>) idList)));
							isExeSqlMap.put(field.getName(), true);

						}

						entityXList = entityXListMap.get(fieldCode);

						List<E> listAll = (List<E>) collectionMap.get(field.getName());

						String columnProperty = columnPropertyMap.get(fieldCode);
						String refColumnProperty = refColumnPropertyMap.get(fieldCode);
						String inverseColumnProperty = inverseColumnPropertyMap.get(fieldCode);
						String inverseRefColumnProperty = inverseRefColumnPropertyMap.get(fieldCode);
						// List<X> entityXList = entityXListMap.get(fieldCode);

						Collection<E> listForThisEntity = new ArrayList<E>();
						if (fieldCollectionType == FieldCollectionType.SET) {
							listForThisEntity = new HashSet<E>();
						}

						for (int k = 0; k < listAll.size(); k++) {
							E entityE = listAll.get(k);
							Field entityField = null;
							Field entity2Field = null;
							Field entityXField = null;
							Field entityXField2 = null;
							try {
								entityField = entity.getClass().getDeclaredField(columnProperty);
								entityField.setAccessible(true);
								Object columnValue = entityField.get(entity);

								entity2Field = entityE.getClass().getDeclaredField(inverseColumnProperty);
								entity2Field.setAccessible(true);
								Object refCoumnValue = entity2Field.get(entityE);

								// table1~table3&&table2~table3
								for (int x = 0; x < entityXList.size(); x++) {
									X entityX = entityXList.get(x);

									entityXField = entityX.getClass()
											.getDeclaredField(JoinTableUtil.getRefColumnProperty(refColumn));
									entityXField.setAccessible(true);
									Object columnValueX = entityXField.get(entityX);

									entityXField2 = entityX.getClass().getDeclaredField(
											JoinTableUtil.getInverseRefColumnProperty(inverseRefColumn));
									entityXField2.setAccessible(true);
									Object refColumnValueX = entityXField2.get(entityX);

									if (columnValueX.toString().equals(columnValue.toString())
											&& refColumnValueX.toString().equals(refCoumnValue.toString())) {
										listForThisEntity.add(entityE);
									}
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}

						}

						return (Set<E>) listForThisEntity;
					}

				});

				// 设置代理
				try {
					field.set(entity, setForThisEntityProxy);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			for (int i = 0; i < this.list.size(); i++) {
				T entity = list.get(i);

				String columnProperty = columnPropertyMap.get(fieldCode);
				String refColumnProperty = refColumnPropertyMap.get(fieldCode);
				String inverseColumnProperty = inverseColumnPropertyMap.get(fieldCode);
				String inverseRefColumnProperty = inverseRefColumnPropertyMap.get(fieldCode);
				// List<X> entityXList = entityXListMap.get(fieldCode);

				@SuppressWarnings("unchecked")
				List<E> listForThisEntityProxy = (List<E>) Enhancer.create(List.class, new LazyLoader() {

					@Override
					public List<E> loadObject() throws Exception {

						List<X> entityXList = null;
						if (isExeSqlMap.get(field.getName()) == false) {
							isExeSqlMap.put(field.getName(), true);

							entityXList = mapperX
									.selectList(new QueryWrapper<X>().in(refColumn, columnPropertyValueList));
							if (!entityXListMap.containsKey(fieldCode)) {
								entityXListMap.put(fieldCode, entityXList);
							}

							ArrayList<Serializable> idList = new ArrayList<Serializable>();
							for (int ii = 0; ii < entityXList.size(); ii++) {
								X entityX = entityXList.get(ii);
								try {
									Field fieldX = entityX.getClass().getDeclaredField(inverseRefColumnProperty);
									fieldX.setAccessible(true);
									Serializable id = (Serializable) fieldX.get(entityX);
									if (!idList.contains(id)) {
										idList.add(id);
									}
								} catch (Exception e1) {
									e1.printStackTrace();
								}

							}
							ArrayList<Serializable> idListDistinct = new ArrayList<Serializable>();
							if (idList.size() > 0) {
								for (int s = 0; s < idList.size(); s++) {
									boolean isExists = false;
									for (int ss = 0; ss < idListDistinct.size(); ss++) {
										if (idList.get(s).toString().equals(idListDistinct.get(ss).toString())) {
											isExists = true;
											break;
										}
									}

									if (!isExists) {
										idListDistinct.add(idList.get(s));
									}
								}
							}
							idList = idListDistinct;

							collectionMap.put(field.getName(), mapper.selectList(
									new QueryWrapper<E>().in(inverseRefColumn, (ArrayList<Serializable>) idList)));
							isExeSqlMap.put(field.getName(), true);

						}

						entityXList = entityXListMap.get(fieldCode);

						List<E> listAll = (List<E>) collectionMap.get(field.getName());

						String columnProperty = columnPropertyMap.get(fieldCode);
						String refColumnProperty = refColumnPropertyMap.get(fieldCode);
						String inverseColumnProperty = inverseColumnPropertyMap.get(fieldCode);
						String inverseRefColumnProperty = inverseRefColumnPropertyMap.get(fieldCode);
						// List<X> entityXList = entityXListMap.get(fieldCode);

						Collection<E> listForThisEntity = new ArrayList<E>();
						if (fieldCollectionType == FieldCollectionType.SET) {
							listForThisEntity = new HashSet<E>();
						}

						for (int k = 0; k < listAll.size(); k++) {
							E entityE = listAll.get(k);
							Field entityField = null;
							Field entity2Field = null;
							Field entityXField = null;
							Field entityXField2 = null;
							try {
								entityField = entity.getClass().getDeclaredField(columnProperty);
								entityField.setAccessible(true);
								Object columnValue = entityField.get(entity);

								entity2Field = entityE.getClass().getDeclaredField(inverseColumnProperty);
								entity2Field.setAccessible(true);
								Object refCoumnValue = entity2Field.get(entityE);

								// table1~table3&&table2~table3
								for (int x = 0; x < entityXList.size(); x++) {
									X entityX = entityXList.get(x);

									entityXField = entityX.getClass()
											.getDeclaredField(JoinTableUtil.getRefColumnProperty(refColumn));
									entityXField.setAccessible(true);
									Object columnValueX = entityXField.get(entityX);

									entityXField2 = entityX.getClass().getDeclaredField(
											JoinTableUtil.getInverseRefColumnProperty(inverseRefColumn));
									entityXField2.setAccessible(true);
									Object refColumnValueX = entityXField2.get(entityX);

									if (columnValueX.toString().equals(columnValue.toString())
											&& refColumnValueX.toString().equals(refCoumnValue.toString())) {
										listForThisEntity.add(entityE);
									}
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}

						}

						return (List<E>) listForThisEntity;
					}

				});

				// 设置代理
				try {
					field.set(entity, listForThisEntityProxy);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static <E> List<E> getListResult(Field field) {
		return null;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public String getFieldCode() {
		return fieldCode;
	}

	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}

	public String getRefColumn() {
		return refColumn;
	}

	public void setRefColumn(String refColumn) {
		this.refColumn = refColumn;
	}

	public BaseMapper<E> getMapperE() {
		return mapperE;
	}

	public void setMapperE(BaseMapper<E> mapperE) {
		this.mapperE = mapperE;
	}

	public FieldCollectionType getFieldCollectionType() {
		return fieldCollectionType;
	}

	public void setFieldCollectionType(FieldCollectionType fieldCollectionType) {
		this.fieldCollectionType = fieldCollectionType;
	}

	public ArrayList<Serializable> getColumnPropertyValueList() {
		return columnPropertyValueList;
	}

	public void setColumnPropertyValueList(ArrayList<Serializable> columnPropertyValueList) {
		this.columnPropertyValueList = columnPropertyValueList;
	}

	public Map<String, String> getRefColumnPropertyMap() {
		return refColumnPropertyMap;
	}

	public void setRefColumnPropertyMap(Map<String, String> refColumnPropertyMap) {
		this.refColumnPropertyMap = refColumnPropertyMap;
	}

	public Map<String, String> getColumnPropertyMap() {
		return columnPropertyMap;
	}

	public void setColumnPropertyMap(Map<String, String> columnPropertyMap) {
		this.columnPropertyMap = columnPropertyMap;
	}

	public FieldCondition<T> getFc() {
		return fc;
	}

	public void setFc(FieldCondition<T> fc) {
		this.fc = fc;
	}

	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public Collection<E> getCollectionAll() {
		return CollectionAll;
	}

	public void setCollectionAll(Collection<E> collectionAll) {
		CollectionAll = collectionAll;
	}

	public Field[] getFields() {
		return fields;
	}

	public void setFields(Field[] fields) {
		this.fields = fields;
	}

	public Map<String, List<X>> getEntityXListMap() {
		return entityXListMap;
	}

	public void setEntityXListMap(Map<String, List<X>> entityXListMap) {
		this.entityXListMap = entityXListMap;
	}

	public String getInverseRefColumn() {
		return inverseRefColumn;
	}

	public void setInverseRefColumn(String inverseRefColumn) {
		this.inverseRefColumn = inverseRefColumn;
	}

	public Map<String, String> getInverseRefColumnPropertyMap() {
		return inverseRefColumnPropertyMap;
	}

	public void setInverseRefColumnPropertyMap(Map<String, String> inverseRefColumnPropertyMap) {
		this.inverseRefColumnPropertyMap = inverseRefColumnPropertyMap;
	}

	public Map<String, String> getInverseColumnPropertyMap() {
		return inverseColumnPropertyMap;
	}

	public void setInverseColumnPropertyMap(Map<String, String> inverseColumnPropertyMap) {
		this.inverseColumnPropertyMap = inverseColumnPropertyMap;
	}

	public BaseMapper<X> getMapperX() {
		return mapperX;
	}

	public void setMapperX(BaseMapper<X> mapperX) {
		this.mapperX = mapperX;
	}
}
