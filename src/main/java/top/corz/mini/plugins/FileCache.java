package top.corz.mini.plugins;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.ResourceUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import top.corz.mini.utils.TimeUtils;

@Slf4j
public class FileCache {
	
	public static final <T> T getObj(String name, Class<T> cla) {
		return getObj(name, cla, -1);
	}
	
	public static final <T> T getObj(String name, Class<T> cla, long expire) {
		String json = get(name, expire);
		if( json==null )
			return null;
		try {
			JSONObject obj = JSONObject.parseObject(json);
			return obj.toJavaObject(cla);
		}catch (Exception e) {
		}
		return null;
	}
	
	public static final JSONArray getJArray(String name, long expire) {
		String json = get(name, expire);
		if( json==null )
			return null;
		try {
			return JSONArray.parseArray(json);
		}catch (Exception e) {
		}
		return null;
	}
	
	public static final String get(String name) {
		return get(name, -1);
	}
	
	public static final String get(String name, long expire) {
		File file = new File(staticFolder("cache") + name + ".cache");
		if( !file.exists() )
			return null;
		if(expire > 0 && System.currentTimeMillis() > file.lastModified() + expire )
			return null;
		try {
			String value = Files.lines(file.toPath()).collect(Collectors.joining());
			return value;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final String set(String name, Object value) {
		File file = new File(staticFolder("cache") + name + ".cache");
		try {
			if( value==null ) {
				if( file.exists() )
					file.delete();
				return null;
			}
			
			if( !file.exists() )
				file.createNewFile();

			file.setLastModified(System.currentTimeMillis());
			
			String str = value.toString();
			
			//FileWriter writter = new FileWriter(file, false);
			BufferedWriter writter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
			writter.write(str);
			writter.close();
			return str;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final void del(String name) {
		fileDeleteFromDisk(staticFolder(""), name);
	}
	
	
	//======================================
	
	public static final <T> List<T> listGet(String name, Class<T> cla){
		return listGet(name, cla, -1);
	}
	public static final <T> List<T> listGet(String name, Class<T> cla, long expire){
		String str = FileCache.get(name, expire);
		if( str==null || str.length()==0 )
			return new ArrayList<T>();
		try {
			return JSONObject.parseArray(str, cla);
		}catch (Exception e) {
		}
		return new ArrayList<T>();
	}
	
	public static final <T> void listAdd(String name, T obj, Class<T> cla) {
		List<T> list = listGet(name, cla, -1);
		list.add(0, obj);
		FileCache.set(name, JSONObject.toJSON(list));
	}
	
	public static final JSONObject listDel(String name, int index) {
		List<JSONObject> list = listGet(name, JSONObject.class, -1);
		if( list.size()<=index )
			return null;
		
		JSONObject obj = list.get(index);
		log.info("cache.list.del: {}:{}", index, obj);
		list.remove(index);
		FileCache.set(name, JSONObject.toJSON(list));
		
		return obj;
	}
	
	//======================================
	
	@SuppressWarnings("unchecked")
	public static final <T> LinkedHashMap<String, T> mapGet(String name, Class<T> cla){
		LinkedHashMap<String, T> map = new LinkedHashMap<String, T>();
		String str = FileCache.get(name);
		if( str==null || str.length()==0 )
			return map;
		try {
			LinkedHashMap<String, JSONObject> cache = new LinkedHashMap<String, JSONObject>();
			cache = JSONObject.parseObject(str, cache.getClass());
			if( cache!=null ) {
				for(String key : cache.keySet()) {
					map.put(key, cache.get(key).toJavaObject(cla));
				}
			}
			return map;
		}catch (Exception e) {
		}
		return map;
	}
	
	public static final <T> LinkedHashMap<String, T> mapAdd(String name, String key, T obj, Class<T> cla){
		LinkedHashMap<String, T> map = mapGet(name, cla);
		map.put(key, obj);
		FileCache.set(name, JSONObject.toJSON(map));
		return map;
	}
	
	public static final <T> T mapDel(String name, String key, Class<T> cla){
		LinkedHashMap<String, T> map = mapGet(name, cla);
		T obj = null;
		if( map.containsKey(key) ) {
			obj = map.get(key);
			map.remove(key);
		}
		log.info("cache.map.del: {}:{}", key, obj);
		FileCache.set(name, JSONObject.toJSON(map));
		return obj;
	}
	
	//======================================
	
	public static final String writeImage(Object prefix, InputStream input, String ext) {
		String subpath = TimeUtils.format("yyyy/MM"); 
		String folder = staticFolder("files/" + subpath);
		String fileName = TimeUtils.format("yyyyMMddHHmmss") + "." + ext;
		if( prefix!=null ) {
			fileName = prefix + "_" + fileName;
		}
		try {
			Files.copy(input, Path.of(folder + "/" + fileName));
			return "/files/" + subpath + "/" + fileName;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final boolean maxFile(long fileBytes, long maxKB) {
		return fileBytes / 1024 <= maxKB;
	}
	
	public static final String fileExt(String name, String contentType) {
		if( name==null && contentType==null )
			return "unknow";
		String[] sp = name.split("\\.");
		if( sp.length>1 )
			return sp[sp.length-1];
		
		sp = contentType.split("/");
		return sp[sp.length-1];
	}
	
	private static final void fileDeleteFromDisk(String folder, String name) {
		try {
			log.info("cache.del: {}", Paths.get(folder + name));
			Files.deleteIfExists(Paths.get(folder + name));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

    public static final String staticFolder(String folder) {
		File classpath = null;
		try {
			classpath = new File(ResourceUtils.getURL("classpath:").getPath());
			String root = classpath.getParentFile().getParentFile().getParent().replace("file:", "") + File.separator;
			if( folder==null )
				return root;
			
			String target = root + folder + File.separator;
			File folderFile = new File(target);
			if( !folderFile.exists() )
				folderFile.mkdirs();

			return folderFile.getPath() + File.separator;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
