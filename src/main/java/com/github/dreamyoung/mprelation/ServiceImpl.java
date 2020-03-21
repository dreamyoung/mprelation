package com.github.dreamyoung.mprelation;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
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
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<T> list() {
		List<T> list = super.list();
		if (isAutoMapperEnabled()) {
			autoMapper.mapperEntityList(list);
		}
		return list;
	}

	@Override
	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.NOT_SUPPORTED, readOnly = true)
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

	@Override
	public boolean isAutoMapperEnabled() {
		return autoMapperEnabled;
	}

	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public void setAutoMapperEnabled(boolean autoMapperEnabled) {
		this.autoMapperEnabled = autoMapperEnabled;
	}

	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public T getById(Serializable id) {
		T t = super.getById(id);
		if (isAutoMapperEnabled()) {
			autoMapper.mapperEntity(t);
		}

		return t;
	}

	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<T> listByIds(Collection<? extends Serializable> idList) {
		List<T> list = super.listByIds(idList);
		if (isAutoMapperEnabled()) {
			autoMapper.mapperEntityList(list);
		}

		return list;
	}

	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<T> listByMap(Map<String, Object> columnMap) {
		List<T> list = super.listByMap(columnMap);
		if (isAutoMapperEnabled()) {
			autoMapper.mapperEntityList(list);
		}

		return list;
	}

	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public T getOne(Wrapper<T> queryWrapper) {
		T t = super.getOne(queryWrapper);
		if (isAutoMapperEnabled()) {
			autoMapper.mapperEntity(t);
		}

		return t;
	}

	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public List<T> list(Wrapper<T> queryWrapper) {
		List<T> list = super.list(queryWrapper);
		if (isAutoMapperEnabled()) {
			autoMapper.mapperEntityList(list);
		}

		return list;
	}

	@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	public <E extends IPage<T>> E page(E page, Wrapper<T> queryWrapper) {
		E ePage = super.page(page, queryWrapper);
		if (isAutoMapperEnabled()) {
			autoMapper.mapperEntityPage(ePage);
		}

		return ePage;
	}

}