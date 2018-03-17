package com.ido.qna.controller.response;

public class WechatLoginResult {
	private String openid;
	private String sessionKkey;
	private String errcode;
	private String errmsg;
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getSessionKkey() {
		return sessionKkey;
	}
	public void setSessionKkey(String sessionKkey) {
		this.sessionKkey = sessionKkey;
	}
	public String getErrcode() {
		return errcode;
	}
	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	@Override
	public String toString() {
		return "WechatLoginResult [openid=" + openid + ", sessionKkey=" + sessionKkey + ", errcode=" + errcode
				+ ", errmsg=" + errmsg + "]";
	}
}
