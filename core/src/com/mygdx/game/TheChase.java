package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;


public class TheChase extends ApplicationAdapter implements InputProcessor{
	SpriteBatch batch;
	MyGameSprite girPiggy;
	MyGameSprite taco;
	Texture img;
	Texture img2;
	Boolean ifLeft = false, ifRight = false;
	Random r;
	String message;
	BitmapFont font;
	
	World world;
	Body edgeB;
	Body edgeL;
	Body edgeR;
	Body edgeT;
	
	int dx = 0;
	int dy = 0;
	int scr =1;
	
	final float scaling = 100f;
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("girPiggy.png");
		img2 = new Texture("taco.png");
		font = new BitmapFont();
		font.setColor(Color.BLACK);
		message = "";
		r = new Random();
		dx  = r.nextInt(11) - 5;
		dy  = r.nextInt(11) - 5;
		
		world = new World(new Vector2(0, 0), true);
		girPiggy = new MyGameSprite(world, img, scaling);
		taco = new MyGameSprite(world, img2, scaling);
		
		taco.setPosition(r.nextInt((int)(Gdx.graphics.getWidth() - taco.getWidth())),
				 r.nextInt((int) (Gdx.graphics.getHeight() - taco.getHeight())));
	
		// Create a "bottom of the screen"
				BodyDef bodyDef = new BodyDef();
				bodyDef.type = BodyDef.BodyType.StaticBody; // This guy won't be affected by any forces
				bodyDef.position.set(0,0);
				edgeB = world.createBody(bodyDef);
				FixtureDef fixtureDef = new FixtureDef();
				EdgeShape edge = new EdgeShape();
				edge.set(0,  0, Gdx.graphics.getWidth()/scaling, 0);
				fixtureDef.shape = edge;
				edgeB.createFixture(fixtureDef);
				edge.dispose();
				
				// Create a "top of the screen"
				edgeT = world.createBody(bodyDef);
				edge = new EdgeShape();
				edge.set(0,  Gdx.graphics.getHeight()/scaling, Gdx.graphics.getWidth()/scaling, Gdx.graphics.getHeight()/scaling);
				fixtureDef.shape = edge;
				edgeT.createFixture(fixtureDef);
				edge.dispose();
				
				// Create a "left of the screen"
				edgeL = world.createBody(bodyDef);
				edge = new EdgeShape();
				edge.set(0, 0, 0, Gdx.graphics.getHeight()/scaling);
				fixtureDef.shape = edge;
				edgeL.createFixture(fixtureDef);
				edge.dispose();

				// Create a "right of the screen"
				edgeL = world.createBody(bodyDef);
				edge = new EdgeShape();
				edge.set(Gdx.graphics.getWidth()/scaling, 0, Gdx.graphics.getWidth()/scaling, Gdx.graphics.getHeight()/scaling);
				fixtureDef.shape = edge;
				edgeL.createFixture(fixtureDef);
				edge.dispose();
				
				// Allow this class to process input
				Gdx.input.setInputProcessor(this);
				world.setContactListener(new ContactListener(){
					public void beginContact(Contact contact){
						// Check to see if collision is between the first body and the ground.
						// If so, push body1 up and spin body2
						// NOTE: have to check both bodies in position A and B
						Body bodyA = contact.getFixtureA().getBody();
						Body bodyB = contact.getFixtureB().getBody();
						
						if( girPiggy.body == bodyA && taco.body == bodyB || girPiggy.body == bodyB && taco.body == bodyA) {
							//System.out.println("Collision!");
							message = "Score: " + scr++;
							taco.rotate90(true);
							
							
						}
					}
					
					// Other methods must be present, but we don't care about them
					public void endContact(Contact contact) {}
					public void preSolve(Contact contact, Manifold oldManifold) {}
					public void postSolve(Contact contact, ContactImpulse impulse) {}	
				});

				
	}//close create()

	@Override
	public void render () {
		
		world.step(Gdx.graphics.getDeltaTime(), 6, 2);
		
		girPiggy.updatePhysics();
		taco.updatePhysics();
		boundaries();
		
			
		Gdx.gl.glClearColor(0, 10, 90, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		font.draw(batch, message, 300, 250);

		batch.draw(girPiggy, girPiggy.getX(), girPiggy.getY(), girPiggy.getOriginX(), girPiggy.getOriginY(),
				   girPiggy.getWidth(), girPiggy.getHeight(), girPiggy.getScaleX(), girPiggy.getScaleY(),
				   girPiggy.getRotation());
		taco.draw(batch);
		moveTaco();
		
		
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		img2.dispose();
		world.dispose();
		
	}
	
	private void moveTaco() {
		if(taco.getX() < 0 || taco.getX() > Gdx.graphics.getWidth() - taco.getWidth())
			dx = - dx;
		if(taco.getY() < 0 || taco.getY() > Gdx.graphics.getHeight() - taco.getHeight())
			dy = - dy;
		taco.setPosition(taco.getX() + dx, taco.getY() + dy);	
	}
	
		private void boundaries() {
			//stop gir from leaving screen
			if(girPiggy.getY() >= Gdx.graphics.getHeight()/1.48f)
				girPiggy.setY(Gdx.graphics.getHeight()/1.48f);
			if(girPiggy.getY() <= 0)
				girPiggy.setY(0);
			if(girPiggy.getX() >= Gdx.graphics.getWidth()/1.25f)
				girPiggy.setX(Gdx.graphics.getWidth()/1.25f);
			if(girPiggy.getX() <= 0)
				girPiggy.setX(0);	
		}

		@Override
		public boolean keyDown(int keycode) {
			if(keycode == Input.Keys.RIGHT) {
				//girPiggy.setPosition(girPiggy.getX()+10f, girPiggy.getY());
				girPiggy.move(10, 0);
				
				if(ifRight == false) {
					girPiggy.flip(true, false);
					ifLeft = false;
					ifRight = true;
				}
			}
	        if(keycode == Input.Keys.LEFT) {
	        	//girPiggy.setPosition(girPiggy.getX()-10f, girPiggy.getY());
	        	girPiggy.move(-10, 0);
				
	        	if(ifLeft == false) {
					girPiggy.flip(true, false);
					ifLeft = true;
					ifRight = false;
				}
	        }
	        if(keycode == Input.Keys.UP)
	        	//girPiggy.setPosition(girPiggy.getX(), girPiggy.getY()+10f);
	        	girPiggy.move(0, 10);
	        if(keycode == Input.Keys.DOWN)
	        	//girPiggy.setPosition(girPiggy.getX(), girPiggy.getY()-10f);
	        	girPiggy.move(0, -10);
	        return true;
		}

		@Override
		public boolean keyUp(int keycode) {
			if(keycode == Input.Keys.LEFT)
				girPiggy.setPosition(girPiggy.getX(), girPiggy.getY());
	        if(keycode == Input.Keys.RIGHT)
	        	girPiggy.setPosition(girPiggy.getX(), girPiggy.getY());
	        if(keycode == Input.Keys.UP)
	        	girPiggy.setPosition(girPiggy.getX(), girPiggy.getY());
	        if(keycode == Input.Keys.DOWN)
	        	girPiggy.setPosition(girPiggy.getX(), girPiggy.getY());
	        	return true;
		}
		
		
		public boolean keyTyped(char character) {return false;}
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {return false;}
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {return false;}
		public boolean touchDragged(int screenX, int screenY, int pointer) {return false;}
		public boolean mouseMoved(int screenX, int screenY) {return false;}
		public boolean scrolled(int amount) {return false;}
}