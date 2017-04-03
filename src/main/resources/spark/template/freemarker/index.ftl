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
										<li classInfo="${info[4]}">${info[0]}:
											<#if info[1] == "0">
												Tank 
											</#if>
											<#if info[2] == "0">
												DPS 
											</#if>
											<#if info[3] == "0">
												Heal
											</#if>
										</li>
									</#list>
									</ul>
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
										<li classInfo="${info[4]}">${info[0]}:
											<#if info[1] == "0">
												Tank 
											</#if>
											<#if info[2] == "0">
												DPS 
											</#if>
											<#if info[3] == "0">
												Heal
											</#if>
										</li>
									</#list>
									</ul>
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
										<li classInfo="${info[4]}">${info[0]}:
											<#if info[1] == "0">
												Tank 
											</#if>
											<#if info[2] == "0">
												DPS 
											</#if>
											<#if info[3] == "0">
												Heal
											</#if>
										</li>
									</#list>
									</ul>
									<a href="#" class="button special">Join</a>
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
							<div class="4u$ 12u$(small)">
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
							<div class="4u$ 12u$(small)">
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
						<div class="row">
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
									<option value="Healer">Healer</option>
									<option value="Tank">Tank</option>
									<option value="DPS">DPS</option>
								</select>
							</div>
							<div class="6u$ 12u$(small)">
								<select class="class-select" data-placeholder="Choose Your Classes" multiple style="display: none;">
									<option value=""></option>
									<option value="Monk">Monk</option>
									<option value="Warrior">Warrior</option>
									<option value="Mage">Mage</option>
									<option value="Flame">Flame Knight</option>
									<option value="Ranger">Ranger</option>
								</select>
							</div>
							<div class="4u$ 12u$(small)">
								<button type="submit" class="button fit">Create Request</button>
							</div>
						</div>
					</form>
				</div>
			</section>
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
			<script src="https://cdn.auth0.com/js/lock/10.0/lock.min.js"></script>
			<script type="text/javascript">
				$(document).ready(function(e) {
					$('.group-select').chosen().change(function(event , params) {
						console.log("Chosen parameters group: " + params);
					});
					$('.role-select').chosen();
					$('.class-select').chosen();
					$('.signup').click(function(e) {
						e.preventDefault();
						lock.show();
					});
					$('#createGroup').submit(function(e) {
						$.ajax({
							url: window.location.href ,
							method: 'POST' ,
							dataType: 'json' ,
							data: $('#groupName').val() ,
							success: function(data) {
								if(data === 'true') {
									$('group-select').append('<option val="' + data.id + '">' + data.name + '</option>');
									$('#createGroup').reset();
									alert("Group created successfully");
								}
								else {
									alert("Could not create group.")
								}
							}
						});
						e.preventDefault();
					});
					$('#joinGroup').submit(function(e) {
						$.ajax({
							url: window.location.href ,
							method: 'POST' ,
							dataType: 'json' , 
							data: $('#groupID').val() ,
							success: function(data) {
								//TODO: Since posting to same location must specify what request is for
								//ALSO
								alert("Joined group");
							}
						});
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