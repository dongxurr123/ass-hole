package com.tmall.asshole.engine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tmall.asshole.common.Event;
import com.tmall.asshole.common.EventContext;


/***
 * 
 * @author tangjinou
 *
 * @param <Event>
 * @param <EventContext>
 */
public class EventEngine implements IEngine<Event,EventContext> {
	private static transient Log logger = LogFactory.getLog(EventEngine.class);
	
	@Autowired
	private IHandlerLocator<Event, EventContext> handlerLocator;
	
	public void setHandlerLocator(IHandlerLocator<Event, EventContext> handlerLocator) {
		this.handlerLocator = handlerLocator;
	}
	
	public void init(){
		 
	}
	

	public boolean fire(Event event, EventContext context) throws Exception {
		boolean flg = false;
		if (event != null) {
			String eventName = event.getClass().getName();
			IHandler handler = handlerLocator.lookup(eventName);
		
			try {
			  // PowerEventEngine ����һ��� EventEngine �� ��Ҫ��AbstractHandler
			  if(!(handler instanceof AbstractHandler)){
				  context.setProcessLogs("�߼���PowerEventEngine ʹ�ò���Ŷ~"+handler.getClass()+"���Ǽ̳�AbstractHandler");
				  return false;
			  }
			    AbstractHandler abstractHandler = (AbstractHandler) handler;
			    boolean before = abstractHandler.beforeHandle(event, context);
			    boolean now = abstractHandler.handle(event, context);
			    boolean after = abstractHandler.afterHandle(event, context);
				flg = before && now && after;
			} catch (Exception e) {
				logger.debug("Event process error name:" + eventName + " content:" + event.toString(), e);
				throw e;
			}
		}
		return flg;
	}

}
