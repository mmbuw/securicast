<%@page import="wbt.SessionAttribute"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html class="no-js">
    <head>
        
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
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
		<script type="text/javascript">
		function alertName(){
			var Msg ='<%=session.getAttribute(SessionAttribute.WRONG_CREDENTIALS)%>';
    		if (<%=session.getAttribute(SessionAttribute.WRONG_CREDENTIALS)%>) {
 				alert("Credentials not correct, please try again.");
 				<%session.setAttribute(SessionAttribute.WRONG_CREDENTIALS, false);%>
 			} 
 		}
 		</script> 
 		
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
        <div id="navbar" class="navbar-collapse collapse">
          <form class="navbar-form navbar-right" role="form" action="LoginServlet" method="post" onsubmit="logTime('sign in')">
            <div class="form-group">
              <input type="text" placeholder="Username" class="form-control" name="username" autofocus id="username">
            </div>
            <div class="form-group">
              <input type="password" placeholder="Password" class="form-control" name="pwd" id="password">
            </div>
            <a style="color:#7E7E7E; float:right; margin-left: 12px; margin-top: 12px;" href="registration.jsp">Sign up</a>
            <button type="submit" class="btn btn-success" onclick="logTime('sign in')">Sign in</button>
          </form>
        </div><!--/.navbar-collapse -->
      </div>
    </nav>

    <!-- Main jumbotron for a primary marketing message or call to action -->
    <div class="jumbotron">
      <div class="container">
        <img src="img/BUW_R0_G107_B148.png" style="max-width:200px; width:100%;">
        <h1>Computer Science for Digital Media, HCI</h1>
        <p>The Faculty of Media offers Computer Science for Digital Media (Bachelor of Science) as an undergraduate bachelor's programme, and Computer Science for Digital Media (Master of Science) as an English-language master's programme. We also offer an English-language Human-Computer Interaction (Master of Science) master's programm. During your studies you will acquire analytical, creative and practical skills for software and systems development in the field of digital media. The BSc and MSc programmes are taught by internationally renowned professors from the respective fields and you will work in our well-equipped modern laboratories. Problem-based active learning and interdisciplinary course work is a central and important aspect of our curriculum.</p>
        <p><a class="btn btn-primary btn-lg" href="https://www.uni-weimar.de/en/media/studies/computer-science-for-digital-media-hci/" role="button">Learn more &raquo;</a></p>
      </div>
    </div>

    <div class="container">
      <!-- Example row of columns -->
      <div class="row">
        <div class="col-md-4">
          <h2 style="height:96px;">Medieninformatik (Computer Science and Media), B.Sc.</h2>
          <p align="justify">Im Bachelor-Studiengang Medieninformatik, Computer Science and Media (B.Sc.), vermitteln Ihnen die Professorinnen und Professiren analytische, kreative und konstruktive Fähigkeiten zur Software- und Systementwicklung für digitale Medien.<br>
          	Das grundlagenorientierte Bachelorstudium basiert auf Lehrveranstaltungen zu den theoretischen und angewandten Themen der Mathematik und Informatik für Digitale Medien in Kombination mit Angeboten aus den Bereichen Medienmanagement, Medienkunst/Mediengestaltung und Medienwissenschaft. Diese Gebiete sind deutschlandweit einzigartig an der Fakultät Medien der Bauhaus-Universität Weimar vereint. Dieser exzellente Rahmen und die projektorientierte Konzeption des Studiengangs erlauben neben der Vermittlung der grundlegenden fachlichen und methodischen Kompetenzen auch die Berücksichtigung von zentralen Schlüsselkompetenzen, &raquo;soft skills&laquo; wie beispielsweise Präsentationsfähigkeiten oder Erfahrungen in der fachübergreifenden Zusammenarbeit.
          <p><a class="btn btn-default" href="https://www.uni-weimar.de/en/media/studies/computer-science-for-digital-media-hci/medieninformatik-computer-science-and-media-bsc/" role="button">Learn more &raquo;</a></p>
        </div>
        <div class="col-md-4">
          <h2 style="height:96px;">Computer Science for Digital Media, M.Sc.</h2>
          <p align="justify">In the English-language Master's degree programme Computer Science for Digital Media, students are introduced to the latest research findings in the field of interactive digital media. In addition to providing research-oriented professional training, the programme helps students acquire communication and presentation skills through their project work.<br>
          	The entire programme is taught in English. »English is the technical language in computer science and thus it has to be an integral part of the curriculum,« says the Computer Science for Digital Media coordinator Prof. Dr. Stefan Lucks. »Our graduates are thus optimally prepared for the IT job market and are competitive especially in international research and development.«</p>
          <p><a class="btn btn-default" href="https://www.uni-weimar.de/en/media/studies/computer-science-for-digital-media-hci/computer-science-for-digital-media-msc/" role="button">Learn more &raquo;</a></p>
       </div>
        <div class="col-md-4">
          <h2 style="height:96px;">Human-Computer Interaction, M.Sc.</h2>
          <p align="justify">Design and development of intelligent software systems and interfaces have become increasingly important with the pervasiveness of mobile devices and ubiquitous technologies. In the English-language Human-Computer Interaction Master of Science programme, students focus on theoretical and practical issues in current Computer Science research in interface design and interactive system development.<br>
			This technically-oriented HCI master's programme further offers the opportunity to participate in interdisciplinary projects and to attend courses from other study programs, such as Media Art and Design, Architecture, and other courses from the Faculty of Art and Design. </p>
          <p><a class="btn btn-default" href="https://www.uni-weimar.de/en/media/studies/computer-science-and-media-hci/human-computer-interaction-msc/" role="button">Learn more &raquo;</a></p>
        </div>
      </div>

      <hr>

      <footer>
        <p>&copy; SecuriCast Demo 2017</p>
      </footer>
    </div> <!-- /container -->        <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
        <script>window.jQuery || document.write('<script src="js/vendor/jquery-1.11.1.min.js"><\/script>')</script>

        <script src="js/vendor/bootstrap.min.js"></script>

        <script src="js/main.js"></script>
        
        <script type="text/javascript"> window.onload = alertName; </script>
    </body>
</html>
