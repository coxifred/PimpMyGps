/*

body, html {
  overflow: hidden;
	background: #111;
	height: 100%;
 -webkit-user-select: none;
}

canvas {
  position: absolute;
	background: #666666;
}
*/

var BEAT_VELOCITY = 0.005,
  BEAT_FREQUENCY = 5,
	BEAT_LIMIT = 7;

// The world dimensions
var world = { 
		width: window.innerWidth, 
		height: window.innerHeight,
    	center: { x: window.innerWidth/2, y: window.innerHeight/2 }
	},

	id = 0,
	currentBeat = null,
	beats = [];

/**
 * 
 */
function initializeRadar() {
	var ctx = Sketch.create();
    ctx.fillStyle = "rgba(0, 0, 200, 0)";
    ctx.setup = function() {
		var i,j,x=0,y=0;

		// Generate beats
		for( i = 0; i < BEAT_LIMIT; i++ ) {
			var beat = new Beat( 
				world.center.x,
				world.center.y,
				i
			);

			beats.push( beat );
		}
	}

	ctx.draw = function() {
	  // Render beats
		ctx.save();

		var activeBeats = 0;

		for( var i = 0, len = beats.length; i < len; i++ ) {
			var beat = beats[i];

			this.updateBeat( beat );
			this.drawBeat( beat );

			if( beat.active ) activeBeats ++;
		}

		ctx.restore();

		var nextBeat = currentBeat ? beats[ ( currentBeat.index + 1 ) % beats.length ] : null;

		if( !currentBeat ) {
			currentBeat = beats[0];
			currentBeat.activate();
		}
		else if( !nextBeat.active && activeBeats < BEAT_FREQUENCY && currentBeat.strength > 1 / BEAT_FREQUENCY ) {
			currentBeat = nextBeat;
			currentBeat.activate();
		}
	}

	

	
	ctx.updateBeat = function( beat ) {
		if( beat.active ) {
			beat.strength += BEAT_VELOCITY;
		}

		// Remove used up beats
		if( beat.strength > 2 ) {
			beat.deactivate();
		}
		
	}

	ctx.drawBeat = function( beat ) {
		if( beat.active && beat.strength > 0 ) {
			beat.x = world.center.x;
	        beat.y = world.center.y;
			ctx.beginPath();
			ctx.arc( beat.x, beat.y, Math.max( (beat.size * beat.strength)-2, 0 ), 0, Math.PI * 2, true );
			ctx.lineWidth = 36;
			ctx.globalAlpha = 0.7 * ( 2.5 - beat.strength );
			ctx.strokeStyle = beat.color;
			ctx.stroke();

			ctx.beginPath();
			ctx.arc( beat.x, beat.y, beat.size * beat.strength, 0, Math.PI * 2, true );
			ctx.lineWidth = 2;
			ctx.globalAlpha = 0.8 * ( 1 - beat.strength );
			ctx.strokeStyle = beat.color;
			ctx.stroke();
		}
	}


}




/**
 * Represents a beat that triggers nodes.
 */
function Beat( x, y, index ) {
	this.x = x;
	this.y = y;
	this.color = 'hsla(0, 82%, 50%, 0.2)';
	this.index = index;
	this.level = ++id;
	this.size = Math.max( world.width, world.height ) * 0.65;
	this.active = false;
	this.strength = 0;
};
Beat.prototype.activate = function() {
	this.level = ++id;
	this.active = true;
	this.strength = 0;
};
Beat.prototype.deactivate = function() {
	this.active = false;
};





