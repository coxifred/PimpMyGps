

$(document).ready(function() {
		  // Check if logged
		
		var actualLocation=document.location.pathname;
	    
	    checkLogged();
	    
	    function checkLogged()
	    {
	    	
	    	getValueFromUrl("admin?action=isLogged",true,function(isLogged){
	    		if ( isLogged == "false" || isLogged == "" )
    			{
	    			if ( actualLocation != "/index.html" )
    					{
	    				document.location="index.html";
    					}
    			}
	    	});
	    } 			    
	});
