/**
 *
 */
package com.tmall.asshole.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author hemaodong
 * @date 2012-5-28 ����11:43:56
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Extends {
	//Ĭ�ϲ��̳и�������
	public boolean value() default false;
}
