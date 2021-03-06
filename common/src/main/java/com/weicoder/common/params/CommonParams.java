package com.weicoder.common.params;

import com.weicoder.common.constants.ArrayConstants;
import com.weicoder.common.constants.DateConstants;
import com.weicoder.common.constants.EncryptConstants;
import com.weicoder.common.constants.StringConstants;

/**
 * Common包参数读取类
 * @author WD
 */
public final class CommonParams {
	/** log实现 */
	public final static String		LOG_CLASS		= Params.getString("log.class", "com.weicoder.core.log.Log4j2");
	/** IO缓冲区大小 */
	public final static int			IO_BUFFERSIZE	= Params.getInt("io.buffer", 8192);
	/** IO模式 */
	public final static String		IO_MODE			= Params.getString("io.mode", "nio");
	/** 默认编码 */
	public final static String		ENCODING		= Params.getString("encoding", "UTF-8");
	/** 日期格式 */
	public final static String		DATE_FORMAT		= Params.getString("date.format", DateConstants.FORMAT_Y_M_D_H_M_S);
	/** 转换字节数组算法 */
	public final static String		BYTES			= Params.getString("bytes", "high");
	/** 加密使用的密钥 字符串 */
	public final static String		ENCRYPT_KEY		= Params.getString("encrypt.key", "www.weicoder.com");
	/** 加密使用的算法 */
	public final static String		ENCRYPT_ALGO	= Params.getString("encrypt.algo", EncryptConstants.ALGO_AES);
	/** 加密使用摘要算法 */
	public final static String		ENCRYPT_DIGEST	= Params.getString("encrypt.digest", EncryptConstants.ALGO_SHA_1);
	/** 包名 */
	public final static String		PACKAGES		= Params.getString("packages");
	/** token 验证长度 */
	public final static int			TOKEN_LENGHT	= Params.getInt("token.lenght", 8);
	/** 获得ips过滤组 */
	public final static String[]	IPS				= Params.getStringArray("ips", ArrayConstants.STRING_EMPTY);

	/**
	 * 获得包名
	 * @param name 名称
	 * @return 名称下的包名
	 */
	public static String getPackages(String name) {
		return Params.getString(Params.getKey(StringConstants.EMPTY, name, "packages"), PACKAGES);
	}

	private CommonParams() {}
}
