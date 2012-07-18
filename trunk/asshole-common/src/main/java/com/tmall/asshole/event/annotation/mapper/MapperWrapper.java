package com.tmall.asshole.event.annotation.mapper;

import java.util.Map;

/**
 * ע�����Ļ��࣬����װ��ģʽʵ�֣�������ע��Ľӿڰ�װ��������Ϊһ��chain
 * @author hemaodong
 * @date 2012-5-28 ����03:54:22
 *
 */
public abstract class MapperWrapper implements Mapper {

	private Mapper wrapped;

	public MapperWrapper(Mapper mapper) {
		this.wrapped = mapper;
	}

	public Mapper lookupMapperOfType(Class<?> type) {
		return type.isAssignableFrom(getClass()) ? this : wrapped.lookupMapperOfType(type);
	}

	public <T> Map<String, Object> processAnnotation(T t, Map<String, Object> fieldMap) throws Exception{
		return wrapped.processAnnotation(t, fieldMap);
	}
}
