( function( /*window, */document, undefined ) {
    'use strict';

    window.commonJs = ( function( options ) { // jshint ignore:line
        // Prevent duplication
        if ( window.commonJs ) {
            return window.commonJs;
        }

        // Internal properties
        // var properties = {};

        // Private Variables and Functions
        var internalFunctions = {
            getCurrentMinute: function() {
                var minutes = 1000 * 60,
                // hours = minutes * 60,
                // days = hours * 24,
                // years = days * 365,
                    d = new Date(),
                    t = d.getTime();

                return Math.round( t / minutes );
            },

            getCurrentSecond: function() {
                var seconds = 1000,
                // var minutes = 1000 * 60,
                // hours = minutes * 60,
                // days = hours * 24,
                // years = days * 365,
                    d = new Date(),
                    t = d.getTime();

                return Math.round( t / seconds );
            },

            updateAccessTime: function() {
                var userInfo = customStorage.read( 'USER' );
                if ( userInfo ) {
                    userInfo.loginTime = this.getCurrentSecond();
                }
            },

            transformToAssocArray: function( prmstr ) {
                var params = {},
                    prmarr = prmstr.split( '&' );
                for ( var i = 0; i < prmarr.length; i++ ) {
                    var tmparr = prmarr[i].split( '=' );
                    params[ tmparr[ 0 ] ] = tmparr[ 1 ];
                }
                return params;
            }
        };

        // Public Variables and Functions
        var externalFunctions = {
            PAGE_LIST: {
                find: function( value ) {
                    var url = value.split( '?' ),
                        keys = Object.keys( this );

                    for ( var i = 0; i < keys.length; i++ ) {
                        if ( url[ 0 ].includes( this[ keys[i] ] ) ) {
                            return keys[i];
                        }
                    }
                },
                PCOFF_AUTH: contextRoot + '/iam-saas/main/pcoff_auth.html',
                MAIN: contextRoot + '/iam-saas/main/main.html',
                LOGIN: contextRoot + '/iam-saas/main/login.html',
                WORKPLACE: contextRoot + '/iam-saas/square/square_home.html',
                WORKPLACE_INBOX: contextRoot + '/iam-saas/square/square_inbox.html',
                WORKPLACE_ATTACHMENT1: contextRoot + '/iam-saas/square/square_attachment1.html',
                WORKPLACE_ATTACHMENT2: contextRoot + '/iam-saas/square/square_attachment2.html',
                SETTING: contextRoot + '/iam-saas/main/setting.html',
                SETTING_TIMEOUT: contextRoot + '/iam-saas/main/setting_timeout.html',
                SETTING_POLICY: contextRoot + '/iam-saas/main/setting_policy.html',
                PC_OFF_REQUEST: contextRoot + '/user/pcoff/request',
                PC_OFF_CHECK: contextRoot + '/user/pcoff/status',
                PC_MAIN: contextRoot + '/iam-saas/portal/portal_main.html',
                PC_LOGIN: contextRoot + '/iam-saas/portal/portal_login.html',
                SINGLEPASS_SETTING: contextRoot + '/iam-saas/singlepass/singlepass_setting.html',
                SINGLEPASS_SETTING_DETAIL: contextRoot + '/iam-saas/singlepass/singlepass_setting_detail.html',
                DUTYFREE: contextRoot + '/iam-saas/dutyfree/dutyfree_main.html',
                DUTYFREE_PAYMENT: contextRoot + '/iam-saas/dutyfree/dutyfree_payment.html',
                DUTYFREE_SUCCESS: contextRoot + '/iam-saas/dutyfree/dutyfree_success.html',
                BANKING: contextRoot + '/iam-saas/banking/banking_main.html',
                BANKING_TRANSFER: contextRoot + '/iam-saas/banking/banking_transfer.html',
                BANKING_SUCCESS: contextRoot + '/iam-saas/banking/banking_success.html',
                FRAUD_SETTING: contextRoot + '/iam-saas/banking/fraud_setting.html',
                SHOPPING: contextRoot + '/iam-saas/shopping/shopping_main.html'
            },

            isLogedIn: function() {
                var userInfo;

                if ( this.isSessionTimeout() ) {
                    this.resetUserData();
                    return false;
                }

                userInfo = customStorage.read( 'USER' );
                if ( userInfo && userInfo.loginId ) {
                    return true;
                }
                return false;
            },

            resetUserData: function( isToast ) {
                var userInfo = customStorage.read( 'USER' );
                if ( userInfo ) {
                    customStorage.remove( 'USER' );
                    if ( isToast === true ) app.requestToast( 'User data has been cleared.' );
                    return true;
                }
                return false;
            },

            clearAllData: function( isToast ) {
                var userInfo = customStorage.read( 'USER' );
                if ( userInfo ) {
                    customStorage.clearAll( userInfo.loginId );
                    if ( isToast === true ) app.requestToast( 'All data has been cleared.' );
                    return true;
                }
                return false;
            },

            getUserInfo: function() {
                if ( this.isLogedIn() ) {
                    return customStorage.read( 'USER' );
                }
                return null;
            },

            setUserInfo: function( userInfo ) {
                if ( userInfo != null ) {
                    customStorage.write( 'USER', userInfo );
                }
            },

            getHistoryInfo: function() {
                var historyInfo = customStorage.read( 'HISTORY' ),
                    currentPageId = this.PAGE_LIST.find( window.location.href );

                if ( historyInfo == null || currentPageId === 'MAIN' ) {
                    customStorage.remove( 'HISTORY' );
                    historyInfo = {
                        history: []
                    };
                    customStorage.write( 'HISTORY', historyInfo );
                }
                return historyInfo;
            },

            addToHistory: function( url ) {
                var historyInfo = this.getHistoryInfo();
                if ( url ) {
                    historyInfo.history.push( url );
                    customStorage.write( 'HISTORY', historyInfo );
                }
            },

            isGalaxyNote7: function() {
                if ( navigator.userAgent.match( 'SAMSUNG|SM-[N|P|T|Z|G]930' ) != null ) {
                    return true;
                }
                return false;
            },

            getLoginTimeSecond: function() {
                var userInfo = customStorage.read( 'USER' );
                if ( userInfo ) {
                    return userInfo.loginTime;
                }
                return null;
            },

            getLoginTimeMinute: function() {
                var userInfo = customStorage.read( 'USER' );
                if ( userInfo ) {
                    return Math.round( userInfo.loginTime / 60 );
                }
                return null;
            },

            getSessionTimeoutSetting: function() {
                var sessionInfo = customStorage.read( 'SESSION' );
                if ( sessionInfo == null ) {
                    sessionInfo = {
                        timeout: 10 * 60 // 10 minutes.
                    };
                    customStorage.write( 'SESSION', sessionInfo );
                }
                return sessionInfo;
            },

            saveSessionTimeoutSetting: function( timeout ) {
                timeout *= 1;
                if ( timeout < 0 ) {
                    return false;
                }
                var sessionInfo = this.getSessionTimeoutSetting();
                sessionInfo.timeout = timeout;
                customStorage.write( 'SESSION', sessionInfo );
                return true;
            },

            isSessionTimeout: function() {
                var sessionInfo = this.getSessionTimeoutSetting(),
                    loginTime = this.getLoginTimeSecond();

                if ( loginTime + sessionInfo.timeout < internalFunctions.getCurrentSecond() ) {
                    return true;
                }
                return false;
            },

            movePage: function( url, flag ) {
                var currentPageId = this.PAGE_LIST.find( window.location.href ),
                    urlArr = url.split( '?' ),
                    historyInfo = this.getHistoryInfo(),
                    nextPageId = this.PAGE_LIST.find( url );

                console.log( 'pageId=' + currentPageId );

                if ( currentPageId !== 'LOGIN' && flag !== false ) {
                    historyInfo.history.push( window.location.href );
                    customStorage.write( 'HISTORY', historyInfo );
                }
                if ( this.isLogedIn() ) {
                    internalFunctions.updateAccessTime();
                    location.replace( url );
                } else if ( nextPageId === 'SETTING' || nextPageId === 'SETTING_POLICY' || nextPageId === 'SETTING_TIMEOUT' ) {
                    location.replace( url );
                } else {
                    if ( urlArr.length === 2 ) {
                        location.replace( this.PAGE_LIST.LOGIN + '?pageId=' + this.PAGE_LIST.find( urlArr[ 0 ] ) + '&' + urlArr[ 1 ] );
                        return;
                    }
                    location.replace( this.PAGE_LIST.LOGIN + '?pageId=' + this.PAGE_LIST.find( url ) );
                }
            },

            moveBack: function() {
                var currentPageId = this.PAGE_LIST.find( window.location.href ),
                    historyInfo = customStorage.read( 'HISTORY' ),
                    url;
                if ( currentPageId === 'MAIN' ) {
                    app.requestFinishApp( false );
                    return;
                }
                if ( this.isLogedIn() ) {
                    internalFunctions.updateAccessTime();
                }
                url = historyInfo.history.pop();
                customStorage.write( 'HISTORY', historyInfo );
                location.replace( url );
            },

            getSearchParameters: function() {
                var prmstr = window.location.search.substr( 1 );
                return prmstr != null && prmstr !== '' ? internalFunctions.transformToAssocArray( prmstr ) : {};
            },

            getCurrentSecond: function() {
                return internalFunctions.getCurrentSecond();
            },

            getFraudInfo: function() {
                var fraudInfo = customStorage.read( 'FRAUD' );
                if ( fraudInfo == null ) {
                    fraudInfo = {
                        isActive: false,
                        type: '', // timeout, geo, behavior
                        timeout: 30, // for timeout
                        startTime: 0, // for timeout
                        lastLocation: '', // for geo
                        currentLocation: '', // for geo
                        behavior: '', // finger, iris, face, voice, facevoice
                        userClick: false
                    };
                    customStorage.write( 'FRAUD', fraudInfo );
                }
                return fraudInfo;
            },

            setFraudInfo: function( fraudInfo ) {
                if ( fraudInfo != null ) {
                    customStorage.write( 'FRAUD', fraudInfo );
                }
            },

            getPolicyInfo: function() {
                var policyInfo = customStorage.read( 'POLICY' );
                if ( policyInfo == null ) {
                    policyInfo = this.isGalaxyNote7() === true ? 'finger' : 'InternalOnly';
                    customStorage.write( 'POLICY', policyInfo );
                }
                return policyInfo;
            },

            setPolicyInfo: function( policyInfo ) {
                if ( policyInfo ) {
                    customStorage.write( 'POLICY', policyInfo );
                }
            }
        };

        return externalFunctions;
    } )();
} )();
