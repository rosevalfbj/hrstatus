<%@ include file="navbar.jsp"%>

<div id="myCarousel" class="carousel slide">
	<!-- alterado na linha 399 -->
	<!-- Carousel items -->
	<div class="carousel-inner">

		<div class="container">
			<div class="content">
				<div class="row">
					<div class="span12">
						<div class="item" align="center">
							<p align="center">
								<b> Consolidado </b>
							</p>
							<table>
								<tr>
									<td align="center"><canvas id="cvs"
											!style="border: 1px solid #ccc">[No canvas support]</canvas>
										<script>
								var graph1 = function() {
								var data = [ ${linux} , ${windows}, ${unix}, ${other}];
								var pie = new RGraph.Pie('cvs', data);
								pie.Set('chart.labels', [ 'Linux', 'Windows','Unix','Outros' ]);
								pie.Set('chart.tooltips', [ 'Linux', 'Windows','Unix','Outros']);
								pie.Set('chart.tooltips.event', 'onmousemove');
								pie.Set('chart.colors', [ '#EC0033', '#A0D300',
										'#FFCD00', '#00B869', '#999999', '#FF7300',
										'#004CB0' ]);
								pie.Set('chart.strokestyle', 'white');
								pie.Set('chart.linewidth', 3);
								pie.Set('chart.shadow', true);
								pie.Set('chart.shadow.offsetx', 2);
								pie.Set('chart.shadow.offsety', 2);
								pie.Set('chart.shadow.blur', 3);
								pie.Set('chart.exploded', 7);
		
								for ( var i = 0; i < data.length; ++i) {
									pie.Get('chart.labels')[i] = pie
											.Get('chart.labels')[i]
											+ ', ' + data[i] + '%';
								}
		
								pie.Draw();
							}
						</script></td>

									<td align="center"><canvas id="cvs1">[No canvas
											support]</canvas> <script>
					        var graph2 = function () 
					        {
					            var bar8 = new RGraph.Bar('cvs1', [${serversOK},${serversNOK}])
					            bar8.Set('chart.labels', ['Servidores OK','Servidores N�o OK']);
					            //bar8.Set('chart.tooltips', function (index) {var label = bar8.Get('chart.labels')[index];return '<h2 style="text-align: center">' + label + '</h2><canvas id="tooltip_canvas" width="250" height="110"></canvas>';});
					            bar8.Draw();
					        }
   					 </script></td>
								</tr>
								<tr>
									<td colspan="2" align="center"><canvas id="cvs2"
											width="900" height="200">[No canvas support]</canvas> <script>
						        var graph3 = function ()
						        {						
						            var bar = new RGraph.Bar('cvs2', [[${serversLinuxOK},${serversLinuxNOK}],[${serversUnixOK},${serversUnixNOK}],[${serversWindowsOK},${serversWindowsNOK}],[${otherOK},${otherNOK}]]);
						            //var bar = new RGraph.Bar('cvs2', [[${serversLinuxOK},${serversLinuxNOK}],[${serversUnixOK},${serversUnixNOK}],[${serversWindowsOK},serversWindowsNOK]]);
						            bar.Set('chart.labels', ['Linux', 'Unix', 'Windows','Outros']);
						            bar.Set('chart.tooltips', ['Linux OK', 'Linux n�o OK', 'Unix OK', 'Unix n�o OK', 'Windows OK', 'Windows n�o OK','Outros OK','Outros n�o OK']);
						            bar.Set('chart.tooltips.event', 'onmousemove');
						            bar.Set('chart.ymax', ${totalServer});
						            bar.Set('chart.strokestyle', 'white');
						            bar.Set('chart.linewidth', 2);
						            bar.Set('chart.shadow', true);
						            bar.Set('chart.shadow.offsetx', 0);
						            bar.Set('chart.shadow.offsety', 0);
						            bar.Set('chart.shadow.blur', 10);
						            bar.Set('chart.hmargin.grouped', 2);
						            
						            bar.Set('chart.title', '');
						            bar.Set('chart.gutter.bottom', 20);
						            bar.Set('chart.gutter.left', 40);
						            bar.Set('chart.gutter.right', 15);
						            bar.Set('chart.colors', [
						                                     RGraph.LinearGradient(bar, 0,225,0,0, 'white', 'rgba(255, 176, 176, 0.5)'),
						                                     RGraph.LinearGradient(bar, 0,225,0,0, 'white', 'rgba(153, 208, 249,0.5)')
						                                    ]); 
						            bar.Set('chart.background.grid.autofit.numhlines', 5);
						            bar.Set('chart.background.grid.autofit.numvlines', 3);
						            
						            // This draws the chart
						            RGraph.Effects.Fade.In(bar, {'duration': 250});        
						        }
    </script></td>
								</tr>


							</table>
						</div>

						<div class="active item" align="center">

							<div class="row">

								<div class="span3"></div>

								<div class="span2">
									<form method="GET"
										action="<c:url value='/home/showByStatus/OK'/>">
										<button input="" type="submit" value="Servidores OK"
											class="btn btn-primary">Servidores OK</button>
									</form>

								</div>

								<div class="span2">
									<div class="btn-group">
										<a class="btn btn-primary dropdown-toggle"
											data-toggle="dropdown" href="#"> Iniciar Verifica��o <span
											class="caret"></span>
										</a>
										<ul class="dropdown-menu">
											<li><a
												href="<c:url value='/home/startVerification/full' />">
													Verifica��o Completa </a></li>

											<li><a
												href="<c:url value='/home/startVerification/notFull' />">
													Verifica��o N�o Completa </a></li>

										</ul>
									</div>
								</div>

								<div class="span2">
									<div class="btn-group">
										<a class="btn btn-primary dropdown-toggle"
											data-toggle="dropdown" href="#"> Servidores N�o OK <span
											class="caret"></span>
										</a>
										<ul class="dropdown-menu">
											<li><a href="<c:url value='/home/showByStatus/NOK' />">
													Listar Servidores Desatualizados </a></li>

											<li>
												<div id="dynamicURL">
													<a href="montadodinamicamente"> Atualizar Selecionados</a>
												</div>
											</li>

											<li><a href="<c:url value='/updateTimeAllClients' />">
													Atualizar todos </a></li>

										</ul>
									</div>
								</div>

								<div class="span3"></div>
							</div>

							<table class="table table-condensed" id="resultTable">
								<thead>
									<tr>
										<th>ID</th>
										<th>Servidor</th>
										<th>IP</th>
										<th>SO</th>
										<th>Client Time</th>
										<th>Server Time</th>
										<th>Diference (s)</th>
										<th>Status</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="server" items="${server}">
										<tr class="${server.trClass}">
											<td>${server.id}</td>
											<c:if test="${server.trClass == 'success'}">
												<td>${server.hostname}</td>
											</c:if>
											<c:if test="${server.trClass == 'error'}">
												<td><a href="<c:url value='/singleServerToVerify/${server.id}' />">${server.hostname}</a></td>
											</c:if>
											<td>${server.ip}</td>
											<td>${server.SO}</td>
											<td>${server.clientTime}</td>
											<td>${server.serverTime}</td>
											<td>${server.difference}</td>
											<td>${server.status}</td>
											<td><c:if test="${server.trClass == 'error'}">
													<div class="find_1">
														<input type="checkbox" value="${server.id}" />
													</div>
												</c:if></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>

							<c:if test="${not empty errors}">
								<div class="alert">
									<button type="button" class="close" data-dismiss="alert">�</button>
									<c:forEach var="error" items="${errors}">
		   		 							${error.message}<br />
									</c:forEach>
								</div>
							</c:if>

							<c:if test="${not empty info}">
								<div class="alert">
									<button type="button" class="close" data-dismiss="alert">�</button>
									${info}
								</div>
							</c:if>
						</div>
						<!-- Caso seja necess�rio inserir novo �tem ao carrossel, inserir aqui.  -->
					</div>
				</div>
			</div>
		</div>


	</div>
	<!-- Carousel nav -->
	<a class="carousel-control left" href="#myCarousel" data-slide="prev">&lsaquo;</a>
	<a class="carousel-control right" href="#myCarousel" data-slide="next">&rsaquo;</a>
</div>
<script>
		window.onload = function() {
			graph1();
			graph2();
			graph3(); 
		}
	</script>
</body>
</html>