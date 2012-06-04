package com.tmall.asshole.engine.model;



import java.util.HashMap;
import java.util.Map;

public class Process {

	public String name;

	public String first;

	
	public Map<String, State> states;

	public Process(String name) {
		this.name = name;
		states = new HashMap<String, State>();
	}

	public State createState(String name) {
		State state = new State(name);
		states.put(name, state);
		return state;
	}
	
	
}
