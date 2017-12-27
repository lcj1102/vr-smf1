/**
 * 
 */
package com.suneee.smf.smf.provider.main;

public class ProviderMain {
	
	public static void main(String[] args) {
		System.setProperty("dubbo.spring.javaconfig","com.suneee.smf.smf.provider.config");
		String[] startArgs = {"javaconfig"};
		com.alibaba.dubbo.container.Main.main(startArgs);
	}
}
