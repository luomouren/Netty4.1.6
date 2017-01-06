package com.yao.module;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by yaozb on 15-4-11.
 * 请求类型的消息
 */
public class AskMsg extends BaseMsg {
    public AskMsg() {
        super();
        setType(MsgType.ASK);
    }
    private AskParams params;

    public AskParams getParams() {
        return params;
    }

    public void setParams(AskParams params) {
        this.params = params;
    }

/*	@Override*/
	public void dealWithServerMsg(ChannelHandlerContext ctx) {
		// TODO Auto-generated method stub
		
	}
}
