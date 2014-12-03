package paoledo.app.theunknownepisode.Utilities;

import android.support.v4.view.ViewPager;
import android.view.View;

public class ZoomOutPageTransformer implements ViewPager.PageTransformer{

    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1){ // [-Infinity, -1)
            // page off screen to the left
            view.setAlpha(0);
        } else if (position <= 1){ // [-1, 1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageHeight * (1 - scaleFactor) / 2;
            if (position < 0){
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // scale the page down
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // fade the page relative to its size
            view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
        } else { // [1, +Infinity]
            // this page is way off screen to the right
            view.setAlpha(0);
        }
    }
}
