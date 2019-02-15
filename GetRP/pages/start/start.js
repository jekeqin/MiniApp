//index.js
//获取应用实例
var httpUtil = require("../../utils/httpUtil.js");
Page({
    data: {
        motto: '......',
        userInfo: {},
        progress: 0,
    },
    //事件处理函数
    bindViewTap: function() {
        wx.navigateTo({
            url: '../logs/logs'
        });
    },
    onLoad: function () {
    },
    onReady:function(){
        var that = this;
        httpUtil.wxLogin(function(code){
            if( code!=0 ){
                that.setData({ progress:30 });
                httpUtil.sessionKey(code,function(data){
                    if( data!=0 ){
                        that.setData({ progress:60 });
                        httpUtil.getUser(function(user){
                            that.setData({
                                userInfo: user,
                                motto: '登录成功',
                                progress:90
                            });
                            httpUtil.regist(data, user, '',function(result){
                                if(result){
                                    that.setData({ progress:100 });
                                    wx.redirectTo({
                                        url: '../index/index'
                                    });
                                }else{
                                    that.setData({
                                        motto: '登录失败，请尝试重新启动'
                                    });
                                }
                            });
                        });
                    }
                });
            }
        });
        //下载音频文件
        //httpUtil.downloadSound();
    }
})
