package com.vinh.multichoice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

public class ViewAnimation {

    public static boolean rotateFab(final View view, boolean rotate) {
        view.animate().setDuration(200).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        }).rotation(rotate ? 135f : 0f);
        return rotate;
    }

    public static void showIn(final View view) {
        view.setVisibility(View.VISIBLE);
        view.setAlpha(0f);
        view.setScaleX(0.0f);
        view.setScaleY(0.0f);
        view.animate()
                .setDuration(200)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                }).alpha(1f).start();
    }

    public static void showOut(final View v) {
        v.setVisibility(View.VISIBLE);
        v.setAlpha(1f);
        v.setScaleX(1f);
        v.setScaleY(1f);
        v.animate()
                .setDuration(200)
                .scaleX(0f)
                .scaleY(0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        v.setVisibility(View.GONE);
                        super.onAnimationEnd(animation);
                    }
                }).alpha(0f)
                .start();
    }

    public static void init(final View v) {
        v.setVisibility(View.GONE);
        v.setTranslationY(v.getHeight());
        v.setAlpha(0f);
    }
}
