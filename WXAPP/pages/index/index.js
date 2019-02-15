// pages/index/index.js
var http = require('../../utils/http.js');
var util = require('../../utils/util.js');

Page({

    data: {
        areaName:'',
        advs: [],
        channels: [],
        vrs: [],
        activitys: [],
        themes: [],
        provs:[],
        citys:[],
        areas:[],
        areaIndex:[0,0,0]
    },

    onLoad: function (options) {

    },

    onReady: function () {
        this.refreshData();
        this.initArea();
    },

    onShow: function () {

    },

    tapSearch: function () {

    },

    refreshData: function(){
        var that = this;
        this.setData({ areaName: http.area().district });
        http.post(http.host.index, { areaCode: http.area().code }, function (json) {
            if (json.status == 1) {
                that.setData({
                    advs: json.data.placeholderList,
                    channels: json.data.industryList,
                    vrs: json.data.recommendVrList,
                    activitys: json.data.activityList,
                    themes: json.data.themeWithShopsList
                });
                wx.setStorage({
                    key: 'channels',
                    data: json.data.industryList
                });
            } else {
                wx.showToast({
                    title: json.showMessage, icon: 'none'
                });
            }
        });
    },

    tapScan: function () {
        wx.scanCode({
            onlyFromCamera: true,
            scanType: ['qrCode', 'barCode'],
            success: (res) => {
                console.log(res);
                console.log(res.result);
            }
        });
    },

    tapChannel: function (e) {
        var obj = this.data.channels[e.currentTarget.dataset.index];
        if (obj.type == null || obj.type == 1) {
            wx.setStorageSync('channelId', obj.industry_id);
            wx.switchTab({
                url: '/pages/shop/list',
            });
        }
    },

    tapAdv: function (e) {
        console.log(e);
        var obj = this.data.advs[e.currentTarget.dataset.index];
        if( obj.type==2 ){
            wx.navigateTo({
                url: '/pages/web/web?url=' + encodeURIComponent(obj.link)
            });
        }else if( obj.type==3 ){
            wx.setStorageSync('channelId', obj.link);
            wx.switchTab({
                url: '/pages/shop/list',
            });
        }else if( obj.type==4 ){
            wx.navigateTo({
                url: '/pages/shop/detail?id='+obj.link
            });
        }else{
            return;
        }
    },

    tapVr: function(e){
        var obj = this.data.vrs[e.currentTarget.dataset.index];
        wx.navigateTo({
            url: '/pages/shop/detail?id=' + obj.merchants_id
        });
    },

    tapActivity: function(e){
        var obj = this.data.activitys[e.currentTarget.dataset.index];
        if (obj.type==1 ){
            wx.navigateTo({
                url: '/pages/web/web?url=' + encodeURIComponent(obj.link)
            });
        }else if(obj.type==2){
            wx.setStorageSync('channelId', obj.link);
            wx.switchTab({
                url: '/pages/shop/list',
            });
        }else if(obj.type==3){
            wx.navigateTo({
                url: '/pages/shop/detail?id=' + obj.link
            });
        }
    },

    onPullDownRefresh: function () {
        this.refreshData();
    },

    onReachBottom: function () {

    },

    onShareAppMessage: function () {

    },
    
    initArea: function(e){
        this.reqArea("",1,(data)=>{
            let index1 = 0;
            for(let i=0;i<data.length;i++){
                if (data[i].area_code.indexOf(http.area().code.substring(0,2))==0 ){
                    this.setData({ areaIndex:[i,0,0] });
                    index1 = i;
                    break;
                }
            }
            this.reqArea(this.data.provs[index1].area_code, 2, data2=>{
                let index2 = 0;
                for (let i = 0; i < data2.length; i++) {
                    if (data2[i].area_code.indexOf(http.area().code.substring(0, 4)) == 0) {
                        this.setData({ areaIndex: [index1, i, 0] });
                        index2 = i;
                        break;
                    }
                }
                this.reqArea(this.data.citys[index2].area_code, 3, data3 => {
                    for (let i = 0; i < data3.length; i++) {
                        if (data3[i].area_code.indexOf(http.area().code) == 0) {
                            this.setData({ areaIndex: [index1, index2, i] });
                            break;
                        }
                    }
                });
            });
        });
    },

    reqArea: function(areaCode,step,callback){
        if( step==1 ){
            areaCode = "";
        }
        http.post(http.host.area, { areaCode: areaCode, step: step }, (json)=> {
            if (json.status == 1) {
                if( step==1 ){
                    this.setData({ provs:json.data });
                }else if( step==2 ){
                    this.setData({ citys: json.data });
                }else{
                    this.setData({ areas: json.data });
                }
                typeof callback == "function" && callback(json.data);
            }
        });
    },

    pickerStart: function(e){
        console.info("start",e);
    },

    pickerEnd: function(e){
        console.info("end",e);
    },

    pickerChange: function(e){
        console.info("change",e);
        var index = this.data.areaIndex;
        var value = e.detail.value;
        if (value[0] != index[0]) {     // 切换省
            index = [value[0], 0, 0]      // 更新选择
            this.setData({ areaIndex: index });
            this.reqArea(this.data.provs[value[0]].area_code, 2, data => {      // 加载市
                this.reqArea(this.data.citys[0].area_code, 3);  //加载区
            });
        } else if (value[1] != index[1]) {  // 切换市
            index[1] = value[1];
            index = [value[0], value[1], 0]
            this.setData({ areaIndex: index }); // 更新选择
            this.reqArea(this.data.citys[value[1]].area_code, 3);   //加载区
        }else{
            index[2] = value[2];
            this.setData({ areaIndex: index }); // 更新选择
        }
        console.log("change", "choose index", index);
        if( areas!=null && areas.length>0 ){
            console.info("current areaCode", areas[index[2]].area_code);
        }else{
            console.info("current areaCode", cityss[index[1]].area_code);
        }
    }
});
