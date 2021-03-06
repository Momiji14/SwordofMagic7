package swordofmagic7.Inventory;

import swordofmagic7.Item.RuneParameter;

import java.util.Comparator;

public enum RuneSortType {
    Name("名前順"),
    Level("レベル順"),
    Quality("品質順"),
    ;

    public String Display;

    RuneSortType(String Display) {
        this.Display = Display;
    }

    public boolean isName() {
        return this == Name;
    }

    public boolean isLevel() {
        return this == Level;
    }

    public boolean isQuality() {
        return this == Quality;
    }
}

class RuneSortName implements Comparator<RuneParameter> {
    public int compare(RuneParameter rune1, RuneParameter rune2) {
        if (rune1.Id.equals(rune2.Id)) {
            return Double.compare(rune1.Quality, rune2.Quality);
        } else return rune1.Id.compareTo(rune2.Id);
    }
}

class RuneSortLevel implements Comparator<RuneParameter> {
    public int compare(RuneParameter rune1, RuneParameter rune2) {
        if (rune1.Level == rune2.Level) {
            return new RuneSortName().compare(rune1, rune2);
        } else return rune1.Level - rune2.Level;
    }
}

class RuneSortQuality implements Comparator<RuneParameter> {
    public int compare(RuneParameter rune1, RuneParameter rune2) {
        if (rune1.Quality == rune2.Quality) {
            return new RuneSortName().compare(rune1, rune2);
        } else return Double.compare(rune1.Quality, rune2.Quality);
    }
}