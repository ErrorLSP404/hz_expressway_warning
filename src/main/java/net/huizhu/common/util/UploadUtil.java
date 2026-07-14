package net.huizhu.common.util;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import net.huizhu.common.config.UploadConfig;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class UploadUtil {

	public static final List<String> extlsit = new ArrayList<String>();

	static{
		extlsit.add("jpg");
		extlsit.add("png");
		extlsit.add("JPG");
		extlsit.add("PNG");
		extlsit.add("jpeg");
		extlsit.add("JPEG");
		extlsit.add("xls");
		extlsit.add("xlsx");
		extlsit.add("mtj");
		extlsit.add("MTJ");
		extlsit.add("zip");
		extlsit.add("ZIP");
		extlsit.add("RAR");
		extlsit.add("rar");
		extlsit.add("txt");
		extlsit.add("TXT");
		extlsit.add("7Z");
		extlsit.add("7z");
		extlsit.add("rar4");
		extlsit.add("RAR4");

	}
	
	public static String upload(String Dbpath,MultipartFile pic){
		String filename = "";
		try {
	
		//获取扩展名
		//扩展名
		String ext = FilenameUtils.getExtension(pic.getOriginalFilename()).toLowerCase();

		if(extlsit.contains(ext)){
		
		
		//获取路径
		String path = Dbpath;
		
		//1:图片名称生成策略   
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String name = df.format(new Date());
		
		//追回三位随机数
		Random  rd = new Random();
		name += rd.nextInt(999);
		//String name = FilenameUtils.removeExtension(pic.getOriginalFilename());
		
		Dbpath = path+"/"+name+"."+ext;
		
		//发送文件  http://localhost:8088/image-web/
		Client client = new Client();
		
		String url = UploadConfig.fileUrl+Dbpath;
		//String url = "http://221.207.8.66:8082/wenjian/Miteno_CDPF"+Dbpath;
		//String url = "http://192.168.1.91:8080/Image-web"+Dbpath;
		//加载Url
		WebResource resource = client.resource(url);
		
		resource.put(String.class, pic.getBytes());
		}else{
			return "0";
		}
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
		return Dbpath;
	}


	public static String uploadByte(String Dbpath,byte[] iamge,String ext){
		String filename = "";
		try {
			if(extlsit.contains(ext)){

				//获取路径
				String path = Dbpath;
				
				//1:图片名称生成策略   
				DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				String name = df.format(new Date());
				//追回三位随机数
				Random  rd = new Random();
				name += rd.nextInt(999);
				
				
				Dbpath = path+"/"+name+"."+ext;
				
				//发送文件  http://localhost:8088/image-web/
				Client client = new Client();
				
				String url = UploadConfig.fileUrl+Dbpath;
				//String url = "http://221.207.8.66:8082/wenjian/Miteno_CDPF"+Dbpath;
				//String url = "http://192.168.1.91:8080/Image-web"+Dbpath;
				//加载Url
				WebResource resource = client.resource(url);
				
				resource.put(String.class, iamge);
			}else{
				return "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
		return Dbpath;
	}
	
	/*public static void main(String[] args) {
		try {
			
		
		// 读取文件
		FileInputStream fileInputStream = new FileInputStream("d:/123.jpg");
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int i;
		while ((i = fileInputStream.read()) != -1) {
			byteArrayOutputStream.write(i);
		}
		fileInputStream.close();
		// 把文件存在一个字节数组中
		byte[] filea = byteArrayOutputStream.toByteArray();
		byteArrayOutputStream.close();

		String fileaString = new String(filea,"ISO-8859-1");
		
		byte[] file = fileaString.getBytes("ISO-8859-1");
		
		

		//int upload = upload("video/1444439088137.mp4", "http://192.168.6.212/aixinbang/MitenoSkillExchange/", file);
		String upload = upload("image/123.jpg", file);
		
		System.out.println(upload);
		
		} catch (Exception e) {
			// TODO: handle exception
		}
	}*/
}
