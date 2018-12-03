/**
 * Created by liucm on 2017-7-28.
 */

//js获取项目根路径，如： http://192.168.70.105:8080/web
var getRootPath = function(){
    //获取当前网址，如： http://192.168.70.105:8080/web/ke_search.jsp
    var curWwwPath=window.document.location.href;
    //获取主机地址之后的目录，如：web/ke_search.jsp
    var pathName=window.document.location.pathname;
    var pos=curWwwPath.indexOf(pathName);
    //获取主机地址，如： http://192.168.70.105:8080
    var localhostPaht=curWwwPath.substring(0,pos);
    //获取带"/"的项目名，如：/web
    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
    return(localhostPaht);
}

//时间转换
function formatDate(date,format){
    var paddNum = function(num){
        num += "";
        return num.replace(/^(\d)$/,"0$1");
    };
    //指定格式字符
    var cfg = {
        yyyy : date.getFullYear() //年 : 4位
        ,yy : date.getFullYear().toString().substring(2)//年 : 2位
        ,M  : date.getMonth() + 1  //月 : 如果1位的时候不补0
        ,MM : paddNum(date.getMonth() + 1) //月 : 如果1位的时候补0
        ,d  : date.getDate()   //日 : 如果1位的时候不补0
        ,dd : paddNum(date.getDate())//日 : 如果1位的时候补0
        ,h : date.getHours()  //时
        ,hh : paddNum(date.getHours())  //时
        ,m : date.getMinutes() //分
        ,mm : paddNum(date.getMinutes()) //分
        ,s : date.getSeconds()
        ,ss : paddNum(date.getSeconds()) //秒
    };
    format || (format = "yyyy-MM-dd hh:mm:ss");
    return format.replace(/([a-z])(\1)*/ig,function(m){return cfg[m]});
}
function CloseWindow(action) {
    if (window.CloseOwnerWindow) return window.CloseOwnerWindow(action);
    else window.close();
}

/**
 * select-tree 删除父节点
 * @param e
 */
function disableParentNode(e) {
    var tree = e.sender.tree;
    var isLeaf = e.isLeaf;
    var node = e.node;
    if (!isLeaf) {
        //把自己删除掉即可
        var valueArr = e.sender.getValue().split(",");
        valueArr.shift();
        e.sender.setValue(valueArr.join(","));

    } else {
        //当子节点全部选中后，会将父节点也选中，先要判断是否全部选中
        var fatherNode = tree.getParentNode(node);
        var childNodes = tree.getChildNodes(fatherNode);
        var checkAll = true;
        for (var ii = 0; ii < childNodes.length; ii++) {
            var cnode = childNodes[ii];
            if (!tree.isCheckedNode(cnode)) {
                checkAll = false;
            }

        }
        //删除父节点
        if (checkAll) {
            var valueArr = e.sender.getValue().split(",");
            var index = valueArr.indexOf(fatherNode.id);
            valueArr.shift();
            e.sender.setValue(valueArr.join(","));
        }
    }
}

function changeTime0(sjq,sjz,month){
    var rqStart = (mini.get(sjq).getValue() == "") ? "" : formatDate(mini.get(sjq).getValue(),"yyyy-MM-dd");
    var rqEnd = (mini.get(sjz).getValue() == "") ? "" : formatDate(mini.get(sjz).getValue(),"yyyy-MM-dd");
    if(rqStart == '' || rqEnd == ''){
        return false;
    }
    //开始时间大于结束时间时将开始时间置为结束时间
    if(!duibi(rqStart,rqEnd)){
        mini.get(sjz).setValue(rqStart);
    }
    if(month != ''){
        rqStart = zjsj(rqStart,month);
        if(duibi(rqStart,rqEnd)){
            mini.get(sjz).setValue(rqStart);
            //alert("最多查询一个月的数据");
        }
    }
}

function changeTime1(sjq,sjz,month){
    var rqStart = (mini.get(sjq).getValue() == "") ? "" : formatDate(mini.get(sjq).getValue(),"yyyy-MM-dd");
    var rqEnd = (mini.get(sjz).getValue() == "") ? "" : formatDate(mini.get(sjz).getValue(),"yyyy-MM-dd");
    if(rqStart == '' || rqEnd == ''){
        return false;
    }
    //开始时间大于结束时间时将开始时间置为结束时间
    if(duibi(rqEnd,rqStart)){
        mini.get(sjq).setValue(rqEnd);
    }
    if(month != ''){
        rqEnd = zjsj(rqEnd,"-"+month);
        if(duibi(rqStart,rqEnd)){
            mini.get(sjq).setValue(rqEnd);
            //alert("最多查询一个月的数据");
        }
    }
}

//日期比较
function duibi(a,b){
    var arr=a.split("-");
    var startTime = arr[0]+""+arr[1]+""+arr[2];
    var arrs = b.split("-");
    var lktime = arrs[0]+""+arrs[1]+""+arrs[2];
    if(startTime < lktime){
        return true;
    }else{
        return false;
    }
}

function zjsj(sj,mon){
    var arr = sj.split("-");
    var year = arr[0];
    var month = arr[1];
    month = month*1 + mon*1;
    if(month > 12){
        month = month*1 -12;
        year = year*1 + 1;
    }else if(month < 0){
        month = month*1 +12;
        year = year*1 - 1;
    }
    if(month < 10){
        month = "0" + month;
    }
    return year+"-"+month+"-"+arr[2];
}