package csv_export;

import java.util.List;

public class ComparisonContainer {
    private String text1;
    private String text2;
    private List<ComparisonResult> results;

    public ComparisonContainer() {
    }

    public ComparisonContainer(String text1, String text2, List<ComparisonResult> results) {
        this.text1 = text1;
        this.text2 = text2;
        this.results = results;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public List<ComparisonResult> getResults() {
        return results;
    }

    public void setResults(List<ComparisonResult> results) {
        this.results = results;
    }

    public void addResult(ComparisonResult result) {
        this.results.add(result);
    }
}
