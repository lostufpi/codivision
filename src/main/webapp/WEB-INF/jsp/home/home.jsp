<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title><fmt:message key="app.name"/></title>

    <!-- Bootstrap Core CSS -->
    <link href="<c:url value="/vendor/bootstrap/css/bootstrap.min.css" />" rel="stylesheet">

    <!-- Custom CSS -->
    <link href="<c:url value="/css/home.css" />" rel="stylesheet">

    <!-- Custom Fonts -->
    <!-- <link href="font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css"> -->
    <!-- <link href="http://fonts.googleapis.com/css?family=Lato:300,400,700,300italic,400italic,700italic" rel="stylesheet" type="text/css"> -->

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    
 

</head>

<body>

    <!-- Navigation -->
    <nav class="navbar navbar-default navbar-fixed-top topnav" role="navigation">
        <div class="container topnav">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <figure class="logo">
                <a class="navbar-brand topnav" href="#"><img src="img/logo.png"></a>
                </figure>
            </div>
            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            	<ul class="nav navbar-nav">
				<c:choose>
					<c:when test="${not empty userSession.user}">
						<li><a href="${linkTo[RepositoryController].list }"><fmt:message key="repository.list"/></a>
					</c:when>
					<c:otherwise>
					</c:otherwise>
				</c:choose>
				</ul>
                <ul class="nav navbar-nav navbar-right">
                    <c:choose>
					<c:when test="${not empty userSession.user}">
						<li><a href="${linkTo[UserController].profile }">${userSession.user.name}</a></li>
						<li><a href="${linkTo[UserController].logout}"><fmt:message key="exit"/></a></li>
					</c:when>
					<c:otherwise>
						
						<li><a href="${linkTo[UserController].login}"><fmt:message key="signin"/></a></li>
					</c:otherwise>
				</c:choose>
				 <li><a href="#">Contato</a></li>
				 <li><a href="#">Sobre</a></li>
                
				
                </ul>
                
                
            
            </div>
            <!-- /.navbar-collapse -->
        </div>
        <!-- /.container -->
    </nav>


    <!-- Header -->
    <div id="home" class="intro-header">
        <div class="container">

            <div class="row">
                <div class="col-lg-12">
                    <div class="intro-message">
                        <h1><br><br><br></h1>
                   
                        <a href="#about" class="scroll btn btn-default btn-lg"><fmt:message key="home.getstarted"/></a>
                        <br><br>
                    </div>
                </div>
            </div>

        </div>
        <!-- /.container -->

    </div>
    <!-- /.intro-header -->

    <!-- Page Content -->

    <div id="about" class="content-section-a">

        <div class="container">
            <div class="row">
                <div class="col-lg-5 col-sm-6">
                    <hr class="section-heading-spacer">
                    <div class="clearfix"></div>
                    <h2 class="section-heading"><fmt:message key="home.one.title"/></h2>
                    <p class="lead"><fmt:message key="home.one.description"/></p>
                </div>
                <div class="col-lg-5 col-lg-offset-2 col-sm-6">
                    <img class="img-responsive" src="img/tree.jpg" alt="">
                </div>
            </div>

        </div>
        <!-- /.container -->

    </div>
    <!-- /.content-section-a -->

    <div class="content-section-b">

        <div class="container">

            <div class="row">
                <div class="col-lg-5 col-lg-offset-1 col-sm-push-6  col-sm-6">
                    <hr class="section-heading-spacer">
                    <div class="clearfix"></div>
                    <h2 class="section-heading"><fmt:message key="home.two.title"/></h2>
                    <p class="lead"><fmt:message key="home.two.description"/></p>
                </div>
                <div class="col-lg-5 col-sm-pull-6  col-sm-6">
                    <img class="img-responsive" src="img/chart.jpg" alt="">
                </div>
            </div>

        </div>
        <!-- /.container -->

    </div>
    <!-- /.content-section-b -->

    <div class="content-section-a">

        <div class="container">

            <div class="row">
                <div class="col-lg-5 col-sm-6">
                    <hr class="section-heading-spacer">
                    <div class="clearfix"></div>
                    <h2 class="section-heading"><fmt:message key="home.three.title"/></h2>
                    <p class="lead"><fmt:message key="home.three.description"/></p>
                </div>
                <div class="col-lg-5 col-lg-offset-2 col-sm-6">
                    <img class="img-responsive" src="img/weight.png" alt="">
                </div>
            </div>

        </div>
        <!-- /.container -->

    </div>
    <!-- /.content-section-a -->

	
    <div id="contact" class="banner">

        <div class="container">

            <div class="row">
                <div class="col-lg-8">
                    <h2><fmt:message key="home.getstarted.message"/></h2>
                </div>
                <div class="col-lg-4">
                    <ul class="list-inline banner-social-buttons">
                        <li>
                            <a href="${linkTo[UserController].add}" class="btn btn-primary btn-lg"><i class="fa fa-linkedin fa-fw"></i> <span class="network-name"><fmt:message key="sign_up"/></span></a>
                        </li>
                    </ul>
                </div>
            </div>

        </div>
        <!-- /.container -->

    </div>
    <!-- /.banner -->

    <!-- Footer -->
    <footer>
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <ul class="list-inline">
                        <li>
                            <a href="#home" class="scroll"><fmt:message key="home"/></a>
                        </li>
                        <li class="footer-menu-divider">&sdot;</li>
                        <li>
                            <a href="#about" class="scroll"><fmt:message key="about"/></a>
                        </li>
                        <li class="footer-menu-divider">&sdot;</li>
                        <li>
                            <a href="#contact" class="scroll"><fmt:message key="sign_up"/></a>
                        </li>
                    </ul>
                    <p class="copyright text-muted small"><fmt:message key="copyright"/></p>
                </div>
            </div>
        </div>
    </footer>

    <script src="<c:url value="/vendor/jquery/jquery.min.js" />"></script>
	<script src="<c:url value="/vendor/bootstrap/js/bootstrap.min.js" />"></script>

	<script>
	jQuery(document).ready(function($) { 
	    $(".scroll").click(function(event){        
	        event.preventDefault();
	        $('html,body').animate({scrollTop:$(this.hash).offset().top}, 800);
	   });
	});
	</script>

</body>

</html>
