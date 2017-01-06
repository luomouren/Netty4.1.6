package com.yao.module;

import io.netty.channel.ChannelHandlerContext;

/**
 * 心跳检测的消息类型
 */
public class PingMsg extends BaseMsg {
    public PingMsg() {
        super();
        setType(MsgType.PING);
    }

	@Override
	public void dealWithServerMsg(ChannelHandlerContext ctx) {
		// TODO Auto-generated method stub
		
	}
}
