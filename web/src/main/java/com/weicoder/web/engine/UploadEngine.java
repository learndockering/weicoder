package com.weicoder.web.engine;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import com.weicoder.common.codec.Hex;
import com.weicoder.common.constants.ArrayConstants;
import com.weicoder.common.constants.StringConstants;
import com.weicoder.common.crypto.Digest;
import com.weicoder.common.io.FileUtil;
import com.weicoder.common.lang.Conversion;
import com.weicoder.common.util.DateUtil;
import com.weicoder.common.util.EmptyUtil;
import com.weicoder.common.util.ImageUtil;
import com.weicoder.common.util.StringUtil;
import com.weicoder.web.params.UploadParams;

/**
 * 上传Action
 * @author WD
 * 
 */
public final class UploadEngine {
	/**
	 * 上传文件
	 * @param request
	 * @param uploads 上传文件
	 * @param uploadsFileName 上传文件名
	 * @param path 上传路径
	 * @return 文件路径
	 */
	public static String[] uploads(HttpServletRequest request, File[] uploads, String[] uploadsFileName, String path) {
		// 如果没有文件跳出
		if (EmptyUtil.isEmpty(uploads)) {
			return ArrayConstants.STRING_EMPTY;
		}
		// 获得文件个数
		int size = uploads.length;
		// 获得路径
		String[] names = new String[size];
		// 循环文件名
		for (int i = 0; i < size; i++) {
			// 获得路径
			names[i] = upload(request, uploads[i], uploadsFileName[i], path);
		}
		// 返回路径
		return names;
	}

	/**
	 * 上传文件
	 * @param request
	 * @param file 上传文件
	 * @param fileName 文件名
	 * @param path 上传路径
	 * @return 文件路径
	 */
	public static String upload(HttpServletRequest request, File file, String fileName, String path) {
		return upload(request, FileUtil.read(file), fileName, path);
	}

	/**
	 * 上传文件
	 * @param request
	 * @param file 上传文件
	 * @param fileName 文件名
	 * @param path 上传路径
	 * @return 文件路径
	 */
	public static String upload(HttpServletRequest request, byte[] file, String fileName, String path) {
		// 如果没有文件跳出
		if (EmptyUtil.isEmpty(file)) {
			return null;
		}
		// 格式化路径
		path = Conversion.toString(path);
		if (path.lastIndexOf(StringConstants.BACKSLASH) > 0) {
			path += StringConstants.BACKSLASH;
		}
		// 是否使用文件目录分隔
		String date = DateUtil.getDate(UploadParams.DIR);
		if (!EmptyUtil.isEmpty(date)) {
			path += date;
		}
		// 获得上次路径
		String name = UploadParams.PATH + getFileName(fileName, path);
		// 上传文件
		// String fn = UploadParams.PATH + name;
		// 文件是否存在
		if (FileUtil.exists(name)) {
			// 获得上传文件MD5
			String md5 = Hex.encode(Digest.md5(file));
			// 文件存在始终循环
			while (FileUtil.exists(name)) {
				// 验证MD5
				if (Hex.encode(Digest.md5(FileUtil.read(name))).equals(md5)) {
					// 相同文件不处理跳出循环
					break;
				} else {
					// 文件不同 获得新文件名
					name = UploadParams.PATH + getFileName((DateUtil.getTime() % 100) + StringConstants.UNDERLINE + fileName, path);
				}
			}
		}
		// 文件不存在写文件
		if (!FileUtil.exists(name)) {
			FileUtil.write(name, file);
			// 是否开启图片压缩 并且是图片
			if (UploadParams.IMAGE_COMPRESS_POWER) {
				// 获得文件
				File img = FileUtil.getFile(name);
				// 是图片
				if (ImageUtil.isImage(img)) {
					// 循环压缩图片
					for (String compress : UploadParams.IMAGE_COMPRESS_NAMES) {
						// 获取压缩文件保存文件名
						String f = StringUtil.subStringLastEnd(name, StringConstants.BACKSLASH) + StringConstants.BACKSLASH + compress + StringConstants.BACKSLASH
								+ StringUtil.subStringLast(name, StringConstants.BACKSLASH);
						// 写入压缩图片
						ImageUtil.compress(img, FileUtil.getOutputStream(f), UploadParams.getWidth(compress), UploadParams.getHeight(compress), true);
					}
				}
			}
		}
		// 返回路径
		return name;
	}

	/**
	 * 获得文件名
	 * @param fileName 文件名
	 * @param path 上传路径
	 * @return 文件名
	 */
	private static String getFileName(String fileName, String path) {
		// 上传路径
		StringBuilder name = new StringBuilder();
		name.append(EmptyUtil.isEmpty(path) ? StringConstants.EMPTY : path + StringConstants.BACKSLASH);
		name.append(Digest.absolute(fileName, 20));
		// 是否处理后缀
		if (UploadParams.SUFFIX) {
			// 获得后缀
			String suffix = StringUtil.subStringLast(fileName, StringConstants.POINT);
			// 判断后缀不在处理列表中
			if (!UploadParams.POSTFIX.contains(suffix)) {
				name.append(StringConstants.POINT);
				name.append(suffix);
			}
		}
		// 返回文件名
		return name.toString();
	}
}
