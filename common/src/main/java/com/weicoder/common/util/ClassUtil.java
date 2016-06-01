package com.weicoder.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.weicoder.common.constants.StringConstants;
import com.weicoder.common.lang.Lists;
import com.weicoder.common.log.Logs;

/**
 * 关于Class的一些操作
 * @author WD 
 *  
 */
public final class ClassUtil {
	/**
	 * 判断是否是基础类型
	 * @param clazz 要检查的类
	 * @return 是否基础类型
	 */
	public static boolean isBaseType(Class<?> clazz) {
		if (clazz == null) { return true; }
		if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
			return true;
		} else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
			return true;
		} else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
			return true;
		} else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
			return true;
		} else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
			return true;
		} else if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
			return true;
		} else if (clazz.equals(Boolean.class) || clazz.equals(boolean.class)) {
			return true;
		} else if (clazz.equals(Character.class) || clazz.equals(char.class)) {
			return true;
		} else if (clazz.equals(String.class) || clazz.equals(BigDecimal.class)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获得指定类型的泛型
	 * @param type 指定的类型
	 * @return 这个类的泛型
	 */
	public static <T> Class<T> getGenericClass(Class<?> clazz) {
		// 查询父类是否有泛型
		Class<T> gc = getGenericClass(clazz.getGenericSuperclass(), 0);
		// 如果没找到
		if (gc == null) {
			// 获得所有接口
			Type[] type = clazz.getGenericInterfaces();
			// 接口不为空
			if (!EmptyUtil.isEmpty(type)) {
				// 循环接口
				for (Type t : type) {
					// 获得泛型
					gc = getGenericClass(t, 0);
					// 泛型不为空 跳出循环
					if (gc != null) {
						break;
					}
				}
			}
		}
		// 返回类
		return gc;
	}

	/**
	 * 获得指定类型的泛型
	 * @param type 指定的类型
	 * @return 这个类的泛型
	 */
	public static Class<?>[] getGenericClass(Type type) {
		// 获得类型类型数组
		Type[] types = ((ParameterizedType) type).getActualTypeArguments();
		// 声明Class数组
		Class<?>[] clazzs = new Class<?>[types.length];
		// 循环
		for (int i = 0; i < types.length; i++) {
			// 强制转换
			clazzs[i] = (Class<?>) types[i];
		}
		// 返回数组
		return clazzs;
	}

	/**
	 * 获得指定类型的泛型
	 * @param type 指定的类型
	 * @param index 索引
	 * @return 这个类型的泛型
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getGenericClass(Type type, int index) {
		try {
			return type instanceof ParameterizedType ? (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[index] : null;
		} catch (Exception e) {
			Logs.error(e);
//			Logs.debug("ClassUtil getGenericClass=" + e.toString());
			return null;
		}
	}

	/**
	 * 加载类
	 * @param className 类名
	 * @return 获得的类
	 */
	public static Class<?> loadClass(String className) {
		// 声明类
		Class<?> theClass = null;
		try {
			// 获得类
			theClass = Class.forName(className);
		} catch (ClassNotFoundException e1) {
			try {
				// 当前线程获得类
				theClass = Thread.currentThread().getContextClassLoader().loadClass(className);
			} catch (ClassNotFoundException e2) {
				try {
					// 使用当前类获得类
					theClass = ClassUtil.class.getClassLoader().loadClass(className);
				} catch (ClassNotFoundException e3) {
					return null;
				}
			}
		}
		// 返回类
		return theClass;
	}

	/**
	 * 获得Class
	 * @param className Class名称
	 * @return Class
	 */
	public static Class<?> forName(String className) {
		try {
			return EmptyUtil.isEmpty(className) ? null : Class.forName(className);
		} catch (Exception e) {
			Logs.error(e);
//			Logs.debug("ClassUtil forName=" + e.toString());
			return null;
		}
	}

	/**
	 * 实例化对象
	 * @param className 类名
	 * @return 实例化对象
	 */
	public static Object newInstance(String className) {
		try {
			return forName(className).newInstance();
		} catch (Exception e) {
			Logs.error(e);
//			Logs.debug("ClassUtil newInstance=" + e.toString());
			return null;
		}
	}

	/**
	 * 实例化对象
	 * @param clazz 类
	 * @return 实例化对象
	 */
	public static <T> T newInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			Logs.error(e);
//			Logs.debug("ClassUtil newInstance=" + e.toString());
			return null;
		}
	}

	/**
	 * 指定包下 指定类的实现
	 * @param cls 指定类
	 * @param i 指定索引
	 * @return 类列表
	 */
	public static <E> Class<E> getAssignedClass(Class<E> cls, int i) {
		return Lists.get(getAssignedClass(StringConstants.EMPTY, cls), i);
	}

	/**
	 * 指定包下 指定类的实现
	 * @param cls 指定类
	 * @return 类列表
	 */
	public static <E> List<Class<E>> getAssignedClass(Class<E> cls) {
		return getAssignedClass(StringConstants.EMPTY, cls);
	}

	/**
	 * 指定包下 指定类的实现
	 * @param packageName 包名
	 * @param cls 指定类
	 * @return 类列表
	 */
	public static <E> List<Class<E>> getAssignedClass(String packageName, Class<E> cls) {
		// 声明类列表
		List<Class<E>> classes = Lists.getList();
		// 循环包下所有类
		for (Class<E> c : getPackageClasses(packageName, cls)) {
			// 是本类实现 并且不是本类
			if (cls.isAssignableFrom(c) && !cls.equals(c)) {
				classes.add(c);
			}
		}
		// 返回列表
		return classes;
	}

	/**
	 * 指定包下 指定类的实现
	 * @param cls 指定类
	 * @param i 指定索引
	 * @return 类列表
	 */
	public static Class<? extends Annotation> getAnnotationClass(Class<? extends Annotation> cls, int i) {
		return Lists.get(getAnnotationClass(cls), i);
	}

	/**
	 * 指定包下 指定类的实现
	 * @param cls 指定类
	 * @return 类列表
	 */
	public static List<Class<? extends Annotation>> getAnnotationClass(Class<? extends Annotation> cls) {
		return getAnnotationClass(StringConstants.EMPTY, cls);
	}

	/**
	 * 指定包下 指定类的实现
	 * @param packageName 包名
	 * @param cls 指定类
	 * @return 类列表
	 */
	@SuppressWarnings("unchecked")
	public static List<Class<? extends Annotation>> getAnnotationClass(String packageName, Class<? extends Annotation> cls) {
		// 声明类列表
		List<Class<? extends Annotation>> classes = Lists.getList();
		// 循环包下所有类
		for (Class<?> c : getPackageClasses(packageName, cls)) {
			// 是本类实现 并且不是本类
			if (c.isAnnotationPresent(cls) && !cls.equals(c)) {
				classes.add((Class<? extends Annotation>) c);
			}
		}
		// 返回列表
		return classes;
	}

	/**
	 * 获得指定包下的所有Class
	 * @param packageName 报名
	 * @return 类列表
	 */
	@SuppressWarnings("unchecked")
	public static <E> List<Class<E>> getPackageClasses(String packageName, Class<E> cls) {
		// 声明返回类列表
		List<Class<E>> classes = Lists.getList();
		// 转换报名为路径格式
		String path = packageName.replace(StringConstants.POINT, StringConstants.BACKSLASH);
		// 获得目录资源
		URL url = ResourceUtil.getResource(path);
		if (url == null) { return classes; }
		// 循环目录下的所有文件与目录
		for (String name : getClasses(url.getPath(), path)) {
			// 如果是class文件
			if (name.endsWith(".class")) {
				try {
					// 反射出类对象 并添加到列表中
					// classes.add(Class.forName(packageName + StringConstants.POINT +
					// StringUtil.subString(name, 0, name.length() - 6)));
					// classes.add(Class.forName(packageName + StringConstants.BACKSLASH +name));
					name = packageName + StringConstants.POINT + StringUtil.subString(name, 0, name.length() - 6);
					name = StringUtil.replace(name, StringConstants.BACKSLASH, StringConstants.POINT);
					// 如果开始是.去掉
					if (name.startsWith(StringConstants.POINT)) {
						name = StringUtil.subString(name, 1);
					}
					classes.add((Class<E>) Class.forName(name));
				} catch (ClassNotFoundException e) {
					Logs.error(e);
//					Logs.debug("ClassUtil getPackageClasses=" + e.toString());
				}
			} else {
				// 迭代调用本方法 获得类列表
				classes.addAll(getPackageClasses(EmptyUtil.isEmpty(packageName) ? name : packageName + StringConstants.BACKSLASH + name, cls));
			}
		}
		// 返回类列表
		return classes;
	}

	private static List<String> getClasses(String name, String packageName) {
		// 获得文件名
		File path = new File(name);
		// 判断是否目录
		if (path.isDirectory()) {
			// 如果是目录
			return Lists.getList(path.list());
		} else if (name.indexOf(".jar!") > -1) {
			// 是否jar文件内
			return getClassesFromJARFile(StringUtil.subString(name, "file:/", "!"), packageName + StringConstants.BACKSLASH);
		}
		// 返回空列表
		return Lists.emptyList();
	}

	private static List<String> getClassesFromJARFile(String jar, String name) {
		// 判断jar第二位是否: 不为:默认linux前面加上/
		if (jar.indexOf(StringConstants.COLON) == -1) {
			jar = StringConstants.BACKSLASH + jar;
		}
		// 声明返回列表
		List<String> list = Lists.getList();
		// 获得jar流
		try (JarInputStream jarFile = new JarInputStream(new FileInputStream(jar))) {
			// 循环获得JarEntry
			JarEntry jarEntry = null;
			while ((jarEntry = jarFile.getNextJarEntry()) != null) {
				// 判断是否包内class
				String className = jarEntry.getName();
				if (className.indexOf(name) > -1 && !className.equals(name)) {
					list.add(StringUtil.subString(className, name));
				}
			}
		} catch (IOException e) {
			Logs.error(e);
//			Logs.debug("ClassUtil getClassesFromJARFile=" + e.toString());
		}
		// 返回列表
		return list;
	}

	private ClassUtil() {}
}
