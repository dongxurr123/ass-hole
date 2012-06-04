package com.tmall.asshole.engine.node;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("process")
public class Definition {

	@XStreamAsAttribute
	public String name;
	
	public Start start;
	
	@XStreamImplicit
	public List<Node> nodes;
	
	@XStreamImplicit
	public List<End> ends;

	public Definition(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Definition [name=" + name + ", start=" + start + ", nodes="
				+ nodes + ", ends=" + ends + "]";
	}
	
}
