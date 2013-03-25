package nl.liacs.adk;

import android.view.View;

public interface IController {
	public ISnake getSnake();
	public void setSnake(ISnake snake);
	public void setListeners(View v);
	public void unsetListeners();
	public void update();
}
