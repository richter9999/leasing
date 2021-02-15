package com.yhzt.leasing.utils;

import lombok.Data;

@Data
public class AjaxResponse {

	private boolean isok;
	private int code;
	private String msg;
	private Object data; 

	private AjaxResponse() {

	}

	public static AjaxResponse success() {
		AjaxResponse returnBean = new AjaxResponse();

		returnBean.setIsok(true);
		returnBean.setCode(200);
		returnBean.setMsg("Success");
		return returnBean;
	}

	public static AjaxResponse success(Object data) {
		AjaxResponse returnBean = new AjaxResponse();

		returnBean.setIsok(true);
		returnBean.setCode(200);
		returnBean.setMsg("Success");
		returnBean.setData(data);
		return returnBean;
	}


	public static AjaxResponse error(Exception e, int errorCode) {
		AjaxResponse returnBean = new AjaxResponse();

		returnBean.setIsok(false);
		returnBean.setCode(errorCode);
		returnBean.setMsg(e.getMessage());
		return returnBean;
	}
}