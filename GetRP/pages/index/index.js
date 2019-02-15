// pages/index/index.js
var httpUtil = require('../../utils/httpUtil.js');
var app = getApp();
Page({
    data:{
        boxHeight: '0rpx',
        msgBoxHeight: '100%',
        msglist:[],
        rplist: [],
        getMoney: 0,
        message: '',
        intoViewId:'',
        userId: httpUtil.getStore('userId'),
        userCount: '',
        animation:{}
    },
    onLoad:function(options){
        var that = this;
        app.onMessage(this.handleMsg);
        wx.getSystemInfo({
            success: function(res) {
                console.log(res);
                that.setData({
                    msgBoxHeight: (res.windowHeight - 120/res.pixelRatio)+"px"
                });
            }
        });
        
    },
    onReady:function(){
        wx.setNavigationBarTitle({
            title: '红包大厅'
        });
    },
    onShow:function(){
        app.socket();
    },
    handleMsg:function(msg,msglist){
        if( msg.msgId && msg.msgId.length>10 ){
            this.setData({ msglist: msglist });
            if( this.autoScroll ){
                this.setData({ intoViewId: "msg"+msg.msgId });
            }
        }else{
            if( msg.message.indexOf('在线')>=0 ){
                this.setData({ userCount:msg.message });
            }else{
                var objlist = this.data.msglist;
                objlist.push(msg);
                this.setData({
                    msglist:objlist
                });
            }
        }
    },
    scroll: function(e) {
        console.info(e);
    },
    lower:function(e){
        console.info(e);
    },
    menuTap:function(){
        if( this.data.boxHeight=='0rpx' ){
            this.setData({ boxHeight: 'auto' });
        }else{
            this.setData({ boxHeight: '0rpx' });
        }
    },
    hideBox:function(){
        this.setData({ boxHeight: '0rpx' });
    },
    input:function(e){
        this.setData({
            message: e.detail.value
        });
    },
    send:function(){
        //var that = this;
        var msg = this.data.message;
        this.setData({ message: '' });
        httpUtil.send( msg ,function(result){
            if( result ){
                httpUtil.msgSound();
            }
        });
    },
    autoScroll:true,
    onReachBottom:function(e){
        this.autoScroll = true;
    },
    getRp:function(e){
        //httpUtil.toast(true,e.currentTarget.id);
        var that = this;
        var objlist = this.data.msglist.filter(function(item){
            return item.rpId == e.currentTarget.id.substring(2);
        });
        if( objlist.length==1 ){
            if( objlist[0].type=='rp.equal' || objlist[0].type=='rp.luck' ){
                httpUtil.getRp(objlist[0].rpId, 0, '', '', function(money){
                    that.setData({ getMoney: money });
                    that.anima();
                    httpUtil.moneySound();
                });
            }else if( objlist[0].type=='rp.click' ){
                wx.navigateTo({
                    url: '../click/click?rpId='+objlist[0].rpId
                });
            }else if( objlist[0].type=='rp.shake' ){
                wx.navigateTo({
                    url: '../shake/shake?rpId='+objlist[0].rpId
                });
            }
        }
    },
    hidePop:function(e){
        //this.setData({ getMoney: 0 });
        if( e.detail.x>=40 && e.detail.y>=40 && e.detail.x<=75 && e.detail.y<=75 ){
            this.setData({ getMoney: 0 });
        }
    },
    msgTap:function(e){
        if( e.currentTarget.id=='msgExit' ){
            app.socket();
        }
    },
    anima:function(){
        var animation = wx.createAnimation({
            duration: 400,
            timingFunction: 'linear', // "linear","ease","ease-in","ease-in-out","ease-out","step-start","step-end"
            delay: 0,
            transformOrigin: '0% 100% 0%'
        });
        animation.scale(1.5, 1.5).step();
        this.setData({
            animation: animation.export()
        });
        setTimeout(function(){
            animation.scale(1.0, 1.0).step();
            this.setData({
                animation: animation.export()
            });
        }.bind(this),200);
    }


})