/*
 * Copyright (C) 2023  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v6.4 TCP版) Project. 
 * All rights reserved.
 * 
 * > Github地址：https://github.com/JackJiang2011/MobileIMSDK
 * > 文档地址：  http://www.52im.net/forum-89-1.html
 * > 技术社区：  http://www.52im.net/
 * > 技术交流群：320837163 (http://www.52im.net/topic-qqgroup.html)
 * > 作者公众号：“即时通讯技术圈】”，欢迎关注！
 * > 联系作者：  http://www.52im.net/thread-2792-1-1.html
 *  
 * "即时通讯网(52im.net) - 即时通讯开发者社区!" 推荐开源工程。
 * 
 * Launch.java at 2023-9-22 11:55:07, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.java.demo;

import javax.swing.SwingUtilities;
/**
 * Demo程序启动入口类.
 * 
 * @author Jack Jiang
 */
public class Launch
{
	public static void runOnUiThread(Runnable r)
	{
		SwingUtilities.invokeLater(r);
	}

	public static void main(final String... args)
	{
		// startup GUI
		runOnUiThread(() -> {
            LoginGUI loginFrame = new LoginGUI();
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        });
	}
}