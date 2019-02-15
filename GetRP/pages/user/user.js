var httpUtil = require('../../utils/httpUtil.js');
var app = getApp();
Page({
    data:{
        userInfo:{},
        controlIndex: 0,
        wallet: 0.00,
        getList:{ next:true, onload:false, page:1, objlist:[] },
        createList:{ next:true, onload:false, page:1, objlist:[] },
        hisList:{ next:true, onload:false, page:1, objlist:[] },
        msglist:{ next:true, onload:false, page:1, objlist:[] }
    },
    onLoad: function () {
        this.setData({
            userInfo: httpUtil.getStore('wxUser')
        });
        this.loadWallet();
        this.loadGet();
    },
    controlClick:function(e){
        if( e.currentTarget.id=='control0' ){
            this.setData({ controlIndex: 0 });
            if( this.data.getList.page==1 ){
                this.loadMore();
            }
        }else if( e.currentTarget.id=='control1' ){
            this.setData({ controlIndex: 1 });
            if( this.data.createList.page==1 ){
                this.loadMore();
            }
        }else if( e.currentTarget.id=='control2' ){
            this.setData({ controlIndex: 2 });
            if( this.data.hisList.page==1 ){
                this.loadMore();
            }
        }else if( e.currentTarget.id=='control3' ){
            this.setData({ controlIndex: 3 });
            if( this.data.msglist.page==1 ){
                this.loadMore();
            }
        }
    },
    loadGet:function(){
        var that = this;
        var obj = this.data.getList;
        if( !obj.onload && obj.next && obj.page<51 ){
            that.setData({ getList:{ next:true, onload:true, page:obj.page, objlist:obj.objlist } });
            wx.request({
                url: httpUtil.baseUrl + 'json/rp/getlist.json',
                data: {
                    'page.size': 10,
                    'page.page': obj.page
                },
                method: 'GET', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
                header: {
                    'cookie': 'token='+httpUtil.getStore('userId')
                }, // 设置请求的 header
                success: function(res){
                    if( res.statusCode==200 && res.data.code==200 ){
                        var objlist = obj.objlist;
                        res.data.value.forEach(function(item){
                            objlist.push(item);
                        });
                        if( res.data.value.length<10 ){
                            that.setData({ getList:{ next:false, onload:false, page:obj.page+1, objlist:objlist } });
                        }else{
                            that.setData({ getList:{ next:true, onload:false, page:obj.page+1, objlist:objlist } });
                        }
                    }
                }
            });
        }
    },
    loadCreate:function(){
        var that = this;
        var obj = this.data.createList;
        if( !obj.onload && obj.next && obj.page<51 ){
            that.setData({ createList:{ next:true, onload:true, page:obj.page, objlist:obj.objlist } });
            wx.request({
                url: httpUtil.baseUrl + 'json/rp/createlist.json',
                data: {
                    'page.size': 10,
                    'page.page': obj.page
                },
                method: 'GET', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
                header: {
                    'cookie': 'token='+httpUtil.getStore('userId')
                }, // 设置请求的 header
                success: function(res){
                    if( res.statusCode==200 && res.data.code==200 ){
                        var objlist = obj.objlist;
                        res.data.value.forEach(function(item){
                            objlist.push(item);
                        });
                        if( res.data.value.length<10 ){
                            that.setData({ createList:{ next:false, onload:false, page:obj.page+1, objlist:objlist } });
                        }else{
                            that.setData({ createList:{ next:true, onload:false, page:obj.page+1, objlist:objlist } });
                        }
                    }
                }
            });
        }
    },
    loadHistory:function(){
        var that = this;
        var obj = this.data.hisList;
        if( !obj.onload && obj.next && obj.page<51 ){
            that.setData({ hisList:{ next:true, onload:true, page:obj.page, objlist:obj.objlist } });
            wx.request({
                url: httpUtil.baseUrl + 'json/wallet/history.json',
                data: {
                    'page.size': 10,
                    'page.page': obj.page
                },
                method: 'GET', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
                header: {
                    'cookie': 'token='+httpUtil.getStore('userId')
                }, // 设置请求的 header
                success: function(res){
                    if( res.statusCode==200 && res.data.code==200 ){
                        var objlist = obj.objlist;
                        res.data.value.forEach(function(item){
                            objlist.push(item);
                        });
                        if( res.data.value.length<10 ){
                            that.setData({ hisList:{ next:false, onload:false, page:obj.page+1, objlist:objlist } });
                        }else{
                            that.setData({ hisList:{ next:true, onload:false, page:obj.page+1, objlist:objlist } });
                        }
                    }
                }
            });
        }
    },
    loadMsg:function(){
        var that = this;
        var obj = this.data.msglist;
        var msgId = obj.objlist.length>0?(obj.objlist[obj.objlist.length-1].msgId):'0'
        if( !obj.onload && obj.next && obj.page<51 ){
            that.setData({ msglist:{ next:true, onload:true, page:obj.page, objlist:obj.objlist } });
            wx.request({
                url: httpUtil.baseUrl + 'json/message/local.json',
                data: {
                    'page.size': 10,
                    'page.page': obj.page
                },
                method: 'GET', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
                header: {
                    'cookie': 'token='+httpUtil.getStore('userId')
                }, // 设置请求的 header
                success: function(res){
                    if( res.statusCode==200 && res.data.code==200 ){
                        var objlist = obj.objlist;
                        res.data.value.forEach(function(item){
                            objlist.push(item);
                        });
                        if( res.data.value.length<10 ){
                            that.setData({ msglist:{ next:false, onload:false, page:obj.page+1, objlist:objlist } });
                        }else{
                            that.setData({ msglist:{ next:true, onload:false, page:obj.page+1, objlist:objlist } });
                        }
                    }
                },
                fail:function(e){
                    console.log(e);
                }
            });
        }
    },
    loadWallet:function(){
        var that = this;
        httpUtil.wallet(function(money){
            that.setData({ wallet: money});
        });
    },
    reSend:function(e){
        httpUtil.reCreate( e.currentTarget.id.substring(3), function(result){
            if( result ){
                wx.navigateBack({ delta: 1 });
            }
        });
    },
    onPullDownRefresh:function(){
        this.setData({
            getList:{ next:true, onload:false, page:1, objlist:[] },
            createList:{ next:true, onload:false, page:1, objlist:[] },
            hisList:{ next:true, onload:false, page:1, objlist:[] },
            msglist:{ next:true, onload:false, page:1, objlist:[] }
        });
        this.loadMore();
    },
    onReachBottom:function(){
        this.loadWallet();
        this.loadMore();
    },
    loadMore:function(){
        if( this.data.controlIndex==0 ){
            this.loadGet();
        }else if( this.data.controlIndex==1 ){
            this.loadCreate();
        }else if( this.data.controlIndex==2 ){
            this.loadHistory();
        }else if( this.data.controlIndex==3 ){
            this.loadMsg();
        }
    }
})