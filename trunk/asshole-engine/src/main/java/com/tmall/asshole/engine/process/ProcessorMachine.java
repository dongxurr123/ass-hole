package com.tmall.asshole.engine.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;

import com.tmall.asshole.common.Event;
import com.tmall.asshole.common.EventConstant;
import com.tmall.asshole.common.EventContext;
import com.tmall.asshole.common.EventResult;
import com.tmall.asshole.common.EventStatus;
import com.tmall.asshole.common.LoggerInitUtil;
import com.tmall.asshole.config.MachineConfig;
import com.tmall.asshole.engine.http.JettyServer;
import com.tmall.asshole.schedule.IDataProcessorCallBack;
import com.tmall.asshole.schedule.monitor.ScheduleMonitor;
import com.tmall.asshole.schedule.node.Node;
import com.tmall.asshole.schedule.node.Transition;
import com.tmall.asshole.schedule.node.helper.ProcessTemplateHelper;
import com.tmall.asshole.util.BeanCopyUtil;
import com.tmall.asshole.util.Initialize;
import com.tmall.asshole.zkclient.INodeChange;
import com.tmall.asshole.zkclient.ZKClient;
import com.tmall.asshole.zkclient.ZKConfig;
/**
 *
 * @author tangjinou (jiuxian.tjo)
 *
 */
public class ProcessorMachine implements IDataProcessorCallBack<Event,EventContext>,Initialize{

	private final static Log logger = LoggerInitUtil.LOGGER;

	private ScriptEngineManager factory;

	private ScriptEngine scriptEngine;

	private MachineConfig machineConfig;

	private ZKClient zkClient;
	

	private  ScheduleMonitor scheduleMonitor;


	public MachineConfig getMachineConfig() {
		return machineConfig;
	}


	public ScheduleMonitor getScheduleMonitor() {
		return scheduleMonitor;
	}


	public void setMachineConfig(MachineConfig machineConfig) {
		this.machineConfig = machineConfig;
	}



	public ProcessorMachine() {
		 factory = new ScriptEngineManager();
		 scriptEngine = factory.getEngineByName("javascript");
	}

	private List<EventSchedulerProcessor> eventSchedulerProcessors=new ArrayList<EventSchedulerProcessor>();

	public List<EventSchedulerProcessor> getEventSchedulerProcessors() {
		return eventSchedulerProcessors;
	}


	/***
	 * ��������ʵ��������ʵ��ID�������
	 *
	 * @param event
	 * @param processName
	 * @throws Exception
	 */
	public EventResult createEventProcess(Event event,String processName) throws Exception{
		return createEventProcess(event,processName,ProcessTemplateHelper.createProcessInstanceID());
	}


	/***
	 * ��������ʵ��������ʵ��ID������Ҫ���� ,�罻�׶�����֮�����ɵ�ΨһprocessInstanceID
	 *
	 * ֧��ͬ�����ú��첽����
	 *
	 * @param event
	 * @param processName
	 * @throws Exception
	 */
	public EventResult createEventProcess(Event event,String processName,Long processInstanceID) throws Exception{
		//�������ͷ��ҵ��ڵ�
		List<Node> nodes = ProcessTemplateHelper.find(processName, event.getClass());
		if(nodes.size()==0){
					throw new NullPointerException("can't find the event, type="+event.getClass()+" in the processs, name="+processName);
		}
	    Node n = nodes.get(0);
		event.setProcessName(processName);
		event.setProcessInstanceId(processInstanceID);
		event.setTypeClass(event.getClass().getName());
		event.setCurrentName(n.getName());
		event.setEnv(machineConfig.getEnv());
		event.setSynInvoke(n.getSyn());

		return invokeNextNode(event, n);
	}

	/***
	 * �������̵���ת
	 *
	 * ֧��ͬ�����ú��첽����
	 *
	 * @param event
	 * @param processName
	 * @param nodeName
	 * @param processInstanceID
	 * @throws Exception
	 */
	public EventResult contineEventProcess(Event event,String processName,String nodeName,Long processInstanceID) throws Exception{
		Node n = ProcessTemplateHelper.find(processName, event.getClass(),nodeName);
		if(n==null){
			throw new NullPointerException("can't find the event, type="+event.getClass()+" in the processs, name="+processName);
		}
		if(processInstanceID==null || processInstanceID==0){
			throw new NullPointerException("processInstanceID can't be null or 0");
		}

		event.setProcessName(processName);
		event.setProcessInstanceId(processInstanceID);
		event.setTypeClass(event.getClass().getName());
		event.setCurrentName(n.getName());
		event.setEnv(machineConfig.getEnv());
		
		//copy ȫ�ֵ�session
		EventSchedulerProcessor eventSchedulerProcessor = getEventSchedulerProcessor(Integer.parseInt(n.getProcessorNumber()));
		Event lastNodeEvent = eventSchedulerProcessor.getEventDAO().queryLastNodeEvent(processInstanceID, n.getName());

        //�����һ���ڵ�ʧ��   ���Ƿ����  �ϲ�ҵ��֤  
        //
        if(lastNodeEvent!=null){
		  event.setSessionContext(lastNodeEvent.getSessionContext());
		}
        
		//��Ϊ���������ã��ֶ��ڵ��Զ�ת���Զ��ڵ�
		n.setType(Node.NODE_AUTO_TYPE);
		
		return invokeNextNode(event, n);

	}

    /***
     * ͬ�����첽�ĵ���
     *
     * @param event
     * @param n
     * @return
     * @throws Exception
     */
	private EventResult invokeNextNode(Event event, Node n) throws Exception {
		//���ж��Ƿ�Ϊ�Զ��ڵ㣬��������Զ��ڵ� ������ִ��
		if(n.getType().trim().equals(Node.NODE_MANUAL_TYPE)){
			logger.info("procss finished, beacause of node type is manu, name="+event.getProcessName()+",id="+event.getProcessInstanceId());
			EventResult result=new EventResult();
			result.setSuccess(true);
			return result;
		}
		
		if(n.getSyn()==true){
			//ͬ������
			//ֱ�ӵ���
		    return synExecute(event,n);

		}else{

			event.setSynInvoke(false);
			//�첽����
		    EventSchedulerProcessor eventSchedulerProcessor = getEventSchedulerProcessor(Integer.parseInt(n.getProcessorNumber()));
			setHashNum(event, n, eventSchedulerProcessor);
			logger.info("procss start, name="+event.getProcessName()+",id="+event.getProcessInstanceId());
			eventSchedulerProcessor.addData(event);
			EventResult result=new EventResult();
			result.setSuccess(true);
			return result;
		}
	}
	
	/**
	 * ͬ��ִ��
	 * @throws Exception 
	 */
	public EventResult synExecute(Event event, Node n) throws Exception{
		
		EventResult result=new EventResult();
		result.setSynInvoke(true);
		
		EventSchedulerProcessor eventSchedulerProcessor = getEventSchedulerProcessor(Integer.parseInt(n.getProcessorNumber()));
		//setHashNum(event, n, eventSchedulerProcessor);
	    logger.info("procss start, name="+event.getProcessName()+",id="+event.getProcessInstanceId()+" syn=true");
	    event.setHashNum(0);
	    event.setSynInvoke(true);
	    
	    EventContext context = eventSchedulerProcessor.create(event);
	    
	    try{
	        event.setExecuteMachineIp(machineConfig.getLocalIPAddress());
	        eventSchedulerProcessor.addData(event);
	        eventSchedulerProcessor.process(event, context);
	        //ͬ������Ҳ��Ҫ��¼IP
	        
	        while(event.getStatus().equals(EventStatus.EVENT_STATUS_FAILED.getCode()) 
	        		&& event.getExecCount() <= Integer.parseInt(n.getRetry()) ){
	        	event.setStatus(EventStatus.EVENT_STATUS_UNEXECUTED.getCode());//���Ϊδִ��
	        	event.setContext(null);
	        	event.setSessionContext(null);//���session
	        	eventSchedulerProcessor.addData(event);
	        	eventSchedulerProcessor.process(event, context);
	        }
	        
	        result.setSuccess(event.getStatus().intValue()==EventConstant.EVENT_STATUS_SUCCESS?true:false);
	        result.setErrorMsg(event.getMemo());
	   
	    }catch (Exception e) {
	    	result.setSuccess(false);
	    	result.setErrorMsg(e.getMessage());
	    	//throw e;
		}

	    triggerNodeTransitions(event, context, n);
	    return result;
	}
	
	
	private void setHashNum(Event event, Node n,
			EventSchedulerProcessor eventSchedulerProcessor) {
		//����趨��hashֵ�򲻻��޸�
		if(!StringUtils.isBlank(n.getHashNum())){
		   event.setHashNum(Integer.parseInt(n.getHashNum()));
		}else{
		   // 0 - MAXHASHNUM
		   event.setHashNum(RandomUtils.nextInt(eventSchedulerProcessor.getSchedule().getScheduleFgetcPolicy().getMaxHashNum()));
		}
	}



	public void callback(Event event,EventContext context) throws Exception {
		
		Node n = ProcessTemplateHelper.find(event.getProcessName(), event.getCurrentName());
		
		//��ִ��ʧ�� ����С�����Դ��� 
		if(event.getStatus().equals(EventStatus.EVENT_STATUS_FAILED.getCode()) 
				&& event.getExecCount() <= Integer.parseInt(n.getRetry())){
			callback(event, context, n, context.getMap(),true);
		}
		
		if(event.getStatus()!=EventConstant.EVENT_STATUS_SUCCESS){
			logger.error("due to node "+event.getCurrentName()+" execute not success, procss "+event.getProcessName()+" finished, process id="+event.getProcessInstanceId()+",last node name="+event.getCurrentName());
		    return;
		}

		if(n.transitions==null || n.transitions.size()==0){
			logger.info("no transitions ,procss finished, name="+event.getProcessName()+",id="+event.getProcessInstanceId()+",last node name="+event.getCurrentName());
			return;
		}
		triggerNodeTransitions(event, context, n);
		return;
	}


	private void triggerNodeTransitions(Event event, EventContext context,
			Node n) throws Exception, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		
		if(n.transitions==null){
			logger.info("procss finished, no transitions, name="+event.getProcessName()+",id="+event.getProcessInstanceId()+",last node name="+event.getCurrentName());
			return;
		}
		
		for (Transition transition : n.transitions) {
			if(trigger(context,transition.exp)){

				if(StringUtils.isBlank( transition.to) || transition.to.trim().toLowerCase().equals("end")){
					logger.info("procss finished, name="+event.getProcessName()+",id="+event.getProcessInstanceId()+",last node name="+event.getCurrentName());
					continue;
				}

				Node nextN = ProcessTemplateHelper.find(event.getProcessName(), transition.to);
                
				//������˹��ڵ� ����ʱ������ִ��
				if(nextN.getType().trim().equals(Node.NODE_MANUAL_TYPE)){
					continue;
				}
				
				if(nextN.isForeach()){
					if(context.getDataList()!=null){
						  List<Map<String, Object>> dataList = context.getDataList();
						  for (Map<String, Object> map : dataList) {
							  callback(event, context, nextN, map,false);
						  }
						  continue;
					}
				}
				else{
		           callback(event, context, nextN, context.getMap(),false);
		           continue;
				}
			}
		}
	}

  
	

	private void callback(Event event, EventContext context, Node nextN,Map<String, Object> map, boolean isRetry)
			throws Exception, ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		  Class<?> eventName = Class.forName(nextN.getClassname());
		  Event newEvent = (Event)eventName.newInstance();
		  //Map<String, Object> map = context.getMap();
		  BeanCopyUtil.copy(newEvent, map);
		//�ؼ�������Ҫcopy
		  newEvent.setProcessName(event.getProcessName());
		  newEvent.setCurrentName(nextN.getName());
		  newEvent.setProcessInstanceId(event.getProcessInstanceId());
		  newEvent.setProcessorNumber(Integer.parseInt(nextN.getProcessorNumber()));
		  newEvent.setEnv(machineConfig.getEnv());
		  newEvent.setTypeClass(nextN.getClassname());
		  newEvent.setSynInvoke(nextN.getSyn());
		  newEvent.setType(nextN.getType());
		  if(isRetry){//��������� ��Ҫ��¼��ǰ�Ѿ�ִ���˼��� 
			 newEvent.setExecCount(event.getExecCount());
		  }
		  //copy ȫ�ֵ�session context
		  newEvent.setSessionContext(event.getSessionContext());
		  
		  logger.info("procss excute, name="+event.getProcessName()+",id="+event.getProcessInstanceId()+",current node name="+event.getCurrentName());
		  invokeNextNode(newEvent,nextN);
	}


	private EventSchedulerProcessor getEventSchedulerProcessor(int processNumber)  throws Exception{
		for (EventSchedulerProcessor processor : eventSchedulerProcessors) {
			if(processor.getProcessorNumber()==processNumber){
				  return processor;
			}
		}
		logger.error("can't find the processor, processorNumber="+processNumber);
		throw new NullPointerException("can't find the processorr, processorNumber="+processNumber);
	}

	private boolean trigger(EventContext context,String exec) {
		for (Entry<String, Object> entry : context.getMap().entrySet()) {
			scriptEngine.put(entry.getKey(), entry.getValue());
		}
		try {
			if(StringUtils.isBlank(exec)){
				return true;
			}
			Boolean result = (Boolean) scriptEngine.eval(exec.replace("$", ""));


			if(!result){
				logger.info("exectue '"+exec+"' fail, so can't move to next node ");
			}

		    return result;
		} catch (ScriptException e) {
			logger.error("execute the script error :"+ exec +"  " +e.getStackTrace());
		    return false;
		}
	}

	public void init() throws Exception {
		if(machineConfig==null){
			throw new NullArgumentException("can' t find the machineConfig in "+this.getClass()+", could not find the engineConfig, pls check if the engineConfig is setted in spring config file;");
		}

		if(machineConfig.getProcessTemplateFolders()==null || machineConfig.getProcessTemplateFolders().size()==0){
			throw new NullArgumentException("process template folder in machineconfig can't be empty!");
		}


		//��������ģ��
		ProcessTemplateHelper.deploy(machineConfig.getProcessTemplateFolders());

		List<INodeChange> iNodeChanges = new ArrayList<INodeChange>();

		for (EventSchedulerProcessor processor : eventSchedulerProcessors) {
			iNodeChanges.add(processor.getSchedule().getScheduleFgetcPolicy());
		}

		if(!machineConfig.getStartZK()){
			logger.error("no need to start zookeeper client, pls check the var of startZK  in EngineConfig");
			return;
		}

		scheduleMonitor = new ScheduleMonitor(this);

		scheduleMonitor.start();


		ZKConfig zkConfig =new ZKConfig(machineConfig.getUsePermissions(), machineConfig.getUsername(), machineConfig.getPassword(), machineConfig.getZkConnectString(), machineConfig.getZkSessionTimeout(), machineConfig.getRootPath(), machineConfig.getLocalIPAddress());


		logger.info("start the the  zookeeper client");
		zkClient = new ZKClient(iNodeChanges,zkConfig);
		zkClient.start();

		new JettyServer(this).start();


	}
	public ZKClient getZkClient() {
		return zkClient;
	}





}
