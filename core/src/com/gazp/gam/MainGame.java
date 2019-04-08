package com.gazp.gam;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	
	@Override
	public void create () {
	    light_cells = new ArrayList<Vector2>();
		batch = new SpriteBatch();
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,
                Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),false);
		sprite = new Sprite(new Texture(Gdx.files.internal("light.png")));
		sprite.setSize(200,200);


		tiledMap = new TmxMapLoader().load("map.tmx");
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



        System.out.println(start);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        camera.setToOrtho(true,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
	}


	@Override
	public void render () {
	    delta += Gdx.graphics.getDeltaTime();
		Gdx.gl.glClearColor(0, 0, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateTouches();
		camera.update();
		mapRenderer.setView(camera);
        batch.setProjectionMatrix(camera.combined);

		mapRenderer.render();

		//lights begin
        frameBuffer.begin();
        Gdx.gl.glClearColor(.2f,.2f,.2f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setBlendFunction(GL20.GL_ONE,GL20.GL_ONE);
        batch.setColor(Color.WHITE);
        batch.begin();
        sprite.setColor(Color.WHITE);
        sprite.setCenter(start.x*2.5f,start.y*2.5f);
        sprite.setScale(1f);
        sprite.draw(batch);

        for (Vector2 v : light_cells){
            sprite.setColor(Color.OLIVE);
            sprite.setCenter(v.x * 2.5f,v.y * 2.5f);
            sprite.setScale(1+(float)(0.1*Math.sin(delta)));
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
            y = y - 10;
            camera.translate(0,-10);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            y = y + 10;
            camera.translate(0,10);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            x = x - 10; camera.translate(-10,0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            x = x + 10;
            camera.translate(10,0);
        }

    }
}
