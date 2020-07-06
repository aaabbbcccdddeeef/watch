package com.wisdomin.studentcard.api;

public interface IAuthCallBack {

	/***
	 * @param response
	 */
	void onSuccess(String response) ;
	/***
	 * @param response
	 */
	void onFailure(String response) ;

}