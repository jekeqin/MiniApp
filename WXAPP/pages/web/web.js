// pages/web/web.js
Page({

    /**
     * 页面的初始数据
     */
    data: {
        url: ''
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        if( options.url ){
            var url = decodeURIComponent(options.url);
            this.setData({ url: url });
        }
    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {

    }
})