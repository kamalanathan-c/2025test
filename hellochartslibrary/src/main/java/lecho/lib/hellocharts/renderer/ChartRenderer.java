package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.Viewport;
import android.graphics.Canvas;

/**
 * Interface for all chart renderer.
 */
public interface ChartRenderer {
	public void setHasLableRect();
		
	
	/**
	 * Initialize maximum viewport, called when chart data changed.Usually you will have to do some calculation in
	 * implementation of that method. If isViewportCalculationEnabled is false this method should do nothing.
	 */
	public void initMaxViewport();

	/**
	 * Initialize currentViewport, usually set it equals to maxViewport. If isViewportCalculationEnabled is false this
	 * method should do nothing.
	 */
	public void initCurrentViewport();

	/**
	 * Initialize measurements i.e. circle area for PieChart.
	 */
	public void initDataMeasuremetns();

	/**
	 * Initialize common data attributes label font size, font color etc. Should be called before viewports
	 * initializations.
	 */
	public void initDataAttributes();

	/**
	 * Draw chart data.
	 */
	public void draw(Canvas canvas);

	/**
	 * Draw chart data that should not be clipped to contentRect area.
	 */
	public void drawUnclipped(Canvas canvas);

	/**
	 * Checks if given pixel coordinates corresponds to any chart value. If yes return true and set selectedValue, if
	 * not selectedValue should be *cleared* and method should return false.
	 */
	public boolean checkTouch(float touchX, float touchY);

	/**
	 * Returns true if there is value selected.
	 */
	public boolean isTouched();

	/**
	 * Clear value selection.
	 */
	public void clearTouch();

	public void setMaxViewport(Viewport maxViewport);

	public Viewport getMaxViewport();

	public void setCurrentViewport(Viewport viewport);

	public Viewport getCurrentViewport();

	public boolean isViewportCalculationEnabled();

	public void setViewportCalculationEnabled(boolean isEnabled);

	public void selectValue(SelectedValue selectedValue);

	public SelectedValue getSelectedValue();

}
