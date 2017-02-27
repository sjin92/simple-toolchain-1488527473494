/*!
* javascript common utils 0.1
*
* Released under Free License
*
* Author : JooYoung, Lee (spec98@hanmail.net, jy98.lee@samsung.com)
* Created : 2016-06-22
* Updated : 2016-07-04
*
*/

"use strict";

var contextRoot = document.URL.match(/[0-9a-zA-Z.\/\-:]*iam-saas/i)[0].replace("/iam-saas","");

// Store Option
var customStorage = {
		write:function(key, value) {
			return localStorage.setItem(key, JSON.stringify(value));
		},
		read:function(key) {
			try {
				return JSON.parse(localStorage.getItem(key));
			}
			catch(e) {
				return localStorage.getItem(key);
			}
		},
		remove:function(key) {
			return localStorage.removeItem(key);
		},
		isExist:function(key) {
			return localStorage.hasOwnProperty(key);
		},
		clearAll:function(key) {
			app.requestRemoveAll(key);
			return localStorage.clear();
		}
	};

// 공용  ajax 호출
function commonAjax(url, data, method, params) {
	// default parameters
	var ajaxParams = {
		requestType:"GET",
		requestsData:"",
		dataType:"json",
		contentType:"application/json; charset=UTF-8",
		successFunc:null,
		errorFunc:null,
		completeFunc:null
	};

	// set request parameters
	$.extend(ajaxParams, params);

	ajaxParams.url = url;
	if (["GET","POST","PUT","DELETE"].indexOf(method) != -1) {
		ajaxParams.type = method;
	}
	else {
		throw ServiceException("MSG_ERR_CALL".i18n());
	}
	var requestData = {};
	if (data != undefined) {
		if(typeof data == "string") {
			ajaxParams.data = data;
		}
		else if(typeof data == "object") {
			ajaxParams.data = JSON.stringify(data);
		}
	}

	// Show loading
	loading.show();

	$.ajax({
		type : ajaxParams.type,
		url : ajaxParams.url,
		cache:false,
		dataType : ajaxParams.dataType,
		contentType : ajaxParams.contentType,
		data : ajaxParams.data,
		success : function(data, textStatus, jqXHR) {
			Logger.info("MSG_ERR_2xx".i18n());
			if(typeof data == "string") {
				Logger.trace("Ajax result : %0", data);
			}
			else if(typeof data == "object") {
				Logger.trace("Ajax result : %0", JSON.stringify(data));
			}

			// Call success function
			loading.hide();
			if(ajaxParams.successFunc) {
				ajaxParams.successFunc(data, textStatus, jqXHR);
			}
		},
		/***************************************************************
		 * success : function(resultData) {
		 * eval(_succesFnc+"(resultData)"); },
		 **************************************************************/
		error : function(xhr, status, error) {
			Logger.trace('Ajax result status is %0', status);
			Logger.trace('Ajax result xhr : %0', JSON.stringify(xhr));
			loading.hide();

			switch(xhr.status) {
			case '400':
				Logger.error("MSG_ERR_400".i18n());
				commonPopup.showPopup("LABEL_ERR_TITLE".i18n(), "MSG_ERR_400".i18n(), "ERROR");
				break;
			case '401':
				Logger.error("MSG_ERR_401".i18n());
				commonPopup.showPopup("LABEL_ERR_TITLE".i18n(), "MSG_ERR_401".i18n(), "ERROR");
				break;
			case '402':
				Logger.error("MSG_ERR_402".i18n());
				commonPopup.showPopup("LABEL_ERR_TITLE".i18n(), "MSG_ERR_402".i18n(), "ERROR");
				break;
			case '403':
				Logger.error("MSG_ERR_403".i18n());
				commonPopup.showPopup("LABEL_ERR_TITLE".i18n(), "MSG_ERR_403".i18n(), "ERROR");
				break;
			case '404':
				Logger.error("MSG_ERR_404".i18n());
				commonPopup.showPopup("LABEL_ERR_TITLE".i18n(), "MSG_ERR_403".i18n(), "ERROR");
				break;
			default:
				Logger.error("MSG_ERR_5xx".i18n());
				commonPopup.showPopup("LABEL_ERR_TITLE".i18n(), "MSG_ERR_5xx".i18n(), "ERROR");
				break;
			}
			// Call error function
			if(ajaxParams.errorFunc) {
				ajaxParams.errorFunc(xhr, status, error);
			}
		},
		complete : function() {
			// Call error function
			if(ajaxParams.completeFunc) {
				ajaxParams.completeFunc(xhr, status, error);
			}
		}
	});
}

var loading = {
	show:function(options) {
		var loadingId = "loading";
		var option = {
				message:"",
				timeout:null
		}
		$.extend(option, options);

		app.requestProgress(true, option.message);

		if(option.timeout) {
			setTimeout(function() {
				if(!app.requestProgress(false)) {
					loading.hide(loadingId);
				}
			}, option.timeout);
		}
	},
	hide:function() {
		app.requestProgress(false);
	}
}

var progress = {
		show:function(message) {
			if(!message) {
				message = "";
			}
			var progressWrap = $("<div>").addClass("progress-wrap");

			var dimBg = $("<div>").addClass("dim");
			var progress = $("<div>").addClass("progress").addClass("v-center").css("height", "20px").css("width", "60%").css("left", "192px");
			var progressBar = $("<div>").addClass("progress-bar").addClass("progress-bar-success").addClass("progress-bar-striped");
			progressBar.attr("role", "progressbar");
			progressBar.attr("aria-valuenow", "0");
			progressBar.attr("aria-valuemin", "0");
			progressBar.attr("aria-valuemax", "100");
			// progressBar.css("width", "0%");
			progressBar.text("0% " + message);

			progress.append(progressBar);
			progressWrap.append(dimBg);
			progressWrap.append(progress);

			progressWrap.data("message", message);
			$("body").append(progressWrap);
		},
		hide:function() {
			$(".progress-wrap").remove();
		},
		setProgress:function(progress) {
			var message = $(".progress-wrap").data("message");
			if(message) {
				message = "";
			}
			var progressElem = $(".progress .progress-bar");
			progressElem.attr("aria-valuenow", progress);
			// progressElem.css("width", Number(progress) + "%");
			progressElem.text(Number(progress) + "% ");

		}
}

var CustomHistory = {
		init:function() {
			var path = location.pathname.split("/");
			// Push current State
/*			$(window).on("popstate", function(event, data) {
				CustomHistory.back(e);
			});*/
		},
		back:function(e) {
			// Get current path
			var exitPath = ["login.html", "main.html"];
			var movePages = {
					"bank_main.html":"../../main/pages/main.html",
					"bank_transfer.html":"./bank_main.html",
					"bank_success.html":"./bank_main.html",
					"shop_main.html":"../../main/pages/main.html",
					"shop_payment.html":"./shop_main.html",
					"shop_success.html":"./shop_main.html",
					"square_home.html":"../../main/pages/main.html",
					"square_mail.html":"./square_home.html",
					"square_ppt.html":"./square_mail.html"
				};

			var path = location.pathname.split("/");


			// 메뉴 닫기
			if($("nav.w3-sidenav").css("display") == "block") {
				$("#myHome > div.w3-overlay.w3-hide-large.w3-animate-opacity").click();
				return;
			}

			Logger.trace("Back : %0, History Length : %1", history.state, history.length);
			///.html$/i.test(path[path.length-1])
			// app exit 처리
			if(exitPath.indexOf(path[path.length-1]) != -1) { // Exit app
				/*if(e && e.type == "popstate") {	// BackKey Back
					history.pushState(path[path.length-1], path[path.length-1].replace(".html", ""), location.toString());
				}*/
				Logger.trace("BackBefore : %0, History Length : %1", path[path.length-1], history.length);
				Logger.debug("Do you want exit?");
			//	app.requestFinishApp();
			}

			// 기타 backkey 이벤트 처리
			if(movePages[path[path.length-1]]){
				location.replace(movePages[path[path.length-1]]);
			}
			else if(path[path.length-1] != "main.html") {
				Logger.debug("History Back!");
				$(window).off("popstate");
				history.go(-1);
			}

		},
		forward:function() {
			// Get current path
			Logger.debug("History Forward!");
			history.go(1);
		},
		go:function(pages) {
			// Get current path
			if(Number(pages)) {
				if(Number(pages) > 0) {
					CustomHistory.forward();
				}
				else if(Number(pages) < 0) {
					CustomHistory.back();
				}
				else {
					CustomHistory.reload();
				}
			}
			else {
				// Do nothing
			}
		}
}

$(document).ready(function() {
	CustomHistory.init();

	if(typeof BLURABLE != "undefined" && BLURABLE) {
		Logger.info("Screen protector enable");
		app.requestBlurView(true);
	}
	else {
		Logger.info("Screen protector disable");
		app.requestBlurView(false);
	}
});
