package com.github.dreamyoung.mprelation;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface IService<T> extends com.baomidou.mybatisplus.extension.service.IService<T> {

	default T getById(Serializable id) {
		return getAutoMapper().mapperEntity(getBaseMapper().selectById(id));
	}

	default List<T> listByIds(Collection<? extends Serializable> idList) {
		return getAutoMapper().mapperEntityList(getBaseMapper().selectBatchIds(idList));
	}

	default List<T> listByMap(Map<String, Object> columnMap) {
		return getAutoMapper()
				.mapperEntityList(getAutoMapper().mapperEntityList(getBaseMapper().selectByMap(columnMap)));
	}

	default T getOne(Wrapper<T> queryWrapper) {
		return getAutoMapper().mapperEntity(getOne(queryWrapper, true));
	}

	default List<T> list(Wrapper<T> queryWrapper) {
		return getAutoMapper().mapperEntityList(getBaseMapper().selectList(queryWrapper));
	}

	default <E extends IPage<T>> E page(E page, Wrapper<T> queryWrapper) {
		return getAutoMapper().mapperEntityPage(getBaseMapper().selectPage(page, queryWrapper));
	}

	AutoMapper getAutoMapper();
}