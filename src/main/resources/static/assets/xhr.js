function ajax(url, data, callback, method){
    console.log('ajax',url, data, method);
    // https://www.jianshu.com/p/536aea258b7f
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = ()=>{
        if(xhr.readyState==4){
            console.log(xhr);
            if((xhr.status >=200 && xhr.status < 300) || xhr.status ==304){
                var data = xhr.responseText;
                try{
                    callback && callback(JSON.parse(data));
                }catch(e){
                    callback && callback(data);
                }
            }else{
                console.error('error:'+ xhr.status)
            }
        }
    }
    xhr.upload.onprogress = (e)=>{
        if (e.lengthComputable) {
            console.debug('onprogress', (e.loaded / e.total) * 100 );
        }
    }
    //xhr.timeout = 3000;
    //xhr.withCredentials = false;

    if( method && method=='get' ){
        xhr.open('GET',url + '?' + data);
    }else{
        xhr.open('POST',url, true);
        xhr.setRequestHeader("Content-Type","application/json");
    }
    
    try{
	    let user = sessionStorage.getItem('user.cookie');
	    if( user ){
			let json = JSON.parse(user);
			xhr.setRequestHeader("token", json.token);
		}
	}catch(e){
		console.error(e);
	}

    if( data && typeof data === 'object' )
        xhr.send( JSON.stringify(data) );
    else
        xhr.send(data);
}