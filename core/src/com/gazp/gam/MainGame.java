package com.gazp.gam;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class MainGame extends Game{
	SpriteBatch batch;
	float x,y;
	FrameBuffer frameBuffer;
	OrthographicCamera camera;
	Sprite sprite;
	TiledMap tiledMap;
	OrthogonalTiledMapRenderer mapRenderer;
	TiledMapTileLayer lights,start_point;
	ArrayList<Vector2> light_cells;
	Vector2 start;
	float delta;
	float step = 2.0f;
	Animation<TextureRegion> wlkUp,wlkDown,wlkLeft,wlkRight;
	TextureRegion[] keyFrameList;
	TextureRegion currentChrFrame;
	float frameRate = 0.15f;
	TiledMapTileLayer block_layer;
	
	@Override
	public void create () {
	    keyFrameList = new TextureRegion[3];

	    for (int i = 0; i<3;i++){
	        keyFrameList[i] = new TextureRegion(new Texture("chr/row-1-col-"+i+".png"));
            keyFrameList[i].flip(false,true);
        }
	    wlkDown = new Animation(frameRate,keyFrameList);
        keyFrameList = new TextureRegion[3];

        for (int i = 0; i<3;i++){
            keyFrameList[i] = new TextureRegion(new Texture("chr/row-2-col-"+i+".png"));
            keyFrameList[i].flip(false,true);
        }
        wlkLeft = new Animation(frameRate,keyFrameList);
        keyFrameList = new TextureRegion[3];

        for (int i = 0; i<3;i++){
            keyFrameList[i] = new TextureRegion(new Texture("chr/row-3-col-"+i+".png"));
            keyFrameList[i].flip(false,true);
        }
        wlkRight = new Animation(frameRate,keyFrameList);
        keyFrameList = new TextureRegion[3];

        for (int i = 0; i<3;i++){
            keyFrameList[i] = new TextureRegion(new Texture("chr/row-4-col-"+i+".png"));
            keyFrameList[i].flip(false,true);
        }
                wlkUp = new Animation(frameRate,keyFrameList);
        keyFrameList = new TextureRegion[3];

        currentChrFrame = wlkDown.getKeyFrame(0); //default

	    light_cells = new ArrayList<Vector2>();
		batch = new SpriteBatch();
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,
                Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),false);
		sprite = new Sprite(new Texture(Gdx.files.internal("light.png")));
		sprite.setSize(200,200);


		tiledMap = new TmxMapLoader().load("map.tmx");
        block_layer = (TiledMapTileLayer) tiledMap.getLayers().get("blocks");
		mapRenderer = new OrthogonalTiledMapRenderer(tiledMap,2.5f);

		lights = (TiledMapTileLayer)tiledMap.getLayers().get("trees");

		for (int i=0;i<lights.getWidth();i++){
		    for (int j=0;j<lights.getHeight();j++){
		        if (lights.getCell(i,j) !=null){
		            if (lights.getCell(i,j).getTile().getProperties().containsKey("enlighted")){
		                light_cells.add(new Vector2(i*lights.getTileWidth() + lights.getTileWidth(),
                                j*lights.getTileHeight() + lights.getTileHeight()));
                    }
                }
            }
        }

		start_point = (TiledMapTileLayer)tiledMap.getLayers().get("bridges");

        for (int i=0;i<start_point.getWidth();i++){
            for (int j=0;j<start_point.getHeight();j++){
                if (start_point.getCell(i,j) !=null){
                    if (start_point.getCell(i,j).getTile().getProperties().containsKey("view_start")){
                        start = new Vector2(i*start_point.getTileWidth() + start_point.getTileWidth()/2,
                                j*start_point.getTileHeight() + start_point.getTileHeight()/2);
                    }
                }
            }
        }

        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        camera.setToOrtho(true,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

        camera.translate(mapRenderer.getUnitScale()*start.x - camera.viewportWidth/2,
                mapRenderer.getUnitScale()*start.y - camera.viewportHeight/2);

        x = mapRenderer.getUnitScale()*start.x - camera.viewportWidth/2;
        y = mapRenderer.getUnitScale()*start.y - camera.viewportHeight/2;
	}


	@Override
	public void render () {
	    delta += Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(0, 0, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateTouches();
		camera.update();
		mapRenderer.setView(camera);
        batch.setProjectionMatrix(camera.combined);

		mapRenderer.render();

        batch.setBlendFunction(GL20.GL_ONE,GL20.GL_ONE_MINUS_SRC_ALPHA);
		batch.begin();
		batch.draw(currentChrFrame,x+camera.viewportWidth/2,y+camera.viewportHeight/2);
        batch.end();

        mapRenderer.getBatch().begin();
        mapRenderer.renderTileLayer((TiledMapTileLayer)tiledMap.getLayers().get("trees"));
        mapRenderer.getBatch().end();

		//lights begin
        frameBuffer.begin();
        Gdx.gl.glClearColor(.15f,.15f,.15f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setBlendFunction(GL20.GL_ONE,GL20.GL_ONE);
        batch.begin();
        sprite.setColor(Color.ORANGE);
        sprite.setCenter(x+camera.viewportWidth/2,y+camera.viewportHeight/2);
        sprite.setScale(1f);
        sprite.draw(batch);

        for (Vector2 v : light_cells){
            sprite.setColor(Color.PURPLE);
            sprite.setCenter(v.x * mapRenderer.getUnitScale(),v.y * mapRenderer.getUnitScale());
            sprite.setScale(1+(float)(0.1f*Math.sin(2f*delta)));
            sprite.draw(batch);
        }

        batch.end();
        frameBuffer.end();

        batch.setBlendFunction( GL20.GL_ZERO,GL20.GL_SRC_COLOR);
        batch.begin();
        batch.draw(frameBuffer.getColorBufferTexture(),x,y);
        batch.end();
        //lights end
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		light_cells.clear();
		tiledMap.dispose();
	}


    public void updateTouches(){
	    
	    if (Gdx.input.isKeyPressed(Input.Keys.UP)){
	        currentChrFrame = wlkUp.getKeyFrame(delta,true);
            y = y - step;
            camera.translate(0,-step);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            currentChrFrame = wlkDown.getKeyFrame(delta,true);
            y = y + step;
            camera.translate(0,step);

        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            currentChrFrame = wlkLeft.getKeyFrame(delta,true);
            x = x - step;
            camera.translate(-step,0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            currentChrFrame = wlkRight.getKeyFrame(delta,true);
            x = x + step;
            camera.translate(step,0);
        }

    }
}
