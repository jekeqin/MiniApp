package top.corz.mini.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class HostUtils {

	private static String _PhysicsHost;
	
	private static String _PhysicsMac;

    public static String PhysicsHost(){
    	if( _PhysicsHost!=null )
    		return _PhysicsHost;
    	
    	InetAddress addr = Physics();
    	if( addr==null )
    		return null;
    	_PhysicsHost = addr.getHostAddress();
    	return _PhysicsHost;
	}
    
    public static String PhysicsMac() {
    	if(_PhysicsMac!=null)
    		return _PhysicsMac;
    	
    	InetAddress addr = Physics();
    	if( addr==null )
    		return null;
    	
    	try {
			NetworkInterface network = NetworkInterface.getByInetAddress(addr);
			if( network==null )
				return null;
			
			byte[] mac = network.getHardwareAddress();
			
			StringBuffer buff = new StringBuffer();
			for(byte b:mac) {
				buff.append("-").append(byteToHex(b));
			}
			
			_PhysicsMac = buff.substring(1).toUpperCase();
			return _PhysicsMac;
		} catch (SocketException e) {
			e.printStackTrace();
		}
    	return null;
    }
    
    public static InetAddress Physics() {
    	String os = System.getProperty("os.name").toLowerCase();
    	
    	try {
			Enumeration<NetworkInterface> all = NetworkInterface.getNetworkInterfaces();
			while(all.hasMoreElements()) {
				NetworkInterface net = all.nextElement();
	
				String name = net.getDisplayName().toLowerCase();
	
				if(os.startsWith("linux")){
					if( !name.startsWith("eth") && !name.startsWith("ens"))
						continue;
				}else {
					if( name.startsWith("software loopback") || name.startsWith("veth") || name.indexOf("virtual")>=0 || name.indexOf("miniport")>=0 || name.indexOf("debug")>=0 || name.indexOf("microsoft")>=0 )
						continue;
				}
	
				//System.out.println(os + " > " +name);

				Enumeration<InetAddress> ips = net.getInetAddresses();
				while(ips.hasMoreElements()) {
					InetAddress addr = ips.nextElement();
					if( !(addr instanceof Inet4Address) )
						continue;
					
					String ip = addr.getHostAddress();
					if( ip.startsWith("127.0") )
						continue;
	
					//System.out.println(os + " > " +name + " > " + ip);

					return addr;
				}
			}
    	}catch (Exception e) {
    		e.printStackTrace();
		}
		
		return null;
    }
    
    public static String byteToHex(byte byt) {
    	String hex = Integer.toHexString(byt & 0xFF);
    	if(hex.length()<2)
    		return "0" + hex;
    	return hex;
    }
    
    public static final String remoteIp(HttpServletRequest req){
    	if( req==null )
    		return "unknow";
    	String ip = req.getHeader("X-Real-IP");
    	if( ip==null || ip.equalsIgnoreCase("unknow") )
    		ip = req.getHeader("X-Forwarded-For");
    	
    	if( ip==null || ip.equalsIgnoreCase("unknow") )
    		ip = req.getRemoteAddr();
    	return ip;
    }
}
