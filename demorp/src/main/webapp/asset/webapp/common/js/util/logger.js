/*!
* javascript Logger 0.1
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
	var LOGGER_LEVEL  = {"ALL":0, "TRACE":1, "DEBUG":2, "INFO":3, "WARN":4, "ERROR":5, "FATAL":6, "OFF":7};
	function LoggerException(message) {
	   this.message = message;
	   this.name = "LoggerException";
	}
	
	window.Logger = (function(logLevel) {
		"use strict";	
		// Prevent duplication
		if(window.Logger) {
			return window.Logger;
		}
		
		// Internal properties
		var properties = {
				logLevel:LOGGER_LEVEL.INFO
		}
		if(logLevel) {
			properties.logLevel = logLevel;
			customStorage.write("LOGGER", properties);
		}
		else if(customStorage.isExist("LOGGER")) {
			var beforeProperties = customStorage.read("LOGGER");
			properties.logLevel = beforeProperties.logLevel; 
		}
		
		// Private Variables and Functions
		var internalFunctions = {
				setLogLevel:function(logLevel) {
					properties.logLevel = logLevel;
					customStorage.write("LOGGER", properties);
				},
				getLogLevel:function() {
					return properties.logLevel;
				},
				isLogEnabled:function(level) {
					return properties.logLevel <= level;
				},
				printMessage:function(message, extra_args) {
					if(console && console.log) {
						if(extra_args) {
							$(extra_args).each(function(index, replaceText) {
								if(index != 0) {
									message = message.replace("%" + (index - 1), replaceText);	
								}								
							});
						}
						app.logger(message);
					}
				}
		}

		// Public Variables and Functions
		var externalFunctions = {
			setErrorLevel:function() {
				return internalFunctions.setLogLevel(logLevel);
			},
			getErrorLevel:function() {
				return internalFunctions.getLogLevel();
			},
			trace:function(message) {
				if(LOGGER_LEVEL.TRACE >= internalFunctions.getLogLevel()) {
					internalFunctions.printMessage("[TRACE] " + message, arguments);
				}
			},
			isTraceEnabled:function() {
				return internalFunctions.isLogEnabled(LOGGER_LEVEL.TRACE);
			},
			info:function(message) {
				if(LOGGER_LEVEL.TRACE >= internalFunctions.getLogLevel()) {
					internalFunctions.printMessage("[INFO] " + message, arguments);
				}
			},
			isInfoEnabled:function() {
				return internalFunctions.isLogEnabled(LOGGER_LEVEL.INFO);
			},
			debug:function(message) {
				if(LOGGER_LEVEL.DEBUG >= internalFunctions.getLogLevel()) {
					internalFunctions.printMessage("[DEBUG] " + message, arguments);
				}
			},
			isDebugEnabled:function() {
				return internalFunctions.isLogEnabled(LOGGER_LEVEL.DEBUG);
			},
			warn:function(message) {
				if(LOGGER_LEVEL.WARN >= internalFunctions.getLogLevel()) {
					internalFunctions.printMessage("[WARN] " + message, arguments);
				}
			},
			isWarnEnabled:function() {
				return internalFunctions.isLogEnabled(LOGGER_LEVEL.WARN);
			},
			error:function(message) {
				if(LOGGER_LEVEL.ERROR >= internalFunctions.getLogLevel()) {
					internalFunctions.printMessage("[ERROR] " + message, arguments);
				}
			},
			isErrorEnabled:function() {
				return internalFunctions.isLogEnabled(LOGGER_LEVEL.ERROR);
			}
		}
		
		return externalFunctions;
	})();
})();