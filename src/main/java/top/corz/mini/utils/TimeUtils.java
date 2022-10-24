package top.corz.mini.utils;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class TimeUtils {

	private static final SimpleDateFormat formatYmd = new SimpleDateFormat("yyyy-MM-dd");
	
	private static final SimpleDateFormat formatYm = new SimpleDateFormat("yyyy-MM");
	
	/**
	 * 指定格式化
	 * @param date
	 * @param formatter
	 * @return
	 */
	public static String format(Date date, String formatter) {
		LocalDateTime dt = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		return dt.format(DateTimeFormatter.ofPattern(formatter));
	}
	
	public static String format(String formatter) {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(formatter));
	}
	
	/**
	 * 今天，yyyy-MM-dd 00:00:00
	 * @return
	 */
	public static Date today() {
		LocalDate today = LocalDate.now();
		Instant ins = today.atStartOfDay(ZoneId.systemDefault()).toInstant();
		return Date.from(ins);
	}
	
	public static String todayStr(String formatter) {
		LocalDateTime now = LocalDateTime.now();
		return now.format(DateTimeFormatter.ofPattern(formatter));
	}
	
	/**
	 * 转换成 Date
	 * @param LocalDate
	 * @return
	 */
	public static Date toDate(LocalDate localDate) {
		Instant ins = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
		return Date.from(ins);
	}
	
	/**
	 * 转换成 Date
	 * @param LocalDateTime
	 * @return
	 */
	public static Date toDate(LocalDateTime localTime) {
		Instant ins = localTime.toInstant(ZoneOffset.of("+8"));
		return Date.from(ins);
	}
	
	/**
	 * 转换成 Date(yyyy-MM-dd 00:00:00.000)
	 * @param date
	 * @return
	 */
	public static Date toDayStart(Date date) {
		LocalDateTime dt = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		return toDate(dt.with(LocalTime.MIN));
	}
	
	/**
	 * 转换成 Date(yyyy-MM-dd 23:59:59.999)
	 * @param date
	 * @return
	 */
	public static Date toDayEnd(Date date) {
		LocalDateTime dt = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		return toDate(dt.with(LocalTime.MAX));
	}
	/**
	 * 本周周一
	 * @param date
	 * @return
	 */
	public static Date weekStart(Date date) {
		LocalDateTime dt = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		return toDate(dt.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.MIN));
	}
	/**
	 * 本周周日
	 * @param date
	 * @return
	 */
	public static Date weekEnd(Date date) {
		LocalDateTime dt = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		return toDate(dt.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).with(LocalTime.MAX));
	}
	
	public static DayOfWeek week(Date date) {
		LocalDateTime dt = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		return dt.getDayOfWeek();
	}
	
	/**
	 * 月1号
	 * @param date
	 * @return
	 */
	public static Date monthStart(Date date) {
		LocalDate ld = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		return toDate(ld.with(TemporalAdjusters.firstDayOfMonth()));
	}
	
	/**
	 * 月最后一天
	 * @param date
	 * @return
	 */
	public static Date monthEnd(Date date) {
		LocalDateTime ld = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		return toDate(ld.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX));
	}
	
	public static Date from(String dateStr) {
		if( dateStr==null )
			return null;
		try {
			if( dateStr.length()>10 ) {
				LocalDateTime dt = LocalDateTime.parse(dateStr.replaceFirst(" ", "T"));
				return toDate(dt);
			}else {
				LocalDate dt = LocalDate.parse(dateStr);
				return toDate(dt);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取年份(yyyy)
	 * @param date
	 * @return
	 */
	public static int getYear(Date date) {
		LocalDate ld = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		return ld.getYear();
	}
	
	/**
	 * 今天加减天数
	 * @param days
	 * @return
	 */
	public static Date dayCalc(int days) {
		LocalDate time = LocalDate.now();
		time = time.plusDays(days);
		return toDate(time);
	}
	
	/**
	 * 是否同年
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean sameYear(Date date1, Date date2)
	{
		if( date1==null || date2==null )
			return false;
		
		LocalDate ld1 = Instant.ofEpochMilli(date1.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate ld2 = Instant.ofEpochMilli(date2.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		
		return ld1.getYear()==ld2.getYear();
	}
	
	/**
	 * 是否同月
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean sameMonth(Date date1, Date date2)
	{
		if( date1==null || date2==null )
			return false;
		return formatYm.format(date1).equals(formatYm.format(date2));
	}
	
	public static boolean sameDay(Date date1, Date date2)
	{
		if( date1==null || date2==null )
			return false;
		return formatYmd.format(date1).equals(formatYmd.format(date2));
	}
	
	public static long differMinute(Date date1, Date date2) {
		if( date1==null || date2==null )
			return 0;
		LocalDateTime ld1 = Instant.ofEpochMilli(date1.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime ld2 = Instant.ofEpochMilli(date2.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		return Duration.between(ld1, ld2).toMinutes();
	}
	
	public static long differHour(Date date1, Date date2) {
		if( date1==null || date2==null )
			return 0;
		LocalDateTime ld1 = Instant.ofEpochMilli(date1.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime ld2 = Instant.ofEpochMilli(date2.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
		return Duration.between(ld1, ld2).toHours();
	}
	
	public static long differDays(Date date1, Date date2) {
		if( date1==null || date2==null )
			return 0;
		LocalDate ld1 = Instant.ofEpochMilli(date1.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate ld2 = Instant.ofEpochMilli(date2.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		return Period.between(ld1, ld2).getDays();
	}
	
	public static long differMonth(Date date1, Date date2) {
		if( date1==null || date2==null )
			return 0;
		LocalDate ld1 = Instant.ofEpochMilli(date1.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate ld2 = Instant.ofEpochMilli(date2.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		return Period.between(ld1, ld2).getMonths();
	}
}
