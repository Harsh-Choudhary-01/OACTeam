<!DOCTYPE HTML>
<!--
	Theory by TEMPLATED
	templated.co @templatedco
	Released for free under the Creative Commons Attribution 3.0 license (templated.co/license)
-->
<html>
	<head>
		<title>OACTeam</title>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
		<link rel="stylesheet" href="css/main.css" />
		<link rel="stylesheet" href="css/chosen.css">
		<link rel="stylesheet" href="css/vex.css" />
		<link rel="stylesheet" href="css/vex-theme-os.css" />
	</head>
	<body>

		<!-- Header -->
			<header id="header">
				<div class="inner">
					<a href="/" class="logo">OACTeam</a>
				</div>
			</header>

		<!-- Banner -->
			<section id="banner">
				<h1>Welcome to OACTeam</h1>
			</section>
		<#if loggedIn>
			<#if nameGiven>
				<#if joinedRequests?size != 0>
					<section id="joined" class="wrapper style1">
						<div class="inner">
							<header>
								<h2>Joined Requests</h2>
							</header>
							<div class="row">
								<#list joinedRequests as x>
									<div id="${x.id}" class="box 3u 12u$(small)">
										<h3 style="word-wrap: break-word;">${x.description}</h3>
										<#if x.owner>
											<ul class="actions vertical small">
												<#list x.joinedInfo as info>
													<li id="${info[0]}" class="removeUser button fit small" style="white-space: normal; height: 100%; word-wrap: break-word;">${info[1]}: ${info[2]}</li>
												</#list>
											</ul>
										<#else>
											<ul class="alt">
												<#list x.joinedInfo as info>
													<li style="white-space: normal; height: 100%; word-wrap: break-word;">${info[1]}: ${info[2]}</li>
												</#list>
											</ul>
										</#if>
										<#if x.joiningReq?has_content>
											<a href="#" id='${x.joiningReq}' class="button fit acceptReq">Active Requests</a>
										</#if>
										<a href="#" id="${x.id}" class="button leave fit">Leave Request</a>
									</div>
								</#list>
							</div>
						</div>
					</section>
				</#if>
				<#if joiningRequests?size != 0>
					<section id="joining" class="wrapper special">
						<div class="inner">
							<header>
								<h2>Joining Requests</h2>
							</header>
							<div class="row">
								<#list joiningRequests as x>
									<div id="${x.id}" class="box 3u 12u$(small)">
										<h3 style="word-wrap: break-word;">${x.description}</h3>
										<ul class="alt">
										<#list x.joinedInfo as info>
											<li style="white-space: normal; height: 100%; word-wrap: break-word;">${info[1]}: ${info[2]}</li>
										</#list>
										</ul>
										<a href="#" class="button special cancelJoin fit">Cancel Join</a>
									</div>
								</#list>
							</div>
						</div>
					</section>
				</#if>
				<#if requests?size != 0>
					<section id="open" class="wrapper style1">
						<div class="inner">
							<header>
								<h2>Open Requests</h2>
							</header>
							<div class="row">
								<div class="6u 12u$(small)">
									<select class="request-group" data-placeholder="Group Filter" multiple>
										<option value=""></option>
										<#list groups as g>
											<option value="${g[0]}" selected>${g[1]}</option>
										</#list>
									</select>
								</div>
								<div class="6u$ 12u$(small) refreshContainer">
									<button class="button fit refresh">Refresh  <i class="fa fa-refresh" style="font-size:24px"></i></button>
								</div>
								<#list requests as x>
									<div id="${x.id}" class="box 3u 12u$(small)">
										<h3 style="word-wrap: break-word;">${x.description}</h3>
										<ul class="alt">
										<#list x.joinedInfo as info>
											<li style="white-space: normal; height: 100%; word-wrap: break-word;">${info[1]}: ${info[2]}</li>
										</#list>
										</ul>
										<a href="#" id="${x.id}" class="button join fit">Join</a>
									</div>
								</#list>
							</div>
							<div class="row">
								<div class="6u 12u$(small)">
									<a href="#open" class="button alt fit prevReq changePage hidden" id="${firstInd}">Previous</a>
								</div>
								<div class="6u$ 12u$(Small)">
									<a href="#open" class="button alt fit nextReq changePage hidden" id="${firstInd}">Next</a>
								</div>
							</div>
						</div>
					</section>
				</#if>

				<section id="groups" class="wrapper special">
					<div class="inner">
						<header><h2>Groups</h2></header>
						<h3>Create Group</h3>
						<form id="createGroup" autocomplete="off" action="#">
							<div class="row">
								<div class="6u 12u$(small)">
									<input type="text" name="groupName" placeholder="Group Name" id="groupName" maxlength="100" autocomplete="off" pattern="[A-Za-z|\s.!\\$-]{1 , 100}" title="Alphanumeric , spaces , . , | , ! , $ , and - allowed">
								</div>
								<div class="6u$ 12u$(small)">
									<button type="submit" class="button fit">Create Group</button>
								</div>
							</div>
						</form>
						<form id="joinGroup" autocomplete="off" action="#">
							<h3>Join Group</h3>
							<div class="row">
								<div class="6u 12u$(small)">
									<input type="text" name="groupID" placeholder="Group ID" id="groupID" autocomplete="off">
								</div>
								<div class="6u$ 12u$(small)">
									<button type="submit" class="button fit">Join Group</button>
								</div>
							</div>
						</form>
						<#if groups?size != 0>
							<h3>Your Groups</h3>
							<ul class="actions vertical small">
								<#list groups as g>
									<li class="button small leaveGroup" id="${g[0]}">Name: ${g[1]} ID: ${g[0]}</li>
								</#list>
							</ul>
						</#if>
					</div>
				</section>

				<section id="requests" class="wrapper special style1">
					<div class="inner">
						<header><h2>Requests</h2></header>
						<h3>Create Request</h3>
						<form id="createRequest" action="#" autocomplete="off">
							<div class="row uniform">
								<div class="6u 12u$(small)">
									<input type="text" name="requestDescription" placeholder="Description" id="requestDescription" autocomplete="off" required maxlength="100" pattern="[A-Za-z|\s.!\\$-]{1 , 100}" title="Alphanumeric , spaces , . , | , ! , $ , and - allowed">
								</div>
								<div class="6u$ 12u$(small)">
									<select required class="group-select" data-placeholder="Groups that can View Request" multiple>
										<option value=""></option>
										<#list groups as g>
											<option value="${g[0]}">${g[1]}</option>
										</#list>
									</select>
								</div>
								<div class="6u 12u$(small)">
									<select required class="role-select" data-placeholder="Choose Your Roles" multiple>
										<option value=""></option>
										<option value="Divine">Divine</option>
										<option value="Martial">Martial</option>
										<option value="Assassin">Assassin</option>
										<option value="Marksman">Marksman</option>
										<option value="Blazer">Blazer</option>
										<option value="Garrison">Garrison</option>
										<option value="Elemental">Elemental</option>
										<option value="Stargazer">Stargazer</option>
										<option value="Bloodseeker">Bloodseeker</option>
										<option value="Guard">Guard</option>
									</select>
								</div>
								<div class="6u$ 12u$(small)">
									<button type="submit" class="button fit">Create Request</button>
								</div>
							</div>
						</form>
					</div>
				</section>
			<#else>
				<section class="wrapper style1 special">
					<div class="inner">
						<form id="assignName" action="#" autocomplete="off">
							<div class="row uniform">
								<h2>What is your Name?</h2>
								<div class="6u 12u$(small)">
									<input type="text" name="name" placeholder="Game Name" id="gameName" autocomplete="off" pattern="[A-Za-z|\s.!\\$-]{5 , 100}" title="Alphanumeric , spaces , . , | , ! , $ , and - allowed , 5 to 100 characters">
								</div>
								<div class="6u$ 12u$(small)">
									<button type="submit" class="button fit">Save</button>
								</div>
							</div>
						</form>
					</div>
				</section>
			</#if>
<!-- 
			<section id="one" class="wrapper">
				<div class="inner">
					<div class="flex flex-3">
						<article>
							<header>
								<h3>Magna tempus sed amet ${user.email}<br /> aliquam veroeros</h3>
							</header>
							<p>Morbi interdum mollis sapien. Sed ac risus. Phasellus lacinia, magna a ullamcorper laoreet, lectus arcu.</p>
							<footer>
								<a href="#" class="button special">More</a>
							</footer>
						</article>
						<article>
							<header>
								<h3>Interdum lorem pulvinar<br /> adipiscing vitae</h3>
							</header>
							<p>Morbi interdum mollis sapien. Sed ac risus. Phasellus lacinia, magna a ullamcorper laoreet, lectus arcu.</p>
							<footer>
								<a href="#" class="button special">More</a>
							</footer>
						</article>
						<article>
							<header>
								<h3>Libero purus magna sapien<br /> sed ullamcorper</h3>
							</header>
							<p>Morbi interdum mollis sapien. Sed ac risus. Phasellus lacinia, magna a ullamcorper laoreet, lectus arcu.</p>
							<footer>
								<a href="#" class="button special">More</a>
							</footer>
						</article>
					</div>
				</div>
			</section>

			<section id="two" class="wrapper style1 special">
				<div class="inner">
					<header>
						<h2>Ipsum Feugiat</h2>
						<p>Semper suscipit posuere apede</p>
					</header>
					<div class="flex flex-4">
						<div class="box person">
							<div class="image round">
								<img src="images/pic03.jpg" alt="Person 1" />
							</div>
							<h3>Magna</h3>
							<p>Cipdum dolor</p>
						</div>
						<div class="box person">
							<div class="image round">
								<img src="images/pic04.jpg" alt="Person 2" />
							</div>
							<h3>Ipsum</h3>
							<p>Vestibulum comm</p>
						</div>
						<div class="box person">
							<div class="image round">
								<img src="images/pic05.jpg" alt="Person 3" />
							</div>
							<h3>Tempus</h3>
							<p>Fusce pellentes</p>
						</div>
						<div class="box person">
							<div class="image round">
								<img src="images/pic06.jpg" alt="Person 4" />
							</div>
							<h3>Dolore</h3>
							<p>Praesent placer</p>
						</div>
					</div>
				</div>
			</section>

			<section id="three" class="wrapper special">
				<div class="inner">
					<header class="align-center">
						<h2>Nunc Dignissim</h2>
						<p>Aliquam erat volutpat nam dui </p>
					</header>
					<div class="flex flex-2">
						<article>
							<div class="image fit">
								<img src="images/pic01.jpg" alt="Pic 01" />
							</div>
							<header>
								<h3>Praesent placerat magna</h3>
							</header>
							<p>Praesent dapibus, neque id cursus faucibus, tortor neque egestas augue, eu vulputate magna eros eu erat. Aliquam erat volutpat. Nam dui mi, tincidunt quis, accumsan porttitor lorem ipsum.</p>
							<footer>
								<a href="#" class="button special">More</a>
							</footer>
						</article>
						<article>
							<div class="image fit">
								<img src="images/pic02.jpg" alt="Pic 02" />
							</div>
							<header>
								<h3>Fusce pellentesque tempus</h3>
							</header>
							<p>Sed adipiscing ornare risus. Morbi est est, blandit sit amet, sagittis vel, euismod vel, velit. Pellentesque egestas sem. Suspendisse commodo ullamcorper magna non comodo sodales tempus.</p>
							<footer>
								<a href="#" class="button special">More</a>
							</footer>
						</article>
					</div>
				</div>
			</section> -->
		<#else>
			<section id="signup" class="wrapper special">
				<div class="inner">
					<a class="signup button special" href="#">Login</a>
				</div>
			</section>
		</#if>
		<!-- Footer -->
			<footer id="footer">
				<div class="inner">
					<div class="flex">
						<div class="copyright">
							&copy; Untitled. Design: <a href="https://templated.co">TEMPLATED</a>. Images: <a href="https://unsplash.com">Unsplash</a>.
						</div>
						<ul class="icons">
							<li><a href="#" class="icon fa-facebook"><span class="label">Facebook</span></a></li>
							<li><a href="#" class="icon fa-twitter"><span class="label">Twitter</span></a></li>
							<li><a href="#" class="icon fa-linkedin"><span class="label">linkedIn</span></a></li>
							<li><a href="#" class="icon fa-pinterest-p"><span class="label">Pinterest</span></a></li>
							<li><a href="#" class="icon fa-vimeo"><span class="label">Vimeo</span></a></li>
						</ul>
					</div>
				</div>
			</footer>

		<!-- Scripts -->
			<script type="text/javascript">
				var a=document.getElementsByTagName("a");
				for(var i=0;i<a.length;i++)
				{
				    a[i].onclick=function()
				    {
				        window.location=this.getAttribute("href");
				        return false
				    }
				}
			</script>
			<!-- <script src="js/jquery.min.js"></script> -->
			<script
			  src="https://code.jquery.com/jquery-3.2.1.min.js"
			  integrity="sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4="
			  crossorigin="anonymous">
			</script>
			<script src="js/skel.min.js"></script>
			<script src="js/util.js"></script>
			<script src="js/main.js"></script>
			<script src="js/chosen.jquery.min.js"></script>
			<script src="js/vex.combined.min.js"></script>
			<script>vex.defaultOptions.className = 'vex-theme-os'</script>
			<script src="https://cdn.auth0.com/js/lock/10.0/lock.min.js"></script>
			<script type="text/javascript">
				var isMoreRequests = ${(moreRequests!false)?c};
				if(isMoreRequests)
					$('.nextReq').removeClass('hidden');
				var currentReqNum = ${firstInd};
				var opt = {
					width : "100%"
				};
				function leaveRequest(cancelling , idNum) {
					vex.dialog.confirm({
					    message: 'Are you sure you want to leave this team?',
					    callback: function (value) {
					        if (value) {
					        	var req = {
					        		type: cancelling ? "cancelRequest" : "leaveRequest" ,
					        		id: idNum
					        	}
					            $.ajax({
					            	url: window.location.href ,
									method: 'POST' ,
									dataType: 'json' ,
									data: JSON.stringify(req) ,
									success: function(data) {
										if(data.success == true) {
											alert("Left team");
										}
										else {
											alert("Could not leave. Please try again");
										}
									}
					            });
					        }
					    }
					});
				}
				function updateRequests(data) {
					$('#open').find('.box').remove();
					$('.changePage').attr('id' , data.firstInd);
					currentReqNum = data.firstInd;
					var requests = data.requests;
					if(requests.length > 0) {
						var list = '';
						for(var j = 0 ; j < requests[0].joinedInfo.length ; j++)
							list += '<li style="white-space: normal; height: 100%; word-wrap: break-word;">' + requests[0].joinedInfo[j][1] + ': ' + requests[0].joinedInfo[j][2] + '</li>';
						var html = ['<div id="' + requests[0].id + '" class="box 3u 12u$(small)">' ,
							'<h3 style="word-wrap: break-word;">' + requests[0].description + '</h3>' ,
							'<ul class="alt">' ,
							list ,
							'</ul>' ,
							'<a href="#" id="' + requests[0].id + '" class="button join fit">Join</a>' , 
						'</div>'].join('');
						$('#open .refreshContainer').last().after($(html));
						for(var i = 1 ; i < requests.length ; i ++) {
							list = '';
							for(var j = 0 ; j < requests[i].joinedInfo.length ; j++)
								list += '<li style="white-space: normal; height: 100%; word-wrap: break-word;">' + requests[i].joinedInfo[j][1] + ': ' + requests[i].joinedInfo[j][2] + '</li>';
							html = ['<div id="' + requests[i].id + '" class="box 3u 12u$(small)">' ,
								'<h3 style="word-wrap: break-word;">' + requests[i].description + '</h3>' ,
								'<ul class="alt">' ,
								list ,
								'</ul>' ,
								'<a href="#" id="' + requests[i].id + '" class="button join fit">Join</a>' , 
							'</div>'].join('');
							$('#open .box').last().after($(html));
						}
					}
					if(data.previous) {
						$('.prevReq').removeClass('hidden');
					}
					else {
						$('.prevReq').addClass('hidden');
					}
					if(data.next) {
						$('.nextReq').removeClass('hidden');
					}
					else {
						$('.nextReq').addClass('hidden');
					}
				}
				$(document).ready(function(e) {
					$('.group-select').chosen(opt);
					$('.request-group').chosen(opt);
					$('.role-select').chosen(opt);
					$('.class-select').chosen(opt);
					$('.signup').click(function(e) {
						e.preventDefault();
						lock.show();
					});
					$('.refresh').click(function(e) {
						e.preventDefault();
						e.stopImmediatePropagation();
						if($(this).find('i').first().hasClass('fa-spin'))
							return false;
						$(this).find('i').first().addClass('fa-spin');
						var req = {
							type: 'refresh' , 
							prev: false ,
							groups: $('.request-group').chosen().val() ,
							current: currentReqNum ,
							change: false
						}
						$.ajax({
							url: window.location.href ,
							method: 'POST' ,
							dataType: 'json' ,
							data: JSON.stringify(req) , 
							success: function(data) {
								$('.refresh').find('i').first().removeClass('fa-spin');
								if(data.success == true)
									updateRequests(data);
								else
									alert("Could not refresh");
							}
						});
					})
					$('.prevReq').click(function(e) {
						e.preventDefault();
						var req = {
							type: 'refresh' , 
							prev: true ,
							groups: $('.request-group').chosen().val() ,
							current: currentReqNum ,
							change: true
						}
						$.ajax({
							url: window.location.href ,
							method: 'POST' ,
							dataType: 'json' ,
							data: JSON.stringify(req) , 
							success: function(data) {
								if(data.success == true)
									updateRequests(data);
								else
									alert("Could not update");
							}
						});
					});
					$('.nextReq').click(function(e) {
						e.preventDefault();
						var req = {
							type: 'refresh' , 
							prev: false ,
							groups: $('.request-group').chosen().val() ,
							current: currentReqNum ,
							change : true
						}
						$.ajax({
							url: window.location.href ,
							method: 'POST' ,
							dataType: 'json' ,
							data: JSON.stringify(req) , 
							success: function(data) {
								if(data.success == true)
									updateRequests(data);
								else
									alert("Could not update");
							}
						});
					});
					$('.leaveGroup').click(function(e) {
						e.preventDefault();
						var groupID = this.id;
						vex.dialog.confirm({
							message: 'Leave ' + $(this).text() + '?',
							callback: function(value) {
								if(value) {
									var req = {
										type: "leaveGroup" , 
										id: groupID
									};
									$.ajax({
										url: window.location.href ,
										method: 'POST' ,
										dataType: 'json' ,
										data: JSON.stringify(req) ,
										success: function(data) {
											if(data.success == true) {
												window.location.reload(true);
											}
											else {
												alert("Could not leave group");
											}
										}
									});
								}
							}
						});
					});
					$('.join').click(function(e) {
						var reqID = this.id;
						vex.dialog.open({
							message: 'Choose roles:' ,
							input: ['<select class="role-select-vex required" data-placeholder="Choose Your Roles" multiple><option value=""></option><option value="Divine">Divine</option><option value="Martial">Martial</option><option value="Assassin">Assassin</option><option value="Marksman">Marksman</option><option value="Blazer">Blazer</option><option value="Garrison">Garrison</option><option value="Elemental">Elemental</option><option value="Stargazer">Stargazer</option><option value="Bloodseeker">Bloodseeker</option><option value="Guard">Guard</option></select>'].join('') ,
							afterOpen: function() {
								$('.role-select-vex').chosen(opt);
							} ,
							callback: function(data) {
								if(data) {
									var req = {
										type: "joinRequest" , 
										roles: $('.role-select-vex').chosen().val() ,
										id: reqID
									}
									$.ajax({
										url: window.location.href ,
										method: 'POST' ,
										dataType: 'json' ,
										data: JSON.stringify(req) ,
										success: function(data) {
											if(data.success == true) {
												window.location.reload(true);
											}
											else {
												alert("Could not join");
											}
										}
									});
								}
							}
						});
						e.preventDefault();
					});
					$('.leave').click(function(e) {
						e.preventDefault();
						leaveRequest(false , $(this).closest("div[id]").attr('id'));
					});
					$('body').on('click' , '.vex-overlay' , function() {
						vex.closeAll();
					});
					$('.removeUser').click(function(e) {
						e.preventDefault();
						var requestId = $(this).closest("div[id]").attr('id');
						var idNum = this.id;
						vex.dialog.confirm({
							message: 'Remove ' + $(this).text() + "?" ,
							callback: function(value) {
								if(value) {
									var req = {
										type: "removeUser" , 
										id: requestId , 
										user: idNum
									};
									$.ajax({
										url: window.location.href ,
										method: 'POST' ,
										dataType: 'json' ,
										data: JSON.stringify(req) ,
										success: function(data) {
											if(data.success == true) {
												window.location.reload(true);
											}
											else {
												alert("Could not remove user");
											}
										}
									});
								}
							}
						});
					});
					$('.cancelJoin').click(function(e) {
						e.preventDefault();
						leaveRequest(true , $(this).closest("div[id]").attr('id'));
					});
					$('.acceptReq').click(function(e) {
						e.preventDefault();
						var joiningReq = JSON.parse(this.id);
						var requestId = $(this).closest("div[id]").attr('id');
						function buildCallback(i) {
							return function(value) {
								var req = {
									type: "acceptRequest" , 
									id: requestId , 
									user: joiningReq[i][0]
								};
								if(value == 'reject') {
									req.type = "rejectRequest";
								}
								if(value != false) {
									$.ajax({
										url: window.location.href ,
										method: 'POST' ,
										dataType: 'json' ,
										data: JSON.stringify(req) ,
										success: function(data) {
											if(data.success == true) {
												window.location.reload(true);
											}
											else {
												if(value == 'accept')
													alert("Could not accept");
												else
													alert("Could not reject");
											}
										}
									});
								}
							}
						}
						for(var i = 0 ; i < joiningReq.length ; i++) {
							var itNum = i;
							vex.dialog.confirm({
								message: 'Accept ' + joiningReq[i][1] + ': ' + joiningReq[i][2] + '?' ,
								showCloseButton: true , 
								overlayClosesOnClick: false ,
								buttons: [
							        $.extend({}, vex.dialog.buttons.YES, { text: 'Accept' , click : function(e) { this.value = 'accept'; this.close(); } }),
							        $.extend({}, vex.dialog.buttons.NO, { text: 'Reject' , click : function(e) { this.value = 'reject'; this.close(); }})
							    ],
								callback: buildCallback(i)
							});
						}
					});
					$('#createGroup').submit(function(e) {
						var req = {
							type : "addGroup" , 
							name : $('#groupName').val()
						};
						$.ajax({
							url: window.location.href ,
							method: 'POST' ,
							dataType: 'json' ,
							data: JSON.stringify(req) ,
							success: function(data) {
								if(data.success == true) {
									$('.group-select').append('<option value="' + data.id + '">' + data.name + '</option>');
									$('.group-select').trigger('chosen:updated');
									$('#groupName').val('');
									alert("Group created successfully");
								}
								else {
									alert("Could not create group.")
								}
							}
						});
						e.preventDefault();
					});
					$('#createRequest').submit(function(e) {
						vex.dialog.confirm({
							message: 'Create Request?' ,
							callback: function(value) {
								if(value) {
									var req = {
										type: "createRequest" ,
										description: $('#requestDescription').val() ,
										groups: $('.group-select').chosen().val() , 
										roles: $('.role-select').chosen().val()
									}
									$.ajax({
										url: window.location.href , 
										method: 'POST' ,
										dataType: 'json' ,
										data: JSON.stringify(req) ,
										success: function(data) {
											if(data.success == true) {
												window.location.reload(true);
												//TODO: append request instead of reloading
											}
											else {
												alert("Could not create request");
											}
										}
									});
								}
							}
						});
						e.preventDefault();
					});
					$('#assignName').submit(function(e) {
						var req = {
							type: "assignName" ,
							name: $('#gameName').val()
						};
						$.ajax({
							url: window.location.href ,
							method: 'POST' ,
							dataType: 'json' ,
							data: JSON.stringify(req) ,
							success: function(data) {
								if(data.success == true) {
									window.location.reload(true);
								}
								else {
									alert("Could not set name.")
								}
							}
						});
						e.preventDefault();
					});
					$('#joinGroup').submit(function(e) {
						var req = {
							type: "joinGroup" ,
							id: $("#groupID").val()
						};
						$.ajax({
							url: window.location.href ,
							method: 'POST' ,
							dataType: 'json' , 
							data: JSON.stringify(req) ,
							success: function(data) {
								if(data.success == true) {
									$('.group-select').append('<option value="' + data.id + '">' + data.name + '</option>');
									$('.group-select').trigger('chosen:updated');
									$('#groupID').val('');
									alert("Joined group");
								}
								else
									alert("Please check ID");
							}
						});
						e.preventDefault();	
					});
					if(!${loggedIn?c}) {
						var lock = new Auth0Lock('${clientId}' , '${clientDomain}' , {
							loginAfterSignup: true ,
							auth: {
								redirect: true ,
								params: {
									scope: 'openid user_id name nickname email'
								}
							}
						});
						lock.on("authenticated" , function(authResult) {
							localStorage.setItem('id_token' , authResult.idToken);
							window.location.href = "/login?token=" + authResult.idToken;
						});
						lock.on("authorization_error" , function(error)
			        	{
			        		lock.show({
			        			flashMessage: {
			        				type: 'error' ,
			        				text: error.error_description
			        			}
			        		});
			        	});
					}
				});
			</script>
	</body>
</html>