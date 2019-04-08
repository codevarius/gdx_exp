package com.gazp.gam;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class MainGame extends Game implements InputProcessor {
	SpriteBatch batch;
	Texture img;
	int x,y;
	FrameBuffer frameBuffer;
	OrthographicCamera camera;
	Sprite sprite;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		Gdx.input.setInputProcessor(this);
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),false);
		camera = new OrthographicCamera();
		camera.setToOrtho(false,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		sprite = new Sprite(new Texture(Gdx.files.internal("light.png")));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setBlendFunction(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
		batch.begin();
		batch.draw(img,x,y);
		batch.end();

        frameBuffer.begin();

        Gdx.gl.glClearColor(.2f,.2f,.2f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.setBlendFunction(GL20.GL_ONE,GL20.GL_ONE);
        batch.begin();
        sprite.draw(batch);
        batch.end();

        frameBuffer.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}

    @Override
    public boolean keyDown(int keycode) {
	    switch(keycode){
            case Input.Keys.UP: y = y + 10; break;
            case Input.Keys.DOWN: y = y - 10; break;
            case Input.Keys.LEFT: x = x - 10; break;
            case Input.Keys.RIGHT: x = x + 10; break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
