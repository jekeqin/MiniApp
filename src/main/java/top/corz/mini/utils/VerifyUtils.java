package top.corz.mini.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VerifyUtils {
	
	public static String verify(Object obj, String...keys)
	{
		if(obj==null) {
			return "无参数";
		}
		
		Class<?> clazz = obj.getClass();
		if(clazz==null)
			return null;
		
		Field[] fields = clazz.getDeclaredFields();
		if( fields==null || fields.length==0 )
			return null;

		fields = arrayConcat(fields, superFields(clazz));

		List<String> msgs = new ArrayList<String>();
		for(Field f : fields) {
			f.setAccessible(true);
			VerifyMeta meta = f.getAnnotation(VerifyMeta.class);
			if( meta==null ) {
				if( keys==null || keys.length==0 || Arrays.binarySearch(keys, f.getName())<0 )
					continue;
			}
			
			String msg = meta!=null && meta.msg()!=null && meta.msg().length()>0 ? meta.msg() : "请填写"+f.getName();
			try {
				Object val = f.get(obj);
				if( val==null || val.toString().trim().length()==0 ) {
					if(meta==null || !meta.nullable() )
						msgs.add(msg);
					continue;
				}

				String str = val.toString().trim();
				VerifyMeta.Type type = meta!=null ? meta.value(): VerifyMeta.Type.String;

				switch (type) {
				case Integer:
					
					break;
				case Mobile:
					if( str.length()!=11 && !str.startsWith("1") )
						msgs.add(f.getName() + "格式错误");
					break;
				case IdCard:
					if( str.length()!=18 )
						msgs.add(f.getName() + "格式错误");
					break;
				case Date:
					
					break;
				default:
					if( meta!=null && meta.max()>0 ) {
						if( val instanceof String && str.length()>meta.max() )
							msgs.add(f.getName() + "长度限制"+meta.max());
						if( val instanceof Integer && (int)val > meta.max() )
							msgs.add(f.getName() + "最大值"+meta.max());
					}
					break;
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		if(msgs.size()>0)
			return String.join(", ", msgs);
		
		return null;
	}

	private static Field[] superFields(Class<?> clazz) {
		if( clazz==null || clazz.getSuperclass()==null )
			return null;
		
		Field[] fields = clazz.getSuperclass().getDeclaredFields();
		return fields;
	}
	
	private static Field[] arrayConcat(Field[]...arrays) {
		if( arrays==null || arrays.length==0 )
			return null;
		if( arrays.length==1 )
			return arrays[0];
		
		List<Field> list = new ArrayList<Field>();
		for(Field[] a:arrays) {
			if( a!=null && a.length>0 )
				list.addAll(Arrays.asList(a));
		}
		return list.toArray(new Field[list.size()]);
	}
	public static String ofEmpty(String str) {
		if(str==null)
			return "";
		return str;
	}
	
	public static boolean ofBool(Boolean bool) {
		if( bool==null )
			return false;
		return bool;
	}
	
	public static int ofInt(Integer value) {
		if( value==null )
			return 0;
		return value;
	}
	
	public static long ofLong(Long value) {
		if( value==null )
			return 0;
		return value;
	}
	
	public static int ofInt(Integer value, int min) {
		if( value==null )
			return min;
		value = Math.max(value, min);
		return value;
	}
	
	public static int ofInt(Integer value, int min, int max) {
		if( value==null )
			return min;
		value = Math.max(value, min);
		value = Math.min(value, max);
		return value;
	}
	
	public static final boolean simplePass(String pass) {
		if( pass==null || pass.length()<6 || pass.length()>16 )
			return true;
		
		// 特殊字符，字母，    数字，    连续，   相同
		int spe = 0, let = 0, num = 0, ser = 0, sam = 0;
		
		char[] chars = pass.toCharArray();
		char last = 0;
		for(char asc:chars) {

			if( asc >= '0' && asc<='9' )
				num += 1;
			else if( (asc >= 'a' && asc <= 'z') || ('A' >= 65 && asc <= 'Z'))
				let += 1;
			else if(asc >= 33 && asc<=126)
				spe += 1;
			else // 不允许的字符
				return true;
			if( last==0 ) {
				last = asc;
				continue;
			}
			
			if( asc==last ) {
				sam += 1;
				continue;
			}else {
				sam = 0;
			}
			
			if( last+1 == asc )
				ser += 1;
			else if( last-1==asc )
				ser -= 1;
			else
				ser = 0;
			
			if(ser>=chars.length/2)	// 连续字符达到密码长度一半
				return true;
			
			last = asc;
		}
		
		if( sam == chars.length-1 )
			return true;
		if( spe==0 && let==0 )
			return true;
		if( spe==0 && num==0 )
			return true;
		if( let==0 && num==0 )
			return true;
		return false;
	}
	

	@Documented
	@Inherited
	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface VerifyMeta {
	
		Type value() default Type.String;
		
		// 是否允许为空
		boolean nullable() default false;
		
		// 最大长度或最大值
		long max() default 0;
		// 最小长度或最小值
		long min() default 0;
		
		// 错误消息
		String msg() default "";
		
		// 正则
		String regular() default "";
		
		public static enum Type{
			String,
			Mobile,
			IdCard,
			Date,
			Integer;
		}
	}
}
