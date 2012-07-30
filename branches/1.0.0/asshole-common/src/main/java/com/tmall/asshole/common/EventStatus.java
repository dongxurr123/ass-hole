package com.tmall.asshole.common;
/**
 * 
 * @author tangjinou (jiuxian.tjo)
 *
 */
public enum EventStatus {

	EVENT_STATUS_FAILED(-1, "ִ��ʧ��"),
	EVENT_STATUS_UNEXECUTED(0, "δִ��"),
	EVENT_STATUS_SUCCESS(1, "ִ�гɹ�"), 
	EVENT_STATUS_DELETED(2, "��ɾ��"), 
	EVENT_STATUS_PARAMETER_ERROR(3, "�����쳣"), 
	EVENT_STATUS_NODE_MISMATCH(4, "�����ҵ���Ӧ�Ĵ���"), 
	EVENT_STATUS_LOADED(9, "������"), 
	EVENT_STATUS_EXCEPTION(10, "�쳣");

	private int code;

	private String name;

	private EventStatus(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static final EventStatus getEventStatusByCode(Integer code) {

		EventStatus es = null;
		for (EventStatus temp : EventStatus.values())
			if (temp.getCode() == code) {
				es = temp;
				break;
			}
		return es;
	}

}