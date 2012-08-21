package com.tmall.asshole.schedule;

import com.tmall.asshole.zkclient.INodeChange;
/**
 * 
 * @author tangjinou (jiuxian.tjo)
 *
 */
public interface IScheduleFgetcPolicy extends INodeChange{
	
	/**
	 * ��ȡ�߳���ҪҪץȡ�����ݷ�Χ de ��ʼindex
	 * 
	 * @return
	 */
	int getStartIndex();

	/**
	 * ��ȡ�߳���ҪҪץȡ�����ݷ�Χ de ����index
	 * 
	 * @return
	 */
	int getEndIndex();
	
	
	/***
	 * һ��ȡ������
	 * 
	 * @return
	 */
	int getRowNum();
	
	/***
	 *  ��ȡ����IP��ַ
	 * @return
	 */
	String getExecuteMachineAlias();
	
	
	int getMaxHashNum();
	

}
