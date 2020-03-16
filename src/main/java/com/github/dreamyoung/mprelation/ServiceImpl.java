package com.github.dreamyoung.mprelation;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

	public void setAutoMapperEnabled(boolean autoMapperEnabled) {
		this.autoMapperEnabled = autoMapperEnabled;
	}
}