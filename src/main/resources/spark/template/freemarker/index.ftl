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
					<nav id="nav">
						<a href="/">Home</a>
						<a href="/generic">Generic</a>
						<a href="/elements">Elements</a>
					</nav>
					<a href="#navPanel" class="navPanelToggle"><span class="fa fa-bars"></span></a>
				</div>
			</header>

		<!-- Banner -->
			<section id="banner">
				<h1>Welcome to OACTeam</h1>
			</section>
		<#if loggedIn>
			<#if nameGiven>
				<#if joinedRequests?size != 0>
					<section id="joined" class="wrapper">
						<div class="inner">
							<header>
								<h2>Joined Requests</h2>
							</header>
							<div class="row">
								<#list joinedRequests as x>
									<div id="${x.id}" class="box 3u 12u$(small)">
										<h3>${x.description}</h3>
										<ul class="alt">
										<#list x.joinedInfo as info>
											<li>${info[1]}: ${info[2]}</li>
										</#list>
										</ul>
										<#if x.joiningReq?has_content>
											<h4>Requests To Join</h4>
											<ul>
												<#list x.joiningReq as y>
													<li id="${y[0]}" class="button special acceptReq">Accept ${y[1]}: ${y[2]}</li>
												</#list>
											</ul>
										</#if>
										<a href="#" id="${x.id}" class="button special leave">Leave Request</a>
									</div>
								</#list>
							</div>
						</div>
					</section>
				</#if>
				<#if joiningRequests?size != 0>
					<section id="joined" class="wrapper special">
						<div class="inner">
							<header>
								<h2>Joining Requests</h2>
							</header>
							<div class="row">
								<#list joiningRequests as x>
									<div id="${x.id}" class="box 3u 12u$(small)">
										<h3>${x.description}</h3>
										<ul class="alt">
										<#list x.joinedInfo as info>
											<li>${info[1]}: ${info[2]}</li>
										</#list>
										</ul>
										<a href="#" class="button special cancelJoin">Cancel Join</a>
									</div>
								</#list>
							</div>
						</div>
					</section>
				</#if>
				<#if requests?size != 0>
					<section id="joined" class="wrapper">
						<div class="inner">
							<header>
								<h2>Open Requests</h2>
							</header>
							<div class="row">
								<#list requests as x>
									<div id="${x.id}" class="box 3u 12u$(small)">
										<h3>${x.description}</h3>
										<ul class="alt">
										<#list x.joinedInfo as info>
											<li>${info[1]}: ${info[2]}</li>
										</#list>
										</ul>
										<a href="#" id="${x.id}" class="button special join">Join</a>
									</div>
								</#list>
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
									<input type="text" name="groupName" placeholder="Group Name" id="groupName" autocomplete="off">
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
					</div>
				</section>

				<section id="requests" class="wrapper special">
					<div class="inner">
						<header><h2>Requests</h2></header>
						<h3>Create Request</h3>
						<form id="createRequest" action="#" autocomplete="off">
							<div class="row uniform">
								<div class="6u 12u$(small)">
									<input type="text" name="requestDescription" placeholder="Description" id="requestDescription" autocomplete="off">
								</div>
								<div class="6u$ 12u$(small)">
									<select class="group-select" data-placeholder="Groups that can View Request" multiple style="display: none;">
										<option value=""></option>
										<#list groups as g>
											<option value="${g[0]}">${g[1]}</option>
										</#list>
									</select>
								</div>
								<div class="6u 12u$(small)">
									<select class="role-select" data-placeholder="Choose Your Roles" multiple style="display: none;">
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
									<input type="text" name="name" placeholder="Game Name" id="gameName" autocomplete="off">
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
			<script src="js/jquery.min.js"></script>
			<script src="js/skel.min.js"></script>
			<script src="js/util.js"></script>
			<script src="js/main.js"></script>
			<script src="js/chosen.jquery.min.js"></script>
			<script src="js/vex.combined.min.js"></script>
			<script>vex.defaultOptions.className = 'vex-theme-os'</script>
			<script src="https://cdn.auth0.com/js/lock/10.0/lock.min.js"></script>
			<script type="text/javascript">
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
				$(document).ready(function(e) {
					$('.group-select').chosen(opt);
					$('.role-select').chosen(opt);
					$('.class-select').chosen(opt);
					$('.signup').click(function(e) {
						e.preventDefault();
						lock.show();
					});
					$('.join').click(function(e) {
						console.log("Join group");
						var reqID = this.id;
						vex.dialog.open({
							message: 'Choose roles:' ,
							input: ['<select class="role-select-vex" data-placeholder="Choose Your Roles" multiple style="display: none;"><option value=""></option><option value="Divine">Divine</option><option value="Martial">Martial</option><option value="Assassin">Assassin</option><option value="Marksman">Marksman</option><option value="Blazer">Blazer</option><option value="Garrison">Garrison</option><option value="Elemental">Elemental</option><option value="Stargazer">Stargazer</option><option value="Bloodseeker">Bloodseeker</option><option value="Guard">Guard</option></select>'].join('') ,
							afterOpen: function() {
								$('.role-select-vex').chosen(opt);
							} ,
							callback: function(data) {
								console.log(data);
								console.log($('.role-select-vex').chosen().val());
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
					$('.cancelJoin').click(function(e) {
						e.preventDefault();
						leaveRequest(true , $(this).closest("div[id]").attr('id'));
					});
					$('.acceptReq').click(function(e) {
						var idNum = this.id;
						var requestId = $(this).closest("div[id]").attr('id');
						e.preventDefault();
						vex.dialog.confirm({
							message: $(this).text() + '?' ,
							callback: function(value) {
								if(value) {
									var req = {
										type: "acceptRequest" , 
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
												alert("Could not accept");
											}
										}
									});
								}
							}
						});
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