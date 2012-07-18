package com.tmall.asshole.schedule.node.helper;

import com.thoughtworks.xstream.XStream;
import com.tmall.asshole.schedule.node.ProcessTemplate;
import com.tmall.asshole.schedule.node.End;
import com.tmall.asshole.schedule.node.Node;
import com.tmall.asshole.schedule.node.Start;
import com.tmall.asshole.schedule.node.Transition;


public class XStreamHelper {

	public volatile static XStream xstream;

	private XStreamHelper() {
	}

	public static XStream getInstance() {
		// ˫�ؼ�����
		if (xstream == null) {
			synchronized (XStream.class) {
				// �ӳ�ʵ����,��Ҫʱ�Ŵ���
				if (xstream == null)
					xstream = new XStream();
				Class<?>[] classes = new Class<?>[] {Node.class,ProcessTemplate.class,Transition.class,Start.class,End.class};
				xstream.processAnnotations(classes);
				xstream.aliasSystemAttribute(null, "class");
			}
		}
		return xstream;
	}
}
