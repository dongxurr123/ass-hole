package com.tmall.asshole.schedule;

import java.util.List;

import com.tmall.asshole.common.EventEnv;

/**
 * 
 * @author tangjinou (jiuxian.tjo)
 *
 */
public interface IDataLoader<T> {
	
	/**
	 * ��ȡ��Ҫ����������б�
	 * @param executeMachineAlias TODO
	 * @return
	 * @throws LoaderException
	 */
	public List<T> getDataList(int start, int end,int rownum,
			EventEnv envionmentGroup, String executeMachineAlias) throws Exception;

}
