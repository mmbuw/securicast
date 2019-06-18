<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
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

        <script src="js/vendor/modernizr-2.6.2-respond-1.1.0.min.js"></script>
    </head>
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
        <!--/.navbar-collapse -->
      </div>
    </nav>

    <!-- Main jumbotron for a primary marketing message or call to action -->
    <div class="jumbotron">
      <div class="container">
        <img src="img/BUW_R0_G107_B148.png" style="max-width:200px; width:100%;">
        <h1>Sign up</h1>
        <p><form action=RegistrationServlet method="post">
      <table>
        <tr>
          <td>First name: </td>
          <td><input style="margin-left:3px;" type=text name=firstname value=<%if (session.getAttribute("firstname") != null) out.println(session.getAttribute("firstname")); session.setAttribute("firstname", "");%>></td>
        </tr>
        <tr>
          <td>Last name: </td>
          <td><input style="margin-left:3px;" type=text name=lastname value=<%if (session.getAttribute("lastname") != null) out.println(session.getAttribute("lastname")); session.setAttribute("lastname", "");%>></td>
        </tr>
        <tr>
          <td>Username: </td>
          <td><input style="margin-left:3px;" type=text name=username value=<%if (session.getAttribute("username") != null) out.println(session.getAttribute("username")); session.setAttribute("username", "");%>></td>
        </tr>
        <tr>
          <td style="align:right;">Password: </td>
          <td><input style="margin-left:3px;" type=password name=pwd></td>
        </tr>
        <tr>
          <td>Reenter password:</td>
          <td><input style="margin-left:3px;" type=password name=repwd></td>
        </tr>
        <tr>
          <td><a class="btn btn-primary btn-lg" style="float:left; margin-top:5px;" href="index.jsp" role="button">&laquo; Back</a></td>
          <td><input class="btn btn-primary btn-lg" style="float:right; margin-top:5px;" type=submit value="Create an account"></td>
        </tr>
      </table>
    </form>
        <p></p>
        <% if(session.getAttribute("HintText") != null) {
    	out.println(session.getAttribute("HintText"));
    	session.setAttribute("HintText", null);
    	}
    	%>
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
    </body>
</html>
