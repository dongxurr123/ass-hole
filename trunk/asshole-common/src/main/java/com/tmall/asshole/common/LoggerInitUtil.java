package com.tmall.asshole.common;

import java.io.File;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class LoggerInitUtil {

    static private final String LOGGER_NAME = "com.tmall.asshole";

    static public final Log LOGGER = LogFactory.getLog(LOGGER_NAME);

    static {
        try { // ������Ϊ�����ʼ��ʧ�ܵ������������ʼ��ʧ��
            initAssholeLogFromBizLog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static private void initAssholeLogFromBizLog() {
        // ʹͨ�Ų��log4j������Ч(Logger, Appender)
        DOMConfigurator.configure(LoggerInitUtil.class.getClassLoader().getResource("asshole-log4j.xml"));
        Logger loggerLog4jImpl = Logger.getLogger(LOGGER_NAME);

        /*
         * �ҵ��ϲ�Ӧ����Root Logger�����õ�FileAppender���Լ�ͨ�Ų����õ�FileAppender��
         * Ŀ����Ϊ����ͨ�Ų����־���ϲ�Ӧ�õ���־�����ͬһ��Ŀ¼��
         */
        FileAppender bizFileAppender = getFileAppender(Logger.getRootLogger());
        if (null == bizFileAppender) {
            LOGGER.warn("�ϲ�ҵ���û����ROOT LOGGER������FileAppender!!!");
            return;
        }
        FileAppender assholeFileAppender = getFileAppender(loggerLog4jImpl);

        // ����ҵ���appender��������ʼ��asshole��appender���������첽Appender�����FileAppender��
        String bizLogDir = new File(bizFileAppender.getFile()).getParent();
        String assholeLogFile = new File(bizLogDir, "asshole.log").getAbsolutePath();
        assholeFileAppender.setFile(assholeLogFile);
        assholeFileAppender.activateOptions(); // ����Ҫ������ԭ����־���ݻᱻ���
        AsyncAppender asynAppender = new AsyncAppender();
        asynAppender.addAppender(assholeFileAppender);
        loggerLog4jImpl.addAppender(asynAppender);
        loggerLog4jImpl.removeAppender(assholeFileAppender);
        LOGGER.warn("�ɹ�Ϊasshole LOGGER���Appender. ���·��:" + assholeLogFile);
    }

    static private FileAppender getFileAppender(Logger logger) {
        FileAppender fileAppender = null;
        for (Enumeration<?> appenders = logger.getAllAppenders();
                (null == fileAppender) && appenders.hasMoreElements();) {
            Appender appender = (Appender) appenders.nextElement();
            if (FileAppender.class.isInstance(appender)) {
                fileAppender = (FileAppender) appender;
            }
        }
        return fileAppender;
    }

}
