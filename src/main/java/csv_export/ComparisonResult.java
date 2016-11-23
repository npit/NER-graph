package csv_export;

public class ComparisonResult {
    private double valueSim;
    private double containmentSim;
    private double sizeSim;

    public ComparisonResult() {
    }

    public ComparisonResult(double valueSim, double containmentSim, double sizeSim) {
        this.valueSim = valueSim;
        this.containmentSim = containmentSim;
        this.sizeSim = sizeSim;
    }

    public double getValueSim() {
        return valueSim;
    }

    public void setValueSim(double valueSim) {
        this.valueSim = valueSim;
    }

    public double getContainmentSim() {
        return containmentSim;
    }

    public void setContainmentSim(double containmentSim) {
        this.containmentSim = containmentSim;
    }

    public double getSizeSim() {
        return sizeSim;
    }

    public void setSizeSim(double sizeSim) {
        this.sizeSim = sizeSim;
    }
}
