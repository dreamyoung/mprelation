package com.github.dreamyoung.mprelation;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

public interface IService<T> extends com.baomidou.mybatisplus.extension.service.IService<T> {
	default T getById(Serializable id) {
		if (isAutoMapperEnabled()) {
			return getAutoMapper().mapperEntity(getBaseMapper().selectById(id));
		} else {
			return getBaseMapper().selectById(id);
		}
	}

	default List<T> listByIds(Collection<? extends Serializable> idList) {
		if (isAutoMapperEnabled()) {
			return getAutoMapper().mapperEntityList(getBaseMapper().selectBatchIds(idList));
		} else {
			return getBaseMapper().selectBatchIds(idList);
		}
	}

	default List<T> listByMap(Map<String, Object> columnMap) {
		if (isAutoMapperEnabled()) {
			return getAutoMapper().mapperEntityList(getBaseMapper().selectByMap(columnMap));
		} else {
			return getBaseMapper().selectByMap(columnMap);
		}
	}

	default T getOne(Wrapper<T> queryWrapper) {
		if (isAutoMapperEnabled()) {
			return getAutoMapper().mapperEntity(getOne(queryWrapper, true));
		} else {
			return getOne(queryWrapper, true);
		}
	}

	default List<T> list(Wrapper<T> queryWrapper) {
		if (isAutoMapperEnabled()) {
			return getAutoMapper().mapperEntityList(getBaseMapper().selectList(queryWrapper));
		} else {
			return getBaseMapper().selectList(queryWrapper);
		}
	}

	default <E extends IPage<T>> E page(E page, Wrapper<T> queryWrapper) {
		if (isAutoMapperEnabled()) {
			return getAutoMapper().mapperEntityPage(getBaseMapper().selectPage(page, queryWrapper));
		} else {
			return getBaseMapper().selectPage(page, queryWrapper);
		}
	}

	default <E extends IPage<T>> E page(E page) {
		if (isAutoMapperEnabled()) {
			return getAutoMapper().mapperEntityPage(getBaseMapper().selectPage(page, Wrappers.emptyWrapper()));
		} else {
			return getBaseMapper().selectPage(page, Wrappers.emptyWrapper());
		}
	}

	AutoMapper getAutoMapper();

	boolean isAutoMapperEnabled();

	<E extends IPage<T>> void initialize(Object t, String... propertyNames);

	void initializeEntity(T t, String... propertyNames);

	void initializeList(List<T> list, String... propertyNames);

	void initializeSet(Set<T> list, String... propertyNames);

	<E extends IPage<T>> void initializePage(E page, String... propertyNames);

}