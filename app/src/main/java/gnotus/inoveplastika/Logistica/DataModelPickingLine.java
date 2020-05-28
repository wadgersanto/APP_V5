package gnotus.inoveplastika.Logistica;

import gnotus.inoveplastika.DataModels.DataModelBi;

public class DataModelPickingLine extends DataModelBi {

    private Double nrboxes_total;
    private Double nrboxes_read;
    private Double nrboxes_pend;
    private Double boxes_qtt_read;
    private Double boxes_qtt_pend;


    public Double getNrboxes_total() {
        return nrboxes_total;
    }

    public void setNrboxes_total(Double nrboxes_total) {
        this.nrboxes_total = nrboxes_total;
    }

    public Double getNrboxes_read() {
        return nrboxes_read;
    }

    public void setNrboxes_read(Double nrboxes_read) {
        this.nrboxes_read = nrboxes_read;
    }

    public Double getNrboxes_pend() {
        return nrboxes_pend;
    }

    public void setNrboxes_pend(Double nrboxes_pend) {
        this.nrboxes_pend = nrboxes_pend;
    }

    public Double getBoxes_qtt_read() {
        return boxes_qtt_read;
    }

    public void setBoxes_qtt_read(Double boxes_qtt_read) {
        this.boxes_qtt_read = boxes_qtt_read;
    }

    public Double getBoxes_qtt_pend() {
        return boxes_qtt_pend;
    }

    public void setBoxes_qtt_pend(Double boxes_qtt_pend) {
        this.boxes_qtt_pend = boxes_qtt_pend;
    }
}
