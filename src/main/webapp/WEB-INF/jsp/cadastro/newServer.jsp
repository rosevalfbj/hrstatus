<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ include file="../home/navbar.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Registrar Servidor</title>

<link href="${pageContext.request.contextPath}/css/bootstrap.css"
	rel="stylesheet">
<link
	href="${pageContext.request.contextPath}/css/bootstrap-responsive.css"
	rel="stylesheet">
<script src="${pageContext.request.contextPath}/js/jquery.js"
	type="text/javascript"></script>
<script
	src="${pageContext.request.contextPath}/js/jquery.maskedinput-1.3.js"
	type="text/javascript"></script>
<script type="text/javascript">
	jQuery(function($) {
		$("#ip").mask("999.999.999.999");
	});
</script>
</head>
<body>

	<div class="container">
		<div class="content">
			<div class="row">
				<div class="span12">
					<c:if test="${not empty errors}">
						<div class="alert">
							<button type="button" class="close" data-dismiss="alert">×</button>
							<c:forEach var="error" items="${errors}">
		   						 ${error.category} - ${error.message}<br />
							</c:forEach>
						</div>
					</c:if>

					<form method="POST" action="<c:url value='/registerServer'/>">
						<table align=center>
							<br>
							<tr>
								<td align=right><span title="Ip do servidor">IP:</span></td>
								<td><input type="text" name="servidores.ip" /></td>
							</tr>
							<tr>
								<td align=right><span title="Hostname do servidor">Hostname:</span></td>
								<td><input type="text" name="servidores.hostname" /></td>
							</tr>
							<tr>
								<td align=right>Usuário:</td>
								<td><input type="text" name="servidores.user" /></td>
							</tr>
							<tr>
								<td align=right>Senha:</td>
								<td><input type="password" name="servidores.pass" /></td>
							</tr>
							<tr>
								<td align="right"><span title="Se desejar buscar por logs no servidor, preencha este campo, Ex: /var/log">Diretório de logs:</span></td>
								<td><input type="text" name="servidores.logDir"></td>
							</tr>
							<tr>
								<td align="right"><span title="Comando ntp que será executado nos ervidor para atualizar data/hora, Ex: sudo ntpdate -u">Comando NTP:</span></td>
								<td><input type="text" name="servidores.suCommand"></td>
							</tr>
							<tr>
								<td align="right"><span title="Arquivo de log corrente do Middleware">Log File Middleware:</span></td>
								<td><input type="text" name="servidores.logMiddleware"></td>
							</tr>
							<tr>
								<td align=right><span title="Porta para conexão Remota, SSH: 22, Telnet: 23">Porta (SSH/TELNET):</span></td>
								<td><input type="text" name="servidores.port" /></td>
							</tr>
							<tr>
								<td align=right><span tilte="Sim o HrStatus irá verificar a data/hora neste servidor em específico, se não, o mesmo não será verificado">Verificação Ativa:</span></td>
								<td><select name="servidores.verify" id="servidores.verify">
										<option value="SIM">Sim</option>
										<option value="NAO">Nao</option>
								</select></td>
							</tr>
							<tr>
								<td align=right>SO:</td>
								<td><select name="servidores.SO" id="servidores.SO">
										<c:forEach items="${OS}" var="OS">
											<option value="<c:out value="${OS}" />">${OS}</option>
										</c:forEach>
								</select></td>

							</tr>

							<tr>
								<td align=right></td>
								<td align=right><button input type=submit value="Salvar"
										class="btn btn-primary">Salvar</button></td>
						</table>
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>