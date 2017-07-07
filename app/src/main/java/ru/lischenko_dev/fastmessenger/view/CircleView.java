package ru.lischenko_dev.fastmessenger.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import ru.lischenko_dev.fastmessenger.R;

public class CircleView extends View {

    private int BORDER_COLOR;
    private int CIRCLE_COLOR;
    private String TEXT;

    private final TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private String mText;

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0);

        CIRCLE_COLOR = a.getColor(R.styleable.CircleView_fill_color_view, Color.BLACK);
        BORDER_COLOR = a.getColor(R.styleable.CircleView_border_color_view, Color.WHITE);
        TEXT = a.getString(R.styleable.CircleView_text_view);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        mText = TEXT;

        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(16);

        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setColor(CIRCLE_COLOR);
        mCirclePaint.setAntiAlias(true);

        mBorderPaint.setStyle(Paint.Style.FILL);
        mBorderPaint.setColor(0x00000000);
        mBorderPaint.setAntiAlias(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int width = getWidth();
        final int height = getHeight();

        // border
        
        canvas.drawCircle(width / 2, height / 2, Math.min(width, height) / 2, mCirclePaint);
		//canvas.drawCircle(width / 2, height / 2, Math.min(width, height) / 2, mBorderPaint);
        if (mText != null) {
            int xPos = (width / 2);
            int yPos = (int) ((height / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));
            canvas.drawText(mText, xPos, yPos, mTextPaint);
        }
    }
	
    /**
     * Set the default text size to a given unit and value.
     * See {@link TypedValue} for the possible dimension units.
     *
     * @param unit the desired dimension unit.
     * @param size the desired size in the given units.
     */
    public void setTextSize(int unit, float size) {
        Context c = getContext();
        Resources res;

        res = c == null ? Resources.getSystem() : c.getResources();

        setRawTextSize(TypedValue.applyDimension(
                unit, size, res.getDisplayMetrics()));
    }

    private void setRawTextSize(float size) {
        if (size != mTextPaint.getTextSize()) {
            mTextPaint.setTextSize(size);

            invalidate();
        }
    }

    /**
     * Returns the current text size
     */
    public float getTextSize() {
        return mTextPaint.getTextSize();
    }

    public void setTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    /**
     * Gets the current color of text
     */
    public int getTextColor() {
        return mTextPaint.getColor();
    }

    /**
     * Sets the text color
     *
     * @param color the color to set
     */
    public void setTextColor(int color) {
        if (mTextPaint.getColor() != color) {
            mTextPaint.setColor(color);
            invalidate();
        }
    }

    /**
     * Gets the current color of circle background
     */
    public int getCircleColor() {
        return mCirclePaint.getColor();
    }

    /**
     * Sets the color to circle background
     *
     * @param color the color to set
     */
    public void setCircleColor(int color) {
        if (mCirclePaint.getColor() != color) {
            mCirclePaint.setColor(color);
            invalidate();
        }
    }

    /**
     * Sets the string value to this view
     *
     * @param text the text to set
     */
    public void setText(String text) {
        if (text == null) {
            text = "";
        }
        mText = text;
        invalidate();
    }
}
