
var bmap = require('bmap-wx.min.js');

const formatTime = date => {
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const day = date.getDate()
    const hour = date.getHours()
    const minute = date.getMinutes()
    const second = date.getSeconds()

    return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}

const formatNumber = n => {
    n = n.toString()
    return n[1] ? n : '0' + n
}



function baiduRegeocoding() {
    var BMap = new bmap.BMapWX({
        ak: 'WkVkCgf2WaSaKbCOSIqcdA7QUiapopC9'
    });
    var fail = function (data) {
        console.log('Baidu weather fail!!!!')
    };
    var locationSuccess = function (data) {
        var obj = data.originalData.result;
        console.log(obj);

        var addressObj = {
            code: obj.addressComponent.adcode,
            city: obj.addressComponent.city,
            district: obj.addressComponent.district,
            address: obj.formatted_address,
            location: obj.location
        };
        wx.setStorageSync("addressObj", addressObj);
    }
    var weatherSuccess = function (data) {
        var weatherData = data.currentWeather[0];
        console.log(data);
        weatherData = '城市：' + weatherData.currentCity
            + '\n' + 'PM2.5：' + weatherData.pm25
            + '\n' + '日期：' + weatherData.date
            + '\n' + '温度：' + weatherData.temperature
            + '\n' + '天气：' + weatherData.weatherDesc
            + '\n' + '风力：' + weatherData.wind + '\n';
        console.log(weatherData);
    }
    /**
    BMap.weather({
        fail: fail,
        success: weatherSuccess
    });
    **/
    BMap.regeocoding({
        fail: fail,
        success: locationSuccess
    });
}



function sleep(numberMillis) {
    var now = new Date();
    var exitTime = now.getTime() + numberMillis;
    while (true) {
        now = new Date();
        if (now.getTime() > exitTime)
            return;
    }
}


module.exports = {
    formatTime: formatTime,
    sleep: sleep,
    baiduRegeocoding: baiduRegeocoding
}
