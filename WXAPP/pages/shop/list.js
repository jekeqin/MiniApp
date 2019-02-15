// pages/shop/list.js
Page({

    /**
     * 页面的初始数据
     */
    data: {
        channels: { list: [], current: '0' }
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {
        var channels = wx.getStorageSync('channels');
        channels.unshift({ industry_id: '0', industry_name: '全部' });
        this.setData({ channels: { list: channels, current: '0' } });
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {
        var id = wx.getStorageSync('channelId');
        if (id!=null) {
            this.setData({ channels: { list: this.data.channels.list, current: id } });
        }
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

    channelSwitch: function (e) {
        var id = e.currentTarget.dataset.id;
        var obj = this.data.channels;
        obj.current = id;
        this.setData({ channels: obj });
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

    }
})