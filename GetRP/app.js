//app.js
var httpUtil = require('utils/httpUtil.js');
App({
    onLaunch: function () {
        //调用API从本地缓存中获取数据
        var logs = wx.getStorageSync('logs') || [];
        logs.unshift(Date.now());
        wx.setStorageSync('logs', logs);
    },
    msglist: [],
    socketOpen: false,
    socket:function(func){
        if( !this.socketOpen ){
            wx.connectSocket({
                url: httpUtil.wssUrl + "socket.ws",
                method: 'GET', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
                header:{
                    'cookie': 'token='+httpUtil.getStore('userId')
                },
                success: function(res){
                }
            });
        }
    },
    onMessage:function(func){
        this.checkSocket();
        var that = this;
            wx.onSocketOpen(function() {
                that.socketOpen = true;
            });
            wx.onSocketMessage(function(res) {
                console.info(res.data);
                var msg = JSON.parse(res.data);
                if( msg ){
                    if( msg.msgId && msg.msgId.length>10 ){
                        if( msg.type=='msg' || msg.type=='notice' ){
                        }else{
                            msg.rpId = msg.message;
                        }
                        that.msglist.push(msg);//.unshift()加到顶部，.push()加到底部
                        if( that.msglist.length>1000 ){
                            that.msglist.splice(0,that.msglist.length-1000);  //删除
                        }
                        typeof func == "function" && func(msg,that.msglist);
                    }else{
                        typeof func == "function" && func(msg,that.msglist);
                    }
                }
            });
            wx.onSocketClose(function() {
                that.socketOpen = false;
                var msg = {userId:httpUtil.getStore('userId'),msgId:'Exit',type:'msg',message:'您已离线，点击重连'};
                typeof func == "function" && func(msg, that.msglist);
                console.info("socket close");
            });
            wx.onSocketError(function(e) {
                wx.closeSocket();
            });
    },
    checkSocket:function(){
        var that = this;
        if( this.socketOpen ){
            wx.sendSocketMessage({
                data: 'check'
            });
        }
        setTimeout(function(){
            that.checkSocket();
        },30000);   //每55秒发送消息，防止断开连接
    }
});