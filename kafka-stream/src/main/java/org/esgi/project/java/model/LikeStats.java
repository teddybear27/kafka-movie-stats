package org.esgi.project.java.model;

public class LikeStats {
    public int id;
    public float mean_score;
    public int count = 0;

    public LikeStats() {
    }

    public void add(float new_val) {
        this.mean_score = (this.mean_score * this.count + new_val) / (this.count + 1);
        this.count++;
    }
}
