package com.weicoder.nosql.params;

import java.util.List;
import java.util.UUID;

import com.weicoder.common.lang.Lists;
import com.weicoder.common.params.Params;

/**
 * kafka参数
 * @author WD
 */
public final class KafkaParams {
	// kafka使用
	private final static String			PREFIX	= "kafka";											// 前缀
	/** kafka 服务器 */
	public final static String			SERVERS	= Params.getString("kafka.servers");
	/** 要监听的topic */
	public final static List<String>	TOPICS	= Params.getList("kafka.topics", Lists.emptyList());

	/**
	 * 获得kafka服务器
	 * @param name 名字
	 * @return 服务器
	 */
	public static String getServers(String name) {
		return Params.getString(Params.getKey(PREFIX, name, "servers"));
	}

	/**
	 * 获得kafka组id
	 * @param name 名字
	 * @return 组id
	 */
	public static String getGroup(String name) {
		return Params.getString(Params.getKey(PREFIX, name, "group"), UUID.randomUUID().toString());
	}

	/**
	 * 获得kafka获取最大records数量
	 * @param name 名字
	 * @return 数量
	 */
	public static int getMaxPoll(String name) {
		return Params.getInt(Params.getKey(PREFIX, name, "maxPoll"), 1000);
	}

	/**
	 * 获得kafka Session 超时时间
	 * @param name 名字
	 * @return 超时时间
	 */
	public static int getTimeout(String name) {
		return Params.getInt(Params.getKey(PREFIX, name, "timeout"), 30000);
	}

	/**
	 * 获得kafka服务器
	 * @param name 名字
	 * @return 服务器
	 */
	public static int getInterval(String name) {
		return Params.getInt(Params.getKey(PREFIX, name, "interval"), 1000);
	}

	private KafkaParams() {}
}
