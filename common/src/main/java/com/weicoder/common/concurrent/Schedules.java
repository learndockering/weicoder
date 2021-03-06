package com.weicoder.common.concurrent;

import java.lang.reflect.Method;

import com.weicoder.common.log.Logs;
import com.weicoder.common.params.CommonParams;
import com.weicoder.common.util.BeanUtil;
import com.weicoder.common.util.ClassUtil;

/**
 * 定时任务执行类初始化
 * @author WD
 */
public final class Schedules {
	/**
	 * 初始化定时任务
	 */
	public static void init() {
		// 循环处理任务类
		for (Class<Schedule> c : ClassUtil.getAnnotationClass(CommonParams.getPackages("schedule"),
				Schedule.class)) {
			// 处理所有方法
			for (Method m : c.getDeclaredMethods()) {
				// 处理delay
				Delay delay = m.getDeclaredAnnotation(Delay.class);
				if (delay == null) {
					// 处理Rate
					Rate rate = m.getDeclaredAnnotation(Rate.class);
					if (rate != null) {
						// 添加定时任务
						ScheduledUtil.rate(() -> BeanUtil.invoke(BeanUtil.newInstance(c), m),
								rate.start(), rate.value(), rate.unit());
						Logs.info("add schedule name={},rate={}", m.getName(), rate);
					}
				} else {
					ScheduledUtil.delay(() -> BeanUtil.invoke(BeanUtil.newInstance(c), m),
							delay.start(), delay.value(), delay.unit());
					Logs.info("add schedule name={},delay={}", m.getName(), delay);
				}
			}
		}
	}

	private Schedules() {}
}
