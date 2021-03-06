<!-- 
    Copyright (C) 2012  Filippe Costa Spolti

	This file is part of Hrstatus.

    Hrstatus is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 -->
<%@ page language="java" isErrorPage="true" %>
<html>
<head>
<meta charset="utf-8">
<title>Erro</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">

<!-- CSS -->
<link href="${pageContext.request.contextPath}/css/bootstrap.css"
	rel="stylesheet">
<style type="text/css">

/* Sticky footer styles
      -------------------------------------------------- */
html,body {
	height: 100%;
	/* The html and body elements cannot have any padding or margin. */
}

/* Wrapper for page content to push down footer */
#wrap {
	min-height: 100%;
	height: auto !important;
	height: 100%;
	/* Negative indent footer by it's height */
	margin: 0 auto -60px;
}

/* Set the fixed height of the footer here */
#push,#footer {
	height: 60px;
}

#footer {
	background-color: #f5f5f5;
}

/* Lastly, apply responsive CSS fixes as necessary */
@media ( max-width : 767px) {
	#footer {
		margin-left: -20px;
		margin-right: -20px;
		padding-left: 20px;
		padding-right: 20px;
	}
}

/* Custom page CSS
      -------------------------------------------------- */
/* Not required for template or sticky footer method. */
.container {
	width: auto;
	max-width: 680px;
}

.container .credit {
	margin: 20px 0;
}
</style>
<link
	href="${pageContext.request.contextPath}/css/bootstrap-responsive.css"
	rel="stylesheet">

</head>

<body>

	<div id="wrap">

		<!-- Begin page content -->
		<div class="container">
			<div class="page-header">
				<h1>Erro inesperado</h1>
			</div>
			<p class="lead">Ocorreu algum erro na execu��o do recurso
				solicitado, copie o c�digo abaixo e envie para o administrador.</p>

			<%=exception.getCause() %>

		</div>

		<div id="push"></div>
	</div>

	<div id="footer">
		<div class="container">
			<p class="muted credit">
				<a href="http://www.hrstatus.com.br/hrstatus/home.html">Hrstatus</a> - 2012, Todos os Direitos Reservados.
			</p>
		</div>
	</div>

</body>
</html>