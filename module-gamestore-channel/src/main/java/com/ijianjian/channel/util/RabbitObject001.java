package com.ijianjian.channel.util;

import java.io.Serializable;

import lombok.Data;

@Data
public class RabbitObject001 implements Serializable {
/**
	 * 
	 */
private static final long serialVersionUID = 1211557176481858338L;
private String uuid;
private Integer channelNumber;
private String clickId;
}
