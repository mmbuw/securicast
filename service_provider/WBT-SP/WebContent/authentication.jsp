<%@page import="wbt.SessionAttribute"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="wbt.Functions"%>
<!DOCTYPE html>
<html class="no-js">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title></title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <link rel="stylesheet" href="css/bootstrap.min.css">
        <style>
            body {
                padding-top: 50px;
                padding-bottom: 20px;
            }
        </style>
        <link rel="stylesheet" href="css/bootstrap-theme.min.css">
        <link rel="stylesheet" href="css/main.css">
        <link rel="stylesheet" href="css/animate.css">

        <script src="js/vendor/modernizr-2.6.2-respond-1.1.0.min.js"></script>
        <script type="text/javascript">
        	var wbtData ='<%=session.getAttribute(SessionAttribute.WBT_DATA)%>';
 		</script> 
		        
    </head>
    
    <%
      String hash = null;
      boolean redirect = false;
      try {
    	  hash = session.getAttribute(SessionAttribute.HASH).toString();
      } catch (Exception e) {
    	  e.printStackTrace();
    	  redirect = true;
      }
      if (hash == null || redirect == true) {
        response.sendRedirect("index.jsp");
      }
    %>

    <body>
    <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">SecuriCast Demo</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
          <form class="navbar-form navbar-right" role="form" action="LogoutServlet">
            <button type="submit" class="btn btn-success">Sign out</button>
          </form>
        </div>
        <!--/.navbar-collapse -->
      </div>
    </nav>

    <!-- Main jumbotron for a primary marketing message or call to action -->
    <div class="jumbotron">
      <div class="container">
        <img src="img/BUW_R0_G107_B148.png" style="max-width:200px; width:100%;">
        <h2>Second Factor</h2>
        <table style="width:100%">
        	<tr>
        		<td style="width:45%" align="center">
	        		<div style="margin-bottom:5px">Passphrase to compare: <b style="font-size: 25px"><%out.print(session.getAttribute(SessionAttribute.PASSPHRASE).toString());%></b></div> 
			    	<!-- <br> -->
			    	<button class="btn btn-primary btn-lg" onclick="onWebBluetoothStart(wbtData); logTime('start web bluetooth')">Start Web Bluetooth Authentication</button>
			    	<form name="hiddenOtpData" action="AuthenticationServlet" method="post">
			    		<input id="otpData" type="hidden" name="otpData">
			    	</form>
			    	<br>
			    	<span id=wbtInfo data-in-effect="fadeIn"></span>
			    </td>
			    <td style="width:10%" align="center">
			    |<br>
			    	OR<br>
			    	|
			    </td>
		    	<!-- <br> -->
		    	<td style="width:45%" align="center">
		    		<p><form action=AuthenticationServlet method="post">
			      	<table>
			        	<tr>
			        		<td style="white-space: nowrap">Please enter your PIN: </td>
			        		<td><input style="margin-left:3px;" type=text name=totp autofocus id="totp"></td>
						</tr>
			        	<tr>
			          		<td colspan="2" align="center"><button type=submit class="btn btn-primary btn-lg" style="margin-top:5px;" onclick="logTime('verify pin')">Verify PIN-Code</button></td>
			        	</tr>
					</table>
			    	</form>
			    </td>
	    	</tr>
    	</table>
      </div>
    </div>

    <div class="container">
      <!-- Example row of columns -->
      <footer>
        <p>&copy; SecuriCast Demo 2017</p>
      </footer>
    </div> <!-- /container -->        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
        <script>window.jQuery || document.write('<script src="js/vendor/jquery-1.11.1.min.js"><\/script>')</script>

        <script src="js/vendor/bootstrap.min.js"></script>

        <script src="js/main.js"></script>
        <script src="js/jquery.lettering.js"></script>
        <script src="js/jquery.textillate.js"></script>
        <script src="js/webbluetooth.js"></script>
    </body>
</html>
