mini.parse();
var grid = mini.get("datagrid");
//查询
function search() {
    var form1 = new mini.Form("#searchForm");//提交表单数据
    var data = form1.getData();  //获取表单多个控件的数据
    grid.setUrl(getRootPath() + "/procurement/service/purchaseInfo/queryPurchaseInfoByResult");
    grid.load({companyNo : data.companyNo, AS : data.AS});
    grid.on("drawcell", function (e) {
        var record = e.record,
            column = e.column,
            field = e.field,
            value = e.value;
        //将产品路径替换成图片
        if (column.field == "picturePath" && value != '' && value != 'undefined') {
            e.cellHtml = "<img border='0' src='"+ getRootPath() +  "/procurement/image/" + value.split('/')[value.split('/').length-1] + "' alt='picture' width='160' height='100'>";
        }
        if (column.field == "supplier" && value != '' && value != 'undefined') {
            //e.cellHtml = "<input property='editor' class='mini-combobox' style='width:100%;' data='" + e.record.supplierList +"' value='"+ e.value +"'/>";
            // e.cellHtml = e.value;
            // e.column.editor = {};
            // e.column.editor.cls = "mini-combobox";
            // e.column.editor.data = e.record.supplierList;
            // e.column.editor.style = "width: 100%;";
            // e.column.editor.type = "combobox";
            // e.column.__editor.data = eval(e.record.supplierList);
            //mini.parse();
            var temps = "<select name=\"select-supplier\" id=\"select-supplier" + e.record.companyNo + "\">"
            var array = eval(e.record.supplierList);
            for(var i = 0; i < array.length; i++){
                var selected = "";
                if(array[i].id == e.value){
                    selected = "selected";
                }
                temps = temps + "<option " + selected +" value=\"" + array[i].text +"\" >" + array[i].id + "</option>";
            }
            e.cellHtml = temps + "</select>";
        }
        //显示换行
        if (column.field == "aS" && value != '' && value != 'undefined') {
            //e.cellHtml = e.value.replace(/\n/g, "<br>");
        }
        if (column.field == "wOODAUTO" && value != '' && value != 'undefined') {
            //e.cellHtml = e.value.replace(/\n/g, "<br>");
        }
    });

}
function reset(){
    mini.get("companyNo").setValue("");
    mini.get("AS").setValue("");
    //mini.get("WOODAUTO").setValue("");
}
function submit(){
    grid.commitEdit();
    var data = grid.getData();  //获取表单多个控件的数据
    for(var i = 0; i < data.length; i++){
        data[i].supplier = $("#select-supplier" + data[i].companyNo).find("option:selected").text();
        data[i].buyUnitPrice = $("#select-supplier" + data[i].companyNo).val();
    }
    $.ajax({
        url: getRootPath() + "/procurement/service/purchaseInfo/updatePurchaseInfo",
        data: JSON.stringify(data),
        contentType:"application/json",
        dataType:"json",
        type : "POST",
        success: function (redata) {
            alert(redata.message);
            search();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(jqXHR.responseText);
        }
    });
}
function getNoResultAS(){
    var form1 = new mini.Form("#searchForm");//提交表单数据
    var data = form1.getData();  //获取表单多个控件的数据
    $.ajax({
        url: getRootPath() + "/procurement/service/purchaseInfo/getNoResultAS",
        data: {companyNo : data.companyNo, AS : data.AS},
        dataType:"json",
        type : "POST",
        success: function (redata) {
            mini.alert(redata.data);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(jqXHR.responseText);
        }
    });
}
//导出采购信息
function exportExcel() {
    try{
        var form1 = new mini.Form("#searchForm");//提交表单数据
        var data = form1.getData();  //获取表单多个控件的数据
        var elemIF = document.createElement("iframe");
        elemIF.src = getRootPath() + "/procurement/service/purchaseInfo/exportPurchaseInfoByResult?companyNo=" + data.companyNo + "&AS=" + encodeURIComponent(data.AS);
        elemIF.style.display = "none";
        document.body.appendChild(elemIF);
    }catch(e){
        alert("导出错误！");
    }
}

//图片上传
var xhr;
//上传文件方法
function uploadFile() {
    var fileObj = document.getElementById("file").files[0]; // js 获取文件对象
    var url =  getRootPath() + "/procurement/service/purchaseInfo/upload"; // 接收上传文件的后台地址

    var form = new FormData(); // FormData 对象
    form.append("file", fileObj); // 文件对象

    xhr = new XMLHttpRequest();  // XMLHttpRequest 对象
    xhr.open("post", url, true); //post方式，url为服务器请求地址，true 该参数规定请求是否异步处理。
    xhr.onload = uploadComplete; //请求完成
    xhr.onerror =  uploadFailed; //请求失败

    xhr.upload.onprogress = progressFunction;//【上传进度调用方法实现】
    xhr.upload.onloadstart = function(){//上传开始执行方法
        ot = new Date().getTime();   //设置上传开始时间
        oloaded = 0;//设置上传开始时，以上传的文件大小为0
    };

    xhr.send(form); //开始上传，发送form数据
}

//上传成功响应
function uploadComplete(evt) {
    //服务断接收完文件返回的结果

    var data = JSON.parse(evt.target.responseText);
    if(data.success) {
        alert("上传成功！");
    }else{
        alert("上传失败！");
    }

}
//上传失败
function uploadFailed(evt) {
    alert("上传失败！");
}
//取消上传
function cancleUploadFile(){
    xhr.abort();
}

//上传进度实现方法，上传过程中会频繁调用该方法
function progressFunction(evt) {
    var progressBar = document.getElementById("progressBar");
    var percentageDiv = document.getElementById("percentage");
    // event.total是需要传输的总字节，event.loaded是已经传输的字节。如果event.lengthComputable不为真，则event.total等于0
    if (evt.lengthComputable) {//
        progressBar.max = evt.total;
        progressBar.value = evt.loaded;
        percentageDiv.innerHTML = Math.round(evt.loaded / evt.total * 100) + "%";
    }
    var time = document.getElementById("time");
    var nt = new Date().getTime();//获取当前时间
    var pertime = (nt-ot)/1000; //计算出上次调用该方法时到现在的时间差，单位为s
    ot = new Date().getTime(); //重新赋值时间，用于下次计算
    var perload = evt.loaded - oloaded; //计算该分段上传的文件大小，单位b
    oloaded = evt.loaded;//重新赋值已上传文件大小，用以下次计算
    //上传速度计算
    var speed = perload/pertime;//单位b/s
    var bspeed = speed;
    var units = 'b/s';//单位名称
    if(speed/1024>1){
        speed = speed/1024;
        units = 'k/s';
    }
    if(speed/1024>1){
        speed = speed/1024;
        units = 'M/s';
    }
    speed = speed.toFixed(1);
    //剩余时间
    var resttime = ((evt.total-evt.loaded)/bspeed).toFixed(1);
    time.innerHTML = '，速度：'+speed+units+'，剩余时间：'+resttime+'s';
    if(bspeed==0) time.innerHTML = '上传已取消';
}

function deletePurchaseInfo(){
    var array = [];
    var rows = grid.getSelecteds();
    if (rows.length > 0) {
        for(var i = 0;i < rows.length; i++){
            array.push(rows[i].companyNo);
        }
    }
    $.ajax({
        url: getRootPath() + "/procurement/service/purchaseInfo/deletePurchaseInfo",
        data: JSON.stringify(array),
        contentType:"application/json",
        dataType:"json",
        type : "POST",
        success: function (redata) {
            alert(redata.message);
            search();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert(jqXHR.responseText);
        }
    });
}
