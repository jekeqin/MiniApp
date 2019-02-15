// pages/public/login.js
var app = getApp();
Page({

  /**
   * 页面的初始数据
   */
  data: {
  
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
  
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
  
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
  
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
  
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {
  
  },

  goHome: function() {
    wx.switchTab({
        url: '/pages/index/index',
    });
  },

  formSubmit: function(e) {
    console.log("form",e.detail.value);
    app.token("token");
    if (app.redirect.indexOf('user/user') > 0 || app.redirect.indexOf('order/list') > 0 || app.redirect.indexOf('index/index') > 0 ){
        wx.switchTab({
            url: '/' + app.redirect
        });
    }else{
        wx.redirectTo({
            url: '/' + app.redirect,
        });
    }
  }

})