package com.tmall.asshole.common.impl;
/**
 * 
 * @author tangjinou (jiuxian.tjo)
 *
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tmall.asshole.common.Event;
import com.tmall.asshole.common.IEventDAO;

public class EventDAO extends SqlMapClientDaoSupport implements IEventDAO {

	/**
	 */
	public Long insertEventDO(Event eventDo){
		Object id = getSqlMapClientTemplate().insert("Event.insert", eventDo);
	        return (Long) id;
	}
    /**
     */
	public Integer updateEventDO(Event eventDo) {
		Object id = getSqlMapClientTemplate().update("Event.update", eventDo);
        return  (Integer) id;
	}
	
	/**
	 */
	public List<Event> queryEvent(int start, int end, int count, int env,int process_number) {
		
		Map<String,Integer> param = new HashMap<String,Integer>();
    	param.put("start", start);
    	param.put("end", end); 
    	param.put("count",count);
    	param.put("env",env);
    	param.put("processorNumber",process_number);
    	List<Event> eventList =  getSqlMapClientTemplate().queryForList("Event.eventQuery", param);
		return eventList;
	}

	/**
	 */
	public Integer batchChangeEventStatus(int from, int to) {
		Map<String,Integer> param = new HashMap<String,Integer>();
		param.put("from", from);
		param.put("to", to);
		Integer count = getSqlMapClientTemplate().update("Event.batchChangeEventStatus", param);
		return count;
	}

	
	/**
	 */
	public Event queryEventByPrimaryKey(Long id,Integer hash_num){
		Map param = new HashMap<String,Integer>();
		param.put("id", id);
		param.put("hash_num", hash_num);
		
		Event event = (Event) getSqlMapClientTemplate(). queryForObject("Event.findByPrimaryKey",param);
		return event;
	}
	@Override
	public Long queryCountOfUnExecuteEvent() {
		Long count = (Long)getSqlMapClientTemplate().queryForObject("Event.countOfUnExecuteEvent");
		return count;
	}
	/**
	 * ����ͬһ��processInstanceId��Ӧ����������  ����ʱ����������
	 */
	@Override
	public List<Event> queryEventList(Long processInstanceId) {
		Map param = new HashMap<String,Integer>();
		param.put("processInstanceId", processInstanceId);
		List<Event> allEvents = getSqlMapClientTemplate().queryForList("Event.queryEventListByProcIncId",param);
		
		return allEvents;
	}
	
	@Override
	public Event queryLastNodeEvent(Long processInstanceId, String currentNode) {
		List<Event> queryEvents = queryEventList(processInstanceId);
		if(queryEvents.isEmpty()){//û�з��Ͻڵ� ����Ϊnull
			return null;
		}
		int currentNodeIndex = 0;
		for(int i =0; i< queryEvents.size();i++){
			if(!queryEvents.get(i).getCurrentName().equals(currentNode)){
				currentNodeIndex++;//��ƥ�� ������
			}
		}
		if((currentNodeIndex+1)< queryEvents.size()){//���������һ���ڵ� ���ظýڵ�
			return queryEvents.get(currentNodeIndex+1);
		}else{//�ýڵ��Ѿ��ǵ�һ���ڵ� ���ؽڵ㱾��  �˴�����ʱ��Ҫ�ж�
			return queryEvents.get(currentNodeIndex);
		}
		
		
		
	}



}
