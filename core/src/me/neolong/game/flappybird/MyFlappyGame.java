package me.neolong.game.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import me.neolong.game.flappybird.component.Bird;
import me.neolong.game.flappybird.component.Pipe;
import me.neolong.game.flappybird.component.ScoreFactory;

public class MyFlappyGame extends ApplicationAdapter {
	private final static Vector2 GRAVITY = new Vector2(0, -20);
	private final static float PULSE_UP = 300;
	private final static int PIPES_PER_SCREEN = 2;
	private final static int VELOCITY_X = 4;
	private final static int H_OFFSET = 0;

	SpriteBatch batch;
	OrthographicCamera camera, uiCamera;

	Bird yellowBird, blueBird, redBird;
	Texture bgDay, bgNight, pipeUp, pipeDown, land, tips;
	Array<Pipe[]> pipes = new Array<Pipe[]>();

	private static float PIPE_OFFSET;
	float stateTime = 0;
	float landOffset = 0;
	float pipeGap = 0;
	int score = 0;
	Vector2 scoreCenter;
	GameState gameState = GameState.LAUNCHED;

	Bird.BirdItem curBird;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.update();
		uiCamera = new OrthographicCamera();
		uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		uiCamera.update();

		pipeGap = Gdx.graphics.getWidth()/PIPES_PER_SCREEN;
		scoreCenter = new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()*3/4);

		bgDay = new Texture("bg_day.png");
		bgNight = new Texture("bg_night.png");
		pipeUp = new Texture("pipe_down.png");
		pipeDown = new Texture("pipe_up.png");
		land = new Texture("land.png");
		tips = new Texture("tutorial.png");

		yellowBird = Bird.getBird(Bird.BirdType.YELLOW);
		blueBird = Bird.getBird(Bird.BirdType.BLUE);
		redBird = Bird.getBird(Bird.BirdType.RED);

		resetGame();
	}
	private void resetGame(){
		yellowBird.pos = blueBird.pos = redBird.pos = new Vector2(Gdx.graphics.getWidth()/4, Gdx.graphics.getHeight()/2);
		yellowBird.velocity = blueBird.velocity = redBird.velocity = new Vector2(Gdx.graphics.getWidth()/VELOCITY_X, 0);
		PIPE_OFFSET = Gdx.graphics.getWidth()/2;
		landOffset = 0;
		score = 0;

		pipes.clear();
		for(int i = 1; i <= PIPES_PER_SCREEN; i++){
			PIPE_OFFSET += pipeGap;
			pipes.add(Pipe.getPipes(land.getHeight() - H_OFFSET, Gdx.graphics.getHeight(), PIPE_OFFSET, pipeUp, pipeDown, yellowBird.getKeyFrame(1).getRegionHeight()));
		}
		camera.position.x = Gdx.graphics.getWidth()/2;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		float deltaTime = Gdx.graphics.getDeltaTime();
		stateTime += deltaTime;
		updateGame(deltaTime);

		batch.setProjectionMatrix(uiCamera.combined);
		batch.begin();
		batch.draw(bgDay, 0, 0, Gdx.graphics.getHeight(), Gdx.graphics.getHeight());
		if(gameState == GameState.LAUNCHED){
			batch.draw(tips, (Gdx.graphics.getWidth()-tips.getWidth())/2, (Gdx.graphics.getHeight()-tips.getHeight())/2);
		}
		batch.end();

		camera.position.x = curBird.pos.x+Gdx.graphics.getWidth()/4;
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		// draw pipes
		for(int i = 0; i < pipes.size; i++){
			Pipe[] pips = pipes.get(i);
			for(Pipe p : pips){
				batch.draw(p.img, p.pos.x, p.pos.y, p.width, p.height);
			}
		}
		batch.draw(curBird, curBird.pos.x, curBird.pos.y, curBird.getRegionWidth()/2, curBird.getRegionHeight()/2, curBird.getRegionWidth(), curBird.getRegionHeight(), 1, 1, blueBird.angle );
		// draw land
		batch.draw(land, landOffset, 0, Gdx.graphics.getWidth(), land.getHeight());
		batch.draw(land, landOffset+Gdx.graphics.getWidth(), 0, Gdx.graphics.getWidth(), land.getHeight());
		batch.end();

		batch.setProjectionMatrix(uiCamera.combined);
		batch.begin();
		if(gameState == GameState.RUNNING || gameState == GameState.OVER){
			ScoreFactory.getScore(score, scoreCenter).draw(batch);
		}
		batch.end();
	}
	private void updateGame(float deltaTime){
		transformState();
		curBird = blueBird.getKeyFrame(stateTime);
		if(gameState == GameState.RUNNING){
			if(isHitBoundary()){
				gameState = GameState.OVER;
			}
			if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched()){
				blueBird.velocity.y = PULSE_UP;
			}else{
				blueBird.velocity.add(GRAVITY);
			}
			calcBirdRotation();
			blueBird.pos.mulAdd(blueBird.velocity, deltaTime);
			blueBird.pos.y = Math.min(Math.max(blueBird.pos.y, land.getHeight()-H_OFFSET),Gdx.graphics.getHeight()-curBird.getRegionHeight()+H_OFFSET);

			// update land
			if(camera.position.x-Gdx.graphics.getWidth()/2 > landOffset+Gdx.graphics.getWidth()){
				landOffset += Gdx.graphics.getWidth();
			}

			if(idBirdHitPipe(blueBird, pipes)){
				this.gameState = GameState.OVER;
			}

			// update pipes
			if(pipes.get(pipes.size-1)[0].pos.x + pipeGap <= camera.position.x + Gdx.graphics.getWidth()/2){
				PIPE_OFFSET += pipeGap;
				pipes.add(Pipe.getPipes(land.getHeight() - H_OFFSET, Gdx.graphics.getHeight(), PIPE_OFFSET, pipeUp, pipeDown, curBird.getRegionHeight()));
			}
			if(pipes.get(0)[0].pos.x+pipeUp.getWidth() <= camera.position.x-Gdx.graphics.getWidth()/2){
				pipes.removeIndex(0);
			}
		}else if(gameState == GameState.OVER){
			// fall down the bird
			blueBird.velocity.add(GRAVITY);
			blueBird.pos.mulAdd(blueBird.velocity, deltaTime);
			blueBird.pos.y = Math.min(Math.max(blueBird.pos.y, land.getHeight()-H_OFFSET),Gdx.graphics.getHeight()-curBird.getRegionHeight()+H_OFFSET   );
			calcBirdRotation();
			if(isHitBoundary()){
				gameState = GameState.SHOW_SCORE;
			}
		}
	}
	private void transformState(){
		if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched()){
			if(gameState == GameState.LAUNCHED){
				gameState = GameState.RUNNING;
			}
			if(gameState == GameState.SHOW_SCORE){
				resetGame();
				gameState = GameState.RUNNING;
			}
		}
	}
	private void stopBird(){
		blueBird.velocity.x = blueBird.velocity.y = 0;
	}
	private void calcBirdRotation(){
		if(blueBird.velocity.y > 0){
			blueBird.angle = Math.max(blueBird.angle,0);
			blueBird.angle++;
			blueBird.angle = Math.min(30, blueBird.angle);
		}else if(blueBird.velocity.y == 0){
			blueBird.angle = 0;
		}else{
			blueBird.angle = Math.min(blueBird.angle, 0);
			blueBird.angle-=1.5;
			blueBird.angle = Math.max(-80, blueBird.angle);
		}
	}

	boolean scoreHasAdd = false;
	private boolean idBirdHitPipe(Bird bird, Array<Pipe[]> pipes){
		if(null == bird || null == pipes || pipes.size == 0){
			return false;
		}
		Rectangle birdRect = blueBird.getRect();
		for(Pipe[] ps : pipes){
			for(int i = 0; i < 2; i++){
				if(ps[i].passed){
					continue;
				}
				Rectangle pipeRect = ps[i].getRect();
				// 鸟在管子后面
				if(birdRect.getX() + birdRect.getWidth() <  pipeRect.getX()){
					scoreHasAdd = false;
					return false;
				}
				//
				if(birdRect.getX() > pipeRect.getX()+pipeRect.getWidth()){
					if(!scoreHasAdd){
						score++;
						scoreHasAdd = true;
					}
					ps[i].passed = true;
					continue;
				}
				if(pipeRect.overlaps(birdRect)){
					stopBird();
					return true;
				}
			}
		}

		return false;
	}
	public boolean isHitBoundary(){
		if(blueBird.pos.y <= land.getHeight()-H_OFFSET || blueBird.pos.y >= Gdx.graphics.getHeight()-curBird.getRegionHeight()+H_OFFSET){
			stopBird();
			return true;
		}
		return false;
	}


	@Override
	public void dispose () {
		batch.dispose();

	}

	public enum GameState{
		LAUNCHED, READY, RUNNING, PAUSE, OVER, SHOW_SCORE;
	}
}