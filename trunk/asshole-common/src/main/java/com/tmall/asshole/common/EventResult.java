package com.tmall.asshole.common;
/**
 * @author jiuxian.tjo
 * 
 * 
 * 
 */
public class EventResult {
	/***
	 * 
	 * �ж��ǲ���ͬ�����û������첽����
	 * 
	 * �����ͬ����������ִ�н�������Ϣ
	 * 
	 * ������첽��������ִ�н�������Ϣ
	 */
	private boolean synInvoke=false;
	
	
	/**
	 * ֻ��ͬ���ſ��ܻ���errorMsg
	 */
	private String errorMsg;

	public boolean isSynInvoke() {
		return synInvoke;
	}

	public void setSynInvoke(boolean synInvoke) {
		this.synInvoke = synInvoke;
	}
	
	private boolean isSuccess;
	
	/**
	 * ����ͬ�����ã�true��ʾִ�гɹ�
	 * 
	 * �����첽����, true����һ����ʾִ�гɹ�
	 * 
	 * @return
	 */
	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	

}
