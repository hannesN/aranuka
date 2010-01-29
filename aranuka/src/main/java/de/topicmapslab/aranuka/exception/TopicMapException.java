package de.topicmapslab.aranuka.exception;


public class TopicMapException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4177758602546789715L;

	public TopicMapException(String msg) {
		super(msg);
	}
	
	public TopicMapException(String msg, Throwable e) {
		super(msg, e);
	}
}
