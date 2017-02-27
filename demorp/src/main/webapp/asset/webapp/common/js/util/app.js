/*!
* javascript APP interfaces 0.1
* 
* Released under Free License
* 
* Author : JooYoung, Lee (spec98@hanmail.net, jy98.lee@samsung.com)
* Created : 2016-06-22          
* Updated : 2016-07-04          
*
*/

/*
* 아래 인터페이스의 사용에 대한 내용은 webapp/main/pages/api_test.html 에 있음
*/

(function(/*window, */document, undefined){
	window.app = {
		// OTP 요청
		// String type REG/AUTH/DEREG 사용 가능하나 reg 는 loginComplete, dereg 는 removeAll 에서 실행
		requestOtp:function(type, userName) {
			if(typeof webInterface != "undefined") {
				webInterface.requestOtp(type, userName);
			}
			else {
				// is Online Mode
				if(navigator.onLine) {
					Logger.error("MSG_ERR_APP_MODE".i18n());
				}
				// is Offline Mode
				else {
					Logger.error("MSG_ERR_OFFLINE".i18n());
				}
			}
		},
			
		// FIDO 요청
		// String type REG/AUTH/DEREG  사용 가능하나 reg 는 loginComplete, dereg 는 removeAll 에서 실행
		requestFido:function (type, userName, policyId) {
			if(typeof webInterface != "undefined") {
				webInterface.requestFido(type, userName, policyId);
			}
			else {
				// is Online Mode
				if(navigator.onLine) {
					Logger.error("MSG_ERR_APP_MODE".i18n());
				}
				// is Offline Mode
				else {
					Logger.error("MSG_ERR_OFFLINE".i18n());
				}
			}
		},
		
		// POLICY 요청
		// String type POLICY, REG_ALL, AUTH_ALL
		requestCommonOperation:function(type, userName) {
			if(typeof webInterface != "undefined") {
				webInterface.requestCommonOperation(type, userName);
			}
			else {
				// do nothing
			}
		},
		
		// progress 요청
		// boolean, string
		requestProgress:function(show, message) {
			if(typeof webInterface != "undefined") {
				webInterface.requestProgress(show, message);
			}
			else {
				if(show) {
					var loadingId = "loading";
					var loadingWrap = $("<div>").addClass("loadingWrap").attr("id", loadingId);
					var dimBg = $("<div>").addClass("dim");
					var loadingIcon = $("<i class='fa fa-spinner fa-spin loading'></i>");
					loadingWrap.append(dimBg);
					loadingWrap.append(loadingIcon);
					if(message) {
						var loadingMessage = $("<div>").addClass("message").text(message);
						loadingWrap.append(loadingMessage);
					}
					
					$("body").append(loadingWrap);
				}
				else {					
					var loadingId = "loading";
					if($("#" + loadingId).length) {
						$("#" + loadingId).remove();
					}
				}
			}
		},
		
		// popup 요청
		// string, string, string, string
		requestPopup:function(title, message, type, positive, negative) {
			if(typeof webInterface != "undefined") {
				if(typeof webInterface.requestPopupV2 != "undefined") {
					webInterface.requestPopupV2(title, message, type, positive, negative);
					return true;
				}
				return false;
			}
			else {
				return false;
			}
		},
		
		// 로그인 완료 전달 // username, mainUrl, token 저장
		// string, string, string, string
		requestLoginComplete:function(username, mainUrl, token) {
			if(typeof webInterface != "undefined") {
				webInterface.loginComplete(username, mainUrl, token);
			}
			else {
				location.assign(mainUrl);
			}
		},	
		
		// 초기화 요청 // username 및 token 삭제, fido 및 otp dereg..
		// string, string, string, string
		requestRemoveAll:function(username) {
			if(typeof webInterface != "undefined") {
				webInterface.deleteAll(username);
			}
			else {
				// do nothing
			}
		},
		
		// 화면 보안 안면 등록 (테스트 용도로 한정합니다...)
		requestRegFace:function(callback) {	// 결과는 callback(true) 또는 callback(false) 로 회신 
			if(typeof webInterface != "undefined") {
				webInterface.registerFace(callback);
			}
			else {
				// do nothing
			}
		},
		
		// 화면 보안 (화면 로딩이 완료된 후 호출해야 함..)
		// 얼굴 동록하지 않은 상태로 요청 시 얼굴 등록 후 동작..
		// true 시작  false 종료 
		requestBlurView:function(flag) {
			if(typeof webInterface != "undefined") {
				webInterface.requestBlurView(flag);
			}
			else {
				// do nothing
				if(flag) {
					var dimBg = $("<div>").addClass("fakeblur");
					$("body").append(dimBg);
				}
				else {
					$(".dim").remove();
				}
			}
		},
		
		// app 만 종료
		requestFinishApp:function() {
			if(typeof webInterface != "undefined") {
				webInterface.finish();
			}
			else {
				commonPopup.showPopup("LABEL_CONFIRM_TITLE".i18n(), "MSG_EXIT_APP".i18n(), "CONFIRM");
			}
		},
		
		// true : app data 삭제 후 app 종료, false : app 만 종료
		requestFinishApp:function(flag) {
			if(typeof webInterface != "undefined") {
				webInterface.finish(flag);
			}
			else {
				commonPopup.showPopup("LABEL_CONFIRM_TITLE".i18n(), "MSG_EXIT_APP".i18n(), "CONFIRM");
			}
		},

		
		// Log 요청
		logger:function(message) {
			if(typeof webInterface != "undefined") {
				webInterface.requestLog(message);
			}
			else {
				if(console && console.log) {
					console.log(message);
				}
			}
		}
	}
})();
