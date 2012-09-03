package com.tmall.asshole.common;
/**
 * 
 * @author tangjinou (jiuxian.tjo)
 *
 */
import java.util.List;


/****
 * 
 * @author tangjinou
 *
 */
public interface IEventDAO {
	/**
     * @param dao
     * @return 
     */
    public Long insertEventDO(Event dao);
    
    public List<Event> queryEvent(int start, int end, int count, int env, int processorNumber);
    
    public Integer updateEventDO(Event dao);
    
    public Integer batchChangeEventStatus(int from, int to);    
	
	public Event queryEventByPrimaryKey(Long id,Integer hashNum);
	
	public Long queryCountOfUnExecuteEvent();
    
	/**
     * ����instanceId���ص�ǰ��������   ������
     */
    public List<Event> queryEventList(int processInstanceId);
    
    /**
     * ���ݵ�ǰ�ڵ� ������������һ���ڵ�
     */
   // public Event queryLastNodeEvent(int processInstanceId,String currentNode);

}
