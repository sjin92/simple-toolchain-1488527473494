/*!
* javascript simple LayerPopup 0.1
* 
* Released under Free License
* 
* Author : JooYoung, Lee (spec98@hanmail.net, jy98.lee@samsung.com)
* Created : 2016-06-22          
* Updated : 2016-07-04      
*
*/

(function(/*window, */document, undefined){
	"use strict";

	window.commonPopup = (function(options) {
		"use strict";	
		// Prevent duplication
		if(window.commonPopup) {
			return window.commonPopup;
		}
		
		// Internal properties
		var properties = {
	
		}
		// Private Variables and Functions
		var internalFunctions = {
				popIdList:{},
				generatePopId:function() {
					var popId = 1;
					do {
						if(!internalFunctions.popIdList["commonPop_" + popId]) {
							internalFunctions.popIdList["commonPop_" + popId] = true;
							break;
						}
					} while(++popId < 20);	// Max 20
					return "commonPop_" + popId;
				},
				removePopId:function(popId) {
					delete internalFunctions.popIdList[popId];
				},
				showPopup:function(options) {
					// Call capp popup
					if(app.requestPopup(options.title, options.message, options.type, options.okFnName, options.noFnName)) {
						return "commonPop_" + "appPopup";
					}
					
					// Generate UniqueId
					var popupId = this.generatePopId();
					
					var dialogMain = $("<div>").addClass("modal fade").attr("role", "dialog").attr("id", popupId);
					var dialogWrap = $("<div>").addClass("modal-dialog");
					var dialogcontent = $("<div>").addClass("modal-content");
					
					// Build Header area
					var dialogHeader = $("<div>").addClass("modal-header");
					var dialogHeaderButton = $("<button>").addClass("close").attr("type","button").attr("data-dismiss", "modal");
					var dialogHeaderTitle = $("<h4>").addClass("modal-title").text(options.title);
					dialogHeader.append(dialogHeaderButton);
					dialogHeader.append(dialogHeaderTitle);
					dialogcontent.append(dialogHeader);
					
					// Build Body area		
					var dialogBody = $("<div>").addClass("modal-body");
					var dialogBodyContent = $("<p>").html(options.message);
					dialogBody.append(dialogBodyContent);
					dialogcontent.append(dialogBody);
					
					// Build Footer area
					var dialogFooter = $("<div>").addClass("modal-footer");
					switch(options.type) {
						case "CONFIRM":
							// YES
							var dialogFooterYesButton = $("<button>").addClass("btn btn-primary").attr("type","button").attr("data-dismiss", "modal").text("LABEL_POPUP_YES".i18n());
							if(typeof options.okFnName == "string") {
								dialogFooterYesButton.click(function() {
									logger.info("user cliked yes button");
									eval(options.okFnName + "()");
								});
							}
							else if(typeof options.okFnName == "function") {
								dialogFooterYesButton.click(function() {
									logger.info("user cliked yes button");
									options.okFnName();
								});
							}
							dialogFooter.append(dialogFooterYesButton);
							
							// No
							var dialogFooterNoButton = $("<button>").addClass("btn btn-default").attr("type","button").attr("data-dismiss", "modal").text("LABEL_POPUP_NO".i18n());
							if(typeof options.okFnName == "string") {
								dialogFooterNoButton.click(function() {
									logger.info("user cliked no button");
									eval(options.okFnName + "()");
								});
							}
							else if(typeof options.noFnName == "function") {
								dialogFooterNoButton.click(function() {
									logger.info("user cliked no button");
									options.noFnName();
								});
							}
							dialogFooter.append(dialogFooterNoButton);

							break;
						case "ERROR":
							var dialogFooterButton = $("<button>").addClass("btn btn-danger").attr("type","button").attr("data-dismiss", "modal").text("LABEL_POPUP_OK".i18n());
							if(typeof options.okFnName == "string") {
								dialogFooterButton.click(function() {
									logger.info("user cliked ok button");
									eval(options.okFnName + "()");
								});
							}
							else if(typeof options.okFnName == "function") {
								dialogFooterButton.click(function() {
									logger.info("user cliked ok button");
									options.okFnName();
								});
							}								
							dialogFooter.append(dialogFooterButton);
							break;							
						case "OK":
						default:
							var dialogFooterButton = $("<button>").addClass("btn btn-success").attr("type","button").attr("data-dismiss", "modal").text("LABEL_POPUP_OK".i18n());
							if(typeof options.okFnName == "string") {
								dialogFooterButton.click(function() {
									logger.info("user cliked ok button");
									eval(options.okFnName + "()");
								});
							}
							else if(typeof options.okFnName == "function") {
								dialogFooterButton.click(function() {
									logger.info("user cliked ok button");
									options.okFnName();
								});
							}							
							dialogFooter.append(dialogFooterButton);
					}
					
					dialogFooter.append(dialogFooterButton);
					dialogcontent.append(dialogFooter);
					
					dialogWrap.append(dialogcontent);
					dialogMain.append(dialogWrap);
					
					$("document").append(dialogMain);
					
					dialogMain.on("hidden.bs.modal", function() {
						internalFunctions.removePopId(popupId);
						dialogMain.remove();
					});
					
					dialogMain.modal({backdrop: "static"});
					
					return popupId;
				},
				hidePopup:function(popupId) {
					var targetDialog;
					if(popupId) {						
						targetDialog = $("#" + popupId);
					}
					else {
						targetDialog = $("div[role=dialog]");
					}
					targetDialog.each(function() {
						$(this).modal("hide");
					});					
				}
				
		}
		
		// Public Variables and Functions
		var externalFunctions = {
			showPopup:function(title, message, type, okFnName, noFnName) {
				return internalFunctions.showPopup({"title":title, "message":message, "type":type, "okFnName":okFnName, "noFnName":noFnName});
			},
			hidePopup:function(popupId) {
				return internalFunctions.hidePopup(popupId);
			}
		
		};
		
		return externalFunctions;
	})();	
})();