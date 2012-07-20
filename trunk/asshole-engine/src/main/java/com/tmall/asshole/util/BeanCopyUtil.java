package com.tmall.asshole.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
/**
 * 
 * @author tangjinou (jiuxian.tjo)
 *
 */
public class BeanCopyUtil {
	
	public static  void copy(Object o, Map<String,Object> map){
		
		for (Entry<String, Object> entry :map.entrySet()) {
				try{
					// ����ֻ����һ��
					Field field = o.getClass().getDeclaredField(entry.getKey());
					Object value = entry.getValue();
					//ֻ�����ֺ�����һֱʱ��ſ���
					if(field.getType() ==value.getClass())
					{
						field.setAccessible(true);
						field.set(o, value);
					}
				}catch (Exception e) {
					
				}
		}
		
		
	}

}
