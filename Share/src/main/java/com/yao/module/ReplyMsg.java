package com.yao.module;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by yaozb on 15-4-11.
 */
public class ReplyMsg extends BaseMsg {
    public ReplyMsg() {
        super();
        setType(MsgType.REPLY);
    }
    private ReplyBody body;

    public ReplyBody getBody() {
        return body;
    }

    public void setBody(ReplyBody body) {
        this.body = body;
    }

	@Override
	public void dealWithServerMsg(ChannelHandlerContext ctx) {
		// TODO Auto-generated method stub
		
	}
}
