(function(){
  window.Utils={
  dateFormat:function(ms){
    var date=new Date(ms);
    var year=date.getYear()+1900;
    var month=date.getMonth()+1;
    var day=date.getDate();
    var hour=date.getHours();
    var mi=date.getMinutes();
    var sec=date.getSeconds();
    month=month<10?("0"+month):(""+month);
    day=day<10?('0'+day):(''+day);
    hour=hour<10?('0'+hour):(''+hour);
    mi=mi<10?('0'+mi):(''+mi);
    sec=sec<10?('0'+sec):(''+sec);
    return year+'-'+month+'-'+day+" "+hour+":"+mi+":"+sec
    
  },
  stDateFormat:function(date){
    var year=date.getYear()+1900;
    var month=date.getMonth()+1;
    var day=date.getDate();
    var hour=date.getHours();
    var mi=date.getMinutes();
    var sec=date.getSeconds();
    month=month<10?("0"+month):(""+month);
    day=day<10?('0'+day):(''+day);
    hour=hour<10?('0'+hour):(''+hour);
    mi=mi<10?('0'+mi):(''+mi);
    sec=sec<10?('0'+sec):(''+sec);
    return year+'-'+month+'-'+day+" "+hour+":"+mi+":"+sec
    
  },
  cvtSToMs:function(s){
    return parseInt(s+'000');
  },
  antiCvtSToMs:function(ms){
    var ms=ms+'';
    return parseInt(ms.substring(0,ms.length-3));
  },
  retTimeFormat:function(l){
      var res=[];
      for(var i=0;i<l.length;i++){
        res.push(this.dateFormat(parseInt(l[i])));
      }
      return res;
  },
  cutDateFormat:function(ms){
    var mstmp=parseInt((ms+"").substring(0,13));
    return this.dateFormat(mstmp);
  },

  FormatDecimal:function(bit,strDecimal)
  {
      var i=0;
      //补零    
      var strFill="";
      //四舍五入后要加的数值
      var addDecimal="0.";             
      while(i<bit)
      {           
        strFill=strFill+"0";
        if(i==bit-1)
        {            
           addDecimal=addDecimal+"1";            
        }
        else
        {            
           addDecimal=addDecimal+"0";
        }         
        i=i+1;         
      } 
      i=0;          
      var beginPlace=strDecimal.indexOf(".");
      //判断此浮点数是否无小数部份
      if(beginPlace==-1)
      {
           if(bit==0)
           {
           //精确位数到个位           
            return strDecimal;                      
           }
           // 精确位数到个位，小数位数不足时添零      
           return strDecimal+"."+strFill;         
      }            
      var strDecimalC=strDecimal+strFill;
      
      var str= strDecimalC.split(/[.]/);      
      var strInt=str[0];      
      var strDecimal=str[1]+strFill;
      var IntDecimal=parseFloat("0."+strDecimal);       
      var validPlace=beginPlace+bit+1;
      var validData=strDecimalC.substring(validPlace,validPlace+1);    
      //进行四舍五入判断
      if(parseInt(validData)>4)
      {  
           if(bit==0)
            {
                //精确位数到个位            
                return parseInt(strInt)+1;                    
            }
           //精确到小数的位数
           var differents="0."+strFill+strDecimal.substring(bit,strDecimal.length);
           IntDecimal=IntDecimal-parseFloat(differents);
           IntDecimal=IntDecimal+parseFloat(addDecimal);
     
           var DecimalValue=parseInt(strInt)+IntDecimal;
          
           if(DecimalValue.toString().indexOf(".")== -1 )//如果算出的值刚好为整数,如"30"; 
              DecimalValue=DecimalValue.toString()+".";//则在末尾加上小数点'.'
             
           strDecimalC=DecimalValue.toString(10)+strFill;
      }
      //strDecimalC为重新得到的浮点数字符串 
      var beginPlace=strDecimalC.indexOf(".");
      //得到整数部分                   
      var beginStr=strDecimalC.substring(0,beginPlace);
      if(bit==0)
      {      
          return beginStr;            
      }
      return strDecimalC.substring(0, beginPlace+bit+1);
  },
  initAllCanvas:function(canvas){
    var width=canvas.getAttribute("width")
    var height=canvas.getAttribute("height")
    var context=canvas.getContext("2d");
    context.fillStyle="white";
    context.fillRect(0,0,width,height);
  },
  popAlert:function(msg){
    var sTop=$("body").scrollTop()
    var height=window.screen.height
    var hoffset=sTop+height/2
    var width=window.screen.width
    var woffset=width/2
    width=200
    height=100
    hoffset=hoffset-height/2
    woffset=woffset-width/2
    $("body").append("<div id='pop' style='background-color:rgba(120,120,120,0.5);border-radius:8px;position:absolute;z-index:10;left:"+woffset+"px;top:"+hoffset+"px;width:"+width+"px;height:"+height+"px;' ><p style='font-weight:bold; font-size:18px;text-align:center;vertical-align:middle;line-height:"+height+"px;'"+">"+msg+"</p></div>")
    $("#pop").fadeOut(1500);
    setTimeout(function(){
      $("#pop").remove();
    },1500);

  },
  popDialog:function(html,div_width,div_height){
      var sTop=$("body").scrollTop()
      var height=window.screen.height
      var hoffset=sTop+height/2
      var width=window.screen.width
      var woffset=width/2
      hoffset=hoffset-div_height/1.5
      woffset=woffset-div_width/2

      $("body").append("<div id='popDialog' style='position:absolute;z-index:10;width:100%;height:"+height+"px;background-color:rgba(128,128,128,0.3);top:"+sTop+"px;left:0px;'><div id='innerDialog' style='position:relative;background-color:#666;border:#555 1px solid;left:"+woffset+"px;top:"+hoffset+"px;width:"+div_width+"px;height:"+div_height+"px;'>"+html+"</div></div>");
      
  }
}
})()