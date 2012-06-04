package com.tmall.asshole.engine.node;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;


/***
 * ����ǰ��node �ж� 
 * 
 * @author jiuxian.tjo
 *
 */
@XStreamAlias("pre-node")
public class PreNode {
   
	@XStreamAsAttribute
	public String name;
	
	public PreNode(String name){
		this.name=name;
	}
	
	@Override
	public String toString() {
		return "PreNode[name="+name+"]";
	}

} 
