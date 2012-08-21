package com.tmall.asshole.schedule;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;

import com.tmall.asshole.common.LoggerInitUtil;
/**
 *
 * @author tangjinou (jiuxian.tjo)
 *
 */
public class SchedulerThreadPoolExecutor {

	private final static Log logger = LoggerInitUtil.LOGGER;

	/** *�̳߳�ά���̵߳��������� */
	private int corePoolSize = 20;

	/** *�̳߳�ά���̵߳�������� */
	private int maxPoolSize = 20;

	/** *�̳߳�ά���߳�������Ŀ���ʱ�� */
	private int keepAliveTime = 0;

	private int defaultScheduleInterval = 50;

	private ReentrantLock rejectQueuePauseLock = new ReentrantLock();

	/** * ������� */
	private Queue<Runnable> rejectQueue = new LinkedList<Runnable>();

	/** �����̳߳� */
	private ScheduledExecutorService scheduler;

	private int rejectQueueMaxSize = 1000;

	private ThreadPoolExecutor threadPool;

//	public SchedulerThreadPoolExecutor() {
//		// startScheduledFuture();
//		// initThreadPoolExecutor();
//	}

	public SchedulerThreadPoolExecutor(int corePoolSize, int maxPoolSize, int keepAliveTime) {
		this.corePoolSize = corePoolSize;
		this.maxPoolSize = maxPoolSize;
		this.keepAliveTime = keepAliveTime;
	}

	public void init(String schedulerName) {
		startScheduledFuture(schedulerName);
		initThreadPoolExecutor(schedulerName);
	}

	public void initThreadPoolExecutor(String schedulerName) {
		RejectedExecutionHandler handler = new RejectedExecutionHandler() {
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				rejectQueuePauseLock.lock();
				try {
					if (rejectQueue.size() < rejectQueueMaxSize) {
						rejectQueue.offer(r);
					}
				} finally {
					rejectQueuePauseLock.unlock();
				}
			}
		};
		threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
				new ThreadFactoryImpl(schedulerName), handler);
	}

	public void startScheduledFuture(String schedulerName) {

		// ������ʱ���
		Timer checkTimer = new Timer("reject-" + schedulerName);
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				/** �鿴�Ƿ��д�����������У��򴴽�һ���µ�Thread������ӵ��̳߳��� */
				int activeCount = threadPool.getActiveCount();
				int maxSize = threadPool.getMaximumPoolSize();
				rejectQueuePauseLock.lock();
				try {
					int rQueueSize = rejectQueue.size();
					if (rQueueSize > 0) {
						int polSize = 0;
						int rQPolSize = maxSize - activeCount;
						polSize = (rQueueSize < rQPolSize) ? rQueueSize : rQPolSize;
						for (int i = 0; i < polSize; i++) {
							Runnable task = rejectQueue.poll();
							if (task != null) {
								threadPool.execute(task);
							}
						}
					}
				} catch (Throwable e) {
					logger.error(e.getMessage(), e);
				} finally {
					rejectQueuePauseLock.unlock();
				}
			}
		};
		checkTimer.schedule(task, new java.sql.Date(System.currentTimeMillis() + 5000), defaultScheduleInterval);

	}

	/**
	 * ʵ��java.util.concurrent.ThreadFactory�ӿ�
	 *
	 */
	private static final class ThreadFactoryImpl implements ThreadFactory {
		private String name = "";

		public ThreadFactoryImpl(String name) {
			this.name = name;
		}

		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setDaemon(true);
			thread.setName("TEP Scheduler: [" + name + "]");
			return thread;
		}
	}

	public void execute(Runnable task) {
		this.threadPool.execute(task);
	}

	public boolean isAllThreadFree() {
		boolean result = false;
		rejectQueuePauseLock.lock();
		try {
			if (rejectQueue.size() == 0 && threadPool.getActiveCount() == 0) {
				result = true;
			}
		} finally {
			rejectQueuePauseLock.unlock();
		}
		return result;
	}

	public boolean isThreadPollFull() {
		boolean result = false;
		rejectQueuePauseLock.lock();
		try {
			if (rejectQueue.size() > 0) {
				result = true;
			}
		} finally {
			rejectQueuePauseLock.unlock();
		}
		return result;
	}

	public int getCorePoolSize() {
		return threadPool.getCorePoolSize();
	}

	public void setCorePoolSize(int corePoolSize) {
		threadPool.setCorePoolSize(corePoolSize);
	}

	public int getMaxPoolSize() {
		return threadPool.getMaximumPoolSize();
	}

	public void setMaxPoolSize(int maxPoolSize) {
		threadPool.setMaximumPoolSize(maxPoolSize);
	}

	public int getActiveCount() {
		return threadPool.getActiveCount();
	}

}
