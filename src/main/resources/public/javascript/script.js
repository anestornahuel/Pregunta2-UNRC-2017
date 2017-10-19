function inner (id, msj) {
	document.getElementById(id).innerHTML = msj;
}

function second(id, max, botonid) {	
	var scnd1 = new Date().getTime();
	var interval = setInterval( 
		function() {
			var d = new Date();
			var scnd = max - Math.round((d.getTime() - scnd1)/1000);
			inner(id, scnd);			
			if (scnd <= 0) {
				document.getElementById(botonid).click();
				clearInterval(interval);
			};
		}
		,1000
	);
}