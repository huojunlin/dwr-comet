<html>
<head>
<title>starting page</title>
<script type="text/javascript" src="./dwr/engine.js"></script>
<script type="text/javascript" src="./dwr/util.js"></script>
<script type="text/javascript" src="./dwr/interface/Message.js"></script>
<script type="text/javascript" src="js/jquery-1.4.2.js"></script>
<script type="text/javascript">
	var chatlog = "";
	var currentUser;
	function sendMessage() {
		var message = $("#message").val();
		var user = $("#user").val();
		Message.addMessage(user, message);
	}
	function receiveMessages(messages) {
		var lastMessage = messages;
		chatlog = "<div>" + lastMessage + "</div>" + chatlog;
		$("#list").html(chatlog);
		 playSound();
	}

	//读取name值作为推送的唯一标示
	function onPageLoad() {
		if (currentUser == null)
			currentUser = getQueryString("name");
		Message.onPageLoad(currentUser);
	}


	function unlogUser() {
		Message.unlogUser(currentUser);
	}
	

	//获取url中的参数
	function getQueryString(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
		var r = window.location.search.substr(1).match(reg);
		if (r != null)
			return unescape(r[2]);
		return null;
	}
 
 	function updateUsers(users){
 		$("#users").html("<font color=\"green\">users: " + users + "</font>");
 	}
 	
 	function playSound(){
 		 $('embed').remove(); 
 		 $('body').append('<embed src="ring.wav" autostart=true hidden="true" loop="0">'); 
 	}
 	
 	function alreadyOnline(name){ 
 		currentUser= "name_" + Math.random();
 		$("#errormsg").html("<font color=\"red\">" + name + " is already online</font>");
 		$("#btn_send").css("display","none");
 		$("#list").css("display","none");
 	}


	function exitService(e) {
		if ($.browser.msie) {//判断是否是IE系列浏览器  
			dwr.engine.setAsync(false);//将dwr的引擎设置为同步   
			unlogUser();
		} else {
			//适应火狐浏览器  
			var evt = e ? e : (window.event ? window.event : null);
			dwr.engine.setAsync(false);//将dwr的引擎设置为同步   
			unlogUser();
			event.returnValue = "";
			return "";//此处是为了适应chrome浏览器，其返回与IE和firefox不同  
		}
	}
	
	function onError() {
 		$("#errormsg").html("<font color=\"red\">connect error, retry later...</font>");
		setTimeout(function() {document.location.reload();}, 5000);
	};
	
	var updatePollStatus = function(pollStatus) {	
		 dwr.util.setValue("errormsg",
				pollStatus ? "<font color='green'>Online</font>"
						: "<font color='red'>Offline</font>", {
					escapeHtml : false
				}); 
	};
	
	$(document).ready(function() {
		dwr.engine.setActiveReverseAjax(true);
		dwr.engine.setNotifyServerOnPageUnload(true);
		window.onbeforeunload = exitService;
		dwr.engine.setErrorHandler(onError);
		dwr.engine.setPollStatusHandler(updatePollStatus);
		dwr.engine.setRetryIntervals([1, 3, 5]);
		updatePollStatus(true);
		onPageLoad();
	});
 </script>
</head>
<body>
	<div id="errormsg"></div>
	<div id="users"></div>
	<!-- <div id="pollStatus"></div>
	 -->
	To: <input id="user" type="text"/><br/>
	Msg:<input id="message" type="text" value="hey"/><br/>
	<input id="btn_send" type="button" value="send" onclick="sendMessage()"/><br>
	<div id="list"></div>
</body>
</html>