package com.tmall.asshole.zkclient;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.springframework.beans.factory.annotation.Autowired;

import com.tmall.asshole.zkclient.data.Data;
import com.tmall.asshole.zkclient.data.NodeData;
import com.tmall.asshole.zkclient.data.PersistenceUtil;

/****
 * ZKClient  Dema�߳�
 * 
 *  �����ṹ��ͼ
 *  
 *                   --- path1(10.12.32.12)
 *                                   |
 *                               NodeData
 *   rootPath   
 *       |            ---- path2(122.23.2.32)
 *       |                            |
 *                               NodeData
 *   NodeData 
 *                  ---- path3(121.234.223)
 *                                  |
 *                              NodeData
 *       
 * 
 *   NodeData  --     Data1
 *                            Data2
 *                            Data3
 *                 
 * 
 * 
 * @author tangjinou
 *
 */
public class ZKClient  implements Watcher {
	private static transient Log log = LogFactory.getLog(ZKClient.class);
	
	@Autowired
	private ZKConfig zKConfig;
	
	private ZKClientVersion version = new ZKClientVersion();
	
	private ZKManager zKManager;
	
	//���ʱ����úܳ� ��Ȼ�����÷�������ѹ������   ĿǰΪ10����
	private long defaultRegisterWatchTime = 600000;
	
	@Autowired
	private INodeChange iNodeChange;
	
	private boolean shutdown;
	
	private Thread thread;
	
	
//	private boolean needRegisterWatch;
	
	private Lock lock = new ReentrantLock();
	
	public ZKClient(INodeChange iNodeChange){
        this.iNodeChange = iNodeChange;
	}
	
	public ZKClient(INodeChange iNodeChange, ZKConfig zKConfig){
		this.zKConfig = zKConfig;
        this.iNodeChange = iNodeChange;
	}
	
	public ZKClient() {
	}
	
	
	public void start()  throws Exception{
		lock.lock();
		try{
		shutdown = false;
		if(iNodeChange==null){
			  throw new Exception("iNodeChange can't be null");
		}
		if(zKConfig==null){
			throw new Exception("zKConfig can't be null");
		}
		zKManager = new ZKManager(this, zKConfig);
		zKManager.init();
		//reRegisterWatch();
		//this.setDaemon(true);
		
		thread = new Thread(new Runnable() {
			public void run() {
				while (true) {
				    //  �����ʱ��û�м�������������������Ϣ, ��������һ��
					try {
						if(shutdown){
							log.error("close the protected thread of zkClient");
							break;
						}
						log.info("the protected thread of zkClient is running");
						Thread.sleep(defaultRegisterWatchTime);
						reRegisterWatch();
					} catch (Exception e) {
						log.error("zkClient dema thread:"+e);
					}
				}
			}
		});
		thread.start();
		}catch (Exception e) {
			throw e;
		}finally{
			lock.unlock();
		}
	}
	

	public void process(WatchedEvent event) {
		//��Ҫ����     Disconnected    SyncConnected NodeDataChanged
		List<String> children = null;
		
		try {
			String path = event.getPath();
			
			
			//������server���� ����PathData �Ѿ��� ZKManager���
			if(path==null && event.getState() ==KeeperState.SyncConnected  && event.getType() == Event.EventType.None){
			    log.info("�������������������");
				return;
			}
			
			if(path == null){
				return;
			}
			
			
			//��������Ͽ����� 
			if( event.getState() ==KeeperState.Disconnected ||   event.getState() ==KeeperState.Expired){
			    log.info("�Ͽ��������������");
				return;
			}
			
			if(zKConfig.getRootPath().trim().equals(path)){
				children = zKManager.getZk().getChildren(zKConfig.getRootPath(), false);
				
				//�Լ��ڵ��ϵ�����
				
				NodeData  nodeData = null;
				
				NodeData parentNodeData = null;
			
			   try{		
			      nodeData=PersistenceUtil.deSerializable(zKManager.getZk().getData(zKConfig.getFullPath(), false, null));
			   }catch (Exception e) {
				log.error(e);
			   }
			
			   //���ڵ��ϵ�����
			   try{
				  parentNodeData=PersistenceUtil.deSerializable(zKManager.getZk().getData(zKConfig.getRootPath(), false, null));
			   }catch (NullPointerException e) {
				   // null pointer ˵�����ڵ�����Ϊ��
			   }catch (Exception e) {
				   log.error(e);
			   }
			    iNodeChange.onChange(nodeData,parentNodeData,children,event);
		   }
			
	   }	    
	   catch (Exception e) {
	    	log.error(e);
		} 	
	    finally{
				reRegisterWatch();
	    }
	 }

	
	public void close(){
		try {
			zKManager.close();
		} catch (Exception e) {
			log.error(e.getCause());
		} finally{
			shutdown=true;
		}
	}
	
	
	private void reRegisterWatch(){
		try {
			List<String> children = zKManager.getZk().getChildren(zKConfig.getRootPath(), true);
			log.info("children:"+children.toArray());
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			return;
		} 
		
	}
	
	public ZKClientVersion getVersion() {
		return version;
	}

	public void setVersion(ZKClientVersion version) {
		this.version = version;
	}
	
	public void reSetNodeData(List<Data> dataL)  throws Exception{
		NodeData nodeData = getNodeData();
		nodeData.setDatas(dataL);
		byte[] datas = PersistenceUtil.serializable(nodeData);
		zKManager.getZk().setData(zKConfig.getFullPath(), datas, -1);
	}
	
	private NodeData getNodeData() throws Exception{
		  return PersistenceUtil.deSerializable(zKManager.getZk().getData(zKConfig.getFullPath(), false, null));  
	}
	
	private NodeData getParentNodeData() throws Exception{
		  return PersistenceUtil.deSerializable(zKManager.getZk().getData(zKConfig.getRootPath(), false, null));  
	}
	
	public List<Data> getNodeDatas() throws Exception{
		  return getNodeData().getDatas();  
	}
	
	public List<Data> getParentNodeDatas() throws Exception{
		  return getParentNodeData().getDatas();  
	}


		

}
