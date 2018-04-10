package pl.lonski.slowouczyciel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Word extends Actor {

	private final FileHandle textureFile;
	private Texture texture;
	private Text label;
	private final String displayName;
	private final String spokenName;
	private boolean drawLabel;

	Word(FileHandle file, String name) {
		this.textureFile = file;
		String[] arr = name.split("&");
		this.displayName = arr[0].substring(0, 1).toUpperCase() + arr[0].substring(1).replaceAll("_", " ");
		this.spokenName = (arr.length == 2 ? arr[1] : displayName).replaceAll("_", " ");
		this.drawLabel = true;
		setPosition(Gdx.graphics.getWidth(), 0);
	}

	void load() {
		if (texture == null) {
			texture = new Texture(textureFile);
			label = new Text(this.displayName, Color.RED);
		}
	}

	String getDisplayName() {
		return displayName;
	}

	String getSpokenName() {
		return spokenName;
	}

	void setLabelVisible(boolean visible) {
		drawLabel = visible;
	}

	@Override
	public void draw(Batch batch, float alpha) {
		if (texture != null) {
			batch.draw(texture, getX(), getY(), Gdx.graphics.getWidth() * getScaleX(),
					Gdx.graphics.getHeight() * getScaleY());
			if (drawLabel) {
				label.draw(batch, getX(), getY(), getScaleX(), getScaleY());
			}
		}
	}
}