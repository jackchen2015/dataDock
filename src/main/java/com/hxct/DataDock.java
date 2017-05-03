/*
 * Copyright 2015 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package com.hxct;

import com.hxct.po.UserData;
import com.hxct.util.DESUtil;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chenwei
 * Created on 2017-5-3, 10:57:11
 */
public class DataDock
{

    /**
     * @param args the command line arguments
     */
    private static final int TIMEOUT = 5000;  //设置接收数据的超时时间  
    private static final int MAXNUM = 5;      //设置重发数据的最多次数 
	private static String password = "c#6zw(qi";
	private static String iv = "salt#&@!";
    public static void main(String args[])throws IOException{
		List<UserData> allUserData = new ArrayList<UserData>();
        DatagramSocket ds = new DatagramSocket(9000);  
        InetAddress loc = InetAddress.getLocalHost();  
        ds.setSoTimeout(TIMEOUT);              //设置接收数据时阻塞的最长时间
		
		for(UserData data : allUserData){
        String str_send = data.toString();  
        byte[] buf = new byte[1024];  
        //客户端在9000端口监听接收到的数据  
        //定义用来发送数据的DatagramPacket实例
		byte[] enCodeDes = DESUtil.CBCEncrypt(str_send.getBytes("UTF-8"), password.getBytes(), iv.getBytes());
        DatagramPacket dp_send= new DatagramPacket(enCodeDes,enCodeDes.length, loc,19890);  
        //定义用来接收数据的DatagramPacket实例  
        DatagramPacket dp_receive = new DatagramPacket(buf, 1024);  
        //数据发向本地3000端口  
        int tries = 0;                         //重发数据的次数  
        boolean receivedResponse = false;     //是否接收到数据的标志位  
        //直到接收到数据，或者重发次数达到预定值，则退出循环  
        while(!receivedResponse && tries<MAXNUM){  
            //发送数据  
            ds.send(dp_send);  
            try{  
                //接收从服务端发送回来的数据  
                ds.receive(dp_receive);  
				try
				{
					Thread.sleep(500);
				}
				catch(InterruptedException ex)
				{
					Logger.getLogger(DataDock.class.getName()).
							log(Level.SEVERE, null, ex);
				}
                //如果接收到的数据不是来自目标地址，则抛出异常  
                if(!dp_receive.getAddress().equals(loc)){  
                    throw new IOException("Received packet from an umknown source");  
                }  
                //如果接收到数据。则将receivedResponse标志位改为true，从而退出循环  
                receivedResponse = true;  
            }catch(InterruptedIOException e){  
                //如果接收数据时阻塞超时，重发并减少一次重发的次数  
                tries += 1;  
                System.out.println("Time out," + (MAXNUM - tries) + " more tries..." );  
            }  
        }  
        if(receivedResponse){  
            //如果收到数据，则打印出来  
            System.out.println("client received data from server：");  
            String str_receive = new String(dp_receive.getData(),0,dp_receive.getLength()) +   
                    " from " + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort();  
            System.out.println(str_receive);  
            //由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，  
            //所以这里要将dp_receive的内部消息长度重置为1024  
            dp_receive.setLength(1024);     
        }else{  
            //如果重发MAXNUM次数据后，仍未获得服务器发送回来的数据，则打印如下信息  
            System.out.println("No response -- give up.");  
        } 
		}
        ds.close();  
    }    
	
}
