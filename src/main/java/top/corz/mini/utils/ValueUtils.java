package top.corz.mini.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ValueUtils {

	public static final String empty(String str) {
		if(str==null)
			return "";
		return str;
	}
	
	public static final int Int(String val) {
		if( val==null )
			return 0;
		try {
			return Integer.parseInt(val);
		}catch (Exception e) {
		}
		return 0;
	}
	
	public static final int Int(Integer val) {
		if( val==null )
			return 0;
		return val.intValue();
	}
	
	public static final int Int(Integer val, int defaultVal) {
		if( val==null )
			return defaultVal;
		return val.intValue();
	}
	
	public static final boolean bool(Boolean bool, boolean def) {
		if( bool==null )
			return def;
		return bool;
	}
	
	public static final int random(int max) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		return random.nextInt(max);
	}
	
	public static final int random(int min, int max) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		return random.nextInt(min, max);
	}
	
	/**
	 * 计算是否超出最大百分比
	 * @param maxRatio	百分比最大值，单位：%
	 * @return
	 */
	public static final boolean lose(double maxRatio) {
		if( maxRatio<=0 )
			return true;
		if( maxRatio>=100 )
			return false;
    	ThreadLocalRandom random = ThreadLocalRandom.current();
    	return (random.nextDouble(100) + 1) > maxRatio;
	}
	
	/**
	 * 随机红包计算
	 * @param oneRatio		1分所占比例, 0不限制，例：80，既80%概率为1分
	 * @param randTimes		降低大额度概率，随机几次，取最低
	 * @param maxCash		最低额度，单位：分
	 * @return
	 */
    public static final int cashRandomReal(double winRatio, int randTimes, Integer maxCash) {
    	if( maxCash==null || maxCash<1 )
    		return 0;

    	ThreadLocalRandom random = ThreadLocalRandom.current();
    	
    	// 先计算概率
		if( lose(winRatio) )
			return 0;
		
    	// 初始化随机次数
    	if( randTimes<=0 )
    		randTimes = 1;

		maxCash += 1;	// maxCash+1 = 范围 0~maxCash,
    	if( randTimes==1 )
    		return random.nextInt(maxCash);

    	List<Integer> list = new ArrayList<>();
    	for(int i=0;i<randTimes;i++) {
    		list.add(random.nextInt(maxCash));
    	}

    	return list.stream().mapToInt(Integer::intValue).min().getAsInt();
    }
    
}
