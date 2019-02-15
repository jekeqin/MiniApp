var baseUrl = "https://127.0.0.1:100/WxApp/";
var wssUrl = "wss://127.0.0.1:100/WxApp/";



/**
 * 登录微信
 */
function wxLogin(func){
    wx.login({
      success: function(res){
          if( res.code ){
              typeof func == "function" && func(res.code);
          }else{
            typeof func == "function" && func(0);
          }
      },
      fail: function() {
          typeof func == "function" && func(0);
      },
      complete: function() {

      }
    });
}

/***
 * 通过code获取openid
 */
function sessionKey(code,func){
    wx.request({
        url: baseUrl+'json/weixin/code.json',
        data: { code: code},
        method: 'GET', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
        // header: {}, // 设置请求的 header
        success: function(res){
            if( res.statusCode==200 && res.data.code==200 ){
                store('sessionKey', res.data.value);
                typeof func == "function" && func(res.data.value.openid);
            }else{
                typeof func == "function" && func(0);
            }
        },
        fail: function(e) {
            typeof func == "function" && func(0);
        },
        complete: function(e) {
            console.info(e);
        }
    });
}

function getUser(func){
    wx.getUserInfo({
        success: function (res) {
            store("wxUser", res.userInfo);
            typeof func == "function" && func(res.userInfo);
        }
    });
}

/***
 * 注册用户到系统
 */
function regist(openId,user,mobile,func){
    wx.request({
        url: baseUrl + "json/user/regist.json",
        data: {
            'obj.openId': openId,
            'obj.nickName': user.nickName,
            'obj.logo': user.avatarUrl,
            'obj.mobile': mobile
        },
        method: 'POST', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
        header: {
            'content-type':'application/x-www-form-urlencoded'
        }, // 设置请求的 header
        success: function(res){
            if( res.statusCode==200 && res.data.code==200 ){
                store('userId', res.data.value);
                typeof func == "function" && func(true);
            }else{
                typeof func == "function" && func(false);
            }
        },
        fail: function() {
            typeof func == "function" && func(false);
        },
        complete: function(res) {
            console.info(res);
        }
    });
}

/**
 * 我的钱包
 */
function wallet(func){
    wx.request({
      url: baseUrl + "json/wallet/money.json",
      method: 'POST', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
      header: {
          'content-type':'application/x-www-form-urlencoded',
          'cookie': 'token='+getStore('userId')
      }, // 设置请求的 header
      success: function(res){
          typeof func == "function" && func(res.data.value.money/100);
      }
    });
}

/***
 * 加入缓存
 */
function store(key,value){
    wx.setStorage({
      key: key,
      data: value,
      success: function(res){
      },
      fail: function() {
      },
      complete: function() {
      }
    });
}

/***
 * 获取缓存
 */
function getStore(key){
    try{
        var value = wx.getStorageSync(key);
        if( value ){
            return value;
        }
    }catch(e){

    }
    return null;
}

/***
 * 发送消息
 */
function send(msg,func){
    wx.request({
        url: baseUrl + 'json/message/send.json',
        data: {
            message: msg
        },
        method: 'POST', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
        header: {
            'content-type':'application/x-www-form-urlencoded',
            'cookie': 'token='+getStore('userId')
        }, // 设置请求的 header
        success: function(res){
            //console.log(res.data);
            if( res.statusCode==200 && res.data.code==200 ){
                typeof func == "function" && func(true);
            }else{
                typeof func == "function" && func(false);
            }
        }
    });
}

/**
 * 发红包
 */
function create(value,rpType,time,func){
    wx.request({
        url: baseUrl + 'json/rp/create.json',
        data: {
            money: value.money * 100,     //单位：分
            allNum: value.number,       //单位：个
            'type': rpType, 
            'game.gameSec': value.sec,         //单位：秒
            'game.perMoney': value.per*100,     //单位：分
            startTime: date()+' '+time+':00'    //开始时间
        },
        method: 'POST', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
        header: {
            'content-type':'application/x-www-form-urlencoded',
            'cookie': 'token='+getStore('userId')
        }, // 设置请求的 header
        success: function(res){
            if( res.statusCode==200 && res.data.code==200 ){
                toast(true, res.data.msg);
                typeof func == "function" && func(true);
            }else{
                toast(false, res.data.msg);
                typeof func == "function" && func(false);
            }
        }
    });
}
/**
 * 重发红包
 */
function reCreate(rpId,func){
    wx.request({
        url: baseUrl + 'json/rp/reSend.json',
        data: { rpId: rpId },
        method: 'POST', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
        header: {
            'content-type':'application/x-www-form-urlencoded',
            'cookie': 'token='+getStore('userId')
        }, // 设置请求的 header
        success: function(res){
            if( res.statusCode==200 && res.data.code==200 ){
                toast(true, res.data.msg);
                typeof func == "function" && func(true);
            }else{
                toast(false, res.data.msg);
                typeof func == "function" && func(false);
            }
        }
    });
}

/**
 * 领红包
 */
function getRp(rpId,score,timeStart,timeStop,func){
    wx.request({
        url: baseUrl + 'json/rp/get.json',
        data: {
            'rpId': rpId,
            'play.playTime': timeStart,
            'play.overTime': timeStop,
            'play.playScore': score
        },
        method: 'POST', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
        header: {
            'content-type':'application/x-www-form-urlencoded',
            'cookie': 'token='+getStore('userId')
        }, // 设置请求的 header
        success: function(res){
            console.info(res.data);
            if( res.statusCode==200 ){
                if( res.data.code==200 ){
                    typeof func == "function" && func(res.data.value/100);
                }else{
                    toast(false, res.data.msg);
                }
            }
        }
    });
}
/**
 * 获取红包游戏设定
 */
function loadSet(rpId,func){
    wx.request({
        url: baseUrl + 'json/rp/set.json',
        data: { rpId: rpId },
        method: 'GET', // OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
        success: function(res){
            if( res.statusCode==200 ){
                if( res.data.code==200 ){
                    typeof func == "function" && func(res.data.value.gameSec);
                }else{
                    typeof func == "function" && func(0);
                    toast(false, res.data.msg);
                }
            }else{
                typeof func == "function" && func(0);
            }
        }
    });
}

function dateTime() {
    var date = new Date();
    var year = date.getFullYear()
    var month = date.getMonth() + 1
    var day = date.getDate()

    var hour = date.getHours()
    var minute = date.getMinutes()
    var second = date.getSeconds()

    return [year, month, day].map(formatNumber).join('-') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}
function date() {
    var date = new Date();
    var year = date.getFullYear()
    var month = date.getMonth() + 1
    var day = date.getDate()

    return [year, month, day].map(formatNumber).join('-')
}
function now(sec) {
    var date = new Date();

    var year = date.getFullYear()
    var month = date.getMonth() + 1
    var day = date.getDate()

    var hour = date.getHours()
    var minute = date.getMinutes()
    var second = date.getSeconds()+sec

    return [hour, minute].map(formatNumber).join(':')
}
function formatNumber(n) {
    n = n.toString()
    return n[1] ? n : '0' + n
}

function dist(x,y,z){
    x = Math.abs(x);
    y = Math.abs(y);
    z = Math.abs(z);
    var e = Math.sqrt( x*x + y*y );
    var d = (Math.sqrt( z*z + e*e ) - 1).toFixed(3);    //减去 (0,0,1)的距离
    if( d<0 ){
        d = 0;
    }
    return d;  //重力坐标点与原点(0,0,0)的距离
}

function toast(succ,msg){
    wx.showToast({
        title: msg,
        icon: succ?'success':'loading',
        duration: 0
    });
    setTimeout(function(){
        wx.hideToast()
    },1500);
}

function moneySound(){
    playAudio("getMoney",'source/get_money.mp3');
}
function msgSound(){
    playAudio("send", 'source/sight_send.wav');
}
function clickSound(){
    playAudio("click", 'source/click.wav');
}
function shakeSound(){
    playAudio("shake", 'source/shake.mp3');
}

/**
 * 播放声音
 */
function playAudio(key,netPath){
    var filePath = getStore(key);
    if( filePath!=null ){
        wx.getSavedFileInfo({
            filePath: filePath,
            success: function(res){
                wx.playVoice({ filePath: filePath });
            },
            fail: function(e) {
                playBackgroundAudio(netPath);
            }
        });
    }else{
        playBackgroundAudio(netPath);
    }
}
/***
 * 后台播放音乐
 */
function playBackgroundAudio(netPath){
    wx.stopBackgroundAudio();
    wx.playBackgroundAudio({
        dataUrl: baseUrl + netPath,
        success: function(res){
        }
    });
}

function download(name,filePath,func){
    wx.downloadFile({
        url: baseUrl + filePath,
        type: 'audio', // 下载资源的类型，用于客户端识别处理，有效值：image/audio/video
        success: function(res){
            wx.saveFile({
                tempFilePath: res.tempFilePath,
                success: function(res){
                    store(name , res.savedFilePath);
                }
            });
        },
        complete:function(){
            typeof func == "function" && func();
        }
    });
}
function downloadSound(){
    // 嵌套调用，用于解决网络并发数量问题
    download("getMoney","source/get_money.mp3",function(){
        download("send","source/sight_send.wav",function(){
            download("click","source/click.wav",function(){
                download("shake","source/shake.mp3");
            });
        });
    });
}

module.exports = {
  baseUrl:baseUrl,
  wssUrl:wssUrl,

  wxLogin: wxLogin,
  sessionKey: sessionKey,
  getUser:getUser,
  regist: regist,

  wallet: wallet,
  send: send,
  create: create,
  loadSet:loadSet,
  getRp:getRp,
  reCreate:reCreate,

  store: store,
  getStore: getStore,
  now:now,
  dateTime:dateTime,
  toast:toast,
  dist:dist,

  moneySound:moneySound,
  msgSound:msgSound,
  clickSound:clickSound,
  shakeSound:shakeSound,
  downloadSound:downloadSound
}