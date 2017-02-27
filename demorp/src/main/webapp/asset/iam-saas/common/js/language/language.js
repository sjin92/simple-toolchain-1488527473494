"use strict";

if (!String.prototype.format) {
	String.prototype.format = function(args) {
		return this.replace(/{(\d+)}/g, function(match, number) {
			return typeof args[number] != 'undefined'
				? args[number]
				: match
			;
		});
	};
}

var Language = {
		refresh:function() {
			$("[replaceText]").each(function(index, target) {
				var textId = $(target).attr("replaceText");
				$(target).text(textId.i18n());		
			});
			$("[replaceAttr]").each(function(index, target) {
				var attr = $(target).attr("replaceAttr");
				try {
					var jsonAttr = JSON.parse(attr);
					for(var key in jsonAttr) {
						$(target).attr(key, jsonAttr[key].i18n());	
					}	
				}
				catch(e) {
					logger.error("replaceAttr grammer error {0}", attr);
				}
			});
		},
		setLanguage:function(lang) {
			var languageSource = document.getElementById("LANG");
			if(languageSource) {
				languageSource.remove();
			}
			
			var head= document.getElementsByTagName('head')[0];
			var script= document.createElement('script');
			script.type= 'text/javascript';
			script.id = "LANGUAGE";
			switch(lang) {
				case "ko": 
					script.src= contextRoot + '/iam-saas/common/js/language/lang_ko.js';
					customStorage.write("LANGUAGE", "ko");
					break;
				case "en": 
					script.src= contextRoot + '/iam-saas/common/js/language/lang_en.js';
					customStorage.write("LANGUAGE", "en");
					break;
				default:
					script.src= contextRoot + '/iam-saas/common/js/language/lang_en.js';
					customStorage.write("LANGUAGE", "en");
			}
			
			head.appendChild(script);
		}
}

// multi language
String.prototype.i18n = function() {

	var key = this.valueOf();
	if(typeof LANG == "undefined" || !LANG || !(LANG[key])){
		return key;
	}
	var val = LANG[key];

	if (!val) val = key;

	if (arguments.length > 0) {
		val = val.format(arguments);
	}

	return val;
}

$(document).ready(function() {
	var lang = customStorage.read("LANGUAGE");
	Language.setLanguage(lang);
	
	var langRefreshTimerHandle = setInterval(function() {
		if(typeof LANG != "undefined") {
			Logger.info("Language resource file load completed");
			clearInterval(langRefreshTimerHandle);
			Language.refresh();
			
		}
	}, 100);
});
