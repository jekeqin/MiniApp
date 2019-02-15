// pages/normal/normal.js
var httpUtil = require('../../utils/httpUtil.js');
var app = getApp();
Page({
    data:{
        types: ['普通红包','拼手气','摇一摇','拼手速'],
        typeStr:['equal', 'luck', 'shake', 'click'],
        index: 0,
        wallet: 0,
        visibility: 'hidden',
        now: httpUtil.now(),  //当前时间
        time: httpUtil.now() //当前时间,选定时间
    },
    onLoad:function(options){
        var that = this;
        this.setData({
            index: options.index,
            visibility: options.index<=1?'hidden':'visible'
        });
        httpUtil.wallet(function(money){
            that.setData({ wallet: money?money:0});
        });
    },
    onReady:function(){
        wx.setNavigationBarTitle({
            title: '发红包'
        });
    },
    onShow:function(){
        this.setData({
            now: httpUtil.now(),  //当前时间
            time: httpUtil.now() //当前时间,选定时间
        });
    },
    typeChange:function(e){
        this.setData({ index: e.detail.value });
        if( e.detail.value>1 ){
            this.setData({ visibility: 'visible' });
        }else{
            this.setData({ visibility: 'hidden' });
        }
    },
    timeChange:function(e){
        this.setData({ time: e.detail.value });
    },
    submit:function(e){
        var value = e.detail.value;
        if( value.money && value.money>=0.01 && value.money<=5000 ){
            if( this.data.index<=1 ){
                if( value.number && value.number>=1 && value.number<=1000 ){
                    httpUtil.create(value,this.data.typeStr[this.data.index],this.data.time,function(result){
                        if(result){
                            httpUtil.moneySound();
                            wx.navigateBack({
                                delta: 1 // 回退前 delta(默认为1) 页面
                            });
                        }
                    });
                }else{
                    httpUtil.toast(false,'个数: 1-1000');
                }
            }else{
                if( value.sec && value.sec>=1 && value.sec<=60 ){
                    var maxPer = (value.money / (value.sec * (this.data.index==2?100:10))).toFixed(2);
                    if( maxPer < 0.01 ){ maxPer = 0.01; }
                    console.log( value.money + " / " + (value.sec * (this.data.index==2?100:10)) + " = " + maxPer );
                    if( value.per && value.per>=0.01 && value.per<=maxPer ){
                        httpUtil.create(value,this.data.typeStr[this.data.index],this.data.time,function(result){
                            if(result){
                                httpUtil.moneySound();
                                wx.navigateBack({
                                    delta: 1 // 回退前 delta(默认为1) 页面
                                });
                            }
                        });
                    }else{
                        httpUtil.toast(false,'基数: 0.01-'+maxPer);
                    }
                }else{
                    httpUtil.toast(false,'限制: 1-60');
                }
            }
        }else{
            httpUtil.toast(false,'总额: 0.01-5000');
        }
    }

})