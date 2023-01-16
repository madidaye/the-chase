package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class MyGameSprite extends Sprite {
	
	public World world;
	public Body body;
	public float scalingFactor;
	public boolean inactive = false;
	public MyGameSprite(World w, Texture t, float scale)
	{
		super(t);
		world = w;
		scalingFactor = scale;
		
		// Create the body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;	// A DynamicBody can move and be affected by physics
		bodyDef.position.set((this.getX() + this.getWidth()/2) / scalingFactor, 
							 (this.getY() + this.getHeight()/2)/ scalingFactor);
		body = world.createBody(bodyDef);
		
		// Define the shape of the body
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(this.getWidth()/2 /scalingFactor, this.getHeight()/2 /scalingFactor);
		
		// Define other properties such as density
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.1f;			// This, along with the size, affects the shape's mass
		fixtureDef.restitution = 0.5f;		// How bouncy the body will be
		body.createFixture(fixtureDef);
		
		shape.dispose();
	}
	
	public void updatePhysics()
	{
		this.setPosition((body.getPosition().x * scalingFactor) - this.getWidth()/2, 
		           (body.getPosition().y * scalingFactor) - this.getHeight()/2);
		this.setRotation((float)Math.toDegrees(body.getAngle()));
	}

	@Override
	public void setPosition(float x, float y) {
		// Override this to make the body match our position
		super.setPosition(x, y);
		
		 body.setTransform((this.getX() + this.getWidth()/2) / scalingFactor, 
				 (this.getY() + this.getHeight()/2)/ scalingFactor, 0);
		
	}
	
	public void move(float x, float y)
	{
		body.applyForceToCenter(x, y, true);
	}
	
	
	

}
