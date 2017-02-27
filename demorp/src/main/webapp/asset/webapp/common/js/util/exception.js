/*!
* javascript Exception 0.1
* 
* Released under Free License
* 
* Author : JooYoung, Lee (spec98@hanmail.net, jy98.lee@samsung.com)
* Created : 2016-06-22          
* Updated : 2016-07-04           
*
*/
"use strict";

function ServiceException(msg, value) {
	if(value) this.value = value;
	else this.value = null;
	
	if(msg) this.msg = msg;
	else this.message = "Unknown Error";
	
	this.toString = function() {
		Logger.error("Message : " + this.message + ", Value" + this.value);
		return this.value + this.message;
	};
}
