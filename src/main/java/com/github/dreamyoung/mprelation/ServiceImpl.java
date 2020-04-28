package com.github.dreamyoung.mprelation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;

public class ServiceImpl<M extends BaseMapper<T>, T>
		extends com.baomidou.mybatisplus.extension.service.impl.ServiceImpl<BaseMapper<T>, T> implements IService<T> {
	@Autowired(required = false)
	protected AutoMapper autoMapper;

	private boolean autoMapperEnabled = true;

	public ServiceImpl() {
		Class<?> clazz = this.getClass();
		if (clazz.getAnnotation(DisableAutoMapper.class) != null
				&& clazz.getAnnotation(DisableAutoMapper.class).value() == true) {
			autoMapperEnabled = false;
		}
	}

	@Override
	public AutoMapper getAutoMapper() {

		return this.autoMapper;
	}

	@Override
	public boolean isAutoMapperEnabled() {
		return autoMapperEnabled;
	}

	public void setAutoMapperEnabled(boolean autoMapperEnabled) {
		this.autoMapperEnabled = autoMapperEnabled;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public <E extends IPage<T>> void initialize(Object object, String... propertyNames) {
		if (object != null) {
			if (object.getClass() == List.class || object.getClass() == ArrayList.class) {
				initializeList((List<T>) object, propertyNames);
			} else if (object.getClass() == Set.class || object.getClass() == HashSet.class) {
				initializeSet((Set<T>) object, propertyNames);
			} else if (object instanceof IPage) {
				initializePage((E) object, propertyNames);
			} else {
				initializeEntity((T) object, propertyNames);
			}
		}
	}

	@Transactional(readOnly = true)
	@Override
	public void initializeEntity(T t, String... propertyNames) {
		for (String propertyName : propertyNames) {
			autoMapper.mapperEntity(t, propertyName);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public void initializeList(List<T> list, String... propertyNames) {
		for (String propertyName : propertyNames) {
			autoMapper.mapperEntityList(list, propertyName);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public void initializeSet(Set<T> set, String... propertyNames) {
		for (String propertyName : propertyNames) {
			autoMapper.mapperEntitySet(set, propertyName);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public <E extends IPage<T>> void initializePage(E page, String... propertyNames) {
		for (String propertyName : propertyNames) {
			autoMapper.mapperEntityPage(page, propertyName);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public T getOne(Wrapper<T> queryWrapper) {
		T t = super.getOne(queryWrapper);
		if (isAutoMapperEnabled()) {
			autoMapper.mapperEntity(t);
		}

		return t;
	}

	@Transactional(readOnly = true)
	@Override
	public T getOne(Wrapper<T> queryWrapper, boolean throwEx) {
		if (isAutoMapperEnabled()) {
			if (throwEx) {
				return autoMapper.mapperEntity(baseMapper.selectOne(queryWrapper));
			}
			return autoMapper.mapperEntity(SqlHelper.getObject(log, baseMapper.selectList(queryWrapper)));
		} else {
			if (throwEx) {
				return baseMapper.selectOne(queryWrapper);
			}
			return SqlHelper.getObject(log, baseMapper.selectList(queryWrapper));
		}
	}

	@Transactional(readOnly = true)
	@Override
	public T getById(Serializable id) {
		T t = super.getById(id);
		if (isAutoMapperEnabled()) {
			autoMapper.mapperEntity(t);
		}

		return t;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> list() {
		List<T> list = super.list();
		if (isAutoMapperEnabled()) {
			autoMapper.mapperEntityList(list);
		}
		return list;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> list(Wrapper<T> queryWrapper) {
		List<T> list = super.list(queryWrapper);
		if (isAutoMapperEnabled()) {
			autoMapper.mapperEntityList(list);
		}

		return list;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> listByIds(Collection<? extends Serializable> idList) {
		List<T> list = super.listByIds(idList);
		if (isAutoMapperEnabled()) {
			autoMapper.mapperEntityList(list);
		}

		return list;
	}

	@Transactional(readOnly = true)
	@Override
	public List<T> listByMap(Map<String, Object> columnMap) {
		List<T> list = super.listByMap(columnMap);
		if (isAutoMapperEnabled()) {
			autoMapper.mapperEntityList(list);
		}

		return list;
	}

	@Transactional(readOnly = true)
	@Override
	public <E extends IPage<T>> E page(E page, Wrapper<T> queryWrapper) {
		E ePage = super.page(page, queryWrapper);
		if (isAutoMapperEnabled()) {
			autoMapper.mapperEntityPage(ePage);
		}

		return ePage;
	}

	@Transactional(readOnly = true)
	@Override
	public <E extends IPage<T>> E page(E page) {
		E ePage = super.page(page);
		if (isAutoMapperEnabled()) {
			autoMapper.mapperEntityPage(ePage);
		}

		return ePage;
	}

}