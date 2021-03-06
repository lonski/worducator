package pl.lonski.worducator;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class WordLearnStage extends WorducatorStage implements DirectionListener {

	private final float screenWidth;
	private final Speaker speaker;
	private final Worducator worducator;
	private final WordLoader loader;
	private final String loadingText;
	private List<Word> words;
	private int currentWordIdx;
	private Word currentWord;
	private Text loadingLabel;

	WordLearnStage(Config.Dataset dataset, Worducator worducator) {
		this.speaker = worducator.getSpeaker();
		this.worducator = worducator;
		this.words = new ArrayList<>();
		this.screenWidth = Gdx.graphics.getWidth();
		this.currentWordIdx = -1;
		this.loader = new WordLoader(dataset);
		this.loadingText = worducator.getConfig().spokenSentences.loading;

		loadingLabel = new Text("", Color.GREEN);
		loadingLabel.setPosition(100, 100);
		loadingLabel.setScale(0.7f);
		addActor(loadingLabel);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (!loader.isDone()) {
			loadingLabel.setText(String.format(loadingText, loader.getLoadedCount(), loader.getTotalCount()));
			loader.loadNext();
		} else if (words.isEmpty()) {
			this.words = loader.getWords();
			onLeft();
		}
	}

	@Override
	InputAdapter getInputAdapter() {
		return new SimpleDirectionGestureDetector(this);
	}

	private Action wrapAction(Action... toWrap) {
		List<Action> actions = new ArrayList<>();
		actions.add(touchable(Touchable.disabled));
		actions.addAll(Arrays.asList(toWrap));
		actions.add(touchable(Touchable.enabled));
		actions.add(new Action() {
			@Override
			public boolean act(float delta) {
				loadWordAt(currentWordIdx + 1);
				return true;
			}
		});
		return sequence(actions.toArray(new Action[]{}));
	}

	@Override
	public void onLeft() {
		if (currentWordIdx < words.size() - 1) {
			currentWordIdx += 1;
			if (currentWord != null) {
				currentWord.addAction(wrapAction(moveTo(-screenWidth, 0, 0.2f), removeActor()));
			}
			currentWord = getWordAtCurrentIdx();
			currentWord.setPosition(screenWidth, 0);
			currentWord.addAction(wrapAction(moveTo(0, 0, 0.2f)));
			addActor(currentWord);
			speaker.speak(currentWord.getWordName());
		} else {
			worducator.startExam(words);
		}
	}

	private Word getWordAtCurrentIdx() {
		loadWordAt(currentWordIdx);
		return words.get(currentWordIdx);
	}

	@Override
	public void onRight() {
		if (currentWordIdx > 0) {
			currentWordIdx -= 1;
			if (currentWord != null) {
				currentWord.addAction(wrapAction(moveTo(screenWidth, 0, 0.2f), removeActor()));
			}
			currentWord = getWordAtCurrentIdx();
			currentWord.setPosition(-screenWidth, 0);
			currentWord.addAction(wrapAction(moveTo(0, 0, 0.2f)));
			addActor(currentWord);
			speaker.speak(currentWord.getWordName());
		}
	}

	private void loadWordAt(int idx) {
		if (idx >= 0 && idx < words.size()) {
			words.get(idx).load();
		}
	}

	@Override
	public void onUp() {
	}

	@Override
	public void onDown() {
	}
}
