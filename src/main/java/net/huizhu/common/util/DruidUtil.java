package net.huizhu.common.util;

import com.alibaba.druid.filter.config.ConfigTools;

/**
 * Druid 密码加解密本地工具。运行时从命令行传入明文，勿将真实密码写入源码。
 * <pre>
 *   java ... DruidUtil your-plain-password
 * </pre>
 */
public class DruidUtil {

	public static void main(String[] args) throws Exception {
		if (args == null || args.length == 0) {
			System.out.println("Usage: DruidUtil <plainPassword> [encryptedPassword]");
			return;
		}
		String password = args[0];
		System.out.println(ConfigTools.encrypt(password));
		if (args.length > 1) {
			System.out.println(ConfigTools.decrypt(args[1]));
		}
	}

}
