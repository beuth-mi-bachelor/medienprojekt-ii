package de.beuth_hochschule.Schabuu.util;

import android.app.Activity;
import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import java.util.ArrayList;

public class SolutionHolder {

    private ArrayList<TextView> solutionInputHolder;
    private String solution;
    private int lengthOfSolution;
    private Activity appContext;
    private int charPointer;
    private Emitter.Listener callback;

    public SolutionHolder(LinearLayout layout, Emitter.Listener callback, Activity appContext, String solution) {
        this.solutionInputHolder = new ArrayList<TextView>();
        this.solution = solution;
        this.lengthOfSolution = solution.length();
        this.appContext = appContext;
        this.charPointer = 0;
        SolutionHolder self = this;
        this.callback = callback;
        for (int i = 0; i < this.lengthOfSolution; i++) {
            TextView textView = new TextView(self.appContext);
            self.solutionInputHolder.add(textView);
        }
        this.appendToView(layout);
    }

    public void appendToView(LinearLayout layout) {
        for (int i = 0; i < this.solutionInputHolder.size(); i++) {
            TextView currentTextView = solutionInputHolder.get(i);
            layout.addView(currentTextView);
        }
    }

    public void addChar(String character) {
        if (this.isSolved()) {
            callback.call();
        }
        TextView currentTextView = this.solutionInputHolder.get(this.charPointer);
        currentTextView.setText(character);
        this.charPointer++;
    }

    public void deleteChar() {
        TextView currentTextView = this.solutionInputHolder.get(this.charPointer);
        currentTextView.setText("");
        this.charPointer--;
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
        if ((this.lengthOfSolution - 1) >= this.charPointer) {
            if (this.solution.equals(this.buildSolution())) {
                return true;
            }
        }
        return false;
    }

}
