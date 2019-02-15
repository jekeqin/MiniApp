var httpUtil = require("../../utils/httpUtil.js");
Page({
    data:{
        time: 0,
        score: 0,
        start: false,
        startTime: '',
        stopTime: '',
        canStart: false,
        rpId: '',
        getMoney: 0 //获得金额
    },
    onLoad:function(options){
        var that = this;
        httpUtil.loadSet(options.rpId,function(sec){
            if( sec>0 ){
                that.setData({
                    time: sec,
                    canStart: true,
                    rpId: options.rpId
                });
            }
        });
    },
    onReady:function(){
        wx.setNavigationBarTitle({
            title: '摇一摇',
        });

    },
    start:function(){
        if( this.data.canStart ){
            this.shake();
            this.setData({
                start: true,
                startTime: httpUtil.dateTime()
            });
            this.readSec();
        }
    },
    readSec:function(){
        var that = this;
        setTimeout(function(){
            that.setData({ time: that.data.time-1 });
            if( that.data.time>0 ){
                that.readSec();
            }else{
                that.setData({
                    start: false,
                    stopTime: httpUtil.dateTime(),
                    canStart: false
                });
                that.submit();
                console.log( that.data.startTime+"  "+that.data.stopTime );
            }
        },1000);
    },
    shake:function(){
        var that = this;
        var minValue = 4;   //摇动基数
        wx.onAccelerometerChange(function(res){
            if( that.data.start ){
                var x = res.x, y = res.y, z = res.z;
                if( Math.abs(x)>=minValue || Math.abs(y)>=minValue || Math.abs(z)>=minValue){
                    console.log(res);
                    var score = that.data.score + parseInt((httpUtil.dist(x,y,z)*10).toFixed(0));
                    that.setData({
                        score: score
                    });
                    wx.getBackgroundAudioPlayerState({
                        success: function(res){
                            console.log(res);
                            if( res.status==0 )
                                httpUtil.shakeSound();
                            }
                    });
                }
            }
        });
    },
    submit:function(){
        var that =this;
        httpUtil.getRp(this.data.rpId, this.data.score, this.data.startTime, this.data.stopTime, function(money){
            that.setData({ getMoney: money });
            that.anima();
            httpUtil.moneySound();
        });
    },
    hidePop:function(e){
        if( e.detail.x>=40 && e.detail.y>=40 && e.detail.x<=75 && e.detail.y<=75 ){
            this.setData({ getMoney: 0 });
        }
    },
    anima:function(){
        var animation = wx.createAnimation({
            duration: 400,
            timingFunction: 'linear', // "linear","ease","ease-in","ease-in-out","ease-out","step-start","step-end"
            delay: 0,
            transformOrigin: '0% 50% 0%'
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