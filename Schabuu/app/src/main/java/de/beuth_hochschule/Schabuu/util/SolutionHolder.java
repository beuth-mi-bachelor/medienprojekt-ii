package de.beuth_hochschule.Schabuu.util;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Layout;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import java.util.ArrayList;

import de.beuth_hochschule.Schabuu.R;

import static de.beuth_hochschule.Schabuu.R.color.schabuu_white;

public class SolutionHolder {

    private ArrayList<TextView> solutionInputHolder;
    private String solution;
    private int lengthOfSolution;
    private Activity appContext;
    private int charPointer;
    private Emitter.Listener callback;

    public SolutionHolder(LinearLayout layout, Emitter.Listener callback, Activity appContext, String solution, Typeface geoBold) {
        this.solutionInputHolder = new ArrayList<TextView>();
        this.solution = solution;
        this.lengthOfSolution = solution.length();
        this.appContext = appContext;
        this.charPointer = 0;
        SolutionHolder self = this;
        this.callback = callback;
        for (int i = 0; i < this.lengthOfSolution; i++) {
            TextView textView = new TextView(new ContextThemeWrapper(self.appContext, R.style.input_solution));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1.0f;
            params.width = 0;
            params.setMargins(10, 5, 10, 5);
            textView.setLayoutParams(params);
            textView.setBackgroundResource(R.color.schabuu_white);
            textView.setTypeface(geoBold);
            textView.setTextSize(20);
            textView.setAlpha(0.7f);
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
            textView.setTextColor(Color.parseColor("#474569"));
            self.solutionInputHolder.add(textView);
        }
        this.appendToView(layout);
    }

    public void appendToView(LinearLayout layout) {
        for (int i = 0; i < this.solutionInputHolder.size(); i++) {
            TextView currentTextView = solutionInputHolder.get(i);
            layout.addView(currentTextView);
            System.out.println(layout.getChildCount());
        }
    }

    public void addChar(String character) {
        if (!this.isFull()) {
            TextView currentTextView = this.solutionInputHolder.get(this.charPointer);
            currentTextView.setText(character);
            this.charPointer++;
        }
        if (this.isSolved()) {
            this.callback.call();
        }
    }

    public void deleteChar() {
        this.charPointer--;
        if (this.charPointer < 0) {
            this.charPointer = 0;
        }
        if (this.charPointer >= 0) {
            TextView currentTextView = this.solutionInputHolder.get(this.charPointer);
            currentTextView.setText("");
        }
    }

    public void deleteWord() {
        for (int i = 0; i < this.solutionInputHolder.size(); i++) {
            TextView currentTextView = solutionInputHolder.get(i);
            currentTextView.setText("");
        }
        this.charPointer = 0;
    }

    private String buildSolution() {
        String solution = "";
        for (int i = 0; i < this.solutionInputHolder.size(); i++) {
            TextView currentTextView = solutionInputHolder.get(i);
            solution += currentTextView.getText();
        }
        return solution;
    }

    private boolean isSolved() {
        System.out.println(this.solution.toLowerCase() + " : " + this.buildSolution().toLowerCase());
        return this.solution.toLowerCase().equals(this.buildSolution().toLowerCase());
    }

    private boolean isFull() {
        return this.charPointer == this.lengthOfSolution;
    }

}
