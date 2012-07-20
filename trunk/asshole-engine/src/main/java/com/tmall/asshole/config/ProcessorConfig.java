package com.tmall.asshole.config;

/**
 * 
 * @author tangjinou (jiuxian.tjo)
 *
 */
public class ProcessorConfig {
    /***
     *  �����㷨����
     */
	private String algorithmType;
	
	/***
	 *  ��������
	 */
	private String envionmentGroup;
	
	/***
	 *  taskname
	 */
	private String taskName;
	/**
	 *  groupingName
	 */
	private String groupingName;
	
	/***
	 *  ������ѯʱ��
	 */
	private int schedulingPollingTime;
	
	/***
	 *  ���hashֵ
	 */
	private int maxHashNum;
	
	private int processorNumber;
	
	/** *�̳߳�ά���̵߳��������� */
	private int corePoolSize = 20;

	/** *�̳߳�ά���̵߳�������� */
	private int maxPoolSize = 20;

	/** *�̳߳�ά���߳�������Ŀ���ʱ�� */
	private int keepAliveTime = 0;
	
	
	
	

	public String getEnvionmentGroup() {
		return envionmentGroup;
	}

	public void setEnvionmentGroup(String envionmentGroup) {
		this.envionmentGroup = envionmentGroup;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getGroupingName() {
		return groupingName;
	}

	public void setGroupingName(String groupingName) {
		this.groupingName = groupingName;
	}

	public int getSchedulingPollingTime() {
		return schedulingPollingTime;
	}

	public void setSchedulingPollingTime(int schedulingPollingTime) {
		this.schedulingPollingTime = schedulingPollingTime;
	}

	public String getAlgorithmType() {
		return algorithmType;
	}

	public void setAlgorithmType(String algorithmType) {
		this.algorithmType = algorithmType;
	}

	public int getMaxHashNum() {
		return maxHashNum;
	}

	public void setMaxHashNum(int maxHashNum) {
		this.maxHashNum = maxHashNum;
	}

	public int getProcessorNumber() {
		return processorNumber;
	}

	public void setProcessorNumber(int processorNumber) {
		this.processorNumber = processorNumber;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(int keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}
	
}
