
const host = {
    base:'https://127.0.0.1/',
    index:'app/homepage/list.json',
    area:'app/area/districtList.json'
}

var addressObj = { code: '330104', city: '杭州市', district: '江干区', address: '金沙湖天街', location: { lng: '120.333808', lat: '30.315854' } };

function area() {
    var obj = wx.getStorageSync('addressObj');
    if (obj == null) {
        //wx.showToast({ title: '定位失败', icon: 'none' });
        obj = addressObj;
    }
    return obj;
}

function post(url, data, callback) {
    if( typeof data == "string" && data=='' ){
        data = {};
    }
    var map = area();
    data.lng = map.location.lng;
    data.lat = map.location.lat;
    console.log(url,data);
    wx.request({
        url: host.base + url,
        data: data,
        method: 'POST',
        header: {
            //'content-type': 'application/x-www-form-urlencoded'
        },
        dataType: 'json',
        success: (res) => {
            if( res.statusCode==200 ){
                console.log(res.data);
                typeof callback == "function" && callback(res.data);
            }else if( res.statusCode==500 ){
                wx.showToast({
                    title: '服务器网络异常',
                    icon: 'none'
                });
            }else if( res.statusCode==404 ){
                wx.showToast({
                    title: '404',
                    icon: 'none'
                });
            }
        }
    })
}

module.exports = {
    host: host,
    post: post,
    area: area
}
