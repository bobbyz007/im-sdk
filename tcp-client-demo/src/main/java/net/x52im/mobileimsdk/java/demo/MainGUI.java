/*
 * Copyright (C) 2022  即时通讯网(52im.net) & Jack Jiang.
 * The MobileIMSDK_TCP (MobileIMSDK v6.x TCP版) Project. 
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
 * MainGUI.java at 2022-7-16 16:53:48, code by Jack Jiang.
 */
package net.x52im.mobileimsdk.java.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;

import net.x52im.mobileimsdk.java.ClientCoreSDK;
import net.x52im.mobileimsdk.java.core.LocalDataSender;
import net.x52im.mobileimsdk.java.utils.Log;

public class MainGUI extends JFrame
{
	private JButton btnLogout = null;
	
	private JTextField editId = null;
	private JTextField editContent = null;
	private JButton btnSend = null;
	private JLabel viewMyid = null;
	
	private JTextPane debugPane;
	private JTextPane imInfoPane;
	
	private SimpleDateFormat hhmmDataFormat = new SimpleDateFormat("HH:mm:ss");
	
	public MainGUI()
	{	
		initViews();
		initListeners();
		initOthers();
	}
	
	private void initViews()
	{
		// 登陆组件初始化
		btnLogout = new JButton("退出程序");
		viewMyid = new JLabel();
		viewMyid.setForeground(new Color(255,0,255));
		viewMyid.setText("未登陆");
		
		// 消息发送组件初始化
		btnSend = new JButton("发送消息");
		editId = new JTextField(20);
		editContent = new JTextField(20);
		
		// debug信息显示面板初始化
		debugPane=new JTextPane();
		debugPane.setBackground(Color.black);
		debugPane.setCaretColor(Color.white);
		Log.getInstance().setLogDest(debugPane);
		
		// IM通讯信息显示面板初始化
		imInfoPane=new JTextPane();
		
		// 登陆认证信息布局
		JPanel authPane = new JPanel();
		authPane.setLayout(new BoxLayout(authPane, BoxLayout.LINE_AXIS));
		authPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
		authPane.add(new JLabel("状态："));
		authPane.add(viewMyid);
		authPane.add(Box.createHorizontalGlue());
		authPane.add(btnLogout);
		
		// 消息发送布局
		JPanel userPane = new JPanel(new BorderLayout());
		userPane.add(new JLabel("对方账号："), BorderLayout.WEST);
		userPane.add(editId, BorderLayout.EAST);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(new JLabel("发送内容："), BorderLayout.WEST);
		contentPane.add(editContent, BorderLayout.EAST);

		JPanel sendPanel=new JPanel();
		BoxLayout layout=new BoxLayout(sendPanel, BoxLayout.Y_AXIS);
		sendPanel.setLayout(layout);
		sendPanel.add(contentPane);
		sendPanel.add(btnSend);

		JPanel oprMainPanel = new JPanel(new BorderLayout());
		oprMainPanel.add(authPane, BorderLayout.NORTH);
		oprMainPanel.add(userPane, BorderLayout.CENTER);
		oprMainPanel.add(sendPanel, BorderLayout.SOUTH);

		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.add(oprMainPanel, BorderLayout.NORTH);
		JScrollPane imInfoSc = new JScrollPane(imInfoPane);
		imInfoSc.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(0, 7, 0, 7), imInfoSc.getBorder()));
		imInfoSc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		leftPanel.add(imInfoSc, BorderLayout.CENTER);
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(leftPanel, BorderLayout.WEST);
		JScrollPane sc = new JScrollPane(debugPane);
		sc.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4, 0, 0, 2), sc.getBorder()));
		sc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.getContentPane().add(sc, BorderLayout.CENTER);
		
		this.setTitle("TCP 演示工程 - 当前登陆: "
				+ClientCoreSDK.getInstance().getCurrentLoginInfo().getLoginUserId());
		this.setLocationRelativeTo(null);
		this.setSize(1000,700);
		
		// 显示当前账号信息
		MainGUI.this.showIMInfo_green("当前账号："+ClientCoreSDK.getInstance().getCurrentLoginInfo().getLoginUserId());
	}
	
	private void initListeners()
	{
		btnLogout.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// 退出登陆
				doLogout();
				// 退出程序
				doExit();
			}
		});
		
		btnSend.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				doSendMessage();
			}
		});
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				// 退出登陆
				doLogout();
				// 退出程序
				doExit();
			}
		});
	}
	
	private void initOthers()
	{
		// Refresh userId to show
		refreshMyid();
		
		// Set MainGUI instance refrence to listeners
		IMClientManager.getInstance().getChatMessageListener().setMainGUI(this);
		IMClientManager.getInstance().getBaseEventListener().setMainGUI(this);
		IMClientManager.getInstance().getMessageQoSListener().setMainGUI(this);
	}
	
	public void refreshMyid()
	{
		boolean connectedToSer = ClientCoreSDK.getInstance().isConnectedToServer();
		this.viewMyid.setText(connectedToSer ? "通信正常":"连接断开");
	}
	
	private void doSendMessage()
	{
		final String msg = editContent.getText().trim();
		final String friendId = editId.getText().trim();
		if(msg.length() > 0 && friendId.length() > 0 )
		{
			MainGUI.this.showIMInfo_black("我对"+friendId+"说："+msg);

			// 发送消息（异步提升体验，你也可直接调用LocalUDPDataSender.send(..)方法发送）
			new LocalDataSender.SendCommonDataAsync(msg, friendId)//, true)
			{
				@Override
				protected void onPostExecute(Integer code)
				{
					if(code == 0)
						Log.i(MainGUI.class.getSimpleName(), "2数据已成功发出！");
					else
						showToast("数据发送失败。错误码是："+code+"！");
				}
			}.execute();  
		}
		else
		{
			String s = "接收者账号len="+friendId.length()
					+", 消息内容len="+msg.length()+", 发送没有继续!";
			showIMInfo_red(s);
			Log.e(MainGUI.class.getSimpleName(), s);
		}
	}
	
	public void doLogout()
	{
		// 发出退出登陆请求包
		int code = LocalDataSender.getInstance().sendLoginout();
		refreshMyid();
		if(code == 0)
			System.out.println("注销登陆请求已完成！");
		else
			System.out.println("注销登陆请求发送失败。错误码是："+code+"！");
		
		//## BUG FIX: 20170713 START by JackJiang
		// 退出登陆时记得一定要调用此行，不然不退出APP的情况下再登陆时会报 code=203错误哦！
		IMClientManager.getInstance().resetInitFlag();
		//## BUG FIX: 20170713 END by JackJiang
	}
	
	public static void doExit()
	{
		// 释放IM占用资源
//		ClientCoreSDK.getInstance().release();
		IMClientManager.getInstance().release();
		// JVM退出
		System.exit(0);
	}
	
	//--------------------------------------------------------------- 控制台输出和Toast显示方法 START
	public void showIMInfo_black(String txt)
	{
		showIMInfo(new Color(0,0,0), txt);
	}
	public void showIMInfo_blue(String txt)
	{
		showIMInfo(new Color(0,0,255), txt);
	}
	public void showIMInfo_brightred(String txt)
	{
		showIMInfo(new Color(255,0,255), txt);
	}
	public void showIMInfo_red(String txt)
	{
		showIMInfo(new Color(255,0,0), txt);
	}
	public void showIMInfo_green(String txt)
	{
		showIMInfo(new Color(0,128,0), txt);
	}
	public void showIMInfo(Color c, String txt)
	{
		try{
			Log.append(c, "["+hhmmDataFormat.format(new Date())+"]"+txt+"\r\n", this.imInfoPane);
			imInfoPane.setCaretPosition(imInfoPane.getDocument().getLength());
		}
		catch(Exception e){
//			e.printStackTrace();
		}
	}
	
	public void showToast(String text)
	{
		JOptionPane.showMessageDialog(MainGUI.this
				, text, "友情提示", JOptionPane.WARNING_MESSAGE);
	}
	//--------------------------------------------------------------- 控制台输出和Toast显示方法 END
}
