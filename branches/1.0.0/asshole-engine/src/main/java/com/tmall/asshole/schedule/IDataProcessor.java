package com.tmall.asshole.schedule;

/**
 * 
 * @author tangjinou (jiuxian.tjo)
 *
 */
public interface IDataProcessor<T,C> {
	
	/**
	 * ������� �����������һ��Ҫ��ʾ����Ϊ�Ѵ�����Ȼ�´λ���ȡ��
	 * 
	 * @param data
	 * @throws SchedulerException
	 */
	public abstract void process(T data,C context) throws Exception;

}
