// pages/shop/detail.js
Page({

    /**
     * 页面的初始数据
     */
    data: {
        id:''
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        console.log('options',options);
        if( options.id ){
            this.setData({ id: options.id });
            this.requestDetail();
        }
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {

    },

    requestDetail: function(){

    },

    tapPay: function () {
        wx.navigateTo({
            url: '/pages/order/create',
        });
    }
})