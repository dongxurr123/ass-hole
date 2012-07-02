package com.tmall.asshole.common;

import java.util.Date;

/****
 *
 * @author tangjinou
 *
 */
public class Event {


	private Long id;

	private EventStatus status;

	private EventEnv env;
	
	/**
	 * 执行log
	 */
	
	private String processLogs; //
    
	/***
	 * 备注暂时不用
	 */
	private String memo;

	private String typeClass;//

	private String context;

	private String executeMachineIp;//

	private String executeMachineHashRange; //

	private Date gmtCreate; //

	private Date gmtModified;//

	private String operator;

	private int hashNum;//

	private int execCount;//
	
	private Long processInstanceId;//
	

	private Integer source;
	
	private String processName;
	
	private String currentName;
	
	/**
	 *  执行弄1�7�1�7�时闄1�7�1�7主要给活动使甄1�7�1�7
	 */
	private Date  execStartTime;//
	
	/***
	 *  是否延期支持 对于活动霄1�7�1�7�延朄1�7�1�7
	 */
	private boolean isDelayExec;//
	
    private int processorNumber;
	

    public Long getProcessInstanceId() {
    	return processInstanceId;
    }
    
    public void setProcessInstanceId(Long processInstanceId) {
    	this.processInstanceId = processInstanceId;
    }
    
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	

	public String getCurrentName() {
		return currentName;
	}

	public void setCurrentName(String current_name) {
		this.currentName = current_name;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String process_name) {
		this.processName = process_name;
	}

	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getStatus() {
		return status.getCode();
	}

//	public EventStatus getEventStatus() {
//		return status;
//	}
	/**
	 * 重载 用于ibatis数据库更斄1�7�1�7
	 * @param status
	 */
	public void setStatus(Integer status) {
		
		this.status = EventStatus.getEventStatusByCode(status);
	}
	/**
	 * 重载
	 * @param status
	 */
	public void setStatus(EventStatus status)	{
		this.status = status;
		
	}

	public Integer getEnv() {
		return env.getCode();
	}

	public void setEnv(EventEnv env) {
		this.env = env;
	}
	
	/**
	 * 重载 用于ibatis数据库更斄1�7�1�7
	 * @param env
	 */
	public void setEnv(Integer env) {
		this.env = EventEnv.getEventEnvByCode(env);
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	
	public String getProcessLogs() {
		return processLogs;
	}

	public void setProcessLogs(String processLogs) {
		this.processLogs = processLogs;
	}

	public String getTypeClass() {
		return typeClass;
	}

	public void setTypeClass(String typeClass) {
		this.typeClass = typeClass;
	}

	public String getExecuteMachineIp() {
		return executeMachineIp;
	}

	public void setExecuteMachineIp(String executeMachineIp) {
		this.executeMachineIp = executeMachineIp;
	}

	public String getExecuteMachineHashRange() {
		return executeMachineHashRange;
	}

	public void setExecuteMachineHashRange(String executeMachineHashRange) {
		this.executeMachineHashRange = executeMachineHashRange;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public int getHashNum() {
		return hashNum;
	}

	public void setHashNum(int hashNum) {
		this.hashNum = hashNum;
	}

	public int getExecCount() {
		return execCount;
	}

	public void setExecCount(int execCount) {
		this.execCount = execCount;
	}

	public Date getExecStartTime() {
		return execStartTime;
	}

	public void setExecStartTime(Date execStartTime) {
		this.execStartTime = execStartTime;
	}

	public boolean isDelayExec() {
		return isDelayExec;
	}

	public void setDelayExec(boolean isDelayExec) {
		this.isDelayExec = isDelayExec;
	}

	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	

	public int getProcessorNumber() {
		return processorNumber;
	}

	public void setProcessorNumber(int processorNumber) {
		this.processorNumber = processorNumber;
	}
	
	

	

}
