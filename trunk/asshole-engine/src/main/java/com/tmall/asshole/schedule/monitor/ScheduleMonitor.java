package com.tmall.asshole.schedule.monitor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tmall.asshole.common.IEventDAO;
import com.tmall.asshole.common.IScheduleMonitorWatcher;
import com.tmall.asshole.common.ScheduleMonitorData;
/***
 * ���ʹ��
 * 
 * @author jiuxian.tjo
 *
 */
public class ScheduleMonitor{
	
	@Autowired
	private IEventDAO eventDAO;
	
	private Thread monitor;
	
	private List<IScheduleMonitorWatcher> watchers=new ArrayList<IScheduleMonitorWatcher>();
	
	
	private static transient Log logger = LogFactory.getLog(ScheduleMonitor.class);
    	

	public void start() throws Exception {
		monitor = new Thread(){
			public void run() {
				try {
				   //һ���ӹ���һ��
				   Thread.sleep(60000);
				   logger.info("schedule monitor is working");
				   ScheduleMonitorData data=new ScheduleMonitorData(eventDAO.queryCountOfUnExecuteEvent());
				   for(IScheduleMonitorWatcher watch:watchers){
					   logger.info("schedule monitor invoke watcher ,"+watch.getClass().getName());
					   watch.onChange(data);
				   }
				} catch (Exception e) {
					logger.error("schedule monitor meet error", e);
				}
				
			}
		};
		monitor.start();
	}

}
